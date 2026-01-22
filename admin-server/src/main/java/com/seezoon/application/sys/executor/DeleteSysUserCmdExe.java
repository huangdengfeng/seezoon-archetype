package com.seezoon.application.sys.executor;

import com.seezoon.application.sys.dto.DeleteSysUserCmd;
import com.seezoon.domain.service.sys.SysUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 删除系统用户
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class DeleteSysUserCmdExe {

    private final SysUserService sysUserService;

    public void execute(@Valid @NotNull DeleteSysUserCmd cmd) {
        sysUserService.deleteUser(cmd.getUid());
        log.info("delete sys user success, uid:{}", cmd.getUid());
    }
}

