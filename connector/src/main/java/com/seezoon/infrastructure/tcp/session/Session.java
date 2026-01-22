package com.seezoon.infrastructure.tcp.session;

import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.tcp.codec.ProtocolMessage;
import com.seezoon.infrastructure.tcp.codec.Serializer;
import com.seezoon.infrastructure.tcp.handler.FutureManager;
import com.seezoon.infrastructure.tcp.handler.ResponseFuture;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务器session
 */
@Slf4j
public class Session {

    public final static AttributeKey<Session> key = AttributeKey.valueOf("session");
    /**
     * session 超时时间，单位S，默认180s
     */
    private final int timeout = 30 * 60;
    /**
     * 等待回包超时，单位毫秒ms
     */
    private final int defaultReadTimeout = 1000 * 10;
    /**
     * 客户端通道
     */
    private final Channel channel;

    /**
     * 链接创建时间
     */
    private final LocalDateTime createTime;
    private final AtomicInteger sequence = new AtomicInteger(0);
    /**
     * 鉴权通过后
     */
    private final DeviceInfo deviceInfo;
    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessTime;
    /**
     * 失效
     */
    private boolean invalidated;

    public Session(DeviceInfo deviceInfo, Channel channel) {
        Assertion.notNull(deviceInfo, "deviceInfo must not be null");
        Assertion.notNull(channel, "channel must not be null");
        this.deviceInfo = deviceInfo;
        this.channel = channel;
        channel.attr(key).set(this);
        this.createTime = LocalDateTime.now();
        this.lastAccessTime = LocalDateTime.now();
    }

    /**
     * 销毁
     */
    public void invalidate() {
        this.invalidated = true;
        if (null != channel && channel.isOpen()) {
            channel.close();
        }
    }

    /**
     * 是否过期
     *
     * @return
     */
    public boolean isInvalidated() {
        boolean result = invalidated || Duration.between(lastAccessTime, LocalDateTime.now()).getSeconds() > timeout;
        if (result) {
            log.info("session invalidated device={}, createTime={}, lastAccessTime={}", deviceInfo, createTime,
                    lastAccessTime);
        }
        return result;
    }

    public int nextSequence() {
        // 无符号short 最大值后，从0 开始
        return sequence.getAndUpdate(current ->
                (current >= 0xFFFF) ? 0 : current + 1
        );
    }

    public DeviceInfo getDeviceInfo() {
        this.lastAccessTime = LocalDateTime.now();
        return deviceInfo;
    }


    public <T extends Serializer> T send(ProtocolMessage msg, Class<T> responseClazz) {
        return this.send(msg, responseClazz, defaultReadTimeout);
    }

    public <T> T send(ProtocolMessage msg, Class<T> responseClazz, long timeout) {
        Assertion.notNull(msg, "send msg must not be null");
        Assertion.notNull(responseClazz, "responseClazz must not be null");
        if (channel == null) {
            throw ExceptionFactory.bizException(ErrorCode.DEVICE_OFFLINE);
        }
        if (!channel.isActive()) {
            log.error("send failed, channel is closed,msg {}", msg);
            throw ExceptionFactory.bizException(ErrorCode.NET_CHANNEL_CLOSED);
        }

        CompletableFuture<T> future = new CompletableFuture<>();
        FutureManager.getInstance().add(channel.id().asLongText(), msg.getRequestId(),
                new ResponseFuture(responseClazz, future, timeout));
        channel.writeAndFlush(msg);
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("get response failed channel:{},msg:{}", channel, msg, e);
            throw ExceptionFactory.bizException(ErrorCode.NET_MESSAGE_READ_TIMEOUT);
        }
    }
}
