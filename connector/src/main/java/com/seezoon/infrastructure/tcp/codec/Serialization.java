package com.seezoon.infrastructure.tcp.codec;

import com.seezoon.infrastructure.exception.Assertion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public class Serialization {

    public static <T extends Serializer> byte[] serialize(T body) {
        Assert.notNull(body, "body must not be null");
        return body.serialize();
    }

    public static <T extends Serializer> T deserialize(byte[] body, Class<T> clazz) {
        Assertion.notNull(body, "body must not be null");
        Assertion.notNull(clazz, "clazz must not be null");
        T instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("serialize error", e);
            throw new RuntimeException(e);
        }
        T t = (T) instance.deserialize(body);
        return t;
    }
}
