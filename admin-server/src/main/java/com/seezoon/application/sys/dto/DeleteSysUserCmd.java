package com.seezoon.application.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 删除系统用户
 */
@Getter
@Setter
public class DeleteSysUserCmd {

    @Schema(description = "用户ID")
    @NotNull
    private Integer uid;
}

