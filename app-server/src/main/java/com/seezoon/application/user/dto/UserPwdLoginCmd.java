package com.seezoon.application.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * 账号密码登录
 */
@Getter
@Setter
public class UserPwdLoginCmd {

    @NotEmpty
    @Schema(description = "用户名")
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty
    @Schema(description = "密码")
    private String password;

}
