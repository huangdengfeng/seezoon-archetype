package com.seezoon.interfaces;

import com.seezoon.application.user.dto.RefreshTokenCmd;
import com.seezoon.application.user.dto.UserPwdLoginCmd;
import com.seezoon.application.user.dto.WxAppLoginCmd;
import com.seezoon.application.user.dto.clientobject.LoginCO;
import com.seezoon.application.user.executor.RefreshTokenCmdExe;
import com.seezoon.application.user.executor.UserPwdLoginCmdExe;
import com.seezoon.application.user.executor.WxAppLoginCmdExe;
import com.seezoon.infrastructure.exception.BizException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户登录", description = "用户登录处理")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserPwdLoginCmdExe userPwdLoginCmdExe;
    private final RefreshTokenCmdExe refreshTokenCmdExe;
    private final WxAppLoginCmdExe wxAppLoginCmdExe;


    @Operation(summary = "账号密码登录")
    @PostMapping("/user_pwd")
    public LoginCO userPwdLogin(@RequestBody UserPwdLoginCmd cmd) {
        return userPwdLoginCmdExe.execute(cmd);
    }

    @Operation(summary = "微信登录")
    @PostMapping("/wx_app")
    public LoginCO wxAppLogin(@RequestBody WxAppLoginCmd cmd) {
        return wxAppLoginCmdExe.execute(cmd);
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh_token")
    public LoginCO refreshToken(@RequestBody RefreshTokenCmd cmd, HttpServletResponse response) {
        try {
            return refreshTokenCmdExe.execute(cmd);
        } catch (BizException e) {
            // 业务错误返回401
            log.error("refresh token error {},{}", e.getCode(), e.getMessage());
            response.setStatus(401);
            return null;
        }
        // 其他错误正常报错，避免DB 抖动造成用户闪退
    }
}
