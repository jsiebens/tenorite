package net.tenorite.game.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import net.tenorite.game.GameRepository;
import net.tenorite.game.actors.GamesActor;
import net.tenorite.game.repository.MongoGameRepository;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {

    @Autowired
    private Jongo jongo;

    @Autowired
    private ActorSystem system;

    @Bean
    public GameRepository gameRepository() {
        return new MongoGameRepository(jongo);
    }

    @Bean
    public ActorRef gameActor() {
        return system.actorOf(GamesActor.props(gameRepository()));
    }

}
