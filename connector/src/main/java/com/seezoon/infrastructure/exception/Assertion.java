package com.seezoon.infrastructure.exception;


import org.apache.commons.lang3.StringUtils;

/**
 * Assertion utility class that assists in validating arguments.
 */
public abstract class Assertion {

    public static void isTrue(boolean expression, String msg) {
        if (!expression) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void notNull(Object object, String msg) {
        if (object == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void notEmpty(String object, String msg) {
        if (StringUtils.isEmpty(object)) {
            throw new IllegalArgumentException(msg);
        }
    }
    
    public static void affectedOne(int affectedRows) {
        isTrue(affectedRows == 1, "expect affected one,actual " + affectedRows);
    }
}
