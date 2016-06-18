package net.tenorite.channel.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import net.tenorite.channel.actors.ChannelsActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChannelsConfig {

    @Autowired
    private ActorSystem actorSystem;

    @Bean(name = "channels")
    public ActorRef channels() {
        return actorSystem.actorOf(ChannelsActor.props());
    }

}
