package com.seezoon.application.user.executor;

import com.seezoon.application.user.dto.UpdateUserProfileCmd;
import com.seezoon.domain.service.user.UserProfileService;
import com.seezoon.domain.service.user.vo.UserProfileVO;
import com.seezoon.infrastructure.configuration.context.SecurityContextHolder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 更新用户信息
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class UpdateUserProfileCmdExe {

    private final UserProfileService userProfileService;

    public void execute(@Valid @NotNull UpdateUserProfileCmd cmd) {
        Long uid = SecurityContextHolder.getUid();
        UserProfileVO vo = new UserProfileVO();
        vo.setUsername(cmd.getUsername());
        vo.setNickName(cmd.getNickName());
        vo.setName(cmd.getName());
        vo.setAvatar(cmd.getAvatar());
        vo.setBirthday(cmd.getBirthday());
        vo.setAddress(cmd.getAddress());

        userProfileService.updateProfile(uid, vo);
    }
}
