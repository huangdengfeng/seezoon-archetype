package com.seezoon.application.sys.executor;

import com.seezoon.application.sys.authentication.context.SecurityContext;
import com.seezoon.application.sys.dto.ChangeUserPasswordCmd;
import com.seezoon.domain.service.sys.SysUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 修改指定用户密码
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class ChangeUserPasswordCmdExe {

    private final SysUserService sysUserService;

    public void execute(@Valid @NotNull ChangeUserPasswordCmd cmd) {
        Integer operator = SecurityContext.getUserId();
        sysUserService.changeUserPassword(cmd.getUid(), cmd.getNewPassword(), operator);
        log.info("change user password success, uid:{}", cmd.getUid());
    }
}

