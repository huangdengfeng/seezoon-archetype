package com.seezoon.application.tcp.dto;

import com.seezoon.infrastructure.tcp.codec.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PingCmd implements Serializer {

    /**
     * acc
     */
    private byte acc;
    /**
     * 是否接通外部电源(0:否,1:是)
     */
    private byte externalPower;
    /**
     * 电压
     */
    private byte voltageLevel;
    /**
     * gms信号强度
     */
    private byte gsmLevel;
    /**
     * 语言：2 字节
     */
    private byte[] language = new byte[2];

    @Override
    public PingCmd deserialize(byte[] data) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
        byte statusByte = byteBuf.readByte();
        this.acc = (byte) ((statusByte & 0b10) >> 1);
        this.externalPower = (byte) ((statusByte & 0b100) >> 2);
        this.voltageLevel = byteBuf.readByte();
        this.gsmLevel = byteBuf.readByte();
        byteBuf.readBytes(language);
        return this;
    }
}
