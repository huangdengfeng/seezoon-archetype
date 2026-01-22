package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.SysUserRolePO;
import com.seezoon.domain.dao.po.SysUserRolePO.Condition;
import java.util.List;

public interface SysUserRoleMapper {

    /**
     * 根据用户ID删除所有角色关联
     */
    int deleteByUid(Integer uid);

    /**
     * 根据用户ID和角色代码删除
     */
    int deleteByUidAndRole(Integer uid, String role);

    /**
     * 插入用户角色关联
     */
    int insert(SysUserRolePO row);

    /**
     * 批量插入用户角色关联
     */
    int batchInsert(List<SysUserRolePO> rows);

    /**
     * 根据用户ID查询所有角色
     */
    List<SysUserRolePO> selectByUid(Integer uid);

    /**
     * 根据条件查询
     */
    List<SysUserRolePO> selectByCondition(Condition condition);
}

