package com.seezoon.interfaces;

import com.seezoon.application.file.executor.FilePreviewQryExe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "公共接口", description = "无需登录态")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    private final FilePreviewQryExe filePreviewQryExe;

    @GetMapping("/file/preview/{id}")
    @Operation(summary = "预览文件")
    public ResponseEntity<byte[]> previewFile(@PathVariable Long id) {
        return filePreviewQryExe.execute(id);
    }

}
