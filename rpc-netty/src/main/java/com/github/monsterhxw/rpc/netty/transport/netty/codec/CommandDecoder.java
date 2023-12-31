package com.github.monsterhxw.rpc.netty.transport.netty.codec;

import com.github.monsterhxw.rpc.netty.transport.command.Command;
import com.github.monsterhxw.rpc.netty.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author huangxuewei
 * @since 2023/12/27
 */
public abstract class CommandDecoder extends ByteToMessageDecoder {

    private static final int LENGTH_FIELD_SIZE = 4; // unit: byte

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inByteBuf, List<Object> outList) throws Exception {
        if (!inByteBuf.isReadable(LENGTH_FIELD_SIZE)) {
            return;
        }
        inByteBuf.markReaderIndex();
        int lengthFieldValue = inByteBuf.readInt();
        int dataLength = lengthFieldValue - LENGTH_FIELD_SIZE;
        if (inByteBuf.readableBytes() < dataLength) {
            inByteBuf.resetReaderIndex();
            return;
        }

        Header header = decodeHeader(inByteBuf);
        int payloadLength = dataLength - header.length();
        byte[] payload = new byte[payloadLength];
        inByteBuf.readBytes(payload);

        Command command = new Command(header, payload);

        outList.add(command);
    }

    protected abstract Header decodeHeader(ByteBuf inByteBuf);
}
