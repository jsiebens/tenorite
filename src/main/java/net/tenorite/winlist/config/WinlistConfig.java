package net.tenorite.winlist.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import net.tenorite.winlist.WinlistRepository;
import net.tenorite.winlist.actors.WinlistActor;
import net.tenorite.winlist.repository.MongoWinlistRepository;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WinlistConfig {

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private Jongo jongo;

    @Bean
    public WinlistRepository winlistRepository() {
        return new MongoWinlistRepository(jongo);
    }

    @Bean(name = "winlistActor")
    public ActorRef winlistActor() {
        return actorSystem.actorOf(WinlistActor.props(winlistRepository()));
    }

}
