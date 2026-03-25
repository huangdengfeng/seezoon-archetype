package com.seezoon.infrastructure.rpc.wx;

import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.rpc.wx.dto.WxCode2SessionResp;
import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 小程序登录凭证校验（code2Session）
 * <p>
 * 通过 wx.login 获取的临时登录凭证 code 传到服务端，调用此接口完成登录流程。
 *
 * @see <a
 *         href="https://developers.weixin.qq.com/miniprogram/dev/server/API/user-login/api_code2session.html">小程序登录凭证校验</a>
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class WxCode2SessionService {

    private static final String API_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final RestClient restClient;

    /**
     * 登录凭证校验，将小程序端 wx.login 获取的 code 换取 session_key、openid 等
     *
     * @param appId 小程序 appId
     * @param secret 小程序 appSecret
     * @param jsCode 小程序端 wx.login 获取的 code
     * @return 会话信息，含 openid、session_key、unionid（若已绑定开放平台）
     */
    public WxCode2SessionResp execute(@NotEmpty String appId, @NotEmpty String secret, @NotEmpty String jsCode) {
        URI uri = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("appid", appId)
                .queryParam("secret", secret)
                .queryParam("js_code", jsCode)
                .queryParam("grant_type", "authorization_code")
                .build()
                .toUri();
        if (log.isDebugEnabled()) {
            log.debug("call jscode2session param: appid={}, js_code={}", appId, jsCode);
        }
        try {
            WxCode2SessionResp resp = restClient.get().uri(uri).retrieve().body(WxCode2SessionResp.class);
            if (!resp.success()) {
                log.error("call wx jscode2session error code:{},msg:{}", resp.getErrcode(), resp.getErrmsg());
                throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
            }
            log.info("call jscode2session resp success:{}", resp != null && resp.success());
            return resp;
        } catch (Exception e) {
            log.error("call wx jscode2session error", e);
            throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
        }

    }
}
