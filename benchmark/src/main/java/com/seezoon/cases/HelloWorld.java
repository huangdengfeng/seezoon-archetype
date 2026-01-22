package com.seezoon.cases;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class HelloWorld {


    @TearDown(Level.Trial)
    public void tearDown() {

    }

    @Benchmark
    public void test() {
        System.out.println("hello jmh");
    }
}
