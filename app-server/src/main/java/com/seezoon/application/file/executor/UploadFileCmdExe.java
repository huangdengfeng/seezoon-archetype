package com.seezoon.application.file.executor;

import com.seezoon.application.file.dto.UploadFileCmd;
import com.seezoon.domain.dao.types.SecurityOperation;
import com.seezoon.domain.service.file.SysFileService;
import com.seezoon.domain.service.file.vo.SysFileVO;
import com.seezoon.domain.service.sys.SysSecurityService;
import com.seezoon.infrastructure.configuration.context.SecurityContextHolder;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class UploadFileCmdExe {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 16MB
    private static final float imageQuality = 0.7f;
    private static final double scale = 0.7d;
    private final SysFileService sysFileService;
    private final SysSecurityService sysSecurityService;

    public Long execute(@Valid @NotNull UploadFileCmd cmd) {
        MultipartFile file = cmd.getFile();
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("file size exceeds limit, size:{}", file.getSize());
            throw ExceptionFactory.bizException(ErrorCode.FILE_TOO_LARGE);
        }

        Long uid = SecurityContextHolder.getUid();
        // 检查操作限制
        sysSecurityService.checkOperationLimit100(uid, SecurityOperation.UPLOAD_FILE.getCode());
        try {
            byte[] fileBytes = file.getBytes();
            // 压缩
            if (StringUtils.trimToEmpty(file.getContentType()).startsWith("image/")) {
                fileBytes = this.imageCompress(file.getInputStream(), imageQuality, scale);
                log.info("compress image file uid:{} original size :{} compressed size:{}", uid, file.getSize(),
                        fileBytes.length);
            }
            SysFileVO vo = new SysFileVO();
            vo.setName(file.getOriginalFilename());
            vo.setMimeType(file.getContentType());
            vo.setFileSize((long) fileBytes.length);
            vo.setData(fileBytes);
            vo.setUid(uid);
            Long fileId = sysFileService.createFile(vo);
            log.info("upload file success, file id:{}, name:{}", fileId, vo.getName());
            // 记录操作
            sysSecurityService.recordOperation(uid, SecurityOperation.UPLOAD_FILE.getCode(), null);
            return fileId;
        } catch (IOException e) {
            log.error("read file error", e);
            throw ExceptionFactory.bizException(ErrorCode.UNKNOWN);
        }
    }

    /**
     * 图片压缩
     *
     * @param source will be auto close
     * @param imageQuality 输出质量
     * @param scale 缩放
     * @return
     */
    private byte[] imageCompress(InputStream source, float imageQuality, double scale) throws IOException {
        try (InputStream in = source; ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            // jpg 压缩效果好
            Thumbnails.of(in).outputFormat("jpg").outputQuality(imageQuality).scale(scale).toOutputStream(bos);
            return bos.toByteArray();
        }
    }
}

