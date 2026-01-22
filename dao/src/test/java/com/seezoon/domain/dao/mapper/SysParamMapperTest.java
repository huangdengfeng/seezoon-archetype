package com.seezoon.domain.dao.mapper;


import com.seezoon.domain.dao.BaseApplicationTest;
import com.seezoon.domain.dao.po.SysParamPO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SysParamMapperTest extends BaseApplicationTest {

    @Autowired
    private SysParamMapper sysParamMapper;

    @Test
    void selectByPrimaryKey() {
        SysParamPO sysParamPO = sysParamMapper.selectByPrimaryKey("APP_UPGRADE_INFO");
        System.out.println(sysParamPO);
    }
}