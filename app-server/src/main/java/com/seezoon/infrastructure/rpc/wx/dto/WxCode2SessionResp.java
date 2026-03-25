package com.seezoon.infrastructure.rpc.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 小程序登录凭证校验（code2Session）响应
 *
 * @see <a href="https://developers.weixin.qq.com/miniprogram/dev/server/API/user-login/api_code2session.html">小程序登录凭证校验</a>
 */
@Getter
@Setter
@ToString(callSuper = true)
public class WxCode2SessionResp extends WxError {

    @JsonProperty("session_key")
    private String sessionKey;

    private String openid;

    private String unionid;
}
