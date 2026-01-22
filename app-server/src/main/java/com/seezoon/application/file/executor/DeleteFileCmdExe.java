package com.seezoon.application.file.executor;

import com.seezoon.application.file.dto.DeleteFileCmd;
import com.seezoon.domain.service.file.SysFileService;
import com.seezoon.infrastructure.configuration.context.SecurityContextHolder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 删除文件
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class DeleteFileCmdExe {

    private final SysFileService sysFileService;

    public void execute(@Valid @NotNull DeleteFileCmd cmd) {
        Long uid = SecurityContextHolder.getUid();
        sysFileService.deleteFile(cmd.getId(), uid);
        log.info("delete file success, file id:{}", cmd.getId());
    }
}

