package net.tenorite.badges.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.actors.BadgesActor;
import net.tenorite.badges.repository.MongoBadgeRepository;
import net.tenorite.game.GameModes;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BadgesConfig {

    @Autowired
    private Jongo jongo;

    @Autowired
    private ActorSystem system;

    @Autowired
    private GameModes gameModes;

    @Bean
    public BadgeRepository badgeRepository() {
        return new MongoBadgeRepository(jongo);
    }

    @Bean
    public ActorRef badgesActor() {
        return system.actorOf(BadgesActor.props(gameModes, badgeRepository()), "badges");
    }

}
