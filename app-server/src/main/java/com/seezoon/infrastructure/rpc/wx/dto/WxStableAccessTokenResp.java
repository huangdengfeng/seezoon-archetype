package com.seezoon.infrastructure.rpc.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 获取稳定版接口调用凭据 响应
 *
 * @see <a href="https://developers.weixin.qq.com/miniprogram/dev/server/API/mp-access-token/api_getstableaccesstoken.html">获取稳定版接口调用凭据</a>
 */
@Getter
@Setter
@ToString(callSuper = true)
public class WxStableAccessTokenResp extends WxError {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;
}
