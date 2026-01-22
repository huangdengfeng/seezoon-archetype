package com.seezoon.application.sys.executor;

import com.seezoon.application.sys.authentication.context.SecurityContext;
import com.seezoon.application.sys.dto.ChangeMyPasswordCmd;
import com.seezoon.domain.service.sys.SysUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 修改自己密码
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class ChangeMyPasswordCmdExe {

    private final SysUserService sysUserService;

    public void execute(@Valid @NotNull ChangeMyPasswordCmd cmd) {
        Integer uid = SecurityContext.getUserId();
        if (uid == null) {
            throw new IllegalStateException("user not authenticated");
        }
        sysUserService.changeMyPassword(uid, cmd.getOldPassword(), cmd.getNewPassword());
        log.info("change my password success, uid:{}", uid);
    }
}

