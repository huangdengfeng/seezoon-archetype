package com.seezoon.application.sys.executor;

import com.seezoon.application.sys.authentication.context.SecurityContext;
import com.seezoon.application.sys.dto.clientobject.UserDetailCO;
import com.seezoon.domain.dao.mapper.SysUserMapper;
import com.seezoon.domain.dao.po.SysUserPO;
import com.seezoon.domain.service.sys.valueobj.UserVO;
import com.seezoon.infrastructure.configuration.RbacConfig;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 查询我的信息
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class MyInfoQryExe {

    private final SysUserMapper sysUserMapper;
    private final RbacConfig rbacConfig;

    public UserDetailCO execute() {
        Integer uid = SecurityContext.getUserId();
        SysUserPO po = sysUserMapper.selectByPrimaryKey(uid);
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
        // 拥有所有权限
        UserVO user = SecurityContext.getUser();
        co.setRoles(user.getRoles());
        co.setPermissions(user.getPermissions());

        // 设置角色名称列表
        List<String> roleNames = new ArrayList<>();
        for (String roleCode : user.getRoles()) {
            String roleName = rbacConfig.getRoleName(roleCode);
            if (roleName != null) {
                roleNames.add(roleName);
            }
        }
        co.setRoleNames(roleNames);
        return co;
    }
}

