package com.bdu.dishmind.service.buffer;

import com.alibaba.fastjson.JSON;
import com.bdu.dishmind.dto.buffer.BehaviorBufferItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisBufferService {

    private static final String BEHAVIOR_BUFFER_LIST_KEY = "behavior:buffer:list";

    /** Lua 脚本：原子地取出前 N 条并删除已取部分 */
    private static final String LUA_LRANGE_TRIM =
            "local n = tonumber(ARGV[1]); " +
                    "local items = redis.call('lrange', KEYS[1], 0, n - 1); " +
                    "redis.call('ltrim', KEYS[1], n, -1); " +
                    "return items;";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /** 生产者：report() 调用，只干这一件事 */
    public void push(BehaviorBufferItem item) {
        String json = JSON.toJSONString(item);
        redisTemplate.opsForList().rightPush(BEHAVIOR_BUFFER_LIST_KEY, json);
    }

    /** 消费者：定时任务调用，原子弹出最多 batchSize 条 */
    public List<BehaviorBufferItem> popBatch(int batchSize) {
        try {
            DefaultRedisScript<List> script = new DefaultRedisScript<>();
            script.setScriptText(LUA_LRANGE_TRIM);
            script.setResultType(List.class);

            @SuppressWarnings("unchecked")
            List<String> jsonList = redisTemplate.execute(
                    script,
                    Collections.singletonList(BEHAVIOR_BUFFER_LIST_KEY),
                    String.valueOf(batchSize)
            );

            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }

            return jsonList.stream()
                    .map(json -> JSON.parseObject(json, BehaviorBufferItem.class))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Redis 缓冲批量取出失败", e);
            return Collections.emptyList();
        }
    }

    /** 监控/优雅关闭时查看残余量 */
    public long getBufferSize() {
        Long size = redisTemplate.opsForList().size(BEHAVIOR_BUFFER_LIST_KEY);
        return size != null ? size : 0;
    }
}
