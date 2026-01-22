package com.seezoon.application.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件
 */
@Getter
@Setter
public class UploadFileCmd {

    @Schema(description = "文件")
    @NotNull
    private MultipartFile file;
}

