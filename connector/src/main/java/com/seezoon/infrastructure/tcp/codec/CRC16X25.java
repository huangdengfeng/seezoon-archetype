package com.seezoon.infrastructure.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CRC16X25 {

    // 预计算的 CRC-16/X.25 查表（使用反射输入/输出）
    private static final int[] CRC_TABLE = new int[256];

    static {
        // 多项式 0x1021，但因为是反射（LSB first），实际使用 0x8408（即 0x1021 的位反转）
        final int POLYNOMIAL = 0x8408; // 反射后的多项式
        for (int i = 0; i < 256; i++) {
            int crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) != 0) {
                    crc = (crc >>> 1) ^ POLYNOMIAL;
                } else {
                    crc >>>= 1;
                }
            }
            CRC_TABLE[i] = crc & 0xFFFF;
        }
    }

    /**
     * 计算 CRC-16/X.25 校验值
     *
     * @param data 输入字节数组
     * @return CRC-16/X.25 校验值（16位，无符号）
     */
    public static int calculateCRC(byte[] data) {
        int crc = 0xFFFF; // 初始值

        for (byte b : data) {
            // 输入字节按位反转（LSB first）：Java 中 byte 转为无符号后查表
            int idx = (crc ^ (b & 0xFF)) & 0xFF;
            crc = (crc >>> 8) ^ CRC_TABLE[idx];
        }

        // 输出反转 + 最终异或 0xFFFF
        // 由于查表法已隐含处理了反射，最后只需异或 0xFFFF
        crc ^= 0xFFFF;
        return crc & 0xFFFF;
    }

    // 测试示例
    public static void main1(String[] args) {
        // 测试向量：例如 "123456789" 的 CRC-16/X.25 应为 0x906E
        byte[] test = "123456789".getBytes();
        int crc = calculateCRC(test);
        System.out.printf("CRC-16/X.25 of '123456789': 0x%04X%n", crc);
        // 输出应为: 0x906E
    }

    public static void main(String[] args) {
//        byte[] bytes1 = {(byte) 0X92, (byte) 0Xf0};
        byte[] bytes1 = {(byte) 0X83, (byte) 0X79};
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes1);
        System.out.println(byteBuf.readUnsignedShort());

        System.out.println(Integer.toHexString(30737));
        System.out.println(String.format("%02X", 30737));

        byte[] bytes = new byte[]{0X11, 0X01, 0X07, 0X52, 0X53, 0X36, 0X78, (byte) 0X90, 0X02, 0X42, 0X70, 0X00, 0X32,
                0X01, 0X00, 0X05};
        System.out.println(bytes.length);
        System.out.println(Integer.toHexString(CRC16X25.calculateCRC(bytes)));
    }
}