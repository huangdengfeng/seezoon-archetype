package com.seezoon.application.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 删除文件
 */
@Getter
@Setter
public class DeleteFileCmd {

    @Schema(description = "文件ID")
    @NotNull
    private Long id;
}

