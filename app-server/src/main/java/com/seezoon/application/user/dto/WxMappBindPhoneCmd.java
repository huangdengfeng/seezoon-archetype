package com.seezoon.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * 小程序绑定手机号（用 getPhoneNumber 的 code 换取手机号并绑定当前用户）
 */
@Getter
@Setter
public class WxMappBindPhoneCmd {

    @NotEmpty
    @Schema(description = "小程序 getPhoneNumber 获取的 code")
    private String code;
}
