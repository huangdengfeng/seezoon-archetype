package com.seezoon.application.sys.executor;

import com.seezoon.domain.service.sys.authentication.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 登出
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class LogoutCmdExe {

    private final SessionService sessionService;

    public void execute() {
        sessionService.invalid();
    }
}

