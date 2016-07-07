package net.tenorite.web.socket;

import akka.actor.ActorSystem;
import net.tenorite.game.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ActorSystem system;

    @Autowired
    private GameRepository gameRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(replayWebSocketHandler(), "/ws/replay")
            .withSockJS()
            .setInterceptors(replayHandshakeInterceptor());
    }

    @Bean
    public ReplayWebSocketHandler replayWebSocketHandler() {
        return new ReplayWebSocketHandler(system, gameRepository);
    }

    @Bean
    public HandshakeInterceptor replayHandshakeInterceptor() {
        return new ReplayHandshakeInterceptor();
    }

}
