package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.SysSecurityPO;
import com.seezoon.domain.dao.po.SysSecurityPO.Condition;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysSecurityMapper {

    int deleteByPrimaryKey(Long id);

    int deleteByUidAndOperation(@Param("uid") Long uid, @Param("operation") Integer operation);

    int insert(SysSecurityPO row);

    SysSecurityPO selectByPrimaryKey(Long id);

    List<SysSecurityPO> selectByCondition(Condition condition);

    /**
     * 统计用户当天指定操作类型的操作次数
     *
     * @param uid 用户ID
     * @param operation 操作类型
     * @param toDay 当天开始时间
     * @return 操作次数
     */
    long countByUidAndOperationToday(@Param("uid") Long uid, @Param("operation") Integer operation,
            @Param("toDay") LocalDate toDay);
}

