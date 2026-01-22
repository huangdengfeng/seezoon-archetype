package com.seezoon.application.sys.executor;

import com.seezoon.application.sys.authentication.context.SecurityContext;
import com.seezoon.application.sys.dto.UpdateSysUserCmd;
import com.seezoon.domain.service.sys.SysUserService;
import com.seezoon.domain.service.sys.valueobj.UpdateUserVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 更新系统用户
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class UpdateSysUserCmdExe {

    private final SysUserService sysUserService;

    public void execute(@Valid @NotNull UpdateSysUserCmd cmd) {
        Integer updateUser = SecurityContext.getUserId();
        UpdateUserVO vo = new UpdateUserVO(cmd.getUid(), cmd.getUserName(), cmd.getName(), cmd.getStatus());
        vo.setMobile(cmd.getMobile());
        vo.setEmail(cmd.getEmail());
        vo.setPhoto(cmd.getPhoto());
        vo.setRemark(cmd.getRemark());
        vo.setRoles(cmd.getRoles());
        sysUserService.updateUser(vo, updateUser);
        log.info("update sys user success, uid:{}", cmd.getUid());
    }
}

