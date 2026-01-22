package com.seezoon.infrastructure.rpc.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 微信用户信息响应
 *
 * @see <a href="https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Authorized_API_call_UnionID.html">获取用户个人信息（UnionID机制）</a>
 */
@Getter
@Setter
@ToString(callSuper = true)
public class WxUserInfoResp extends WxError {

    /**
     * 普通用户的标识，对当前开发者账号唯一
     */
    private String openid;

    /**
     * 普通用户昵称
     */
    private String nickname;

    /**
     * 普通用户性别，1为男性，2为女性
     */
    private Integer sex;

    /**
     * 普通用户个人资料填写的省份
     */
    private String province;

    /**
     * 普通用户个人资料填写的城市
     */
    private String city;

    /**
     * 国家，如中国为CN
     */
    private String country;

    /**
     * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像）
     */
    @JsonProperty("headimgurl")
    private String headImgUrl;

    /**
     * 用户特权信息，json数组
     */
    private List<String> privilege;

    /**
     * 用户统一标识。针对一个微信开放平台账号下的应用，同一用户的unionid是唯一的
     */
    private String unionid;
}

