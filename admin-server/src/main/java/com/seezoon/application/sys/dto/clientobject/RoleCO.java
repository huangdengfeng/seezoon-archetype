package com.seezoon.application.sys.dto.clientobject;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 角色客户端对象
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleCO {

    @Schema(title = "角色代码")
    private String code;

    @Schema(title = "角色名称")
    private String name;

    @Schema(title = "权限列表")
    private List<PermissionCO> permissions = new ArrayList<>();
}

