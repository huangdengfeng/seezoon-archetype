package com.seezoon.interfaces;

import com.seezoon.application.user.dto.ChangePasswordCmd;
import com.seezoon.application.user.dto.UpdateUserProfileCmd;
import com.seezoon.application.user.dto.WxMappBindPhoneCmd;
import com.seezoon.application.user.dto.clientobject.UserProfileCO;
import com.seezoon.application.user.executor.ChangePasswordCmdExe;
import com.seezoon.application.user.executor.LogoutCmdExe;
import com.seezoon.application.user.executor.UpdateUserProfileCmdExe;
import com.seezoon.application.user.executor.UserProfileQryExe;
import com.seezoon.application.user.executor.WxMappBindPhoneCmdExe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户信息", description = "用户信息接口相关")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserProfileQryExe userProfileQryExe;
    private final LogoutCmdExe logoutCmdExe;
    private final ChangePasswordCmdExe changePasswordCmdExe;
    private final UpdateUserProfileCmdExe updateUserProfileCmdExe;
    private final WxMappBindPhoneCmdExe wxMappBindPhoneCmdExe;

    @Operation(summary = "用户个人信息")
    @GetMapping("/info")
    public UserProfileCO info() {
        return userProfileQryExe.execute();
    }

    @Operation(summary = "修改用户信息")
    @PostMapping("/update")
    public void updateProfile(@RequestBody @Valid UpdateUserProfileCmd cmd) {
        updateUserProfileCmdExe.execute(cmd);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    @SuppressWarnings("unchecked")
    public void logout() {
        logoutCmdExe.execute();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/change_password")
    public void changePassword(@RequestBody @Valid ChangePasswordCmd cmd) {
        changePasswordCmdExe.execute(cmd);
    }

    @Operation(summary = "小程序绑定手机号")
    @PostMapping("/wx_mapp_bind_phone")
    public void wxMappBindPhone(@RequestBody @Valid WxMappBindPhoneCmd cmd) {
        wxMappBindPhoneCmdExe.execute(cmd);
    }

}
