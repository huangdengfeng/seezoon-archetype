package com.seezoon.application.sys.dto;

import com.seezoon.infrastructure.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取用户列表
 */
@Getter
@Setter
public class UserPageQry extends PageQuery {

    @Schema(description = "用户ID")
    private Integer uid;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "姓名（模糊查询）")
    private String fuzzyName;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "状态：1.正常;2.停用;3.锁定")
    private Byte status;

    @Schema(description = "排序字段")
    private String sortBy;
    @Schema(description = "排序方式")
    private String orderBy;

}




