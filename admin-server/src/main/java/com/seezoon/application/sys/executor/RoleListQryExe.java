package com.seezoon.application.sys.executor;

import com.seezoon.application.sys.dto.clientobject.PermissionCO;
import com.seezoon.application.sys.dto.clientobject.RoleCO;
import com.seezoon.infrastructure.configuration.RbacConfig;
import com.seezoon.infrastructure.configuration.properties.RbacConfigProperties.RoleDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 查询所有角色
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class RoleListQryExe {

    private final RbacConfig rbacConfig;

    public List<RoleCO> execute() {
        List<RoleCO> roleList = new ArrayList<>();
        List<RoleDefinition> allRoleCodes = rbacConfig.getRoles();

        for (RoleDefinition role : allRoleCodes) {
            RoleCO roleCO = new RoleCO();
            roleCO.setCode(role.getCode());
            roleCO.setName(role.getName());
            List<String> permissions = role.getPermissions();
            List<PermissionCO> permissionList = new ArrayList<>();

            for (String permission : permissions) {
                PermissionCO permissionCO = new PermissionCO();
                permissionCO.setCode(permission);
                permissionCO.setName(rbacConfig.getPermissionName(permission));
                permissionList.add(permissionCO);
            }
            roleCO.setPermissions(permissionList);
            roleList.add(roleCO);
        }

        return roleList;
    }
}

