package com.seezoon.application.user.executor;

import com.seezoon.domain.service.user.LoginService;
import com.seezoon.infrastructure.configuration.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 退出登录
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Validated
public class LogoutCmdExe {

    private final LoginService loginService;

    public void execute() {
        loginService.logout(SecurityContextHolder.getUid());
    }
}
