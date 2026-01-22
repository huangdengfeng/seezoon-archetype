package com.seezoon.infrastructure.properties;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceProperties {

    /**
     * 单位S
     */
    @Min(1)
    private long heartbeatTimeout = 10 * 60;
    /**
     * 清理下线任务周期，单位毫秒，默认{@link com.seezoon.infrastructure.constants.Constants#OFFLINE_CLEANER_INTERVAL}
     */
    private long offlineCleanerInterval;
    /**
     * 上报批次处理大小
     */
    @Min(1)
    private int batchSize = 200;


    /**
     * 上报处理最大队列
     */
    @Min(1)
    private int maxQueueSize = 10000;
}
