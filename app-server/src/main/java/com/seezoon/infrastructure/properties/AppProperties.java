package com.seezoon.infrastructure.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * 业务应用配置
 *
 * @author huangdengfeng
 * @date 2023/9/8 16:29
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

    /**
     * 服务器安全密钥，登录验证，验证码等使用
     */
    @NotEmpty
    @Length(min = 32)
    private String secretKey;

    @NotNull
    @Valid
    @NestedConfigurationProperty
    private LoginProperties login = new LoginProperties();

    @Valid
    @NotNull
    @NestedConfigurationProperty
    private CorsProperties cors = new CorsProperties();

    @Valid
    @NotNull
    @NestedConfigurationProperty
    private WxProperties wx = new WxProperties();

}
