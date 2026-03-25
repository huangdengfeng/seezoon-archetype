package com.seezoon.infrastructure.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WxProperties {

    /**
     * appId
     */
    @NotEmpty
    private String appId;
    /**
     * appSecret
     */
    @NotEmpty
    private String appSecret;

    /**
     * 小程序appId
     */
    @NotEmpty
    private String mappId;
    /**
     * 小程序appSecret
     */
    @NotEmpty
    private String mappSecret;

}
