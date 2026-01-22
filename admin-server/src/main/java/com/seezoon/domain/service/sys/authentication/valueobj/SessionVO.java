package com.seezoon.domain.service.sys.authentication.valueobj;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionVO {

    private String sessionId;

    public SessionVO(String sessionId) {
        this.sessionId = Objects.requireNonNull(sessionId);
    }
}
