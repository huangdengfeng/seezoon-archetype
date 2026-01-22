package com.seezoon.domain.dao.po;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserRolePO {

    /**
     * 用户ID (not null)
     */
    private Integer uid;

    /**
     * 角色代码 (not null)
     */
    private String role;

    /**
     * 创建时间 (not null)
     */
    private LocalDateTime createTime;

    @Getter
    @Setter
    public static class Condition {

        /**
         * 用户ID
         */
        private Integer uid;

        /**
         * 角色代码
         */
        private String role;
    }
}

