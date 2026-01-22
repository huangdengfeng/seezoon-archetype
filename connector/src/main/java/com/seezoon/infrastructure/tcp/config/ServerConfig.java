package com.seezoon.infrastructure.tcp.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServerConfig {

    /**
     * 监听ip，默认任意IP
     */
    private String ip = "0.0.0.0";
    /**
     * 监听端口
     */
    private int port = 9000;
    /**
     * 空闲超时时间，单位秒,一般为心跳时间的2.5-3 倍较好，小于等于0则不空闲超时
     */
    private int idleTimeout = 450;
    /**
     * 建链接线程
     */
    private int bossThreads = 1;
    /**
     * 默认cpu * 2
     */
    private int ioThreads = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * 最大链接数
     */
    private int maxConnections = 102400;

}
