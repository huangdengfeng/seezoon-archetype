package com.seezoon.infrastructure.properties;

import com.seezoon.infrastructure.tcp.config.ServerConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

    /**
     * 通信配置
     */
    @NestedConfigurationProperty
    @Valid
    @NotNull
    private ServerConfig server = new ServerConfig();
    @NestedConfigurationProperty
    @Valid
    @NotNull
    private DeviceProperties device = new DeviceProperties();
}
