package com.seezoon.domain.service.sys.authentication;

import com.seezoon.domain.dao.mapper.SysUserMapper;
import com.seezoon.domain.dao.mapper.SysUserRoleMapper;
import com.seezoon.domain.dao.po.SysUserPO;
import com.seezoon.domain.dao.types.SysUserStatusVO;
import com.seezoon.domain.service.sys.authentication.support.PasswordEncoder;
import com.seezoon.domain.service.sys.valueobj.UserVO;
import com.seezoon.infrastructure.configuration.RbacConfig;
import com.seezoon.infrastructure.constants.Constants;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.constraints.NotEmpty;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 用户登录服务
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
@Validated
public class LoginService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final RbacConfig rbacConfig;

    /**
     * 账号密码登录
     *
     * @param username
     * @param password
     * @return 用户信息 not null
     */
    public UserVO login(@NotEmpty String username, @NotEmpty String password) {
        SysUserPO po = sysUserMapper.selectByUserName(username);
        if (po == null || StringUtils.isEmpty(po.getPassword())) {
            log.error("user null or password is empty:{}", username);
            throw ExceptionFactory.bizException(ErrorCode.USER_PASSWD_WRONG);
        }
        if (!PasswordEncoder.matches(password, po.getPassword())) {
            log.error("username password is wrong:{}", username);
            throw ExceptionFactory.bizException(ErrorCode.USER_PASSWD_WRONG);
        }

        if (SysUserStatusVO.isInvalid(po.getStatus())) {
            throw ExceptionFactory.bizException(ErrorCode.USER_STATUS_INVALID);
        }
        if (SysUserStatusVO.isLocked(po.getStatus())) {
            throw ExceptionFactory.bizException(ErrorCode.USER_STATUS_LOCKED);
        }
        
        UserVO userVO = new UserVO(po.getUid(), po.getUsername());
        if (Objects.equals(Constants.SUPER_ADMIN_USER_ID, userVO.getUid())) {
            userVO.setRoles(rbacConfig.getAllRoleCodes());
            userVO.setPermissions(rbacConfig.getAllPermissionCodes());
        } else {
            Set<String> userRoles = sysUserRoleMapper.selectByUid(po.getUid()).stream()
                    .map(v -> v.getRole()).collect(Collectors.toSet());
            userVO.setRoles(userRoles);
            userVO.setPermissions(rbacConfig.getPermissionsByRoles(userRoles));
        }
        return userVO;
    }
}
