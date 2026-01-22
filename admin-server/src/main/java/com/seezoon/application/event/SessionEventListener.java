package com.seezoon.application.event;

import com.seezoon.domain.service.sys.authentication.SessionService;
import com.seezoon.domain.service.sys.authentication.valueobj.SessionVO;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SessionEventListener implements ApplicationListener<PayloadApplicationEvent<SessionVO>> {

    private final SessionService sessionService;
    private final Set<String> sessionIds = new HashSet<>();


    // 定时器，每隔一定时间刷一次
    @Scheduled(fixedDelay = 1000 * 60 * 1)
    public void scheduledFlush() {
        if (!sessionIds.isEmpty()) {
            try {
                sessionService.batchUpdateByAccess(sessionIds);
                sessionIds.clear();
            } catch (Throwable e) {
                log.error("batch flush session error", e);
            }
        }
    }

    @Override
    public void onApplicationEvent(PayloadApplicationEvent<SessionVO> event) {
        SessionVO payload = event.getPayload();
        sessionIds.add(payload.getSessionId());
    }

}
