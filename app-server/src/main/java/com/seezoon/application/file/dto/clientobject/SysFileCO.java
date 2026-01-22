package com.seezoon.application.file.dto.clientobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seezoon.infrastructure.constants.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 文件信息客户端对象
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SysFileCO {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "文件名")
    private String name;

    @Schema(description = "MIME类型")
    private String mimeType;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = Constants.DATETIME_PATTERN)
    private LocalDateTime createTime;
}

