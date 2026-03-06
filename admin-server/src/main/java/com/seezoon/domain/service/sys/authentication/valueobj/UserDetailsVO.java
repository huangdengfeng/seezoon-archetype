package com.seezoon.domain.service.sys.authentication.valueobj;

import com.seezoon.domain.service.sys.valueobj.UserVO;
import java.util.Collection;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户信息
 *
 * @author huangdengfeng
 * @date 2023/9/10 16:57
 */
public class UserDetailsVO implements UserDetails {

    private final UserVO userVO;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsVO(UserVO userVO, Collection<? extends GrantedAuthority> authorities) {
        this.userVO = Objects.requireNonNull(userVO);
        this.authorities = Objects.requireNonNull(authorities);
    }

    public UserVO getUser() {
        return userVO;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userVO.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
