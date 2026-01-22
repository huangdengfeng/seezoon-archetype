package com.seezoon.infrastructure.configuration.context;

import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.tcp.session.Session;

public class SecurityContextHolder {

    private static ThreadLocal<Session> threadLocal = new ThreadLocal<>();

    public static Session getSession() {
        return threadLocal.get();
    }

    public static void setSession(Session session) {
        Assertion.notNull(session, "session is null");
        threadLocal.set(session);
    }

    public static void clear() {
        threadLocal.remove();
    }
}
