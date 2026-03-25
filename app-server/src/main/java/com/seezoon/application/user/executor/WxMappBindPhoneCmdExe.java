package com.seezoon.application.user.executor;

import com.seezoon.application.user.dto.WxMappBindPhoneCmd;
import com.seezoon.domain.service.user.UserProfileService;
import com.seezoon.infrastructure.configuration.context.SecurityContextHolder;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.properties.AppProperties;
import com.seezoon.infrastructure.rpc.wx.WxGetPhoneNumberService;
import com.seezoon.infrastructure.rpc.wx.WxStableAccessTokenService;
import com.seezoon.infrastructure.rpc.wx.dto.WxGetPhoneNumberResp;
import com.seezoon.infrastructure.rpc.wx.dto.WxStableAccessTokenResp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 小程序绑定手机号：用 getPhoneNumber 的 code 换取手机号并绑定当前登录用户
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class WxMappBindPhoneCmdExe {

    private final AppProperties appProperties;
    private final WxStableAccessTokenService wxStableAccessTokenService;
    private final WxGetPhoneNumberService wxGetPhoneNumberService;
    private final UserProfileService userProfileService;

    public void execute(@Valid @NotNull WxMappBindPhoneCmd cmd) {
        Long uid = SecurityContextHolder.getUid();
        var wx = appProperties.getWx();
        WxStableAccessTokenResp tokenResp = wxStableAccessTokenService.execute(wx.getMappId(), wx.getMappSecret());
        if (!tokenResp.success()) {
            throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
        }
        WxGetPhoneNumberResp phoneResp = wxGetPhoneNumberService.execute(tokenResp.getAccessToken(), cmd.getCode());
        if (!phoneResp.success()) {
            throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
        }
        WxGetPhoneNumberResp.PhoneInfo info = phoneResp.getPhoneInfo();
        userProfileService.updateMobile(uid, info.getPhoneNumber());
        log.info("wx mapp bind phone success, uid:{}, mobile:{}", uid, info.getPhoneNumber());

    }
}
