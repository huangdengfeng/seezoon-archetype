package com.seezoon.interfaces;

import com.seezoon.application.sys.dto.ChangeMyPasswordCmd;
import com.seezoon.application.sys.dto.ChangeUserPasswordCmd;
import com.seezoon.application.sys.dto.CreateSysUserCmd;
import com.seezoon.application.sys.dto.DeleteSysUserCmd;
import com.seezoon.application.sys.dto.UpdateSysUserCmd;
import com.seezoon.application.sys.dto.UserDetailQry;
import com.seezoon.application.sys.dto.UserPageQry;
import com.seezoon.application.sys.dto.clientobject.UserCO;
import com.seezoon.application.sys.dto.clientobject.UserDetailCO;
import com.seezoon.application.sys.executor.ChangeMyPasswordCmdExe;
import com.seezoon.application.sys.executor.ChangeUserPasswordCmdExe;
import com.seezoon.application.sys.executor.CreateSysUserCmdExe;
import com.seezoon.application.sys.executor.DeleteSysUserCmdExe;
import com.seezoon.application.sys.executor.LogoutCmdExe;
import com.seezoon.application.sys.executor.MyInfoQryExe;
import com.seezoon.application.sys.executor.SysUserPageQryExe;
import com.seezoon.application.sys.executor.UpdateSysUserCmdExe;
import com.seezoon.application.sys.executor.UserDetailQryExe;
import com.seezoon.infrastructure.dto.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统用户管理
 *
 * @author huangdengfeng
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/sys/user")
@Tag(name = "系统用户管理")
public class SysUserController {

    private final CreateSysUserCmdExe createSysUserCmdExe;
    private final UpdateSysUserCmdExe updateSysUserCmdExe;
    private final DeleteSysUserCmdExe deleteSysUserCmdExe;
    private final SysUserPageQryExe sysUserPageQryExe;
    private final ChangeUserPasswordCmdExe changeUserPasswordCmdExe;
    private final ChangeMyPasswordCmdExe changeMyPasswordCmdExe;
    private final UserDetailQryExe userDetailQryExe;
    private final MyInfoQryExe myInfoQryExe;
    private final LogoutCmdExe logoutCmdExe;

    @PreAuthorize("hasAuthority('sys:user:create')")
    @PostMapping("/create")
    @Operation(summary = "创建系统用户")
    public void createUser(@RequestBody @Valid CreateSysUserCmd cmd) {
        createSysUserCmdExe.execute(cmd);
    }

    @PreAuthorize("hasAuthority('sys:user:update')")
    @PostMapping("/update")
    @Operation(summary = "更新系统用户")
    public void updateUser(@RequestBody @Valid UpdateSysUserCmd cmd) {
        updateSysUserCmdExe.execute(cmd);
    }

    @PreAuthorize("hasAuthority('sys:user:delete')")
    @PostMapping("/delete")
    @Operation(summary = "删除系统用户")
    public void deleteUser(@RequestBody @Valid DeleteSysUserCmd cmd) {
        deleteSysUserCmdExe.execute(cmd);
    }

    @PreAuthorize("hasAuthority('sys:user:qry')")
    @PostMapping("/page")
    @Operation(summary = "获取系统用户列表")
    public Page<UserCO> userPage(@RequestBody @Valid UserPageQry qry) {
        return sysUserPageQryExe.execute(qry);
    }

    @PreAuthorize("hasAuthority('sys:user:update')")
    @PostMapping("/change_password")
    @Operation(summary = "修改指定用户密码")
    public void changeUserPassword(@RequestBody @Valid ChangeUserPasswordCmd cmd) {
        changeUserPasswordCmdExe.execute(cmd);
    }

    @PreAuthorize("hasAuthority('sys:user:qry')")
    @PostMapping("/detail")
    @Operation(summary = "查询指定用户个人信息")
    public UserDetailCO getUserDetail(@RequestBody @Valid UserDetailQry qry) {
        return userDetailQryExe.execute(qry);
    }

    @PostMapping("/change_my_password")
    @Operation(summary = "修改自己密码")
    public void changeMyPassword(@RequestBody @Valid ChangeMyPasswordCmd cmd) {
        changeMyPasswordCmdExe.execute(cmd);
    }

    @GetMapping("/my_info")
    @Operation(summary = "查询我的信息")
    public UserDetailCO getMyInfo() {
        return myInfoQryExe.execute();
    }

    @PostMapping("/logout")
    @Operation(summary = "登出")
    public void logout() {
        logoutCmdExe.execute();
    }
}

