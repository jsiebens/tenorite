/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tenorite.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.tenorite.clients.ClientsRegistry;

import java.nio.charset.Charset;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * @author Johan Siebens
 */
public final class TetrinetServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final DelimiterAppender DELIMITER_APPENDER = new DelimiterAppender();

    private static final StringDecoder DECODER = new StringDecoder(Charset.forName("Cp1252"));

    private static final StringEncoder ENCODER = new StringEncoder(Charset.forName("Cp1252"));

    private final ClientsRegistry clientsRegistry;

    public TetrinetServerInitializer(ClientsRegistry clientsRegistry) {
        this.clientsRegistry = clientsRegistry;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel
            .pipeline()
            .addLast(new DelimiterBasedFrameDecoder(8192, wrappedBuffer(new byte[]{(byte) 0xFF})))
            .addLast(DECODER)
            .addLast(DELIMITER_APPENDER)
            .addLast(ENCODER)
            .addLast(new TetrinetServerHandler(clientsRegistry))
        ;
    }

    @Sharable
    private static class DelimiterAppender extends MessageToByteEncoder<ByteBuf> {

        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
            out.writeBytes(msg);
            out.writeBytes(wrappedBuffer(new byte[]{(byte) 0xFF}));
        }

    }

}
