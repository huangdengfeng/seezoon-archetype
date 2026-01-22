package com.seezoon.application.user.dto.clientobject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginCO {


    @Schema(description = "登录accessToken,请求头携带Authorization:Bearer token")
    private String accessToken;

    @Schema(description = "accessToken过期时间,单位秒，过期后http status = 401")
    private Long accessTokenExpire;
    @Schema(description = "刷新refreshToken,单位秒，可以调用刷新接口获得新的accessToken")
    private String refreshToken;

    @Schema(description = "refreshToken过期时间,单位秒，过期后刷新接口http status 401")
    private Long refreshTokenExpire;


}
