package com.seezoon.domain.dao.po;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoPO {

    /**
     * 用户ID (not null)
     */
    private Long uid;

    private String username;

    private String password;

    /**
     * 用户安全Key (not null)
     */
    private String secretKey;

    /**
     * 状态1.有效;2.无效;3.锁定 (not null)
     */
    private Byte status;

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
         * 用户名
         */
        private String username;

        /**
         * 状态
         *
         * @see com.seezoon.domain.dao.types.UserInfoStatus
         */
        private Byte status;
    }
}