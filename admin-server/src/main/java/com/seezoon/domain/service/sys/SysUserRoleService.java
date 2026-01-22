package com.seezoon.domain.service.sys;

import com.seezoon.domain.dao.mapper.SysUserRoleMapper;
import com.seezoon.domain.dao.po.SysUserRolePO;
import com.seezoon.infrastructure.configuration.RbacConfig;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 用户角色关联服务
 *
 * @author huangdengfeng
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
@Validated
public class SysUserRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final RbacConfig rbacConfig;

    /**
     * 为用户分配角色
     *
     * @param uid 用户ID
     * @param roles 角色代码集合（可为空）
     */
    public void assignRoles(@NotNull Integer uid, @NotEmpty Set<String> roles) {
        // 删除用户现有角色
        sysUserRoleMapper.deleteByUid(uid);
        // 验证角色是否存在
        for (String role : roles) {
            if (!rbacConfig.hasRole(role)) {
                log.error("role not exists, role:{}", role);
                throw ExceptionFactory.bizException(ErrorCode.PARAM_INVALID);
            }
        }

        // 批量插入新角色
        LocalDateTime now = LocalDateTime.now();
        List<SysUserRolePO> roleList = new ArrayList<>();
        for (String role : roles) {
            SysUserRolePO po = new SysUserRolePO();
            po.setUid(uid);
            po.setRole(role);
            po.setCreateTime(now);
            roleList.add(po);
        }
        sysUserRoleMapper.batchInsert(roleList);
        log.info("assign roles success, uid:{}, roles:{}", uid, roles);
    }

    /**
     * 根据用户ID获取所有角色代码
     *
     * @param uid 用户ID
     * @return 角色代码集合
     */
    @Transactional(readOnly = true)
    public Set<String> getRolesByUid(@NotNull Integer uid) {
        List<SysUserRolePO> roleList = sysUserRoleMapper.selectByUid(uid);
        return roleList.stream()
                .map(SysUserRolePO::getRole)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 删除用户的所有角色
     *
     * @param uid 用户ID
     */
    public void deleteByUid(@NotNull Integer uid) {
        sysUserRoleMapper.deleteByUid(uid);
        log.info("delete user roles success, uid:{}", uid);
    }
}

