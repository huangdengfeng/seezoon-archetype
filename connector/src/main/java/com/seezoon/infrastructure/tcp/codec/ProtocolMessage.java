package com.seezoon.infrastructure.tcp.codec;

import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProtocolMessage {

    // 长度为1时候
    public static final byte[] MAGIC_0X78 = new byte[]{0x78, 0x78};
    // 长度为2的时候
    public static final byte[] MAGIC_0X79 = new byte[]{0x79, 0x79};
    public static final byte[] STOP_BIT = new byte[]{0x0D, 0x0A};
    private static final int ONE_BYTE_MAX_LENGTH = 0xFF;
    /**
     * 包长度（1或2字节，小端）
     */
    private int length;
    /**
     * 协议号（1字节,小端）
     */
    private short cmd;
    /**
     * 信息内容（N字节，按需定义，这里用byte[]）
     */
    private byte[] body;
    /**
     * 信息序列号（2字节，小端）
     */
    private int seqNo;

    /**
     * 可选，方便服务端向客户端发送消息后等回包。
     *
     * 一般设备不用，是一问一大模式，可以通过命令字代表
     */
    private int requestId;

    public ProtocolMessage(short cmd, byte[] body, int seqNo) {
        this.body = Objects.requireNonNull(body);
        // 长度 = 协议号(1) + 内容 + 序号(2) + crc (2)
        this.length = body.length + 1 + 2 + 2;
        this.cmd = cmd;
        this.seqNo = seqNo;
        // 通常是一问一答，用同样的命令字
        this.setRequestId(cmd);
    }

    /**
     * 信息内容长度是1字节表示
     *
     * @return
     */
    public boolean isLengthByte() {
        return body.length <= ONE_BYTE_MAX_LENGTH;
    }

    /**
     * 是否需要登录
     *
     * @return
     */
    public boolean isNeedAuth() {
        return cmd != Cmd.C_Login;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}
