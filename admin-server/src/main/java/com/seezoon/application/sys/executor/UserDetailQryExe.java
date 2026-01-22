package com.seezoon.application.sys.executor;

import com.seezoon.application.sys.authentication.context.SecurityContext;
import com.seezoon.application.sys.dto.UserDetailQry;
import com.seezoon.application.sys.dto.clientobject.UserDetailCO;
import com.seezoon.domain.dao.mapper.SysUserMapper;
import com.seezoon.domain.dao.mapper.SysUserRoleMapper;
import com.seezoon.domain.dao.po.SysUserPO;
import com.seezoon.infrastructure.configuration.RbacConfig;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 查询指定用户信息
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class UserDetailQryExe {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final RbacConfig rbacConfig;

    public UserDetailCO execute(@Valid @NotNull UserDetailQry qry) {
        SysUserPO po = sysUserMapper.selectByPrimaryKey(qry.getUid());
        if (po == null) {
            throw ExceptionFactory.bizException(ErrorCode.RECORD_NOT_EXISTS);
        }

        UserDetailCO co = new UserDetailCO();
        co.setUid(po.getUid());
        co.setUsername(po.getUsername());
        co.setName(po.getName());
        co.setMobile(po.getMobile());
        co.setEmail(po.getEmail());
        co.setPhoto(po.getPhoto());
        co.setStatus(po.getStatus());
        co.setCreateTime(po.getCreateTime());
        co.setCreateUser(po.getCreateUser());
        co.setUpdateTime(po.getUpdateTime());
        co.setUpdateUser(po.getUpdateUser());
        co.setRemark(po.getRemark());
        Set<String> userRoles;
        if (SecurityContext.isSuperAdmin()) {
            userRoles = rbacConfig.getAllRoleCodes();
            co.setRoles(userRoles);
            co.setPermissions(rbacConfig.getAllPermissionCodes());
        } else {
            userRoles = sysUserRoleMapper.selectByUid(qry.getUid()).stream().map(v -> v.getRole()).collect(
                    Collectors.toSet());
            co.setRoles(userRoles);
            co.setPermissions(rbacConfig.getPermissionsByRoles(userRoles));
        }
        // 设置角色名称列表
        List<String> roleNames = new ArrayList<>();
        for (String roleCode : userRoles) {
            String roleName = rbacConfig.getRoleName(roleCode);
            if (roleName != null) {
                roleNames.add(roleName);
            }
        }
        co.setRoleNames(roleNames);
        return co;
    }
}

