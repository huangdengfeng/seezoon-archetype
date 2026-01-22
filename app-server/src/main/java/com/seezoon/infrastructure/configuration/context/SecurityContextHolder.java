package com.seezoon.infrastructure.configuration.context;

public class SecurityContextHolder {

    private static ThreadLocal<SecurityContext> threadLocal = new ThreadLocal<>();

    public static SecurityContext get() {
        return threadLocal.get();
    }

    public static void set(SecurityContext context) {
        threadLocal.set(context);
    }

    public static Long getUid() {
        SecurityContext securityContext = threadLocal.get();
        if (securityContext == null) {
            return null;
        }
        return securityContext.getUid();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
