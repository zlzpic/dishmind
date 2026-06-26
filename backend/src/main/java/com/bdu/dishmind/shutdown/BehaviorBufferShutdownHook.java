package com.bdu.dishmind.shutdown;

import com.bdu.dishmind.service.buffer.BehaviorFlushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BehaviorBufferShutdownHook implements DisposableBean {

    @Autowired
    private BehaviorFlushService flushService;

    @Override
    public void destroy() throws Exception {
        log.info("应用关闭，开始优雅刷盘行为缓冲...");
        int maxRound = 10; // 最多处理 10 × 100 = 1000 条，防止关闭过程无限阻塞
        int round = 0;

        while (round < maxRound) {
            boolean hasMore = flushService.flushRemaining();
            if (!hasMore) {
                break;
            }
            round++;
            log.info("优雅关闭刷盘第 {} 轮完成", round);
        }

        if (round >= maxRound) {
            log.warn("优雅关闭刷盘达到上限，可能仍有数据残留于 Redis");
        } else {
            log.info("优雅关闭刷盘完成，Redis 缓冲已清空");
        }
    }
}
