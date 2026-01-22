package com.seezoon.cases;

import com.seezoon.domain.dao.mapper.SysParamMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@State(Scope.Benchmark)
@SpringBootApplication
@MapperScan(basePackages = "com.seezoon.domain.dao.mapper")
public class SpringDemo {

    private SysParamMapper sysParamMapper;

    @Setup(Level.Trial)
    public void setUp() {
        sysParamMapper = SpringApplication.run(SpringDemo.class).getBean(SysParamMapper.class);
    }

    @TearDown(Level.Trial)
    public void tearDown() {

    }

    @Benchmark
    public void test() {
        sysParamMapper.selectByPrimaryKey("");
    }
}
