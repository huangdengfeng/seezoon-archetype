package com.seezoon.infrastructure.tcp.session;

import com.seezoon.infrastructure.exception.Assertion;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端session 管理
 */
@Slf4j
public class SessionManager {

    private final static SessionManager instance = new SessionManager();
    private final Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;
    private final String threadName = "TcpSessionManager-Cleaner";
    /**
     * 1 分钟清理一次
     */
    private final long interval = Duration.ofMinutes(1).toMillis();

    private SessionManager() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, threadName);
            thread.setDaemon(true);
            return thread;
        });

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                sessionMap.entrySet().removeIf(e -> e.getValue().isInvalidated());
            } catch (Throwable e) {
                log.error(threadName + " sdk scheduler error", e);
            }
        }, interval, interval, TimeUnit.MILLISECONDS);
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public Session get(String sessionId) {
        Assertion.notEmpty(sessionId, "sessionId is empty");
        return sessionMap.get(sessionId);
    }

    public void add(String sessionId, Session newSession) {
        Assertion.notEmpty(sessionId, "sessionId is empty");
        Assertion.notNull(newSession, "newSession is null");
        Session oldSession = sessionMap.put(sessionId, newSession);
        if (oldSession != null) {
            oldSession.invalidate();
        }
    }

    public void remove(String sessionId) {
        Assertion.notEmpty(sessionId, "sessionId is empty");
        Session removed = sessionMap.remove(sessionId);
        if (removed != null) {
            removed.invalidate();
        }
    }

    public void invalidateAll() {
        sessionMap.forEach((sessionId, session) -> {
            session.invalidate();
        });
    }
}
