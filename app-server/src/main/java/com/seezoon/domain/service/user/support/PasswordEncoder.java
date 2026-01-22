package com.seezoon.domain.service.user.support;

import com.seezoon.infrastructure.exception.Assertion;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码处理
 */
public class PasswordEncoder {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     *
     * @param password not empty
     * @return
     */
    public static String encode(String password) {
        Assertion.notEmpty(password, "password is empty");
        return encoder.encode(password);
    }


    /**
     * 验证密码
     *
     * @param rawPassword 不能为空
     * @param encodedPassword 不能为空
     * @return
     */
    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        Assertion.isTrue(null != rawPassword && rawPassword.length() > 0, "rawPassword must not empty");
        Assertion.notEmpty(encodedPassword, "encodedPassword must not empty");
        return encoder.matches(rawPassword, encodedPassword);
    }

}
