package com.seezoon.infrastructure.configuration;

import com.seezoon.infrastructure.configuration.properties.RbacConfigProperties;
import com.seezoon.infrastructure.configuration.properties.RbacConfigProperties.PermissionDefinition;
import com.seezoon.infrastructure.configuration.properties.RbacConfigProperties.RoleDefinition;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 权限配置加载器
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RbacConfig {

    private final RbacConfigProperties rbacConfigProperties;

    /**
     * 权限定义映射：code -> name
     */
    @Getter
    private Map<String, String> permissionMap = new HashMap<>();

    /**
     * 角色定义映射：code -> name
     */
    @Getter
    private Map<String, String> roleMap = new HashMap<>();

    /**
     * 角色权限映射：roleCode -> Set<permissionCode>
     */
    @Getter
    private Map<String, Set<String>> rolePermissionMap = new HashMap<>();

    /**
     * 启动时加载权限和角色配置
     */
    @PostConstruct
    public void init() {

        // 加载权限定义
        List<PermissionDefinition> permissions = rbacConfigProperties.getPermissions();
        for (PermissionDefinition perm : permissions) {
            permissionMap.put(perm.getCode(), perm.getName());
        }
        log.info("loaded {} permissions", permissionMap.size());

        // 加载角色定义和角色权限关联
        List<RoleDefinition> roles = rbacConfigProperties.getRoles();
        for (RoleDefinition role : roles) {
            roleMap.put(role.getCode(), role.getName());
            Set<String> permissionCodes = role.getPermissions().stream()
                    .collect(Collectors.toSet());
            rolePermissionMap.put(role.getCode(), permissionCodes);
        }
        log.info("loaded {} roles", roleMap.size());

        // 验证权限引用
        validatePermissions();
    }

    /**
     * 验证角色引用的权限是否存在
     */
    private void validatePermissions() {
        for (Map.Entry<String, Set<String>> entry : rolePermissionMap.entrySet()) {
            String roleCode = entry.getKey();
            Set<String> permissionCodes = entry.getValue();
            for (String permissionCode : permissionCodes) {
                if (!permissionMap.containsKey(permissionCode)) {
                    log.error("role [{}] references undefined permission [{}]", roleCode, permissionCode);
                    throw new IllegalArgumentException(
                            "role [" + roleCode + "] references undefined permission [" + permissionCode + "]");
                }
            }
        }
    }

    /**
     * 根据角色代码获取该角色拥有的所有权限代码
     *
     * @param roleCode 角色代码
     * @return 权限代码集合
     */
    public Set<String> getPermissionsByRole(String roleCode) {
        return rolePermissionMap.getOrDefault(roleCode, Set.of());
    }

    /**
     * 根据角色代码集合获取所有权限代码
     *
     * @param roleCodes 角色代码集合
     * @return 权限代码集合
     */
    public Set<String> getPermissionsByRoles(Set<String> roleCodes) {
        return roleCodes.stream()
                .flatMap(roleCode -> getPermissionsByRole(roleCode).stream())
                .collect(Collectors.toSet());
    }

    /**
     * 检查权限是否存在
     *
     * @param permissionCode 权限代码
     * @return 是否存在
     */
    public boolean hasPermission(String permissionCode) {
        return permissionMap.containsKey(permissionCode);
    }

    /**
     * 检查角色是否存在
     *
     * @param roleCode 角色代码
     * @return 是否存在
     */
    public boolean hasRole(String roleCode) {
        return roleMap.containsKey(roleCode);
    }

    /**
     * 获取所有权限代码
     *
     * @return 权限代码集合
     */
    public Set<String> getAllPermissionCodes() {
        return new HashSet<>(permissionMap.keySet());
    }

    /**
     * 获取所有角色代码
     *
     * @return 角色代码集合
     */
    public Set<String> getAllRoleCodes() {
        return new HashSet<>(roleMap.keySet());
    }

    public List<RoleDefinition> getRoles() {
        return rbacConfigProperties.getRoles();
    }

    public String getRoleName(String roleCode) {
        return roleMap.get(roleCode);
    }

    /**
     * 根据权限代码获取权限名称
     *
     * @param permissionCode 权限代码
     * @return 权限名称
     */
    public String getPermissionName(String permissionCode) {
        return permissionMap.get(permissionCode);
    }
}

