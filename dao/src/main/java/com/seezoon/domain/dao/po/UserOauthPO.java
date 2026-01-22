package com.seezoon.domain.dao.po;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOauthPO {

    /**
     * 用户ID (not null)
     */
    private Long uid;

    /**
     * auth类型 (not null)
     *
     * @see com.seezoon.domain.dao.types.UserOauthType
     */
    private Byte oauthType;

    /**
     * OAuth ID (not null)
     */
    private String oauthId;

    /**
     * Union ID
     */
    private String unionId;

    /**
     * 创建时间 (not null)
     */
    private LocalDateTime createTime;

    /**
     * 更新时间 (not null)
     */
    private LocalDateTime updateTime;


    @Getter
    @Setter
    public static class Condition {

        /**
         * 用户ID
         */
        private Long uid;

        /**
         * auth类型
         *
         * @see com.seezoon.domain.dao.types.UserOauthType
         */
        private Byte oauthType;

        /**
         * OAuth ID
         */
        private String oauthId;
    }
}

