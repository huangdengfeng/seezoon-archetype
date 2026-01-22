package com.seezoon.infrastructure.tcp;

import com.seezoon.BaseApplicationTest;
import com.seezoon.infrastructure.tcp.codec.Cmd;
import com.seezoon.infrastructure.tcp.codec.ProtocolMessage;
import com.seezoon.infrastructure.tcp.codec.Serializer;
import com.seezoon.infrastructure.tcp.session.Session;
import com.seezoon.infrastructure.tcp.session.SessionManager;
import org.junit.jupiter.api.Test;

/**
 * 服务端向客户端发消息示例
 */
public class ServerSendToClientTest extends BaseApplicationTest {


    @Test
    public void send() {
        String sessionId = "deviceNo";
        Session session = SessionManager.getInstance().get(sessionId);
        ProtocolMessage protocolMessage = new ProtocolMessage(Cmd.S_Online, new byte[]{}, session.nextSequence());
        protocolMessage.setRequestId(Cmd.C_Online_Resp);
        ClientResp coObj = session.send(protocolMessage, ClientResp.class);
    }

    public static class ClientResp implements Serializer {

    }
}
