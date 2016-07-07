package net.tenorite.web.socket;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.GameMessage;
import net.tenorite.game.GameRepository;
import net.tenorite.protocol.EndGameMessage;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlayerJoinMessage;
import net.tenorite.protocol.TeamMessage;
import net.tenorite.util.AbstractActor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import scala.concurrent.duration.FiniteDuration;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static akka.actor.ActorRef.noSender;
import static akka.actor.PoisonPill.getInstance;
import static java.util.Optional.ofNullable;

public class ReplayWebSocketHandler extends TextWebSocketHandler {

    private static final String START = "GO";

    private final ActorSystem system;

    private final GameRepository gameRepository;

    private final Map<String, ActorRef> sessions = new ConcurrentHashMap<>();

    public ReplayWebSocketHandler(ActorSystem system, GameRepository gameRepository) {
        this.system = system;
        this.gameRepository = gameRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String tempo = (String) session.getAttributes().get(Parameters.TEMPO);
        String id = (String) session.getAttributes().get(Parameters.ID);

        Optional<Game> game = gameRepository.gameOps(Tempo.valueOf(tempo)).loadGame(id);
        ActorRef actor = system.actorOf(Props.create(ReplayWebSocketActor.class, session, game.get()), "replay_" + session.getId());
        actor.tell(START, noSender());

        sessions.put(session.getId(), actor);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ofNullable(sessions.remove(session.getId())).ifPresent(r -> r.tell(getInstance(), noSender()));
    }

    private static class ReplayWebSocketActor extends AbstractActor {

        private WebSocketSession session;

        private Game game;

        private LinkedList<GameMessage> messages;

        public ReplayWebSocketActor(WebSocketSession session, Game game) {
            this.session = session;
            this.game = game;
            this.messages = new LinkedList<>(game.getMessages());
        }

        @Override
        public void postStop() throws Exception {
            super.postStop();
            if (session.isOpen()) {
                session.close();
            }
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message == START) {
                game.getPlayers().forEach(p -> {
                    send(PlayerJoinMessage.of(p.getSlot(), p.getName()));
                    send(TeamMessage.of(p.getSlot(), p.getTeam().orElse("")));
                });

                scheduleFirst();
            }
            else if (message instanceof GameMessage) {
                GameMessage gm = (GameMessage) message;
                send(gm.getMessage());
                if (messages.isEmpty()) {
                    send(EndGameMessage.of());
                    context().stop(self());
                }
                else {
                    scheduleNext(gm);
                }
            }
        }

        private void scheduleFirst() {
            GameMessage first = messages.removeFirst();
            FiniteDuration delay = FiniteDuration.create(first.getTimestamp(), TimeUnit.MILLISECONDS);
            context().system().scheduler().scheduleOnce(delay, self(), first, context().dispatcher(), noSender());
        }

        private void scheduleNext(GameMessage previous) {
            GameMessage next = messages.removeFirst();
            FiniteDuration delay = FiniteDuration.create((next.getTimestamp() - previous.getTimestamp()), TimeUnit.MILLISECONDS);
            context().system().scheduler().scheduleOnce(delay, self(), next, context().dispatcher(), noSender());
        }

        private void send(Message m) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(m.raw(Tempo.NORMAL)));
                }
            }
            catch (Exception e) {
                // TODO!!
                e.printStackTrace();
            }
        }

    }

}
