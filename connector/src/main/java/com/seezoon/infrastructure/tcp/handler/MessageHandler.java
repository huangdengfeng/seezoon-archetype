package com.seezoon.infrastructure.tcp.handler;

import com.seezoon.infrastructure.tcp.codec.Serializer;
import com.seezoon.infrastructure.tcp.common.RpcContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 消息处理器
 *
 * @param <Req> request
 * @param <Resp> response
 */
public interface MessageHandler<Req extends Serializer, Resp extends Serializer> {

    /**
     * 请求命令字
     *
     * @return
     */
    short requestCmd();

    /**
     * 应答命令字
     *
     * @return
     */
    short responseCmd();

    /**
     * 执行
     *
     * @param request
     * @param rpcContext
     * @return
     */
    Resp execute(Req request, RpcContext rpcContext);

    /**
     * 请求类型
     *
     * @return
     */
    default Class<Req> getRequestType() {
        final Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericInterfaces()[0])
                .getActualTypeArguments();
        return (Class<Req>) actualTypeArguments[0];
    }

    /**
     * 应答类型
     *
     * @return
     */
    default Class<Resp> getResponseType() {
        final Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericInterfaces()[0])
                .getActualTypeArguments();
        return (Class<Resp>) actualTypeArguments[1];
    }

    default boolean isServerHandler() {
        return true;
    }
}
