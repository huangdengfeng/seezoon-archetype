package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.UserRefreshTokenPO;
import com.seezoon.domain.dao.po.UserRefreshTokenPO.Condition;
import java.time.LocalDateTime;
import java.util.List;

public interface UserRefreshTokenMapper {

    int deleteByPrimaryKey(Long id);

    int deleteByExpireToken(LocalDateTime now);

    int deleteByUidAndClientId(Long uid, String clientId);

    int insert(UserRefreshTokenPO row);

    UserRefreshTokenPO selectByPrimaryKey(Long id);

    UserRefreshTokenPO selectByRefreshTokenId(String refreshTokenId);

    List<UserRefreshTokenPO> selectByUidAndClientIdForUpdate(Long uid, String clientId);

    List<UserRefreshTokenPO> selectByCondition(Condition condition);

    int updateByPrimaryKeySelective(UserRefreshTokenPO row);

    int updateByPrimaryKey(UserRefreshTokenPO row);
}