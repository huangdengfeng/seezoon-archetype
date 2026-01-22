package com.seezoon.domain.dao.types;

import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;

@Getter
public enum SecurityOperation {

    LOGIN(1, "登录"),
    UPLOAD_FILE(2, "上传文件"),
    ;

    private int code;
    private String name;

    SecurityOperation(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static void check(Byte code) {
        boolean match = Arrays.stream(SecurityOperation.values()).anyMatch(item -> Objects.equals(item.code, code));
        if (!match) {
            throw new IllegalArgumentException("security operation code invalid:" + code);
        }
    }
}
