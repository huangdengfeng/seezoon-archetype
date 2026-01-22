package com.seezoon.infrastructure.rpc.wx.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@ToString
public class WxError {

    private static final String SUCCESS = "0";

    private String errcode;
    private String errmsg;

    public boolean success() {
        return StringUtils.isEmpty(errcode) || SUCCESS.equals(errcode);
    }

}
