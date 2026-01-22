package com.seezoon.infrastructure.tcp;

import com.seezoon.BaseApplicationTest;
import com.seezoon.application.tcp.dto.ServerRespCO;
import com.seezoon.infrastructure.tcp.codec.Cmd;
import com.seezoon.infrastructure.tcp.codec.ProtocolMessage;
import com.seezoon.infrastructure.tcp.codec.Serializer;
import com.seezoon.infrastructure.tcp.handler.DispatcherHandler;
import com.seezoon.infrastructure.tcp.transport.NettyClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 客户端向服务端发消息示例
 */
public class ClientSendToServerTest extends BaseApplicationTest {

    private static final String ip = "127.0.0.1";
    private static final int port = 8000;
    protected NettyClient nettyClient;
    @Autowired
    @Qualifier("clientDispatcherHandler")
    private DispatcherHandler dispatcherHandler;

    @BeforeEach
    public void setUp() {
        nettyClient = new NettyClient(ip, port, dispatcherHandler);
        nettyClient.connect();
    }

    @AfterEach
    public void tearDown() {
        nettyClient.stop();
    }

    @Test
    public void send() {
        ProtocolMessage protocolMessage = new ProtocolMessage(Cmd.C_Login, new byte[]{},
                nettyClient.nextSequence());
        ServerRespCO send = nettyClient.send(protocolMessage, ServerRespCO.class);
    }

    public static class ClientResp implements Serializer {

    }
}
