package com.bdu.dishmind.service.buffer;

import com.bdu.dishmind.dto.buffer.BehaviorBufferItem;
import com.bdu.dishmind.entity.UserBehavior;
import com.bdu.dishmind.entity.UserTagPreference;
import com.bdu.dishmind.repository.UserBehaviorRepository;
import com.bdu.dishmind.repository.UserTagPreferenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BehaviorFlushService {

    @Autowired
    private RedisBufferService redisBufferService;
    @Autowired
    private UserBehaviorRepository userBehaviorRepository;
    @Autowired
    private UserTagPreferenceRepository userTagPreferenceRepository;

    /** 定时任务入口：弹出 batchSize 条并落库 */
    @Transactional
    public void flushBatch(int batchSize) {
        List<BehaviorBufferItem> items = redisBufferService.popBatch(batchSize);

        // 新增日志 1：看有没有从 Redis 取出数据
        log.info("【刷盘】从 Redis 取出 {} 条行为记录", items.size());

        if (items.isEmpty()) {
            return;
        }
        processItems(items);
    }

    /** 优雅关闭入口：返回是否还有残余数据 */
    public boolean flushRemaining() {
        List<BehaviorBufferItem> items = redisBufferService.popBatch(100);
        if (items.isEmpty()) {
            return false;
        }
        processItems(items);
        return redisBufferService.getBufferSize() > 0;
    }

    /** 真正的批量写库逻辑 */
    @Transactional
    public void processItems(List<BehaviorBufferItem> items) {
        // 1. 批量插入 user_behavior（单次 INSERT，数据库压力恒定）
        List<UserBehavior> behaviors = items.stream().map(item -> {
            UserBehavior b = new UserBehavior();
            b.setUserId(item.getUserId());
            b.setRecipeId(item.getRecipeId());
            b.setBehaviorType(item.getBehaviorType());
            b.setDurationSeconds(item.getDurationSeconds());
            // 如果 UserBehavior 实体有 createdAt 字段，在这里赋值：
            // b.setCreatedAt(item.getTimestamp());
            return b;
        }).collect(Collectors.toList());
        log.info("【刷盘】准备写入 user_behavior {} 条", behaviors.size());
        userBehaviorRepository.saveAll(behaviors);
        log.info("【刷盘】user_behavior 写入完成");
        log.debug("行为表批量写入 {} 条", behaviors.size());

        // 2. 内存聚合 Tag 权重：Map<userId, Map<tagId, Agg>>
        Map<Long, Map<Integer, TagWeightAgg>> aggregation = new HashMap<>();
        for (BehaviorBufferItem item : items) {
            if (item.getTagIds() == null || item.getWeightDelta() == null || item.getWeightDelta() == 0) {
                continue;
            }
            boolean isPositive = item.getWeightDelta() > 0;
            Map<Integer, TagWeightAgg> userAgg = aggregation
                    .computeIfAbsent(item.getUserId(), k -> new HashMap<>());

            for (Integer tagId : item.getTagIds()) {
                userAgg.computeIfAbsent(tagId, k -> new TagWeightAgg())
                        .add(item.getWeightDelta(), isPositive, item.getTimestamp());
            }
        }

        if (aggregation.isEmpty()) {
            return;
        }

        // 3. 按用户批量查、批量写 user_tag_preference
        List<UserTagPreference> allToSave = new ArrayList<>();

        for (Map.Entry<Long, Map<Integer, TagWeightAgg>> userEntry : aggregation.entrySet()) {
            Long userId = userEntry.getKey();
            Map<Integer, TagWeightAgg> tagAggMap = userEntry.getValue();
            List<Integer> tagIds = new ArrayList<>(tagAggMap.keySet());

            // 批量查询该用户这批 tag 的现有偏好（1 次查询替代 N 次）
            List<UserTagPreference> existingList = userTagPreferenceRepository
                    .findByUserIdAndTagIdIn(userId, tagIds);
            Map<Integer, UserTagPreference> existMap = existingList.stream()
                    .collect(Collectors.toMap(UserTagPreference::getTagId, p -> p, (a, b) -> a));

            for (Map.Entry<Integer, TagWeightAgg> tagEntry : tagAggMap.entrySet()) {
                Integer tagId = tagEntry.getKey();
                TagWeightAgg agg = tagEntry.getValue();
                UserTagPreference pref = existMap.get(tagId);

                if (pref != null) {
                    double newScore = pref.getScore().doubleValue() + agg.getScoreDelta();
                    pref.setScore(BigDecimal.valueOf(newScore));
                    pref.setPositiveCount(pref.getPositiveCount() + agg.getPositiveCount());
                    pref.setNegativeCount(pref.getNegativeCount() + agg.getNegativeCount());
                    pref.setLastInteraction(agg.getLastInteraction());
                    allToSave.add(pref);
                } else {
                    // 负向行为不新建（保持原业务规则）
                    if (agg.getScoreDelta() > 0) {
                        UserTagPreference n = new UserTagPreference();
                        n.setUserId(userId);
                        n.setTagId(tagId);
                        n.setScore(BigDecimal.valueOf(agg.getScoreDelta()));
                        n.setPositiveCount(agg.getPositiveCount());
                        n.setNegativeCount(agg.getNegativeCount());
                        n.setLastInteraction(agg.getLastInteraction());
                        allToSave.add(n);
                    }
                }
            }
        }
        log.info("【刷盘】准备写入 user_tag_preference {} 条", allToSave.size());

        if (!allToSave.isEmpty()) {
            userTagPreferenceRepository.saveAll(allToSave);
            log.info("【刷盘】user_tag_preference 写入完成");
            log.debug("偏好表批量写入 {} 条", allToSave.size());
        }
    }

    /** 内存聚合对象 */
    private static class TagWeightAgg {
        private double scoreDelta = 0;
        private int positiveCount = 0;
        private int negativeCount = 0;
        private LocalDateTime lastInteraction;

        public double getScoreDelta() {
            return scoreDelta;
        }

        public void setScoreDelta(double scoreDelta) {
            this.scoreDelta = scoreDelta;
        }

        public int getPositiveCount() {
            return positiveCount;
        }

        public void setPositiveCount(int positiveCount) {
            this.positiveCount = positiveCount;
        }

        public int getNegativeCount() {
            return negativeCount;
        }

        public void setNegativeCount(int negativeCount) {
            this.negativeCount = negativeCount;
        }

        public LocalDateTime getLastInteraction() {
            return lastInteraction;
        }

        public void setLastInteraction(LocalDateTime lastInteraction) {
            this.lastInteraction = lastInteraction;
        }

        void add(double delta, boolean isPositive, LocalDateTime time) {
            this.scoreDelta += delta;
            if (isPositive) this.positiveCount++;
            else this.negativeCount++;
            if (this.lastInteraction == null || time.isAfter(this.lastInteraction)) {
                this.lastInteraction = time;
            }
        }
    }
}
