package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.SysSessionPO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;

public interface SysSessionMapper {

    int deleteByPrimaryKey(String sessionId);

    int insert(SysSessionPO row);

    SysSessionPO selectByPrimaryKey(String sessionId);

    /**
     * 根据用户ID查询会话列表
     */
    List<SysSessionPO> selectByUid(Integer uid);

    int updateByPrimaryKeySelective(SysSessionPO row);

    int updateByPrimaryKey(SysSessionPO row);

    /**
     * 访问后
     * @param sessionIds
     * @param now
     * @return
     */
    int batchUpdateByAccess(@Param("sessionIds") Set<String> sessionIds,@Param("now") LocalDateTime now);
    /**
     * 删除过期会话
     */
    int deleteExpiredSessions(LocalDateTime expireTime);

    /**
     * 统计用户当天创建的会话数量
     *
     * @param uid 用户ID
     * @param today 当天日期
     * @return 会话数量
     */
    long countByUidToday(@Param("uid") Integer uid, @Param("today") LocalDate today);
}

