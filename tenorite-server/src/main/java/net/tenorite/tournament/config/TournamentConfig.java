package net.tenorite.tournament.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import net.tenorite.game.GameModes;
import net.tenorite.tournament.TournamentRepository;
import net.tenorite.tournament.actors.TournamentChannelsActors;
import net.tenorite.tournament.actors.TournamentsActor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Johan Siebens
 */
@Configuration
public class TournamentConfig {

    @Bean
    public ActorRef tournamentsActor(ActorSystem system, TournamentRepository tournamentRepository) {
        return system.actorOf(TournamentsActor.props(tournamentRepository));
    }

    @Bean
    public TournamentChannelsActors tournamentChannelsActors(ActorSystem system, GameModes gameModes, TournamentRepository tournamentRepository) {
        return new TournamentChannelsActors(system, gameModes, tournamentRepository);
    }

}
