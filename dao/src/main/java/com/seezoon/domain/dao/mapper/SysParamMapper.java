package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.SysParamPO;

public interface SysParamMapper {

    int deleteByPrimaryKey(String paramKey);

    int insert(SysParamPO row);

    SysParamPO selectByPrimaryKey(String paramKey);

    int updateByPrimaryKeySelective(SysParamPO row);

    int updateByPrimaryKey(SysParamPO row);
}

