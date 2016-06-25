package net.tenorite.winlist.actors;

import akka.actor.Props;
import net.tenorite.channel.events.ChannelJoined;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.GameMode;
import net.tenorite.game.Player;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;
import net.tenorite.util.AbstractActor;
import net.tenorite.util.ImmutableStyle;
import net.tenorite.winlist.WinlistItem;
import net.tenorite.winlist.WinlistRepository;
import net.tenorite.winlist.events.WinlistUpdated;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class WinlistActor extends AbstractActor {

    public static Props props(WinlistRepository repository) {
        return Props.create(WinlistActor.class, repository).withDispatcher("winlist-dispatcher");
    }

    private final WinlistRepository winlistRepository;

    public WinlistActor(WinlistRepository winlistRepository) {
        this.winlistRepository = winlistRepository;
    }

    @Override
    public void preStart() throws Exception {
        subscribe(GameFinished.class);
        subscribe(ChannelJoined.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof GameFinished) {
            handleGameFinished((GameFinished) message);
        }
        else if (message instanceof ChannelJoined) {
            handleChannelJoined((ChannelJoined) message);
        }
    }

    private void handleChannelJoined(ChannelJoined event) {
        publishWinlist(event.getTempo(), event.getGameMode());
    }

    private void handleGameFinished(GameFinished event) {
        Game game = event.getGame();

        long timestamp = game.getTimestamp();
        GameMode mode = game.getGameMode();
        Tempo tempo = game.getTempo();

        List<Tuple> ranking = createWinlistUpdate(event.getRanking());

        if (ranking.size() > 1) {
            EloCalculator calc = new EloCalculator();

            for (int i = 0; i < ranking.size(); i++) {
                Tuple s = ranking.get(i);
                calc.addPlayer(s.toString(), i + 1, winlistRepository.winlistOps(tempo).getWinlistItem(mode, s.getType(), s.getName()).map(WinlistItem::getScore).orElse(1500L));
            }

            calc.calculateELOs();

            for (Tuple s : ranking) {
                winlistRepository.winlistOps(tempo).saveWinlistItem(mode, WinlistItem.of(s.getType(), s.getName(), calc.getELO(s.toString()), timestamp));
            }

            publishWinlist(tempo, mode);
        }
    }

    private void publishWinlist(Tempo tempo, GameMode mode) {
        publish(WinlistUpdated.of(tempo, mode, winlistRepository.winlistOps(tempo).loadWinlist(mode)));
    }

    private List<Tuple> createWinlistUpdate(List<PlayingStats> ranking) {
        LinkedList<Tuple> result = new LinkedList<>();

        ArrayList<PlayingStats> players = new ArrayList<>(ranking);
        Collections.reverse(players);

        for (PlayingStats stats : players) {
            Player player = stats.getPlayer();
            if (player.isTeamPlayer()) {
                Tuple tuple = tuple(WinlistItem.Type.TEAM, player.getTeam().get());
                result.remove(tuple);
                result.addFirst(tuple);
            }
            else {
                Tuple tuple = tuple(WinlistItem.Type.PLAYER, player.getName());
                result.remove(tuple);
                result.addFirst(tuple);
            }
        }

        return result;
    }

    @Value.Immutable
    @ImmutableStyle
    static abstract class Tuple {

        abstract WinlistItem.Type getType();

        abstract String getName();

    }

    private static Tuple tuple(WinlistItem.Type type, String name) {
        return new TupleBuilder().type(type).name(name).build();
    }

    /**
     * Kindly borrowed from https://github.com/FigBug/Multiplayer-ELO
     */
    private static class EloCalculator {

        private List<EloPlayer> players = new ArrayList<>();

        public void addPlayer(String name, int place, long elo) {
            players.add(new EloPlayer(name, place, elo));
        }

        public long getELO(String name) {
            return
                players.stream()
                    .filter(p -> p.name.equals(name))
                    .findFirst()
                    .map(p -> p.eloPost)
                    .orElse(1500L);
        }

        public void calculateELOs() {
            int n = players.size();
            float K = 32 / (float) (n - 1);

            for (int i = 0; i < n; i++) {
                int curPlace = players.get(i).place;
                long curELO = players.get(i).eloPre;

                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        int opponentPlace = players.get(j).place;
                        long opponentELO = players.get(j).eloPre;

                        //work out S
                        float S;
                        if (curPlace < opponentPlace) {
                            S = 1.0F;
                        }
                        else if (curPlace == opponentPlace) {
                            S = 0.5F;
                        }
                        else {
                            S = 0.0F;
                        }

                        //work out EA
                        float EA = 1 / (1.0f + (float) Math.pow(10.0f, (opponentELO - curELO) / 400.0f));

                        //calculate ELO change vs this one opponent, add it to our change bucket
                        //I currently round at this point, this keeps rounding changes symetrical between EA and EB, but changes K more than it should
                        players.get(i).eloChange += Math.round(K * (S - EA));
                    }
                }
                //add accumulated change to initial ELO for final ELO
                players.get(i).eloPost = players.get(i).eloPre + players.get(i).eloChange;
            }
        }

    }

    private static class EloPlayer {

        private final String name;

        private final int place;

        private final long eloPre;

        private long eloPost = 0;

        private long eloChange = 0;

        EloPlayer(String name, int place, long eloPre) {
            this.name = name;
            this.place = place;
            this.eloPre = eloPre;
        }

    }

}
