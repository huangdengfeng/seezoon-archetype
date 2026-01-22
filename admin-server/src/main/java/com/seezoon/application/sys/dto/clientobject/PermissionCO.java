package com.seezoon.application.sys.dto.clientobject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 权限客户端对象
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCO {

    @Schema(title = "权限代码")
    private String code;

    @Schema(title = "权限名称")
    private String name;
}

