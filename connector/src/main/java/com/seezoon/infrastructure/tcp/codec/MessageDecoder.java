package com.seezoon.infrastructure.tcp.codec;

import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    private static final int MIN_FRAME_SIZE = 10; // 最小帧长度 = 起始位（2） + 包长度（1） + 协议号（1） + 信息内容（0） + 序列号（2） + CRC（2） + 停止位（2）

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out)
            throws Exception {
        // 不够最小帧长度
        if (in.readableBytes() < MIN_FRAME_SIZE) {
            return;
        }
        in.markReaderIndex();
        // 魔数
        byte[] magic = new byte[2];
        in.readBytes(magic);
        int crcStartIndex = in.readerIndex();
        // 数据长度长度
        int length = 0;
        if (Arrays.equals(magic, ProtocolMessage.MAGIC_0X78)) {
            length = in.readUnsignedByte();
        } else if (Arrays.equals(magic, ProtocolMessage.MAGIC_0X79)) {
            length = in.readUnsignedShort();
        } else {
            log.error("magic {} not match", Hex.encodeHexString(magic));
            throw ExceptionFactory.bizException(ErrorCode.NET_MAGIC_NOT_MATCH);
        }

        // 数据长度 + 停止符号（2） 不够
        if (in.readableBytes() < length + 2) {
            in.resetReaderIndex();
            return;
        }
        // 协议号
        short cmd = in.readUnsignedByte();
        // 内容 = 长度 - 协议号(1) - 序号(2) - crc (2)
        byte[] content = new byte[length - 1 - 2 - 2];
        in.readBytes(content);
        // 序列号
        int seqNo = in.readUnsignedShort();
        // CRC check
        byte[] crcContent = new byte[in.readerIndex() - crcStartIndex];
        in.getBytes(crcStartIndex, crcContent);
        int crc16 = in.readUnsignedShort();
        if (CRC16X25.calculateCRC(crcContent) != crc16) {
            log.error("crc {} not match", crc16);
            throw ExceptionFactory.bizException(ErrorCode.NET_CRC_NOT_MATCH);
        }
        // 停止符号
        byte[] stopBit = new byte[2];
        in.readBytes(stopBit);
        if (!Arrays.equals(stopBit, ProtocolMessage.STOP_BIT)) {
            log.error("stop bit {} not match", Hex.encodeHexString(stopBit));
            throw ExceptionFactory.bizException(ErrorCode.NET_MAGIC_NOT_MATCH);
        }
        ProtocolMessage protocolMessage = new ProtocolMessage(cmd, content, seqNo);
        out.add(protocolMessage);
    }
}
