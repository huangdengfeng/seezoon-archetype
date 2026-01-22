package com.seezoon.domain.valueobj;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PingVO {

    private long deviceId;
    /**
     * ACC 开关
     */
    private Byte acc;
    /**
     * 是否接通外部电源(0:否,1:是)
     */
    private Byte externalPower;
    /**
     * 电压
     */
    private byte voltageLevel;
    /**
     * gms信号强度
     */
    private byte gsmLevel;

    /**
     * 时间
     */
    private LocalDateTime timestamp;


}
