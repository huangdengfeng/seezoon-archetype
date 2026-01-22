package com.seezoon.infrastructure.error;

import com.seezoon.infrastructure.exception.ErrorDefinition;

public enum ErrorCode implements ErrorDefinition {
    /**
     * 建议公共错误定义在1000-2000 的范围
     */
    UNKNOWN(1000, "系统错误，请稍后重试"),

    PARAM_INVALID(1001, "参数错误"),

    SQL_ERROR(1002, "数据库操作错误", true),
    ASSERTION_ERROR(1003, "断言错误"),
    RECORD_NOT_EXISTS(1004, "记录不存在"),
    RECORD_EXISTS(1005, "记录已存在"),
    RECORD_TOO_MANY(1006, "操作太频繁"),
    ILLEGAL_OP(1007, "非法操作"),
    FILE_TOO_LARGE(1008, "文件过大"),
    SYS_PARAM_NOT_EXISTS(1009, "系统参数不存在"),


    USER_STATUS_INVALID(2001, "用户状态异常"),
    USER_STATUS_LOCKED(2002, "用户已锁定"),
    USER_NOT_EXISTS(2003, "用户不存在"),
    USER_ALREADY_EXISTS(2004, "用户已存在"),
    FREQUENT_LOGIN(2005, "登录频繁"),
    REFRESH_TOKEN_ERROR(2006, "刷新登录失败"),
    USERNAME_EXISTS(2007, "用户名已存在"),
    WX_ERROR(3000, "调用微信出错，请稍后重试"),
    USER_PWD_ERROR(3001, "账号密码错误，错误6次后将锁定"),
    PASSWORD_NOT_MATCH(3002, "原密码不正确"),
    ;


    public static final int ERROR_TYPE_BUSINESS = 0;
    public static final int ERROR_TYPE_SYSTEM = 1;

    private int code;
    private String msg;

    private int type;


    ErrorCode(int code, String msg) {
        this(code, msg, false);
    }

    ErrorCode(int code, String msg, boolean sysError) {
        this.code = code;
        this.msg = msg;
        this.type = sysError ? ERROR_TYPE_SYSTEM : ERROR_TYPE_BUSINESS;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }

    @Override
    public int type() {
        return type;
    }

    public boolean IsSystemError() {
        return type == ERROR_TYPE_SYSTEM;
    }
}
