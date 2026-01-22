package com.seezoon.interfaces;

import com.seezoon.application.sys.dto.clientobject.RoleCO;
import com.seezoon.application.sys.executor.RoleListQryExe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统角色管理
 *
 * @author huangdengfeng
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/sys/role")
@Tag(name = "系统角色管理")
public class SysRoleController {

    private final RoleListQryExe roleListQryExe;

    @PreAuthorize("hasAuthority('sys:role:qry')")
    @PostMapping("/list")
    @Operation(summary = "查询所有角色")
    public List<RoleCO> listRoles() {
        return roleListQryExe.execute();
    }
}

