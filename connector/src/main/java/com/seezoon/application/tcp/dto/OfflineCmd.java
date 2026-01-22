package com.seezoon.application.tcp.dto;

import com.seezoon.infrastructure.tcp.codec.Serializer;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OfflineCmd implements Serializer {

    private long deviceId;

    @Override
    public OfflineCmd deserialize(byte[] data) {
        this.deviceId = Long.valueOf(new String(data, StandardCharsets.UTF_8));
        return this;
    }
}
