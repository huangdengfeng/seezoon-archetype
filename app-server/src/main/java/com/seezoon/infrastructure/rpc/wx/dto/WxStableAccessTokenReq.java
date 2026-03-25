package com.seezoon.infrastructure.rpc.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取稳定版接口调用凭据 请求体
 *
 * @see <a
 *         href="https://developers.weixin.qq.com/miniprogram/dev/server/API/mp-access-token/api_getstableaccesstoken.html">获取稳定版接口调用凭据</a>
 */
@Getter
@Setter
public class WxStableAccessTokenReq {

    @JsonProperty("grant_type")
    private String grantType = "client_credential";

    private String appid;
    private String secret;

    @JsonProperty("force_refresh")
    private Boolean forceRefresh;
}
