package com.seezoon.application.scheduler;

import com.seezoon.domain.service.sys.authentication.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 登录态清理
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CleanupExpiredSessionsTask {

    private final SessionService sessionService;

    @Scheduled(fixedDelay = 1000 *60 *5)
    public void execute() {
        try {
            int cleaned = sessionService.cleanExpiredSessions();
            log.debug("cleanup expired sessions task success:{}", cleaned);
        } catch (Throwable e) {
            log.error("cleanup expired sessions task error", e);
        }
    }
}
