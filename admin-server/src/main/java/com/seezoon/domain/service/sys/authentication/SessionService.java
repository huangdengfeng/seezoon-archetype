package com.seezoon.domain.service.sys.authentication;

import com.seezoon.domain.dao.mapper.SysSessionMapper;
import com.seezoon.domain.dao.po.SysSessionPO;
import com.seezoon.domain.service.sys.authentication.valueobj.SessionVO;
import com.seezoon.domain.service.sys.valueobj.UserVO;
import com.seezoon.infrastructure.constants.Constants;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 用户登录服务
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
@Validated
public class SessionService {

    private final ApplicationEventPublisher publisher;
    private final SysSessionMapper sysSessionMapper;

    @Transactional(readOnly = true)
    public UserVO getSessionData(@NotEmpty String sessionId) {
        SysSessionPO sysSessionPO = sysSessionMapper.selectByPrimaryKey(sessionId);
        if (null == sysSessionPO || StringUtils.isEmpty(sysSessionPO.getData())) {
            return null;
        }
        if (sysSessionPO.getExpireTime().isBefore(LocalDateTime.now())) {
            log.info("session expired at {} sessionId:{}", sysSessionPO.getExpireTime(), sessionId);
            return null;
        }
        UserVO userVO = JsonUtils.fromJson(sysSessionPO.getData(), UserVO.class);
        publisher.publishEvent(new SessionVO(sessionId));
        return userVO;
    }

    /**
     * 创建会话
     *
     * @param userVO 用户信息
     * @param maxInactiveInterval 最长不活跃时间（秒）
     * @return 会话ID
     */
    public String createSession(@NotNull UserVO userVO, long maxInactiveInterval) {
        // 检查用户当天创建的会话数量
        LocalDate today = LocalDate.now();
        long todayCount = sysSessionMapper.countByUidToday(userVO.getUid(), today);
        if (todayCount >= Constants.MAX_RECORD) {
            log.error("user create session exceeded daily limit, uid:{}, todayCount:{}, limit:{}", userVO.getUid(),
                    todayCount, Constants.MAX_RECORD);
            throw ExceptionFactory.bizException(ErrorCode.RECORD_TOO_MANY);
        }

        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusSeconds(maxInactiveInterval);

        SysSessionPO po = new SysSessionPO();
        po.setSessionId(sessionId);
        po.setUid(userVO.getUid());
        po.setMaxInactiveInterval((int) maxInactiveInterval);
        po.setExpireTime(expireTime);
        po.setCreateTime(now);
        po.setLastAccessTime(now);
        po.setData(JsonUtils.toJson(userVO));
        int affectedRows = sysSessionMapper.insert(po);
        Assertion.affectedOne(affectedRows);
        log.info("create session success, sessionId:{}, uid:{}", sessionId, userVO.getUid());
        return sessionId;
    }

    /**
     * 销毁会话
     *
     * @param sessionId 会话ID
     */
    public void destroySession(@NotEmpty String sessionId) {
        int affectedRows = sysSessionMapper.deleteByPrimaryKey(sessionId);
        if (affectedRows > 0) {
            log.info("destroy session success, sessionId:{}", sessionId);
        } else {
            log.warn("session not found, sessionId:{}", sessionId);
        }
    }

    /**
     * 更新会话（更新最后访问时间和过期时间）
     *
     * @param sessionId 会话ID
     */
    public void updateSession(@NotEmpty String sessionId) {
        SysSessionPO po = sysSessionMapper.selectByPrimaryKey(sessionId);
        if (po == null) {
            log.warn("session not found, sessionId:{}", sessionId);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusSeconds(po.getMaxInactiveInterval());

        po.setLastAccessTime(now);
        po.setExpireTime(expireTime);

        int affectedRows = sysSessionMapper.updateByPrimaryKey(po);
        Assertion.affectedOne(affectedRows);
    }

    /**
     * 批量更新会话访问时间
     *
     * @param sessionIds 会话ID集合
     */
    public void batchUpdateByAccess(@NotEmpty Set<String> sessionIds) {
        LocalDateTime now = LocalDateTime.now();
        int affectedRows = sysSessionMapper.batchUpdateByAccess(sessionIds, now);
        log.debug("batch update sessions by access success, count:{}, affectedRows:{}", sessionIds.size(),
                affectedRows);
    }

    /**
     * 清理过期会话
     *
     * @return 清理的会话数量
     */
    public int cleanExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = sysSessionMapper.deleteExpiredSessions(now);
        if (deletedCount > 0) {
            log.info("clean expired sessions success, deleted count:{}", deletedCount);
        }
        return deletedCount;
    }

    /**
     * 根据用户ID销毁所有会话
     *
     * @param uid 用户ID
     * @return 销毁的会话数量
     */
    public int destroySessionsByUid(@NotNull Integer uid) {
        List<SysSessionPO> sessions = sysSessionMapper.selectByUid(uid);
        int count = 0;
        for (SysSessionPO session : sessions) {
            sysSessionMapper.deleteByPrimaryKey(session.getSessionId());
            count++;
        }
        if (count > 0) {
            log.info("destroy sessions by uid success, uid:{}, count:{}", uid, count);
        }
        return count;
    }

}
