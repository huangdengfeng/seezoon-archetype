package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.SysFilePO;
import com.seezoon.domain.dao.po.SysFilePO.Condition;
import java.util.List;

public interface SysFileMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysFilePO row);

    SysFilePO selectByPrimaryKey(Long id);

    List<SysFilePO> selectByCondition(Condition condition);
}

