package com.bdu.dishmind.scheduler;

import com.bdu.dishmind.config.BehaviorBufferProperties;
import com.bdu.dishmind.service.buffer.BehaviorFlushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BehaviorFlushScheduler {

    @Autowired
    private BehaviorBufferProperties properties;

    @Autowired
    private BehaviorFlushService flushService;

    @Scheduled(fixedRateString = "${behavior.buffer.flush-interval-ms:5000}")
    public void scheduledFlush() {
        if (!properties.isEnabled()) {
            return;
        }
        try {
            flushService.flushBatch(properties.getBatchSize());
        } catch (Exception e) {
            log.error("行为缓冲定时刷盘异常", e);
        }
    }
}
