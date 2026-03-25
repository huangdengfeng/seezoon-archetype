package com.seezoon.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序登录
 */
@Getter
@Setter
public class WxMappLoginCmd {

    @NotEmpty
    @Schema(description = "小程序 wx.login 获取的 code")
    private String code;
}
