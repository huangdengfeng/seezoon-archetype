package com.seezoon.domain.service.user;

import com.seezoon.domain.dao.mapper.UserInfoMapper;
import com.seezoon.domain.dao.mapper.UserOauthMapper;
import com.seezoon.domain.dao.mapper.UserProfileMapper;
import com.seezoon.domain.dao.po.UserInfoPO;
import com.seezoon.domain.dao.po.UserOauthPO;
import com.seezoon.domain.dao.po.UserProfilePO;
import com.seezoon.domain.dao.types.UserInfoStatus;
import com.seezoon.domain.dao.types.UserOauthType;
import com.seezoon.domain.service.user.vo.LoginTokenVO;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 登录服务
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
@Validated
public class LoginService {

    private final UserInfoService userInfoService;
    private final LoginTokenService loginTokenService;
    private final UserInfoMapper userInfoMapper;
    private final UserOauthMapper userOauthMapper;
    private final UserProfileMapper userProfileMapper;

    /**
     * 用户名登录
     *
     * @param userName
     * @return
     */
    public LoginTokenVO loginByUserName(@NotEmpty String userName) {
        UserInfoPO userPO = userInfoMapper.selectByUsername(userName);
        if (userPO == null) {
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }
        Byte userStatus = userPO.getStatus();
        checkUserStatus(userStatus);
        return loginTokenService.createToken(userPO.getUid(), userPO.getSecretKey());
    }


    /**
     * Oauth 登录
     *
     * @param oauthType not null
     * @param oauthId not empty
     * @param unionId optional
     * @return
     */
    public LoginTokenVO loginByOauth(@NotNull UserOauthType oauthType, @NotEmpty String oauthId, String unionId) {
        UserOauthPO oauthPO = userOauthMapper.selectByOauthTypeAndOauthId(oauthType.getCode(), oauthId);
        if (null != oauthPO) {
            // 更新unionId
            if (StringUtils.isEmpty(oauthPO.getUnionId()) && StringUtils.isNotEmpty(unionId)) {
                oauthPO.setUnionId(unionId);
                oauthPO.setUpdateTime(LocalDateTime.now());
                Assertion.affectedOne(this.userOauthMapper.updateByPrimaryKey(oauthPO));
            }
            UserInfoPO userPO = userInfoMapper.selectByPrimaryKey(oauthPO.getUid());
            Byte userStatus = userPO.getStatus();
            this.checkUserStatus(userStatus);
            return loginTokenService.createToken(userPO.getUid(), userPO.getSecretKey());
        }
        // 注册
        LocalDateTime now = LocalDateTime.now();
        UserInfoPO user = new UserInfoPO();
        user.setSecretKey(userInfoService.genSecretKey());
        user.setStatus(UserInfoStatus.VALID.getCode());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        int affectedRows = userInfoMapper.insert(user);
        Assertion.affectedOne(affectedRows);
        Assertion.notNull(user.getUid(), "uid is null");

        // 保存oauth 信息
        UserOauthPO po = new UserOauthPO();
        po.setUid(user.getUid());
        po.setOauthType(oauthType.getCode());
        po.setOauthId(oauthId);
        po.setUnionId(unionId);
        po.setCreateTime(now);
        po.setUpdateTime(now);
        Assertion.affectedOne(userOauthMapper.insert(po));

        // 保存用户信息
        UserProfilePO userProfilePO = new UserProfilePO();
        userProfilePO.setUid(user.getUid());
        userProfilePO.setCreateTime(now);
        userProfilePO.setUpdateTime(now);
        Assertion.affectedOne(userProfileMapper.insert(userProfilePO));
        return loginTokenService.createToken(user.getUid(), user.getSecretKey());
    }

    /**
     * 退出登录
     *
     * @param uid
     */
    public void logout(@NotNull Long uid) {
        loginTokenService.logout(uid);
    }


    /**
     * 刷新token
     *
     * @param refreshToken
     * @return
     */
    public LoginTokenVO refresh(@NotEmpty String refreshToken) {
        return this.loginTokenService.refreshToken(refreshToken);
    }


    private void checkUserStatus(Byte userStatus) {
        // 停用
        if (UserInfoStatus.isInvalid(userStatus)) {
            throw ExceptionFactory.bizException(ErrorCode.USER_STATUS_INVALID);
        }
        // 锁定
        if (UserInfoStatus.isLocked(userStatus)) {
            throw ExceptionFactory.bizException(ErrorCode.USER_STATUS_LOCKED);
        }
    }

}
