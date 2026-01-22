package com.seezoon.domain.service.user.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginTokenVO {

    private Long uid;
    private String accessToken;
    /**
     * 单位秒
     */
    private Long accessTokenExpire;

    private String refreshToken;
    /**
     * 单位秒
     */
    private Long refreshTokenExpire;

}
