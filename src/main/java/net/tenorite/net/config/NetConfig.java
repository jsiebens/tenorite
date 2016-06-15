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
