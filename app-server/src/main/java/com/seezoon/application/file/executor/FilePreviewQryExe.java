package com.seezoon.application.file.executor;

import com.seezoon.domain.service.file.SysFileService;
import com.seezoon.domain.service.file.vo.SysFileVO;
import jakarta.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 文件预览查询
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class FilePreviewQryExe {

    private final SysFileService sysFileService;

    public ResponseEntity<byte[]> execute(@NotNull Long id) {
        SysFileVO fileVO = sysFileService.getFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileVO.getMimeType()));
        // 设置 Content-Disposition 为 inline，让浏览器直接预览而不是下载
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(URLEncoder.encode(fileVO.getName(), StandardCharsets.UTF_8))
                .build());
        byte[] data = fileVO.getData();
        headers.setContentLength(data.length);
        log.info("file length:{},actual length:{}", fileVO.getFileSize(), fileVO.getData().length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}

