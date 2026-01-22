package com.seezoon.domain.valueobj;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @param deviceId
 * @param timestamp 时间
 */
public record OfflineVO(long deviceId, LocalDateTime timestamp) {

    public OfflineVO(long deviceId, LocalDateTime timestamp) {
        this.deviceId = deviceId;
        this.timestamp = Objects.requireNonNull(timestamp);
    }

   
}
