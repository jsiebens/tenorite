package net.tenorite.channel.config;

import akka.actor.ActorSystem;
import net.tenorite.channel.actors.ChannelsActors;
import net.tenorite.game.GameModes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChannelsConfig {

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private GameModes gameModes;

    @Bean
    public ChannelsActors channelsActors() {
        return new ChannelsActors(actorSystem, gameModes);
    }

}
