package com.seezoon.domain.service.sys.valueobj;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * 添加用户值对象
 *
 * @author huangdengfeng
 * @date 2023/8/26 18:47
 */
@Getter
@Setter
public class AddUserVO {

    @NotEmpty
    private String userName;
    @NotEmpty
    private String password;
    @NotEmpty
    private String name;
    private String mobile;
    private String email;
    private String photo;
    private String remark;

    /**
     * 角色代码集合
     */
    @NotNull
    private Set<String> roles = Collections.emptySet();

    public AddUserVO(String userName, String name) {
        this.userName = userName;
        this.name = name;
    }
}
