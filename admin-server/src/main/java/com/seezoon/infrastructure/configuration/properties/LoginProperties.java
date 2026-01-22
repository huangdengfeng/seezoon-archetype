package com.seezoon.infrastructure.configuration.properties;

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
     * 登录有效期,默认两小时
     * <p>spring boot 可配置2h、7200s</p>
     */
    @NotNull
    private Duration sessionTimeout = Duration.ofHours(2);

}
