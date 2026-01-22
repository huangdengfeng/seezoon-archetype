package com.seezoon;

import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;


public class Main {


    public static void main(String[] args) throws RunnerException, ParseException {
        org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

        // forks
        options.addOption("f", true, "forks default 1");
        // 线程数
        options.addOption("t", true, "thread default 1");
        // 迭代次数
        options.addOption("it", true, "iterations default 1");
        // 每轮迭代时长，单位S
        options.addOption("d", true, "duration(S) default 10s");
        // 类名全称或者正则 选择指定的跑
        options.addOption("i", true, "include default all");

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("help", options);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        final String f = cmd.getOptionValue("f");
        final String t = cmd.getOptionValue("t");
        final String it = cmd.getOptionValue("it");
        final String d = cmd.getOptionValue("d");
        final String i = cmd.getOptionValue("i");

        int forks = StringUtils.isEmpty(f) ? 1 : Integer.parseInt(f);
        int threads = StringUtils.isEmpty(t) ? 1 : Integer.parseInt(t);
        int iterations = StringUtils.isEmpty(it) ? 1 : Integer.parseInt(it);
        int duration = StringUtils.isEmpty(d) ? 10 : Integer.parseInt(d);

        final OptionsBuilder optionsBuilder = new OptionsBuilder();
        optionsBuilder.mode(Mode.Throughput);
        //  P99
        // optionsBuilder.mode(Mode.SampleTime);
        if (StringUtils.isNotEmpty(i)) {
            optionsBuilder.include(i);
        }
        optionsBuilder.forks(forks);
        optionsBuilder.threads(threads);
        optionsBuilder.warmupIterations(1);
        // 一次迭代后间隔时间
        optionsBuilder.warmupTime(TimeValue.seconds(1));
        optionsBuilder.measurementIterations(iterations);
        optionsBuilder.shouldFailOnError(true);
        // 一次迭代后间隔时间
        optionsBuilder.measurementTime(TimeValue.seconds(duration));
        optionsBuilder.timeUnit(TimeUnit.MILLISECONDS);
        Options opt = optionsBuilder.build();
        final Runner runner = new Runner(opt);
        runner.run();
    }


}
