package com.seezoon.domain.service.user;

import com.seezoon.domain.dao.mapper.UserInfoMapper;
import com.seezoon.domain.dao.mapper.UserProfileMapper;
import com.seezoon.domain.dao.po.UserInfoPO;
import com.seezoon.domain.dao.po.UserProfilePO;
import com.seezoon.domain.dao.types.UserInfoStatus;
import com.seezoon.domain.service.user.support.PasswordEncoder;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 用户密码验证服务
 *
 * @author huangdengfeng
 * @date 2023/9/10 23:30
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Validated
@Transactional(readOnly = true)
public class UserPasswdVerifyService {

    private final UserInfoMapper userInfoMapper;
    private final UserProfileMapper userProfileMapper;

    public boolean verify(@NotEmpty String username, @NotEmpty String password) {
        UserInfoPO userInfoPO = userInfoMapper.selectByUsername(username);
        if (userInfoPO != null) {
            return this.verify(userInfoPO.getUid(), password);
        }
        UserProfilePO userProfilePO = userProfileMapper.selectByMobile(username);
        if (userProfilePO != null) {
            return this.verify(userProfilePO.getUid(), password);
        }
        return false;
    }

    public boolean verify(@NotEmpty Long uid, @NotEmpty String password) {
        UserInfoPO po = userInfoMapper.selectByPrimaryKey(uid);
        if (po == null) {
            return false;
        }
        if (UserInfoStatus.isLocked(po.getStatus())) {
            throw ExceptionFactory.bizException(ErrorCode.USER_STATUS_LOCKED);
        }
        if (UserInfoStatus.isInvalid(po.getStatus())) {
            throw ExceptionFactory.bizException(ErrorCode.USER_STATUS_INVALID);
        }
        String userPassword = po.getPassword();
        if (StringUtils.isEmpty(userPassword)) {
            return false;
        }
        if (PasswordEncoder.matches(password, userPassword)) {
            return true;
        }
        return false;
    }
}
