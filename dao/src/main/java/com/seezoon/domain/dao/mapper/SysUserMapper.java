package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.SysUserPO;
import com.seezoon.domain.dao.po.SysUserPO.Condition;
import java.util.List;

public interface SysUserMapper {

    int deleteByPrimaryKey(Integer uid);

    int insert(SysUserPO row);

    SysUserPO selectByPrimaryKey(Integer uid);

    /**
     * 根据用户名查询用户信息
     */
    SysUserPO selectByUserName(String userName);

    List<SysUserPO> selectByCondition(Condition condition);

    int updateByPrimaryKeySelective(SysUserPO row);

    int updateByPrimaryKey(SysUserPO row);
}

