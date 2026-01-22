package com.seezoon.infrastructure.error;

import com.seezoon.infrastructure.exception.ErrorDefinition;

public enum ErrorCode implements ErrorDefinition {
    /**
     * 建议公共错误定义在1000-2000 的范围
     */
    UNKNOWN(1000, "系统错误，请稍后重试"),

    PARAM_INVALID(1001, "param invalid"),

    NET_MAGIC_NOT_MATCH(2001, "magic not match"),
    NET_STOP_NOT_MATCH(2002, "stop bit not match"),
    NET_CRC_NOT_MATCH(2003, "crc not match"),
    NET_CHANNEL_CLOSED(2004, "net channel closed"),
    NET_MESSAGE_READ_TIMEOUT(2005, "read net message timeout"),

    DEVICE_OFFLINE(3011, "device is offline"),

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
