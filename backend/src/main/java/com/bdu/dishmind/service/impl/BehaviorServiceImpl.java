package com.bdu.dishmind.service.impl;

import com.bdu.dishmind.config.BehaviorBufferProperties;
import com.bdu.dishmind.dto.buffer.BehaviorBufferItem;
import com.bdu.dishmind.dto.request.BehaviorReportRequest;
import com.bdu.dishmind.entity.UserBehavior;
import com.bdu.dishmind.entity.UserTagPreference;
import com.bdu.dishmind.repository.RecipeTagRepository;
import com.bdu.dishmind.repository.UserBehaviorRepository;
import com.bdu.dishmind.repository.UserTagPreferenceRepository;
import com.bdu.dishmind.service.BehaviorService;
import com.bdu.dishmind.service.buffer.RedisBufferService;
import com.bdu.dishmind.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    @Autowired
    private UserTagPreferenceRepository userTagPreferenceRepository;

    @Autowired
    private RecipeTagRepository recipeTagRepository;

    @Autowired
    private RedisBufferService redisBufferService;

    @Autowired
    private BehaviorBufferProperties bufferProperties;

    @Override
    @Transactional
    public void report(BehaviorReportRequest request) {
        ServiceUtil.requireNonNull(request.getUserId(), "用户ID不能为空");
        ServiceUtil.requireNonNull(request.getRecipeId(), "菜谱ID不能为空");
        ServiceUtil.requireNonNull(request.getBehaviorType(), "行为类型不能为空");

        // 预计算：无论走缓冲还是直接写库都需要
        double weightDelta = resolveWeightDelta(request.getBehaviorType());
        List<Integer> tagIds = recipeTagRepository.findTagIdsByRecipeId(request.getRecipeId());

        // ===== 缓冲模式（正常情况） =====
        if (bufferProperties.isEnabled()) {
            try {
                BehaviorBufferItem item = new BehaviorBufferItem();
                item.setUserId(request.getUserId());
                item.setRecipeId(request.getRecipeId());
                item.setBehaviorType(request.getBehaviorType());
                item.setDurationSeconds(request.getDurationSeconds());
                item.setTagIds(tagIds);
                item.setWeightDelta(weightDelta);
                item.setTimestamp(LocalDateTime.now());

                redisBufferService.push(item);
                return; // 只写 Redis，< 5ms 返回，削峰完成
            } catch (Exception e) {
                log.error("Redis 缓冲写入失败，降级为直接写库", e);
                // 降级：继续执行下方直接写库逻辑
            }
        }

        // ===== 直接写库（降级 fallback / 缓冲关闭时） =====
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(request.getUserId());
        behavior.setRecipeId(request.getRecipeId());
        behavior.setBehaviorType(request.getBehaviorType());
        behavior.setDurationSeconds(request.getDurationSeconds());
        userBehaviorRepository.save(behavior);

        // 同步更新 Tag 权重
        if (weightDelta != 0 && tagIds != null && !tagIds.isEmpty()) {
            updateTagWeightDirect(request.getUserId(), tagIds, weightDelta);
        }
    }

    private double resolveWeightDelta(String behaviorType) {
        if ("VIEW".equals(behaviorType)) {
            return 0.05;
        } else if ("CLICK".equals(behaviorType)) {
            return 0.2;
        } else if ("COLLECT".equals(behaviorType)) {
            return 0.5;
        } else if ("SHARE".equals(behaviorType)) {
            return 0.3;
        } else if ("UNCOLLECT".equals(behaviorType)) {
            return -0.3;
        } else if ("DISLIKE".equals(behaviorType)) {
            return -2.0;
        } else {
            return 0.0;
        }
    }

    /**
     * 直接写库时的 Tag 权重更新（供降级使用）
     */
    @Transactional
    public void updateTagWeightDirect(Long userId, List<Integer> tagIds, double weightDelta) {
        boolean isPositive = weightDelta > 0;

        for (Integer tagId : tagIds) {
            Optional<UserTagPreference> exist = userTagPreferenceRepository
                    .findByUserIdAndTagId(userId, tagId);

            if (exist.isPresent()) {
                UserTagPreference pref = exist.get();
                double newScore = pref.getScore().doubleValue() + weightDelta;
                pref.setScore(BigDecimal.valueOf(newScore));

                if (isPositive) {
                    pref.setPositiveCount(pref.getPositiveCount() + 1);
                } else {
                    pref.setNegativeCount(pref.getNegativeCount() + 1);
                }

                pref.setLastInteraction(LocalDateTime.now());
                userTagPreferenceRepository.save(pref);

            } else {
                if (isPositive) {
                    UserTagPreference pref = new UserTagPreference();
                    pref.setUserId(userId);
                    pref.setTagId(tagId);
                    pref.setScore(BigDecimal.valueOf(weightDelta));
                    pref.setPositiveCount(1);
                    pref.setNegativeCount(0);
                    pref.setLastInteraction(LocalDateTime.now());
                    userTagPreferenceRepository.save(pref);
                }
            }
        }
    }
}
