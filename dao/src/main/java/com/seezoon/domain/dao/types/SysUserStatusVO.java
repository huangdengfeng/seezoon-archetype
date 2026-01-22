package com.seezoon.domain.dao.types;

import java.util.Objects;

/**
 * 用户状态
 */
public enum SysUserStatusVO {
    VALID((byte)1,"有效"),
    INVALID((byte)2,"无效"),
    LOCKED((byte)3,"锁定"),
    ;

    private Byte code;
    private String name;


    SysUserStatusVO(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static void check(Byte code) {
        boolean match =false;
        for (SysUserStatusVO value : SysUserStatusVO.values()) {
            if (Objects.equals(value.getCode(), code)) {
                match = true;
                break;
            }
        }
        if (!match) {
            throw new IllegalArgumentException("user status code [" + code +"]error");
        }
    }

    public static boolean isValid(Byte code) {
        return Objects.equals(code,SysUserStatusVO.VALID.code);
    }

    public static boolean isInvalid(Byte code) {
        return Objects.equals(code,SysUserStatusVO.INVALID.code);
    }

    public static boolean isLocked(Byte code) {
        return Objects.equals(code,SysUserStatusVO.LOCKED.code);
    }

    public Byte getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
