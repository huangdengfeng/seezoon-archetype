package com.seezoon.infrastructure.constants;

public class Constants {

    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String BATCH_SUBMIT_INTERVAL = "${app.device.batch-submit-interval:1500}";
    public static final String OFFLINE_CLEANER_INTERVAL = "${app.device.offline-cleaner-interval:60000}";

}
