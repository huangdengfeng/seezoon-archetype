package com.seezoon.infrastructure.properties;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录相关参数
 *
 * @author huangdengfeng
 * @date 2023/9/8 16:30
 */
@Getter
@Setter
public class LoginProperties {

    /**
     * 登录有效期,默认1小时
     */
    @NotNull
    private Duration accessTokenExpire = Duration.ofHours(1);
    /**
     *
     */
    @NotNull
    private Duration refreshTokenExpire = Duration.ofDays(1);
}
