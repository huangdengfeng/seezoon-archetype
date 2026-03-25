package com.seezoon.infrastructure.rpc.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 获取手机号 响应
 *
 * @see <a href="https://developers.weixin.qq.com/miniprogram/dev/server/API/user-info/phone-number/api_getphonenumber.html">获取手机号</a>
 */
@Getter
@Setter
@ToString(callSuper = true)
public class WxGetPhoneNumberResp extends WxError {

    @JsonProperty("phone_info")
    private PhoneInfo phoneInfo;

    /**
     * 用户手机号信息
     */
    @Getter
    @Setter
    @ToString
    public static class PhoneInfo {
        /**
         * 用户绑定的手机号（国外手机号会有区号）
         */
        @JsonProperty("phoneNumber")
        private String phoneNumber;

        /**
         * 没有区号的手机号
         */
        @JsonProperty("purePhoneNumber")
        private String purePhoneNumber;

        /**
         * 区号
         */
        @JsonProperty("countryCode")
        private String countryCode;

        /**
         * 数据水印
         */
        private Watermark watermark;
    }

    /**
     * 数据水印
     */
    @Getter
    @Setter
    @ToString
    public static class Watermark {
        /**
         * 用户获取手机号操作的时间戳
         */
        private Long timestamp;
        /**
         * 小程序 appid
         */
        private String appid;
    }
}
