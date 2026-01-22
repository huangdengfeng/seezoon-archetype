package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.UserOauthPO;
import com.seezoon.domain.dao.po.UserOauthPO.Condition;
import java.util.List;

public interface UserOauthMapper {

    int deleteByPrimaryKey(Long uid, Byte oauthType, String oauthId);

    int deleteByUid(Long uid);

    int insert(UserOauthPO row);

    UserOauthPO selectByPrimaryKey(Long uid, Byte oauthType, String oauthId);

    /**
     * 根据oauth类型和oauthId查询
     */
    UserOauthPO selectByOauthTypeAndOauthId(Byte oauthType, String oauthId);

    List<UserOauthPO> selectByCondition(Condition condition);

    int updateByPrimaryKeySelective(UserOauthPO row);

    int updateByPrimaryKey(UserOauthPO row);
}

