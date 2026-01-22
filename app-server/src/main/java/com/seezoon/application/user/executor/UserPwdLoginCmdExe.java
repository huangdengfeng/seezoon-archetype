package com.seezoon.application.user.executor;

import com.seezoon.application.user.dto.UserPwdLoginCmd;
import com.seezoon.application.user.dto.clientobject.LoginCO;
import com.seezoon.domain.service.user.LoginService;
import com.seezoon.domain.service.user.UserPasswdVerifyService;
import com.seezoon.domain.service.user.vo.LoginTokenVO;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.utils.OtelUtils;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 账号密码登录
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class UserPwdLoginCmdExe {

    private static final AttributeKey<String> ATTR_STATUS = AttributeKey.stringKey("status");
    private static final AttributeKey<String> ATTR_USERNAME = AttributeKey.stringKey("username");

    private final UserPasswdVerifyService userPasswdVerifyService;
    private final LoginService loginService;

    /**
     * 登录总数计数器（按天聚合由后端观测系统完成，如 Prometheus/ARMS）
     */
    private LongCounter loginTotalCounter;

    /**
     * 用户登录计数器（区分用户和状态）
     */
    private LongCounter loginUserCounter;

    @PostConstruct
    public void init() {
        // 获取 OpenTelemetry Meter
        Meter meter = GlobalOpenTelemetry.getMeter("com.seezoon.login");

        // 登录总数指标
        loginTotalCounter = meter.counterBuilder("login.total")
                .setDescription("Total login attempts count")
                .setUnit("1")
                .build();

        // 用户登录指标
        loginUserCounter = meter.counterBuilder("login.user")
                .setDescription("Login attempts per user")
                .setUnit("1")
                .build();
    }

    public LoginCO execute(@Valid @NotNull UserPwdLoginCmd cmd) {
        OtelUtils.setAttribute(ATTR_USERNAME, cmd.getUsername());
        String username = cmd.getUsername();
        boolean verified = userPasswdVerifyService.verify(username, cmd.getPassword());
        if (!verified) {
            log.info("user pwd verify fail,username:{}", username);
            // 记录登录失败指标
            recordLoginMetrics(username, "failure");
            throw ExceptionFactory.bizException(ErrorCode.USER_PWD_ERROR);
        }
        LoginTokenVO loginTokenVO = loginService.loginByUserName(username);
        LoginCO co = new LoginCO();
        co.setAccessToken(loginTokenVO.getAccessToken());
        co.setAccessTokenExpire(loginTokenVO.getAccessTokenExpire());
        co.setRefreshToken(loginTokenVO.getRefreshToken());
        co.setRefreshTokenExpire(loginTokenVO.getRefreshTokenExpire());
        log.info("user pwd login success,username:{}", username);
        // 记录登录成功指标
        recordLoginMetrics(username, "success");
        return co;
    }

    /**
     * 记录登录指标
     *
     * @param username 用户名
     * @param status 状态：success 或 failure
     */
    private void recordLoginMetrics(String username, String status) {
        // 登录总数 +1（按 status 区分成功/失败）
        loginTotalCounter.add(1, Attributes.of(ATTR_STATUS, status));

        // 用户登录次数 +1（按 username 和 status 区分）
        loginUserCounter.add(1, Attributes.of(
                ATTR_USERNAME, username,
                ATTR_STATUS, status
        ));
    }
}
