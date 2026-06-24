package com.seezoon.infrastructure.tcp.common;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @param deviceId
 * @param timestamp 时间
 */
public record OfflineEvent(long deviceId, LocalDateTime timestamp) {

    public OfflineEvent(long deviceId, LocalDateTime timestamp) {
        this.deviceId = deviceId;
        this.timestamp = Objects.requireNonNull(timestamp);
    }

   
}
