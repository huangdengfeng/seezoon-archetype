package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.UserProfilePO;
import com.seezoon.domain.dao.po.UserProfilePO.Condition;
import java.util.List;

public interface UserProfileMapper {

    UserProfilePO selectByMobile(String mobile);

    UserProfilePO selectByEmail(String email);

    int deleteByPrimaryKey(Long uid);

    int insert(UserProfilePO row);

    UserProfilePO selectByPrimaryKey(Long uid);

    UserProfilePO selectByPrimaryKeyForUpdate(Long uid);

    List<UserProfilePO> selectByCondition(Condition condition);

    int updateByPrimaryKeySelective(UserProfilePO row);

    int updateByPrimaryKey(UserProfilePO row);
}