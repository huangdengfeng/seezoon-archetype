package com.seezoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
public class BaseApplicationTest {

    @Autowired
    protected JsonMapper jsonMapper;

}

