package com.seezoon.domain.service.file.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysFileVO {

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 文件名
     */
    @NotEmpty
    private String name;

    /**
     * MIME类型（如 "image/jpeg", "image/png"）
     */
    @NotEmpty
    private String mimeType;

    /**
     * 文件原始大小（字节）
     */
    @NotNull
    private Long fileSize;

    /**
     * 二进制数据
     */
    @NotNull
    private byte[] data;

    /**
     * 用户ID
     */
    private Long uid;
}

