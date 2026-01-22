package com.seezoon.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信应用登录
 */
@Getter
@Setter
public class WxAppLoginCmd {

    @NotEmpty
    @Schema(description = "微信code")
    private String code;
}