package com.seezoon.domain.service.file;

import com.seezoon.domain.dao.mapper.SysFileMapper;
import com.seezoon.domain.dao.po.SysFilePO;
import com.seezoon.domain.service.file.vo.SysFileVO;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 文件存储领域服务
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Validated
public class SysFileService {

    private final SysFileMapper sysFileMapper;

    /**
     * 创建文件
     */
    @Transactional
    public Long createFile(@Valid @NotNull SysFileVO vo) {
        SysFilePO po = new SysFilePO();
        po.setName(vo.getName());
        po.setMimeType(vo.getMimeType());
        po.setFileSize(vo.getFileSize());
        po.setData(vo.getData());
        po.setUid(vo.getUid());
        po.setCreateTime(LocalDateTime.now());
        int affectedRows = sysFileMapper.insert(po);
        Assertion.affectedOne(affectedRows);
        return po.getId();
    }

    /**
     * 删除文件
     */
    @Transactional
    public void deleteFile(@NotNull Long id, Long uid) {
        SysFilePO po = sysFileMapper.selectByPrimaryKey(id);
        if (po == null) {
            log.error("file not exists, id:{}", id);
            throw ExceptionFactory.bizException(ErrorCode.RECORD_NOT_EXISTS);
        }
        // 只能删除自己的文件
        if (Objects.equals(po.getUid(), uid)) {
            log.error("file not belong to user, file id:{}, file uid:{}, request uid:{}", id, po.getUid(), uid);
            throw ExceptionFactory.bizException(ErrorCode.ILLEGAL_OP);
        }

        int affectedRows = sysFileMapper.deleteByPrimaryKey(id);
        Assertion.affectedOne(affectedRows);
    }

    /**
     * 获取文件（包含二进制数据）
     */
    public SysFileVO getFile(@NotNull Long id) {
        SysFilePO po = sysFileMapper.selectByPrimaryKey(id);
        if (po == null) {
            log.error("file not exists, id:{}", id);
            throw ExceptionFactory.bizException(ErrorCode.RECORD_NOT_EXISTS);
        }

        SysFileVO vo = new SysFileVO();
        vo.setId(po.getId());
        vo.setName(po.getName());
        vo.setMimeType(po.getMimeType());
        vo.setFileSize(po.getFileSize());
        vo.setData(po.getData());
        vo.setUid(po.getUid());
        return vo;
    }
}

