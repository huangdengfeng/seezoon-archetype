package com.seezoon.interfaces;

import com.seezoon.application.sys.authentication.dto.UserPasswdLoginCmd;
import com.seezoon.application.sys.authentication.dto.clientobject.AuthorizationTokenCO;
import com.seezoon.application.sys.authentication.executor.UserPasswdLoginCmdExe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangdengfeng
 * @date 2023/9/12 00:39
 */
@Tag(name = "系统登录", description = "仅系统用户登录")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserPasswdLoginCmdExe userPasswdLoginCmdExe;

    @Operation(summary = "账号密码登录")
    @PostMapping("/user_passwd")
    public AuthorizationTokenCO userPasswdLogin(@RequestBody UserPasswdLoginCmd cmd,
            HttpServletRequest request) {
        log.info("user login username:{} remoteAddr:{}", cmd.getUsername(), request.getRemoteAddr());
        return userPasswdLoginCmdExe.execute(cmd);
    }
}