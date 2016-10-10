package net.tenorite.tournament.actors;

import akka.actor.Props;
import net.tenorite.core.Tempo;
import net.tenorite.tournament.TournamentMatch;
import net.tenorite.tournament.TournamentRepository;
import net.tenorite.tournament.events.TournamentGameFinished;
import net.tenorite.util.AbstractActor;

/**
 * @author Johan Siebens
 */
public final class TournamentsActor extends AbstractActor {

    private final TournamentRepository tournamentRepository;

    public static Props props(TournamentRepository tournamentRepository) {
        return Props.create(TournamentsActor.class, tournamentRepository);
    }

    public TournamentsActor(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        subscribe(TournamentGameFinished.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof TournamentGameFinished) {
            handleTournamentMatchFinished((TournamentGameFinished) message);
        }
    }

    private void handleTournamentMatchFinished(TournamentGameFinished tmf) {
        String winner = tmf.getRanking().get(0).getPlayer().getName();

        TournamentMatch match = tmf.getTournamentMatch();
        Tempo tempo = tmf.getGame().getTempo();
        tournamentRepository.tournamentOps(tempo).saveTournamentMatch(match);

        if (match.getState().equals(TournamentMatch.State.FINISHED)) {
            tournamentRepository.tournamentOps(tempo).incrScore(match.getTournament(), winner);
            if (tournamentRepository.tournamentOps(tempo).nrOfUnfinishedMatches(match.getTournament(), match.getRound()) == 0) {
                tournamentRepository.tournamentOps(tempo).openTournamentRound(match.getTournament(), match.getRound() + 1);
            }
        }
    }

}
