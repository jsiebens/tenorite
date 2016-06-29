package net.tenorite.game.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModes;
import net.tenorite.game.GameRepository;
import net.tenorite.game.actors.GamesActor;
import net.tenorite.game.repository.MongoGameRepository;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

    @Bean
    public GameModes gameModes(List<GameMode> modes) {
        return new GameModes(modes);
    }

}
