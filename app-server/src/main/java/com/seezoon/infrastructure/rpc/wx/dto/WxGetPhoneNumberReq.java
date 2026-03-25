package com.seezoon.infrastructure.rpc.wx.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 获取手机号 请求体
 * <p>
 * 将小程序端 getPhoneNumber 获取的 code 换取用户手机号，每个 code 仅能使用一次，有效期 5 分钟。
 *
 * @see <a
 *         href="https://developers.weixin.qq.com/miniprogram/dev/server/API/user-info/phone-number/api_getphonenumber.html">获取手机号</a>
 */
@Getter
@Setter
public class WxGetPhoneNumberReq {

    /**
     * 手机号获取凭证（小程序端 getPhoneNumber 获取）
     */
    private String code;
}
