package net.tenorite.stats.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import net.tenorite.stats.PlayerStatsRepository;
import net.tenorite.stats.actors.PlayingStatsActor;
import net.tenorite.stats.repository.MongoPlayerStatsRepository;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerStatsConfig {

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private Jongo jongo;

    @Bean
    public PlayerStatsRepository playerStatsRepository() {
        return new MongoPlayerStatsRepository(jongo);
    }

    @Bean
    public ActorRef playerStatsActor() {
        return actorSystem.actorOf(PlayingStatsActor.props(playerStatsRepository()));
    }

}
