package com.seezoon.domain.service.user;

import com.seezoon.domain.dao.mapper.UserInfoMapper;
import com.seezoon.domain.dao.po.UserInfoPO;
import com.seezoon.domain.service.user.support.PasswordEncoder;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 用户服务
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Validated
@Transactional
public class UserInfoService {

    private final UserInfoMapper userInfoMapper;

    public String resetSecretKey(@NotNull Long uid) {
        UserInfoPO userInfoPO = userInfoMapper.selectByPrimaryKeyForUpdate(uid);
        if (userInfoPO == null) {
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }
        String secretKey = this.genSecretKey();
        userInfoPO.setSecretKey(secretKey);
        userInfoPO.setUpdateTime(LocalDateTime.now());
        int affectedRow = this.userInfoMapper.updateByPrimaryKey(userInfoPO);
        Assertion.affectedOne(affectedRow);
        return secretKey;
    }

    /**
     * 根据用户ID重置密码
     *
     * @param uid 用户ID
     * @param newPassword 新密码
     */
    public void resetPasswordByUid(@NotNull Long uid, @NotEmpty String newPassword) {
        UserInfoPO userInfoPO = userInfoMapper.selectByPrimaryKeyForUpdate(uid);
        if (userInfoPO == null) {
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }

        userInfoPO.setPassword(PasswordEncoder.encode(newPassword));
        userInfoPO.setUpdateTime(LocalDateTime.now());

        int affectedRows = userInfoMapper.updateByPrimaryKey(userInfoPO);
        Assertion.affectedOne(affectedRows);

        log.info("user reset password success uid:{}", uid);
    }


    public String genSecretKey() {
        return RandomStringUtils.insecure().randomAlphanumeric(32);
    }

}
