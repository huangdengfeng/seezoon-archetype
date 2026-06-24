package com.seezoon.infrastructure.tcp.handler;

import com.seezoon.infrastructure.configuration.context.SpringContextHolder;
import com.seezoon.infrastructure.tcp.codec.ProtocolMessage;
import com.seezoon.infrastructure.tcp.codec.Serialization;
import com.seezoon.infrastructure.tcp.codec.Serializer;
import com.seezoon.infrastructure.tcp.common.OfflineEvent;
import com.seezoon.infrastructure.tcp.common.RpcContext;
import com.seezoon.infrastructure.tcp.session.DeviceInfo;
import com.seezoon.infrastructure.tcp.session.Session;
import com.seezoon.infrastructure.utils.OtelUtils;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

/**
 * 也可以响应读写事件 ，使用ChannelDuplexHandler
 */
@Slf4j
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    private final AtomicInteger sequence = new AtomicInteger(0);

    private final DispatcherHandler dispatcher;
    private final ExecutorService workerPool;

    public NettyServerHandler(DispatcherHandler dispatcher) {
        Assert.notNull(dispatcher, "dispatcher must not be null");
        this.dispatcher = dispatcher;
        this.workerPool = OtelUtils.wrap(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * 断开链接事件
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = getSession(ctx);
        if (null != session) {
            // 发送离线事件
            ApplicationEventPublisher publisher = SpringContextHolder.getBean(ApplicationEventPublisher.class);
            DeviceInfo deviceInfo = session.getDeviceInfo();
            publisher.publishEvent(new OfflineEvent(deviceInfo.getDeviceId(), LocalDateTime.now()));
            session.invalidate();
            log.debug("channelInactive device:{},channel:{}", session.getDeviceInfo(), ctx.channel());
        }
        ctx.close();
        log.debug("channelInactive channel:{}", ctx.channel());
        super.channelInactive(ctx);
    }

    /**
     * 握手完成
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelActive channel:{}", ctx.channel());
        super.channelActive(ctx);
    }

    /**
     * 响应netty 心跳  这个可以给客户端发送一些特定包 来标识
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Session session = getSession(ctx);
            if (null != session) {
                session.invalidate();
                log.debug("userEventTriggered device:{} channel:{}", session.getDeviceInfo(), ctx.channel());
            }
            log.debug("userEventTriggered channel:{}", ctx.channel());
            ctx.close();
        }
        super.userEventTriggered(ctx, evt);
    }

    private void doChannelRead(ChannelHandlerContext ctx, ProtocolMessage msg) {
        Session session = getSession(ctx);
        // 鉴权
        if (msg.isNeedAuth() && null == session) {
            log.error("not authorized channel:{} msg:{}", ctx.channel(), msg);
            ctx.close();
            return;
        }

        MessageHandler messageHandler = dispatcher.dispatch(msg.getCmd());
        // 服务端读取客户端请求
        if (messageHandler != null) {
            long start = System.currentTimeMillis();
            Serializer request = Serialization.deserialize(msg.getBody(), messageHandler.getRequestType());
            RpcContext rpcContext = new RpcContext(ctx.channel());
            if (session != null) {
                DeviceInfo deviceInfo = session.getDeviceInfo();
                rpcContext.setDeviceId(deviceInfo.getDeviceId());
                rpcContext.setDeviceNo(deviceInfo.getDeviceNo());
            }
            Serializer response = messageHandler.execute(request, rpcContext);
            if (null == response) {
                return;
            }
            byte[] serialized = Serialization.serialize(response);

            ProtocolMessage protocolMessage = new ProtocolMessage(messageHandler.responseCmd(), serialized,
                    this.nextSequence());
            ctx.writeAndFlush(protocolMessage);

            if (log.isInfoEnabled()) {
                log.info("server channelRead request:{}, channelWrite response:{},use {} ms channel:{}", request,
                        response, System.currentTimeMillis() - start, ctx.channel());
            }
        } else { // 客户端响应
            // 终端一般没有固定的requestId ，所以用命令字当requestId来回调
            ResponseFuture responseFuture = FutureManager.getInstance()
                    .remove(ctx.channel().id().asLongText(), msg.getRequestId());
            if (responseFuture == null) { // 没有找到消息
                log.warn("server channelRead no mapping cmd:{},cmd hex:{},msg:{},channel:{}", msg.getCmd(),
                        Integer.toHexString(msg.getCmd()), msg, ctx.channel());
                return;
            }
            Object response = Serialization.deserialize(msg.getBody(), responseFuture.getClazz());
            if (log.isDebugEnabled()) {
                log.debug("server channelRead response:{} msg:{},channel:{}", response, msg, ctx.channel());
            }
            responseFuture.getFuture().complete(response);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg) throws Exception {
        try {
            workerPool.execute(() -> {
                try {
                    this.doChannelRead(ctx, msg);
                } catch (Exception e) {
                    log.error("server channelRead exception:{}", msg, e);
                    ctx.close();
                }
            });
        } catch (RejectedExecutionException e) {
            log.error("server channelRead reject msg:{},channel:{}", msg, ctx.channel());
            throw e;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught channel:{}", ctx.channel(), cause);
        ctx.close();
    }


    private Session getSession(ChannelHandlerContext ctx) {
        Session session = ctx.channel().attr(Session.key).get();
        return session;
    }

    private int nextSequence() {
        // 无符号short 最大值后，从0 开始
        return sequence.getAndUpdate(current ->
                (current >= 0xFFFF) ? 0 : current + 1
        );
    }
}
