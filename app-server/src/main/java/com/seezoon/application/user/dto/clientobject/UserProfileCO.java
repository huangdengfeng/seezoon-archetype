package com.seezoon.application.user.dto.clientobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seezoon.infrastructure.constants.Constants;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileCO {

    private Long uid;
    private String username;
    private String nickName;

    private String name;

    private String mobile;

    private String avatar;

    private String email;

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate birthday;

    private String address;

    @JsonFormat(pattern = Constants.DATETIME_PATTERN)
    private LocalDateTime createTime;

    private boolean hasPassword;
}
