package com.seezoon.domain.dao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootApplication
@SpringBootTest
@MapperScan("com.seezoon.domain.dao.mapper")
public class BaseApplicationTest {

}
