package com.seezoon.infrastructure.rpc.wx;

import com.seezoon.infrastructure.rpc.wx.dto.WxUserInfoResp;
import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 微信获取用户个人信息（UnionID机制）
 *
 * @see <a
 *         href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Authorized_API_call_UnionID.html">获取用户个人信息（UnionID机制）</a>
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class WxUserInfoService {

    private static final String API_URL = "https://api.weixin.qq.com/sns/userinfo";

    private final RestClient restClient;

    /**
     * 获取用户个人信息
     *
     * @param accessToken 调用凭证
     * @param openid 普通用户的标识，对当前开发者账号唯一
     * @return 用户信息
     */
    public WxUserInfoResp execute(@NotEmpty String accessToken, @NotEmpty String openid) {
        URI uri = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("access_token", accessToken)
                .queryParam("openid", openid)
                .queryParam("lang", "zh_CN")
                .build().toUri();
        if (log.isDebugEnabled()) {
            log.debug("call sns/userinfo param:{}", uri.getQuery());
        }
        WxUserInfoResp resp = restClient.get().uri(uri).retrieve().body(WxUserInfoResp.class);
        log.info("call sns/userinfo resp:{}", resp);
        return resp;
    }
}

