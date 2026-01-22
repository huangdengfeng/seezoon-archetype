package com.seezoon.infrastructure.configuration.context;

import com.seezoon.infrastructure.exception.Assertion;
import lombok.Getter;

@Getter
public class SecurityContext {

    private Long uid;

    public SecurityContext(Long uid) {
        Assertion.notNull(uid, "uid can not be null");
        this.uid = uid;
    }
}
