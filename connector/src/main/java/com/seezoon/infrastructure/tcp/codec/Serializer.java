package com.seezoon.infrastructure.tcp.codec;

/**
 * 序列化器
 *
 * @param
 */
public interface Serializer {

    default byte[] serialize() {
        throw new UnsupportedOperationException();
    }

    default <T> T deserialize(byte[] data) {
        throw new UnsupportedOperationException();
    }
}
