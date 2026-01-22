package com.seezoon.infrastructure.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 权限配置属性
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "rbac")
@Validated
public class RbacConfigProperties {

    @NotEmpty
    @Valid
    private List<PermissionDefinition> permissions = new ArrayList<>();
    
    @NotEmpty
    @Valid
    private List<RoleDefinition> roles = new ArrayList<>();

    @Getter
    @Setter
    public static class PermissionDefinition {

        /**
         * 权限英文名（唯一标识）
         */
        @NotBlank
        private String code;

        /**
         * 权限中文名
         */
        @NotBlank
        private String name;
    }

    @Getter
    @Setter
    public static class RoleDefinition {

        /**
         * 角色英文名（唯一标识）
         */
        @NotBlank
        private String code;

        /**
         * 角色中文名
         */
        @NotBlank
        private String name;

        /**
         * 该角色拥有的权限列表（权限code）
         */
        @NotNull
        private List<String> permissions = Collections.emptyList();
    }
}

