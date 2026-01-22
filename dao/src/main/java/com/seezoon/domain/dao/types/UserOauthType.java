package com.seezoon.domain.dao.types;

import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;

@Getter
public enum UserOauthType {
    WX_APP((byte) 1, "微信APP"),
    WX_MIN_APP((byte) 2, "微信小程序"),
    WX_MP((byte) 3, "公众号"),
    ;

    private byte code;
    private String name;

    UserOauthType(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean isWxApp(Byte code) {
        return Objects.equals(WX_APP.code, code);
    }

    public static boolean isWxMinApp(Byte code) {
        return Objects.equals(WX_MIN_APP.code, code);
    }

    public static boolean isWxMP(Byte code) {
        return Objects.equals(WX_MP.code, code);
    }

    public static void check(Byte code) {
        boolean match = Arrays.stream(UserOauthType.values()).anyMatch(item -> Objects.equals(item.code, code));
        if (!match) {
            throw new IllegalArgumentException("user oauth type code invalid:" + code);
        }
    }
}
