package com.seezoon.application.sys.authentication.executor;

import com.seezoon.application.sys.authentication.dto.UserPasswdLoginCmd;
import com.seezoon.application.sys.authentication.dto.clientobject.AuthorizationTokenCO;
import com.seezoon.domain.service.sys.authentication.LoginService;
import com.seezoon.domain.service.sys.authentication.SessionService;
import com.seezoon.domain.service.sys.valueobj.UserVO;
import com.seezoon.infrastructure.configuration.properties.AppProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 账号密码登录
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class UserPasswdLoginCmdExe {

    private final AppProperties appProperties;
    private final LoginService loginService;
    private final SessionService sessionService;

    public AuthorizationTokenCO execute(@NotNull @Valid UserPasswdLoginCmd cmd) {
        UserVO userVO = loginService.login(cmd.getUsername(), cmd.getPassword());
        String sessionId = sessionService.createSession(userVO,
                appProperties.getLogin().getSessionTimeout().toSeconds());
        AuthorizationTokenCO co = new AuthorizationTokenCO(sessionId);
        return co;
    }
}
