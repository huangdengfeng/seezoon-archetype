package com.seezoon.infrastructure.rpc.wx;

import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.rpc.wx.dto.WxStableAccessTokenReq;
import com.seezoon.infrastructure.rpc.wx.dto.WxStableAccessTokenResp;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

/**
 * 获取稳定版接口调用凭据（Access Token）
 * <p>
 * 与 getAccessToken 互相隔离，推荐使用。普通模式下有效期内重复调用不会更新 token；可选强制刷新。
 *
 * @see <a
 *         href="https://developers.weixin.qq.com/miniprogram/dev/server/API/mp-access-token/api_getstableaccesstoken.html">获取稳定版接口调用凭据</a>
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class WxStableAccessTokenService {

    private static final String API_URL = "https://api.weixin.qq.com/cgi-bin/stable_token";

    private final RestClient restClient;

    /**
     * 普通模式获取 token，有效期内重复调用不会更新
     */
    public WxStableAccessTokenResp execute(@NotEmpty String appId, @NotEmpty String secret) {
        return execute(appId, secret, false);
    }

    /**
     * 获取稳定版接口调用凭据
     *
     * @param appId 小程序/公众号 appId
     * @param secret appSecret
     * @param forceRefresh true 时强制刷新，会使上次 token 失效（每天限 20 次且需间隔 30 秒）
     * @return 含 access_token、expires_in（秒，通常 7200）
     */
    public WxStableAccessTokenResp execute(@NotEmpty String appId, @NotEmpty String secret, boolean forceRefresh) {
        WxStableAccessTokenReq req = new WxStableAccessTokenReq();
        req.setAppid(appId);
        req.setSecret(secret);
        req.setForceRefresh(forceRefresh);
        if (log.isDebugEnabled()) {
            log.debug("call stable_token appid={}, forceRefresh={}", appId, forceRefresh);
        }
        try {
            WxStableAccessTokenResp resp = restClient.post()
                    .uri(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(req)
                    .retrieve()
                    .body(WxStableAccessTokenResp.class);
            if (!resp.success()) {
                log.error("call wx stable_token error code:{},msg:{}", resp.getErrcode(), resp.getErrmsg());
                throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
            }
            return resp;
        } catch (Exception e) {
            log.error("call wx stable_token error", e);
            throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
        }
    }
}
