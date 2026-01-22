package com.seezoon.application.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seezoon.infrastructure.constants.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * 更新用户信息
 */
@Getter
@Setter
public class UpdateUserProfileCmd {

    @Schema(description = "用户名")
    @Size(max = 64, message = "用户名")
    private String username;

    @Schema(description = "昵称")
    @Size(max = 64, message = "昵称长度不能超过64")
    private String nickName;

    @Schema(description = "姓名")
    @Size(max = 64, message = "姓名长度不能超过64")
    private String name;

    @Schema(description = "头像，提交文件上传后的ID")
    @Size(max = 255, message = "头像地址长度不能超过255")
    private String avatar;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128")
    private String email;

    @Schema(description = "生日")
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate birthday;

    @Schema(description = "地址")
    @Size(max = 255, message = "地址长度不能超过255")
    private String address;
}

