package com.seezoon.application.tcp.executor;

import com.seezoon.application.tcp.dto.PingCmd;
import com.seezoon.application.tcp.dto.ServerRespCO;
import com.seezoon.domain.valueobj.PingVO;
import com.seezoon.infrastructure.tcp.codec.Cmd;
import com.seezoon.infrastructure.tcp.common.RpcContext;
import com.seezoon.infrastructure.tcp.handler.MessageHandler;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 终端心跳
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PingCmdExe implements MessageHandler<PingCmd, ServerRespCO> {

    private final ApplicationEventPublisher publisher;

    @Override
    public short requestCmd() {
        return Cmd.C_Ping;
    }

    @Override
    public short responseCmd() {
        return Cmd.C_Ping;
    }

    @Override
    public ServerRespCO execute(PingCmd request, RpcContext rpcContext) {
        String deviceNo = rpcContext.getDeviceNo();
        log.info("receive ping cmd deviceNo:{}", deviceNo);
        PingVO pingVO = new PingVO();
        pingVO.setDeviceId(rpcContext.getDeviceId());
        pingVO.setAcc(request.getAcc());
        pingVO.setExternalPower(request.getExternalPower());
        pingVO.setVoltageLevel(request.getVoltageLevel());
        pingVO.setGsmLevel(request.getGsmLevel());
        pingVO.setTimestamp(LocalDateTime.now());
        publisher.publishEvent(pingVO);
        ServerRespCO resp = new ServerRespCO();
        return resp;
    }

}
