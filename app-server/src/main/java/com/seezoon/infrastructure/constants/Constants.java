package com.seezoon.infrastructure.constants;

import java.time.format.DateTimeFormatter;

/**
 * @author dfenghuang
 * @date 2023/9/15 00:13
 */
public class Constants {

    public static final Integer SUPER_ADMIN_USER_ID = 1;
    public static final int MAX_RECORD = 1000;
    public static final String COMMA = ",";
    public static final String UNDERLINE = "_";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_PATTERN_NO_SECOND = "yyyy-MM-dd HH:mm";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_MONTH_PATTERN = "yyyy-MM";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATETIME_PATTERN);


}
