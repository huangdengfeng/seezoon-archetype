package com.seezoon.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改密码
 */
@Getter
@Setter
public class ChangePasswordCmd {

    @Schema(description = "原密码")
    @NotEmpty(message = "原密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码")
    @NotEmpty(message = "新密码不能为空")
    @Size(min = 6, message = "新密码长度不能小于6位")
    private String newPassword;
}

