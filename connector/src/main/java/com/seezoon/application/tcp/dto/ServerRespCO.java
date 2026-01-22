package com.seezoon.application.tcp.dto;

import com.seezoon.infrastructure.tcp.codec.Serializer;
import lombok.ToString;

@ToString
public class ServerRespCO implements Serializer {

    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    @Override
    public ServerRespCO deserialize(byte[] data) {
        return new ServerRespCO();
    }
}
