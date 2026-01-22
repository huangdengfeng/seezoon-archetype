package com.seezoon.infrastructure.tcp.session;

import com.seezoon.infrastructure.exception.Assertion;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeviceInfo {

    private final Long deviceId;
    private final String deviceNo;

    public DeviceInfo(Long deviceId, String deviceNo) {
        Assertion.notNull(deviceId, "deviceId not null");
        Assertion.notEmpty(deviceNo, "deviceNo is empty");
        this.deviceId = deviceId;
        this.deviceNo = deviceNo;
    }
}
