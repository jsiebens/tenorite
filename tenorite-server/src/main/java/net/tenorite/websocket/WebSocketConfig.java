package net.tenorite.websocket;

import akka.actor.ActorSystem;
import net.tenorite.channel.actors.ChannelsActors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ActorSystem system;

    private final ChannelsActors channels;

    @Autowired
    public WebSocketConfig(ActorSystem system, ChannelsActors channels) {
        this.system = system;
        this.channels = channels;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(spectateWebSocketHandler(), "/ws/spectate")
            .withSockJS()
            .setInterceptors(spectateHandshakeInterceptor());
    }

    @Bean
    public SpectateWebSocketHandler spectateWebSocketHandler() {
        return new SpectateWebSocketHandler(system, channels);
    }

    @Bean
    public SpectateHandshakeInterceptor spectateHandshakeInterceptor() {
        return new SpectateHandshakeInterceptor();
    }

}
