package com.seezoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

/**
 * 测试基类
 * <p>
 * 使用 @Transactional 确保测试后自动回滚，不会留下测试数据
 * <p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BaseApplicationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JsonMapper jsonMapper;

}

