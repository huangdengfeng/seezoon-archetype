package com.seezoon.domain.dao.po;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRefreshTokenPO {

    public static final byte VALID = 1;
    public static final byte REPLACED = 2;

    /**
     * 主键ID (not null)
     */
    private Long id;

    /**
     * 用户ID (not null)
     */
    private Long uid;

    /**
     * 终端Id (not null)
     */
    private String clientId;

    /**
     * refresh token id (not null)
     */
    private String refreshTokenId;

    /**
     * 令牌代数 (not null)
     */
    private Integer tokenGeneration;

    private LocalDateTime replacedTime;

    private LocalDateTime gracePeriodEnd;

    /**
     * 状态1.有效;2.被替换;3.失效 (not null)
     */
    private Byte status;

    /**
     * 颁发时间 (not null)
     */
    private LocalDateTime issueTime;

    /**
     * 过期时间 (not null)
     */
    private LocalDateTime expireTime;

    /**
     * 更新时间 (not null)
     */
    private LocalDateTime updateTime;


    @Getter
    @Setter
    public static class Condition {

        /**
         * 主键ID
         */
        private Long id;

        /**
         * 用户ID
         */
        private Long uid;

        /**
         * 终端Id
         */
        private String clientId;

        /**
         * refresh token id
         */
        private String refreshTokenId;

        /**
         * 状态
         */
        private Byte status;
    }
}