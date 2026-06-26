package com.bdu.dishmind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "behavior.buffer")
public class BehaviorBufferProperties {
    /** 总开关：false 时直接走原逻辑写库 */
    private boolean enabled = true;
    /** 定时刷盘周期，默认 5000ms */
    private long flushIntervalMs = 5000;
    /** 单次从 Redis 取出上限，默认 100 条 */
    private int batchSize = 100;
}
