package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.UserInfoPO;
import com.seezoon.domain.dao.po.UserInfoPO.Condition;
import java.util.List;

public interface UserInfoMapper {

    int deleteByPrimaryKey(Long uid);

    int insert(UserInfoPO row);

    UserInfoPO selectByPrimaryKey(Long uid);

    UserInfoPO selectByPrimaryKeyForUpdate(Long uid);

    UserInfoPO selectByUsername(String username);

    List<UserInfoPO> selectByCondition(Condition condition);

    int updateByPrimaryKeySelective(UserInfoPO row);

    int updateByPrimaryKey(UserInfoPO row);
}