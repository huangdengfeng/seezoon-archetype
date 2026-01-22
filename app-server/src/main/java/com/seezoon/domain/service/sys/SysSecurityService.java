package com.seezoon.domain.service.sys;

import com.seezoon.domain.dao.mapper.SysSecurityMapper;
import com.seezoon.domain.dao.po.SysSecurityPO;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 用户安全操作记录领域服务
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Validated
public class SysSecurityService {

    /**
     * 默认一个操作一天最多10次
     */
    private static final int DEFAULT_DAILY_LIMIT_10 = 10;
    private static final int DEFAULT_DAILY_LIMIT_100 = 100;

    private final SysSecurityMapper sysSecurityMapper;

    /**
     * 记录安全操作
     *
     * @param uid 用户ID
     * @param operation 操作类型
     * @param data 操作相关的详细数据
     * @return 记录ID
     */
    @Transactional
    public void recordOperation(@NotNull Long uid, @NotNull Integer operation, String data) {
        try {
            SysSecurityPO po = new SysSecurityPO();
            po.setUid(uid);
            po.setOperation(operation);
            po.setData(data);
            po.setCreateTime(LocalDateTime.now());
            int affectedRows = sysSecurityMapper.insert(po);
            Assertion.affectedOne(affectedRows);
        } catch (Exception e) {
            log.info("record operation failure uid:{} operation:{}", uid, operation, e);
        }
    }


    /**
     * 检查用户当天操作是否超限
     *
     * @param uid 用户ID
     * @param operation 操作类型
     * @return true-超限，false-未超限
     */
    public boolean isOperationExceededToday(@NotNull Long uid, @NotNull Integer operation, int limit) {
        LocalDate today = LocalDate.now();
        long count = sysSecurityMapper.countByUidAndOperationToday(uid, operation, today);
        return count >= limit;
    }


    /**
     * 删除超限控制
     *
     * @param uid
     * @param operation
     */
    public void deleteOperation(@NotNull Long uid, @NotNull Integer operation) {
        int affectedRows = sysSecurityMapper.deleteByUidAndOperation(uid, operation);
        log.info("delete uid:{} operation{}, affectedRows:{}", uid, operation, affectedRows);
    }

    /**
     * 检查用户当天操作是否超限，如果超限则抛出异常
     *
     * @param uid 用户ID
     * @param operation 操作类型
     */
    public void checkOperationLimit10(@NotNull Long uid, @NotNull Integer operation, int limit) {
        if (isOperationExceededToday(uid, operation, limit)) {
            log.error("operation exceeded daily limit, uid:{}, operation:{}", uid, operation);
            throw ExceptionFactory.bizException(ErrorCode.RECORD_TOO_MANY);
        }
    }

    /**
     * 检查用户当天操作是否超限，如果超限则抛出异常
     *
     * @param uid 用户ID
     * @param operation 操作类型
     */
    public void checkOperationLimit10(@NotNull Long uid, @NotNull Integer operation) {
        this.checkOperationLimit10(uid, operation, DEFAULT_DAILY_LIMIT_10);
    }

    /**
     * 检查用户当天操作是否超限，如果超限则抛出异常
     *
     * @param uid 用户ID
     * @param operation 操作类型
     */
    public void checkOperationLimit100(@NotNull Long uid, @NotNull Integer operation) {
        this.checkOperationLimit10(uid, operation, DEFAULT_DAILY_LIMIT_100);
    }

}

