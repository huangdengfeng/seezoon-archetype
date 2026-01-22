package com.seezoon.infrastructure.tcp.common;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 上下文，部分协议需要通过这个传递协议中的字段，比如消息序号等
 */
@Getter
@Setter
@ToString
public class RpcContext {

    private final Channel channel;
    /**
     * 以下信息登录后才有
     */
    private String deviceNo;
    private Long deviceId;

    public RpcContext(Channel channel) {
        this.channel = channel;
    }

}
