package com.seezoon.domain.dao.po;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysFilePO {

    /**
     * 文件唯一ID（主键） (not null)
     */
    private Long id;

    /**
     * 文件名 (not null)
     */
    private String name;

    /**
     * MIME类型（如 "image/jpeg", "image/png"） (not null)
     */
    private String mimeType;

    /**
     * 文件原始大小（字节） (not null)
     */
    private Long fileSize;

    /**
     * 二进制数据（MEDIUMBLOB支持最大16MB） (not null)
     */
    private byte[] data;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 创建时间 (not null)
     */
    private LocalDateTime createTime;

    @Getter
    @Setter
    public static class Condition {

        /**
         * 文件ID
         */
        private Long id;

        /**
         * 用户ID
         */
        private Long uid;
    }
}

