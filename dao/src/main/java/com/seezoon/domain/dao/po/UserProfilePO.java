package com.seezoon.domain.dao.po;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfilePO {

    /**
     * 用户ID (not null)
     */
    private Long uid;

    private String nickName;

    private String name;

    private String mobile;

    private String avatar;

    private String email;

    private LocalDate birthday;

    private String address;

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
         * 手机号
         */
        private String mobile;

        /**
         * 邮箱
         */
        private String email;
    }
}