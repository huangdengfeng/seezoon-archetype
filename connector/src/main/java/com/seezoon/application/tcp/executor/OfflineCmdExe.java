package com.seezoon.application.tcp.executor;

import com.seezoon.application.tcp.dto.OfflineCmd;
import com.seezoon.application.tcp.dto.ServerRespCO;
import com.seezoon.domain.valueobj.OfflineVO;
import com.seezoon.infrastructure.tcp.codec.Cmd;
import com.seezoon.infrastructure.tcp.common.RpcContext;
import com.seezoon.infrastructure.tcp.handler.MessageHandler;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 终端离线，服务端逻辑
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OfflineCmdExe implements MessageHandler<OfflineCmd, ServerRespCO> {

    private final ApplicationEventPublisher publisher;

    @Override
    public short requestCmd() {
        return Cmd.System_Offline;
    }

    @Override
    public short responseCmd() {
        return Cmd.System_Offline;
    }

    @Override
    public ServerRespCO execute(OfflineCmd request, RpcContext rpcContext) {
        String deviceNo = rpcContext.getDeviceNo();
        log.info("receive offline cmd deviceNo:{}", deviceNo);
        OfflineVO offlineVO = new OfflineVO(request.getDeviceId(), LocalDateTime.now());
        publisher.publishEvent(offlineVO);
        return null;
    }

}
