package com.seezoon.application.user.executor;

import com.seezoon.application.user.dto.WxMappLoginCmd;
import com.seezoon.application.user.dto.clientobject.LoginCO;
import com.seezoon.domain.dao.types.UserOauthType;
import com.seezoon.domain.service.user.LoginService;
import com.seezoon.domain.service.user.vo.LoginTokenVO;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.properties.AppProperties;
import com.seezoon.infrastructure.properties.WxProperties;
import com.seezoon.infrastructure.rpc.wx.WxCode2SessionService;
import com.seezoon.infrastructure.rpc.wx.dto.WxCode2SessionResp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 微信小程序登录
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class WxMappLoginCmdExe {

    private final LoginService loginService;
    private final AppProperties appProperties;
    private final WxCode2SessionService code2SessionService;

    @SuppressWarnings("unchecked")
    public LoginCO execute(@Valid @NotNull WxMappLoginCmd cmd) {
        WxProperties wx = appProperties.getWx();
        WxCode2SessionResp resp = code2SessionService.execute(wx.getMappId(), wx.getMappSecret(), cmd.getCode());
        if (!resp.success()) {
            throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
        }
        LoginTokenVO loginTokenVO = loginService.loginByOauth(UserOauthType.WX_MIN_APP, resp.getOpenid(),
                resp.getUnionid());
        LoginCO co = new LoginCO();
        co.setAccessToken(loginTokenVO.getAccessToken());
        co.setAccessTokenExpire(loginTokenVO.getAccessTokenExpire());
        co.setRefreshToken(loginTokenVO.getRefreshToken());
        co.setRefreshTokenExpire(loginTokenVO.getRefreshTokenExpire());
        log.info("user wx mapp login success uid:{}", loginTokenVO.getUid());
        return co;
    }
}
