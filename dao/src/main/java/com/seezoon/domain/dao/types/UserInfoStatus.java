package com.seezoon.domain.dao.types;

import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;

@Getter
public enum UserInfoStatus {
    VALID((byte) 1, "有效"),
    INVALID((byte) 2, "无效"),
    LOCKED((byte) 3, "锁定");

    private byte code;
    private String name;

    UserInfoStatus(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean isValid(Byte code) {
        return Objects.equals(VALID.code, code);
    }

    public static boolean isInvalid(Byte code) {
        return Objects.equals(INVALID.code, code);
    }

    public static boolean isLocked(Byte code) {
        return Objects.equals(LOCKED.code, code);
    }

    public static void check(Byte code) {
        boolean match = Arrays.stream(UserInfoStatus.values()).anyMatch(item -> Objects.equals(item.code, code));
        if (!match) {
            throw new IllegalArgumentException("user info status code invalid:" + code);
        }
    }
}
