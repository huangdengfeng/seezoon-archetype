package com.seezoon.domain.service.user.vo;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息值对象
 */
@Getter
@Setter
public class UserProfileVO {

    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickName;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 地址
     */
    private String address;
}

