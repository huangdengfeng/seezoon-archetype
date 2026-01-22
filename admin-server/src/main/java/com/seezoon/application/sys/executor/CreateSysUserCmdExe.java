package com.seezoon.application.sys.executor;

import com.seezoon.application.sys.authentication.context.SecurityContext;
import com.seezoon.application.sys.dto.CreateSysUserCmd;
import com.seezoon.domain.service.sys.SysUserService;
import com.seezoon.domain.service.sys.valueobj.AddUserVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 创建系统用户
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class CreateSysUserCmdExe {

    private final SysUserService sysUserService;

    public void execute(@Valid @NotNull CreateSysUserCmd cmd) {
        Integer createUser = SecurityContext.getUserId();
        AddUserVO vo = new AddUserVO(cmd.getUserName(), cmd.getName());
        vo.setPassword(cmd.getPassword());
        vo.setMobile(cmd.getMobile());
        vo.setEmail(cmd.getEmail());
        vo.setPhoto(cmd.getPhoto());
        vo.setRemark(cmd.getRemark());
        vo.setRoles(cmd.getRoles());
        sysUserService.createUser(vo, createUser);
        log.info("create sys user success, userName:{}", cmd.getUserName());
    }
}

