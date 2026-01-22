package com.seezoon.application.scheduler;

import com.seezoon.infrastructure.constants.Constants;
import com.seezoon.infrastructure.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 设备状态检查
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DeviceStatusTask {

    private final AppProperties appProperties;

    @Scheduled(fixedDelayString = Constants.OFFLINE_CLEANER_INTERVAL)
    public void execute() {
        try {
            long heartbeatTimeout = appProperties.getDevice().getHeartbeatTimeout();
            // TODO
            log.info("offline device count:{} heartbeatTimeout:{}", 1, heartbeatTimeout);
        } catch (Throwable e) {
            log.error("task error", e);
        }
    }
}
