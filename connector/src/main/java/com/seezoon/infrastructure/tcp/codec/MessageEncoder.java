package com.seezoon.infrastructure.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码
 */
public class MessageEncoder extends MessageToByteEncoder<ProtocolMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtocolMessage protocolMessage, ByteBuf out)
            throws Exception {
        // 魔数
        ByteBuf tempBuf = Unpooled.buffer(protocolMessage.getLength());
        if (protocolMessage.isLengthByte()) {
            out.writeBytes(ProtocolMessage.MAGIC_0X78);
            tempBuf.writeByte(protocolMessage.getLength());
        } else {
            out.writeBytes(ProtocolMessage.MAGIC_0X79);
            tempBuf.writeShort(protocolMessage.getLength());
        }
        tempBuf.writeByte(protocolMessage.getCmd());
        tempBuf.writeBytes(protocolMessage.getBody());
        tempBuf.writeShort(protocolMessage.getSeqNo());
        // CRC16
        byte[] crcData = new byte[tempBuf.readableBytes()];
        tempBuf.getBytes(0, crcData);
        out.writeBytes(tempBuf);
        out.writeShort(CRC16X25.calculateCRC(crcData));
        out.writeBytes(ProtocolMessage.STOP_BIT);
    }
}
