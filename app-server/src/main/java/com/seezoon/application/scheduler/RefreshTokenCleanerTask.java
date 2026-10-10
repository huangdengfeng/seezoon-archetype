package com.seezoon.application.scheduler;

import com.seezoon.domain.service.sys.TaskLockService;
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

    private static final String TASK_ID = "RefreshTokenCleanerTask";
    private static final int LOCK_SECONDS = 60;
    private final TaskLockService taskLockService;
    /**
     * 1 小时
     */
    private final long interval = 60 * 60 * 1000;
    private final LoginTokenService loginTokenService;

    @Scheduled(fixedDelay = interval)
    public void execute() {
        if (!taskLockService.tryLock(TASK_ID, LOCK_SECONDS)) {
            return;
        }
        try {
            int cleared = loginTokenService.clear();
            log.info("clean refresh token count: {}", cleared);
        } catch (Throwable e) {
            log.error("task error", e);
        } finally {
            taskLockService.unlock(TASK_ID);
        }
    }
}
