package com.seezoon.domain.dao.po;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysSessionPO {

    /**
     * 会话ID (not null)
     */
    private String sessionId;

    /**
     * 用户ID (not null)
     */
    private Integer uid;

    /**
     * 最长不活跃时间（S） (not null)
     */
    private Integer maxInactiveInterval;

    /**
     * 过期时间 (not null)
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间 (not null)
     */
    private LocalDateTime createTime;

    /**
     * 最后访问时间 (not null)
     */
    private LocalDateTime lastAccessTime;

    /**
     * 会话数据 (not null)
     */
    private String data;

    @Getter
    @Setter
    public static class Condition {

        /**
         * 会话ID
         */
        private String sessionId;

        /**
         * 用户ID
         */
        private Integer uid;

        /**
         * 过期时间（用于查询过期会话）
         */
        private LocalDateTime expireTime;
    }
}

