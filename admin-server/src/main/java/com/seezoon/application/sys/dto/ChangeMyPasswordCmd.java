package com.seezoon.application.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改自己密码
 */
@Getter
@Setter
public class ChangeMyPasswordCmd {

    @Schema(description = "原密码")
    @NotBlank
    @Size(max = 100)
    private String oldPassword;

    @Schema(description = "新密码")
    @NotBlank
    @Size(max = 100)
    private String newPassword;
}

