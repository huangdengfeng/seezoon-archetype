package com.seezoon.application.sys.executor;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageSerializable;
import com.google.common.base.CaseFormat;
import com.seezoon.application.sys.dto.UserPageQry;
import com.seezoon.application.sys.dto.clientobject.UserCO;
import com.seezoon.domain.dao.mapper.SysUserMapper;
import com.seezoon.domain.dao.po.SysUserPO;
import com.seezoon.infrastructure.dto.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 获取系统用户列表
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class SysUserPageQryExe {

    private final SysUserMapper sysUserMapper;

    public Page<UserCO> execute(@Valid @NotNull UserPageQry qry) {
        SysUserPO.Condition condition = new SysUserPO.Condition();
        condition.setUid(qry.getUid());
        condition.setUsername(qry.getUserName());
        condition.setFuzzyName(qry.getFuzzyName());
        condition.setMobile(qry.getMobile());
        condition.setStatus(qry.getStatus());
        condition.setIncludeSysAdmin(false);

        com.github.pagehelper.Page<Object> startPage = PageHelper.startPage(qry.getPage(), qry.getPageSize());
        if (StringUtils.isNotEmpty(qry.getOrderBy()) && StringUtils.isNotEmpty(qry.getSortBy())) {
            // 将驼峰命名转换为下划线命名（如：createTime -> create_time）
            String sortBy = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, qry.getSortBy());
            startPage.setOrderBy(sortBy + " " + qry.getOrderBy());
        }
        PageSerializable<SysUserPO> page = new PageSerializable<>(sysUserMapper.selectByCondition(condition));

        List<UserCO> data = new ArrayList<>();
        page.getList().forEach(item -> {
            UserCO co = new UserCO();
            co.setUid(item.getUid());
            co.setUsername(item.getUsername());
            co.setName(item.getName());
            co.setMobile(item.getMobile());
            co.setEmail(item.getEmail());
            co.setPhoto(item.getPhoto());
            co.setStatus(item.getStatus());
            co.setCreateTime(item.getCreateTime());
            co.setUpdateTime(item.getUpdateTime());
            co.setRemark(item.getRemark());
            data.add(co);
        });
        return new Page<>(page.getTotal(), data);
    }
}

