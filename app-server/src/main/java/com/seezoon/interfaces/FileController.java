package com.seezoon.interfaces;

import com.seezoon.application.file.dto.DeleteFileCmd;
import com.seezoon.application.file.dto.UploadFileCmd;
import com.seezoon.application.file.executor.DeleteFileCmdExe;
import com.seezoon.application.file.executor.UploadFileCmdExe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/file")
@Tag(name = "文件管理")
public class FileController {

    private final UploadFileCmdExe uploadFileCmdExe;
    private final DeleteFileCmdExe deleteFileCmdExe;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Long uploadFile(@RequestParam MultipartFile file) {
        UploadFileCmd cmd = new UploadFileCmd();
        cmd.setFile(file);
        return uploadFileCmdExe.execute(cmd);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除文件")
    public void deleteFile(@RequestBody @Valid DeleteFileCmd cmd) {
        deleteFileCmdExe.execute(cmd);
    }

}

