package net.tenorite.tournament.actors;

import akka.actor.Props;
import net.tenorite.channel.actors.AbstractChannelActor;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.GameMode;
import net.tenorite.game.Player;
import net.tenorite.game.PlayingStats;
import net.tenorite.protocol.PlineMessage;
import net.tenorite.tournament.TournamentMatch;
import net.tenorite.tournament.events.TournamentGameFinished;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * @author Johan Siebens
 */
public final class TournamentChannelActor extends AbstractChannelActor {

    public static Props props(Tempo tempo, GameMode gameMode, TournamentMatch match) {
        return Props.create(TournamentChannelActor.class, tempo, gameMode, match);
    }

    private TournamentMatch tournamentMatch;

    public TournamentChannelActor(Tempo tempo, GameMode gameMode, TournamentMatch tournamentMatch) {
        super(tempo, gameMode, true);
        this.tournamentMatch = tournamentMatch;
    }

    @Override
    protected Optional<SlotReservationFailed> validateReservation(ReserveSlot rs) {
        return tournamentMatch.hasPlayer(rs.getName()) ? Optional.empty() : Optional.of(SlotReservationFailed.channelIsFull());
    }

    @Override
    protected void sendWelcomeMessage(Slot slot) {
        slot.send(PlineMessage.of(""));
        slot.send(PlineMessage.of(format("Hello <b>%s</b>, welcome to match <b>%s</b>", slot.getName(), tournamentMatch.getId())));
        slot.send(PlineMessage.of(""));
    }

    @Override
    protected boolean canStartGame(Slot moderator) {
        if (tournamentMatch.getState().equals(TournamentMatch.State.FINISHED)) {
            moderator.send(PlineMessage.of("<red>match is already finished!</red>"));
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    protected void onEndGame(Game game, List<PlayingStats> ranking) {
        Player winner = ranking.get(0).getPlayer();
        tournamentMatch = tournamentMatch.incrScore(game, winner.getName());
        publish(TournamentGameFinished.of(tournamentMatch, game, ranking));
    }

}
