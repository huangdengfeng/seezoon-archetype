package com.seezoon.application.user.executor;

import com.seezoon.application.user.dto.WxAppLoginCmd;
import com.seezoon.application.user.dto.clientobject.LoginCO;
import com.seezoon.domain.dao.mapper.UserProfileMapper;
import com.seezoon.domain.dao.po.UserProfilePO;
import com.seezoon.domain.dao.types.UserOauthType;
import com.seezoon.domain.service.file.SysFileService;
import com.seezoon.domain.service.file.vo.SysFileVO;
import com.seezoon.domain.service.user.LoginService;
import com.seezoon.domain.service.user.UserProfileService;
import com.seezoon.domain.service.user.vo.LoginTokenVO;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.properties.AppProperties;
import com.seezoon.infrastructure.properties.WxProperties;
import com.seezoon.infrastructure.rpc.wx.WxOauthAccessTokenService;
import com.seezoon.infrastructure.rpc.wx.WxUserInfoService;
import com.seezoon.infrastructure.rpc.wx.dto.WxOauthAccessTokenResp;
import com.seezoon.infrastructure.rpc.wx.dto.WxUserInfoResp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 微信登录
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class WxAppLoginCmdExe {

    private final LoginService loginService;
    private final AppProperties appProperties;
    private final WxOauthAccessTokenService oauthAccessTokenService;
    private final WxUserInfoService wxUserInfoService;
    private final UserProfileMapper userProfileMapper;
    private final SysFileService sysFileService;

    private final UserProfileService userProfileService;

    public LoginCO execute(@Valid @NotNull WxAppLoginCmd cmd) {
        WxProperties wx = appProperties.getWx();
        WxOauthAccessTokenResp tokenResp = oauthAccessTokenService.execute(wx.getAppId(), wx.getAppSecret(),
                cmd.getCode());
        if (!tokenResp.success()) {
            throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
        }
        LoginTokenVO loginTokenVO = loginService.loginByOauth(UserOauthType.WX_APP, tokenResp.getOpenid(),
                tokenResp.getUnionid());
        // 补充图像昵称
        try {
            this.updateInfo(loginTokenVO.getUid(), tokenResp.getAccessToken(), tokenResp.getOpenid());
        } catch (IOException e) {
            log.error("update use info form wx error uid:{}", loginTokenVO.getUid(), e);
        }
        LoginCO co = new LoginCO();
        co.setAccessToken(loginTokenVO.getAccessToken());
        co.setAccessTokenExpire(loginTokenVO.getAccessTokenExpire());
        co.setRefreshToken(loginTokenVO.getRefreshToken());
        co.setRefreshTokenExpire(loginTokenVO.getRefreshTokenExpire());
        log.info("user wx login success uid:{}", loginTokenVO.getUid());
        return co;
    }

    private void updateInfo(Long uid, String accessToken, String openid) throws IOException {
        UserProfilePO userProfilePO = userProfileMapper.selectByPrimaryKey(uid);
        Assertion.notNull(userProfilePO, "use profile is null");
        if (StringUtils.isNotEmpty(userProfilePO.getNickName()) || StringUtils.isNotEmpty(userProfilePO.getAvatar())) {
            return;
        }
        WxUserInfoResp userInfoResp = wxUserInfoService.execute(accessToken, openid);
        if (!userInfoResp.success()) {
            throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
        }
        String imageId = userProfilePO.getAvatar();
        if (StringUtils.isNotEmpty(userInfoResp.getHeadImgUrl())) {
            byte[] fileBytes = IOUtils.toByteArray(new URL(userInfoResp.getHeadImgUrl()));
            SysFileVO vo = new SysFileVO();
            vo.setName("wx_head_img");
            vo.setMimeType("image/jpeg");
            vo.setFileSize((long) fileBytes.length);
            vo.setData(fileBytes);
            vo.setUid(uid);
            Long fileId = sysFileService.createFile(vo);
            imageId = fileId.toString();
        }
        userProfileService.update(uid, userInfoResp.getNickname(), imageId);
    }
}
