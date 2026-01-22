package com.seezoon.application.user.executor;

import com.seezoon.application.user.dto.ChangePasswordCmd;
import com.seezoon.domain.dao.mapper.UserInfoMapper;
import com.seezoon.domain.dao.po.UserInfoPO;
import com.seezoon.domain.service.user.UserInfoService;
import com.seezoon.domain.service.user.support.PasswordEncoder;
import com.seezoon.infrastructure.configuration.context.SecurityContextHolder;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 修改密码
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Validated
public class ChangePasswordCmdExe {

    private final UserInfoMapper userInfoMapper;
    private final UserInfoService userInfoService;

    public void execute(@Valid @NotNull ChangePasswordCmd cmd) {
        Long uid = SecurityContextHolder.getUid();
        String oldPassword = cmd.getOldPassword();
        String newPassword = cmd.getNewPassword();

        // 获取用户信息
        UserInfoPO userInfoPO = userInfoMapper.selectByPrimaryKey(uid);
        if (userInfoPO == null) {
            log.error("user info not found, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }

        // 验证原密码
        if (!PasswordEncoder.matches(oldPassword, userInfoPO.getPassword())) {
            log.error("old password validation failed, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 更新密码
        userInfoService.resetPasswordByUid(uid, newPassword);
        log.info("change password success, uid:{}", uid);

    }
}

