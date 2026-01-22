package com.seezoon.domain.service.sys;

import com.seezoon.domain.dao.mapper.SysUserMapper;
import com.seezoon.domain.dao.po.SysUserPO;
import com.seezoon.domain.dao.types.SysUserStatusVO;
import com.seezoon.domain.service.sys.authentication.support.PasswordEncoder;
import com.seezoon.domain.service.sys.valueobj.AddUserVO;
import com.seezoon.domain.service.sys.valueobj.UpdateUserVO;
import com.seezoon.infrastructure.constants.Constants;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 系统用户领域服务
 *
 * @author huangdengfeng
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
@Validated
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleService sysUserRoleService;

    /**
     * 创建用户
     *
     * @param vo 用户信息
     * @param operator 操作人
     * @return 用户ID
     */
    public Integer createUser(@Valid @NotNull AddUserVO vo, @NotNull Integer operator) {
        // 检查用户名是否已存在
        SysUserPO existingUser = sysUserMapper.selectByUserName(vo.getUserName());
        if (existingUser != null) {
            log.error("user name exists, userName:{}", vo.getUserName());
            throw ExceptionFactory.bizException(ErrorCode.SYS_USER_NAME_EXISTS);
        }

        SysUserPO po = new SysUserPO();
        po.setUsername(vo.getUserName());
        // 加密密码
        if (StringUtils.isNotEmpty(vo.getPassword())) {
            po.setPassword(PasswordEncoder.encode(vo.getPassword()));
        }
        po.setSecretKey(this.generate());
        po.setName(vo.getName());
        po.setMobile(vo.getMobile());
        po.setEmail(vo.getEmail());
        po.setPhoto(vo.getPhoto());
        po.setStatus(SysUserStatusVO.VALID.getCode());
        LocalDateTime now = LocalDateTime.now();
        po.setCreateTime(now);
        po.setCreateUser(operator);
        po.setUpdateTime(now);
        po.setUpdateUser(operator);
        po.setRemark(vo.getRemark());

        int affectedRows = sysUserMapper.insert(po);
        Assertion.affectedOne(affectedRows);
        Integer uid = Objects.requireNonNull(po.getUid());

        if (!vo.getRoles().isEmpty()) {
            // 分配角色
            sysUserRoleService.assignRoles(uid, vo.getRoles());
        }

        log.info("create user success, uid:{}, userName:{}", uid, vo.getUserName());
        return uid;
    }

    /**
     * 更新用户信息
     *
     * @param vo 用户信息
     * @param operator 操作人
     */
    public void updateUser(@Valid @NotNull UpdateUserVO vo, @NotNull Integer operator) {
        SysUserStatusVO.check(vo.getStatus());
        // 检查用户是否存在
        SysUserPO po = sysUserMapper.selectByPrimaryKey(vo.getUid());
        if (po == null) {
            log.error("user not exists, uid:{}", vo.getUid());
            throw ExceptionFactory.bizException(ErrorCode.RECORD_NOT_EXISTS);
        }

        // 检查是否为系统管理员
        if (Objects.equals(po.getUid(), Constants.SUPER_ADMIN_USER_ID) && !Objects.equals(vo.getUid(), operator)) {
            log.error("system admin not allow modify,operator uid:{}", operator);
            throw ExceptionFactory.bizException(ErrorCode.SYS_ADMIN_NOT_ALLOW_MODIFY);
        }

        // 检查用户名是否被其他用户使用
        SysUserPO existingUser = sysUserMapper.selectByUserName(vo.getUserName());
        if (existingUser != null && !Objects.equals(existingUser.getUid(), vo.getUid())) {
            log.error("user name already used, userName:{}", vo.getUserName());
            throw ExceptionFactory.bizException(ErrorCode.SYS_USER_NAME_EXISTS);
        }

        po.setUsername(vo.getUserName());
        po.setName(vo.getName());
        po.setMobile(vo.getMobile());
        po.setEmail(vo.getEmail());
        po.setPhoto(vo.getPhoto());
        po.setStatus(vo.getStatus());
        po.setUpdateTime(LocalDateTime.now());
        po.setUpdateUser(operator);
        po.setRemark(vo.getRemark());

        int affectedRows = sysUserMapper.updateByPrimaryKey(po);
        Assertion.affectedOne(affectedRows);

        // 更新角色
        if (!vo.getRoles().isEmpty()) {
            sysUserRoleService.assignRoles(vo.getUid(), vo.getRoles());
        }

        log.info("update user success, uid:{}", vo.getUid());
    }

    /**
     * 删除用户
     *
     * @param uid 用户ID
     */
    public void deleteUser(@NotNull Integer uid) {
        // 检查用户是否存在
        SysUserPO po = sysUserMapper.selectByPrimaryKey(uid);
        if (po == null) {
            log.error("user not exists, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }

        // 检查是否为系统管理员
        if (Objects.equals(uid, Constants.SUPER_ADMIN_USER_ID)) {
            log.error("system admin not allow delete, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.SYS_ADMIN_NOT_ALLOW_MODIFY);
        }

        int affectedRows = sysUserMapper.deleteByPrimaryKey(uid);
        Assertion.affectedOne(affectedRows);
        sysUserRoleService.deleteByUid(uid);
        log.info("delete user success, uid:{}", uid);
    }

    /**
     * 修改指定用户密码（管理员操作）
     *
     * @param uid 用户ID
     * @param newPassword 新密码
     * @param operator 操作人
     */
    public void changeUserPassword(@NotNull Integer uid, @NotBlank String newPassword,
            @NotNull Integer operator) {
        // 检查用户是否存在
        SysUserPO po = sysUserMapper.selectByPrimaryKey(uid);
        if (po == null) {
            log.error("user not exists, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }

        // 检查是否为系统管理员，且不是自己修改
        if (Objects.equals(uid, Constants.SUPER_ADMIN_USER_ID) && !Objects.equals(uid, operator)) {
            log.error("system admin password not allow modify by others, operator uid:{}", operator);
            throw ExceptionFactory.bizException(ErrorCode.SYS_ADMIN_NOT_ALLOW_MODIFY);
        }

        // 加密新密码
        po.setPassword(PasswordEncoder.encode(newPassword));
        po.setUpdateTime(LocalDateTime.now());
        po.setUpdateUser(operator);

        int affectedRows = sysUserMapper.updateByPrimaryKey(po);
        Assertion.affectedOne(affectedRows);
        log.info("change user password success, uid:{}", uid);
    }

    /**
     * 修改自己密码
     *
     * @param uid 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    public void changeMyPassword(@NotNull Integer uid, @NotBlank String oldPassword,
            @NotBlank String newPassword) {
        // 检查用户是否存在
        SysUserPO po = sysUserMapper.selectByPrimaryKey(uid);
        if (po == null) {
            log.error("user not exists, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }

        // 验证原密码
        if (StringUtils.isEmpty(po.getPassword()) || !PasswordEncoder.matches(oldPassword, po.getPassword())) {
            log.error("old password wrong, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.OLD_PASSWD_WRONG);
        }

        // 加密新密码
        po.setPassword(PasswordEncoder.encode(newPassword));
        po.setUpdateTime(LocalDateTime.now());
        po.setUpdateUser(uid);

        int affectedRows = sysUserMapper.updateByPrimaryKey(po);
        Assertion.affectedOne(affectedRows);
        log.info("change my password success, uid:{}", uid);
    }


    private String generate() {
        return RandomStringUtils.randomAlphanumeric(32);
    }

}

