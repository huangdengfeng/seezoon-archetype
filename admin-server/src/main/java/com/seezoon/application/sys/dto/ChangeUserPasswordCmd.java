package com.seezoon.application.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改指定用户密码
 */
@Getter
@Setter
public class ChangeUserPasswordCmd {

    @Schema(description = "用户ID")
    @NotNull
    private Integer uid;

    @Schema(description = "新密码")
    @NotBlank
    @Size(max = 100)
    private String newPassword;
}

