package com.seezoon.domain.service.sys.valueobj;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息
 */
@Getter
@Setter
public class UserVO {

    @NotEmpty
    private Integer uid;
    @NotEmpty
    private String userName;
    @NotNull
    private Set<String> roles = Collections.emptySet();
    @NotNull
    private Set<String> permissions = Collections.emptySet();

    public UserVO() {
    }

    public UserVO(Integer uid, String userName) {
        this.uid = Objects.requireNonNull(uid);
        this.userName = Objects.requireNonNull(userName);
    }
}
