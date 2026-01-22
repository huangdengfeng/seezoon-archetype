package com.seezoon.application.tcp.dto;

import com.seezoon.infrastructure.tcp.codec.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@ToString
public class LoginCmd implements Serializer {

    /**
     * 设备编号：8 字节
     * <pre>
     * 例：IMEI 号为 123456789123456，则终端 ID 为：0x01 0x23 0x45
     * 0x67 0x89 0x12 0x34 0x56
     * </pre>
     */
    private String deviceNo;
    /**
     * 终端识别码：2 字节
     * 根据此识别码判断终端类型、型号
     */
    private int typeIdentifier;
    /**
     * 时区语言标志: 2 字节
     */
    private int timeZoneLanguageCode;

    public static void main(String[] args) {
        String deviceNo = "0010234567890";
        String withoutZero = StringUtils.stripStart(deviceNo, "0");
        System.out.println(withoutZero);
    }

    @Override
    public LoginCmd deserialize(byte[] data) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
        byte[] deviceNoBytes = new byte[8];
        byteBuf.readBytes(deviceNoBytes);
        // 去掉开头的0
        this.deviceNo = StringUtils.stripStart(Hex.encodeHexString(deviceNoBytes), "0");
        this.typeIdentifier = byteBuf.readUnsignedShort();
        this.timeZoneLanguageCode = byteBuf.readUnsignedShort();
        return this;
    }

    @Override
    public byte[] serialize() {
        ByteBuf byteBuf = Unpooled.buffer(8 + 2 + 2);
        try {
            byteBuf.writeBytes(Hex.decodeHex(deviceNo));
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
        byteBuf.writeShort(typeIdentifier);
        byteBuf.writeShort(timeZoneLanguageCode);
        return byteBuf.array();
    }

}
