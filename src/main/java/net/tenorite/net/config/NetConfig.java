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
package net.tenorite.net.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.tenorite.clients.ClientsRegistry;
import net.tenorite.net.TetrinetServerInitializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Johan Siebens
 */
@Configuration
public class NetConfig {

    private static final boolean USE_EPOLL = System.getProperty("os.name").contains("Linux") && System.getProperty("os.arch").contains("amd64");

    private static final int TETRINET_PORT = 31457;

    @Autowired
    private ClientsRegistry clientsRegistry;

    @Bean(destroyMethod = "shutdownGracefully")
    public EventLoopGroup eventLoopGroup() {
        return USE_EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    private Class<? extends ServerSocketChannel> socketChannelClass() {
        return USE_EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    @Bean
    public InitializingBean tetrinetServer() {
        EventLoopGroup group = eventLoopGroup();

        return () ->
            new ServerBootstrap()
                .group(group)
                .channel(socketChannelClass())
                .childHandler(new TetrinetServerInitializer(clientsRegistry))
                .bind(TETRINET_PORT)
                .sync().get();
    }

}
