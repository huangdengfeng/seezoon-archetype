package com.seezoon.domain.dao.po;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserPO {

    /**
     * 用户ID (not null)
     */
    private Integer uid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 安全密钥 (not null)
     */
    private String secretKey;

    /**
     * 姓名 (not null)
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 照片
     */
    private String photo;

    /**
     * 状态：1.正常;2.停用;3.锁定 (not null)
     */
    private Byte status;

    /**
     * 创建时间 (not null)
     */
    private LocalDateTime createTime;

    /**
     * 创建人 (not null)
     */
    private Integer createUser;

    /**
     * 更新时间 (not null)
     */
    private LocalDateTime updateTime;

    /**
     * 更新用户 (not null)
     */
    private Integer updateUser;

    /**
     * 备注
     */
    private String remark;

    @Getter
    @Setter
    public static class Condition {

        /**
         * 用户ID
         */
        private Integer uid;

        /**
         * 用户名
         */
        private String username;

        /**
         * 姓名（模糊查询）
         */
        private String fuzzyName;

        /**
         * 手机号
         */
        private String mobile;

        /**
         * 状态
         */
        private Byte status;

        /**
         * 是否包含系统管理员
         */
        private Boolean includeSysAdmin;
    }
}

