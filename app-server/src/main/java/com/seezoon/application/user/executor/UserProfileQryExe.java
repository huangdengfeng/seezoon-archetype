package com.seezoon.application.user.executor;

import com.seezoon.application.user.dto.clientobject.UserProfileCO;
import com.seezoon.domain.dao.mapper.UserInfoMapper;
import com.seezoon.domain.dao.mapper.UserProfileMapper;
import com.seezoon.domain.dao.po.UserInfoPO;
import com.seezoon.domain.dao.po.UserProfilePO;
import com.seezoon.infrastructure.configuration.context.SecurityContextHolder;
import com.seezoon.infrastructure.exception.Assertion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class UserProfileQryExe {

    private final UserProfileMapper userProfileMapper;
    private final UserInfoMapper userInfoMapper;

    public UserProfileCO execute() {
        Long uid = SecurityContextHolder.getUid();
        Assertion.notNull(uid, "uid is null");
        UserProfilePO po = userProfileMapper.selectByPrimaryKey(uid);
        UserInfoPO userInfoPO = userInfoMapper.selectByPrimaryKey(uid);
        UserProfileCO co = new UserProfileCO();
        co.setUid(po.getUid());
        co.setUsername(userInfoPO.getUsername());
        co.setNickName(po.getNickName());
        co.setName(po.getName());
        co.setMobile(po.getMobile());
        co.setAvatar(po.getAvatar());
        co.setEmail(po.getEmail());
        co.setBirthday(po.getBirthday());
        co.setAddress(po.getAddress());
        co.setCreateTime(po.getCreateTime());
        co.setHasPassword(StringUtils.isNotEmpty(userInfoPO.getPassword()));
        return co;
    }
}
