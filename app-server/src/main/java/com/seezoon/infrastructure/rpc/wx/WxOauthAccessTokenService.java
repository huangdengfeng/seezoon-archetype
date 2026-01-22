package com.seezoon.infrastructure.rpc.wx;

import com.seezoon.infrastructure.rpc.wx.dto.WxOauthAccessTokenResp;
import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 微信OAuth2.0授权获取用户信息
 *
 * @see <a href="https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Wechat_Login.html">...</a>
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class WxOauthAccessTokenService {

    private static final String apiUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";

    private final RestClient restClient;

    public WxOauthAccessTokenResp execute(@NotEmpty String appId, @NotEmpty String secret, @NotEmpty String code) {
        URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("appid", appId)
                .queryParam("secret", secret)
                .queryParam("code", code)
                .queryParam("grant_type", "authorization_code").build().toUri();
        if (log.isDebugEnabled()) {
            log.debug("call oauth2 access_token param:{}", uri.getQuery());
        }
        WxOauthAccessTokenResp resp = restClient.get().uri(uri).retrieve().body(WxOauthAccessTokenResp.class);
        log.info("call oauth2 access_token resp:{}", resp);
        return resp;
    }
} 