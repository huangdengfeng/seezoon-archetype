package com.seezoon.domain.service.sys.authentication;

import com.seezoon.domain.service.sys.authentication.valueobj.UserDetailsVO;
import com.seezoon.domain.service.sys.authentication.valueobj.UserGrantedAuthority;
import com.seezoon.domain.service.sys.valueobj.UserVO;
import com.seezoon.infrastructure.configuration.security.UserDetailsLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 登录认证处理
 * <p>
 * spring security filter会调用 {@link com.seezoon.infrastructure.configuration.security.WebSecurityConfig}
 * </p>
 *
 * @author huangdengfeng
 * @date 2023/9/10 10:43
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Validated
public class UserDetailsLoaderService implements UserDetailsLoader {


    private final SessionService sessionService;

    @Override
    public UserDetails getUserDetails() throws Throwable {
        UserVO userVO = sessionService.getSessionData();
        if (null == userVO) {
            return null;
        }
        List<UserGrantedAuthority> userGrantedAuthorities = new ArrayList<>();
        Set<String> roles = userVO.getRoles();
        for (String role : roles) {
            userGrantedAuthorities.add(new UserGrantedAuthority(role, true));
        }
        Set<String> permissions = userVO.getPermissions();
        for (String permission : permissions) {
            userGrantedAuthorities.add(new UserGrantedAuthority(permission));
        }

        UserDetailsVO userDetails = new UserDetailsVO(userVO, userGrantedAuthorities);
        return userDetails;
    }
}
