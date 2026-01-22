package com.seezoon.domain.dao.po;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysSecurityPO {

    /**
     * 自增主键ID，唯一标识一条操作记录 (not null)
     */
    private Long id;

    /**
     * uid (not null)
     */
    private Long uid;

    /**
     * 操作类型 (not null)
     */
    private Integer operation;

    /**
     * 操作相关的详细数据
     */
    private String data;

    /**
     * 创建时间 (not null)
     */
    private LocalDateTime createTime;

    @Getter
    @Setter
    public static class Condition {

        /**
         * 自增主键ID
         */
        private Long id;

        /**
         * uid
         */
        private Long uid;

        /**
         * 操作类型
         */
        private Integer operation;

        /**
         * 创建时间（用于查询当天）
         */
        private LocalDateTime createTime;
    }
}

