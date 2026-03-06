package com.seezoon.infrastructure.configuration.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户信息接口
 *
 * @author huangdengfeng
 * @date 2023/9/10 22:54
 */
public interface UserDetailsLoader {

    /**
     * @return
     */
    UserDetails getUserDetails() throws Throwable;
}
