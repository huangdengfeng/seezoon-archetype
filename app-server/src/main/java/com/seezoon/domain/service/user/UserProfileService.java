package com.seezoon.domain.service.user;

import static com.seezoon.infrastructure.exception.Assertion.affectedOne;

import com.seezoon.domain.dao.mapper.UserInfoMapper;
import com.seezoon.domain.dao.mapper.UserProfileMapper;
import com.seezoon.domain.dao.po.UserInfoPO;
import com.seezoon.domain.dao.po.UserProfilePO;
import com.seezoon.domain.service.user.vo.UserProfileVO;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Slf4j
@Service
@Validated
@Transactional
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final UserInfoMapper userInfoMapper;

    public void update(@NotNull Long uid, String nickname, String headImgId) {
        UserProfilePO po = userProfileMapper.selectByPrimaryKeyForUpdate(uid);
        if (po == null) {
            log.error("user profile not found, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }
        po.setNickName(nickname);
        po.setAvatar(headImgId);
        po.setUpdateTime(LocalDateTime.now());
        affectedOne(userProfileMapper.updateByPrimaryKey(po));
        log.info("user profile updated, uid:{}, nickname:{},headImgId:{}", uid, nickname, headImgId);
    }

    /**
     * 更新用户手机号
     *
     * @param uid 用户ID
     * @param mobile 手机号
     * @return 是否更新成功
     */
    public void updateMobile(@NotNull Long uid, @NotEmpty String mobile) {
        UserProfilePO po = userProfileMapper.selectByPrimaryKeyForUpdate(uid);
        if (po == null) {
            log.error("user profile not found, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }
        if (Objects.equals(mobile, po.getMobile())) {
            return;
        }
        // 检查手机号是否已被其他用户使用，占用就解绑
        UserProfilePO existingProfile = userProfileMapper.selectByMobile(mobile);
        if (existingProfile != null && !Objects.equals(existingProfile.getUid(), uid)) {
            log.error("mobile already used by another user, mobile:{}, uid:{}", mobile, existingProfile.getUid());
            existingProfile.setMobile(null);
            existingProfile.setUpdateTime(LocalDateTime.now());
            affectedOne(userProfileMapper.updateByPrimaryKey(existingProfile));
        }

        po.setMobile(mobile);
        po.setUpdateTime(LocalDateTime.now());
        affectedOne(userProfileMapper.updateByPrimaryKey(po));
    }

    /**
     * 更新用户手机号
     *
     * @param uid 用户ID
     * @param email 邮箱
     * @return 是否更新成功
     */
    public void updateEmail(@NotNull Long uid, @NotEmpty String email) {
        UserProfilePO po = userProfileMapper.selectByPrimaryKeyForUpdate(uid);
        if (po == null) {
            log.error("user profile not found, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }
        if (Objects.equals(email, po.getEmail())) {
            return;
        }
        // 检查手机号是否已被其他用户使用，占用就解绑
        UserProfilePO existingProfile = userProfileMapper.selectByEmail(email);
        if (existingProfile != null && !Objects.equals(existingProfile.getUid(), uid)) {
            log.error("mobile already used by another user, email:{}, uid:{}", email, existingProfile.getUid());
            existingProfile.setMobile(null);
            existingProfile.setUpdateTime(LocalDateTime.now());
            affectedOne(userProfileMapper.updateByPrimaryKey(existingProfile));
        }

        po.setEmail(email);
        po.setUpdateTime(LocalDateTime.now());
        affectedOne(userProfileMapper.updateByPrimaryKey(po));
    }

    /**
     * 更新用户信息
     *
     * @param uid 用户ID
     * @param vo 用户信息
     */
    public void updateProfile(@NotNull Long uid, @Valid @NotNull UserProfileVO vo) {
        UserProfilePO po = userProfileMapper.selectByPrimaryKeyForUpdate(uid);
        if (po == null) {
            log.error("user profile not found, uid:{}", uid);
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }
        if (StringUtils.isNotEmpty(vo.getUsername())) {
            UserInfoPO userInfoPO = userInfoMapper.selectByUsername(vo.getUsername());
            if (userInfoPO != null && !Objects.equals(uid, userInfoPO.getUid())) {
                throw ExceptionFactory.bizException(ErrorCode.USERNAME_EXISTS);
            }

            UserInfoPO my = userInfoMapper.selectByPrimaryKey(uid);
            my.setUsername(vo.getUsername());
            my.setUpdateTime(LocalDateTime.now());
            affectedOne(userInfoMapper.updateByPrimaryKey(my));
        }
        po.setNickName(vo.getNickName());
        po.setName(vo.getName());
        po.setAvatar(vo.getAvatar());
        po.setBirthday(vo.getBirthday());
        po.setAddress(vo.getAddress());
        po.setUpdateTime(LocalDateTime.now());

        affectedOne(userProfileMapper.updateByPrimaryKey(po));
        log.info("update user profile success, uid:{}", uid);
    }
} 