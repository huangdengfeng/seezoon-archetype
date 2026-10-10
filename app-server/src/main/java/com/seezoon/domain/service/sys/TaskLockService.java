package com.seezoon.domain.service.sys;

import com.seezoon.domain.dao.mapper.TaskInfoMapper;
import com.seezoon.infrastructure.utils.NetUtils;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@Validated
@Slf4j
@RequiredArgsConstructor
public class TaskLockService {

    private final TaskInfoMapper taskInfoMapper;
    private final String lockBy = NetUtils.getHostIp();

    public boolean tryLock(@NotEmpty String taskId, int lockSeconds) {
        if (StringUtils.isEmpty(lockBy)) {
            log.error("task lock by is empty");
        }
        return taskInfoMapper.tryLock(taskId, lockBy, lockSeconds) > 0;
    }

    public boolean unlock(@NotEmpty String taskId) {
        return taskInfoMapper.unlock(taskId, lockBy) > 0;
    }
}
