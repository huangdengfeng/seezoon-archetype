package com.seezoon.application.sys.executor;

import com.seezoon.application.sys.authentication.context.SecurityContext;
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
        String accessToken = SecurityContext.getAccessToken();
        if (accessToken != null) {
            sessionService.destroySession(accessToken);
            log.info("logout success, token:{}", accessToken);
        } else {
            log.warn("logout but no access token found");
        }
    }
}

