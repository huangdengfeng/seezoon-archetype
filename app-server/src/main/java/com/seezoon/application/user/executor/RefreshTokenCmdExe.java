package com.seezoon.application.user.executor;

import com.seezoon.application.user.dto.RefreshTokenCmd;
import com.seezoon.application.user.dto.clientobject.LoginCO;
import com.seezoon.domain.service.user.LoginTokenService;
import com.seezoon.domain.service.user.vo.LoginTokenVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 刷新令牌
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class RefreshTokenCmdExe {

    private final LoginTokenService loginTokenService;

    public LoginCO execute(@Valid @NotNull RefreshTokenCmd cmd) {
        LoginTokenVO loginTokenVO = loginTokenService.refreshToken(cmd.getRefreshToken());
        LoginCO loginCO = new LoginCO();
        loginCO.setAccessToken(loginTokenVO.getAccessToken());
        loginCO.setAccessTokenExpire(loginTokenVO.getAccessTokenExpire());
        loginCO.setRefreshToken(loginTokenVO.getRefreshToken());
        loginCO.setRefreshTokenExpire(loginTokenVO.getRefreshTokenExpire());
        return loginCO;
    }

}