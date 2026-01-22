package com.seezoon.application.event;

import com.seezoon.domain.valueobj.OfflineVO;
import com.seezoon.infrastructure.constants.Constants;
import com.seezoon.infrastructure.properties.AppProperties;
import com.seezoon.infrastructure.properties.DeviceProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OfflineEventListener implements ApplicationListener<PayloadApplicationEvent<OfflineVO>> {

    private final Queue<OfflineVO> queue = new ConcurrentLinkedQueue<>();
    private final int batchSize;
    private final int maxQueueSize;

    public OfflineEventListener(AppProperties appProperties) {
        DeviceProperties device = appProperties.getDevice();
        this.batchSize = device.getBatchSize();
        this.maxQueueSize = device.getMaxQueueSize();
    }

    // 定时器，每隔一定时间刷一次
    @Scheduled(fixedDelayString = Constants.BATCH_SUBMIT_INTERVAL)
    public void scheduledFlush() {
        if (!queue.isEmpty()) {
            try {
                flush();
            } catch (Throwable e) {
                log.error("batch flush ping error", e);
            }
        }
    }

    @Override
    public void onApplicationEvent(PayloadApplicationEvent<OfflineVO> event) {
        OfflineVO payload = event.getPayload();
        queue.add(payload);
        int size = queue.size();
        if (size >= maxQueueSize) {
            queue.clear();
            log.warn("offlineQueue size:{} maxQueueSize limit:{} discard queue", size, maxQueueSize);
        }
        if (size >= batchSize) {
            flush();
        }
    }


    public void flush() {
        List<OfflineVO> vos = new ArrayList<>();
        while (!queue.isEmpty() && vos.size() <= batchSize) {
            OfflineVO offlineVO = queue.poll();
            // 没有了也批量一次
            if (null == offlineVO) {
                break;
            }
            vos.add(offlineVO);
        }
        if (!vos.isEmpty()) {
            // TODO
        }
    }
}
