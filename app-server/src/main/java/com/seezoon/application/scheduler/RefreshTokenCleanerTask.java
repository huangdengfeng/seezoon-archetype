package com.seezoon.application.scheduler;

import com.seezoon.domain.service.user.LoginTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 清理过期的refresh token
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenCleanerTask {

    /**
     * 1 小时
     */
    private final long interval = 60 * 60 * 1000;
    private final LoginTokenService loginTokenService;

    @Scheduled(fixedDelay = interval)
    public void execute() {
        try {
            int cleared = loginTokenService.clear();
            log.info("clean refresh token count: {}", cleared);
        } catch (Throwable e) {
            log.error("task error", e);
        }
    }
}
