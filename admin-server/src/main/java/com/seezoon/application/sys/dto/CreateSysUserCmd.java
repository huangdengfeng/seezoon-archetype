package com.seezoon.application.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建系统用户
 */
@Getter
@Setter
public class CreateSysUserCmd {

    @Schema(description = "用户名")
    @NotBlank
    @Size(max = 50)
    private String userName;

    @Schema(description = "密码")
    @NotBlank
    @Size(max = 100)
    private String password;

    @Schema(description = "姓名")
    @NotBlank
    @Size(max = 50)
    private String name;

    @Schema(description = "手机号")
    @Size(max = 20)
    private String mobile;

    @Schema(description = "邮箱")
    @Size(max = 50)
    private String email;

    @Schema(description = "照片")
    @Size(max = 100)
    private String photo;

    @Schema(description = "备注")
    @Size(max = 200)
    private String remark;

    @Schema(description = "角色代码集合")
    @NotNull
    private Set<String> roles = Collections.emptySet();
}

