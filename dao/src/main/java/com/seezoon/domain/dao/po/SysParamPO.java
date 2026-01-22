package com.seezoon.domain.dao.po;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysParamPO {

    /**
     * 升级信息
     */
    public static final String PARAM_KEY_APP_UPGRADE_INFO = "APP_UPGRADE_INFO";
    public static final String PARAM_MAP_BAIDU = "MAP_BAIDU";
    public static final String PARAM_MAP_TENCENT = "MAP_TENCENT";
    public static final String PARAM_MAP_GAODE = "MAP_GAODE";
    
    /**
     * 参数KEY (not null)
     */
    private String paramKey;

    /**
     * 参数名称 (not null)
     */
    private String paramName;

    /**
     * 参数值 (not null)
     */
    private String paramValue;

    /**
     * 创建时间 (not null)
     */
    private LocalDateTime createTime;

    /**
     * 更新时间 (not null)
     */
    private LocalDateTime updateTime;
}

