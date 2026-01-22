package com.seezoon.application.sys.authentication.dto.clientobject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录凭据
 *
 * @author huangdengfeng
 * @date 2022/10/12 12:56
 */
@Getter
@Setter
public class AuthorizationTokenCO {

    @Schema(title = "登录凭证", description = "添加header Authorization:Bearer token")
    private String accessToken;

    public AuthorizationTokenCO(String accessToken) {
        this.accessToken = accessToken;
    }
}
