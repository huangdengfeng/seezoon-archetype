package com.seezoon.application.tcp.executor;

import com.seezoon.application.tcp.dto.LoginCmd;
import com.seezoon.application.tcp.dto.ServerRespCO;
import com.seezoon.infrastructure.tcp.codec.Cmd;
import com.seezoon.infrastructure.tcp.common.RpcContext;
import com.seezoon.infrastructure.tcp.handler.MessageHandler;
import com.seezoon.infrastructure.tcp.session.DeviceInfo;
import com.seezoon.infrastructure.tcp.session.Session;
import com.seezoon.infrastructure.tcp.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 终端登录处理
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LoginCmdExe implements MessageHandler<LoginCmd, ServerRespCO> {

    @Override
    public short requestCmd() {
        return Cmd.C_Login;
    }

    @Override
    public short responseCmd() {
        return Cmd.C_Login;
    }

    @Override
    public ServerRespCO execute(LoginCmd request, RpcContext rpcContext) {
        String deviceNo = request.getDeviceNo();
        log.info("receive login cmd deviceNo:{}", deviceNo);
        // TODO 验证后得到ID
        Long deviceId = 1L;
        // 创建Session
        Session session = new Session(new DeviceInfo(deviceId, deviceNo), rpcContext.getChannel());
        SessionManager.getInstance().add(deviceNo, session);
        ServerRespCO resp = new ServerRespCO();
        return resp;
    }
}
