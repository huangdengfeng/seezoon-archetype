package com.seezoon.application.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取用户详情
 */
@Getter
@Setter
public class UserDetailQry {

    @Schema(description = "用户ID")
    @NotNull
    private Integer uid;
}




