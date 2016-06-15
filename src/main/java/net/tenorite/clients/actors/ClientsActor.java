package net.tenorite.clients.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import net.tenorite.clients.commands.RegisterClient;
import net.tenorite.clients.events.ClientRegistered;
import net.tenorite.clients.events.ClientRegistrationFailed;
import net.tenorite.util.AbstractActor;
import org.springframework.util.DigestUtils;
import scala.Option;

public final class ClientsActor extends AbstractActor {

    public static Props props() {
        return Props.create(ClientsActor.class);
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return SupervisorStrategy.stoppingStrategy();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof RegisterClient) {
            handle((RegisterClient) message);
        }
    }

    private void handle(RegisterClient rc) {
        String key = actorName(rc.getName());
        Option<ActorRef> child = context().child(key);

        if (!isValid(rc.getName())) {
            replyWith(ClientRegistrationFailed.invalidName());
        }
        else if (child.isDefined()) {
            replyWith(ClientRegistrationFailed.nameAlreadyInUse());
        }
        else {
            ActorRef client = context().actorOf(ClientActor.props(rc.getTempo(), rc.getName(), rc.getChannel()), key);
            replyWith(ClientRegistered.of(client));
        }
    }

    private boolean isValid(String name) {
        return name.length() >= 2 && name.length() <= 50;
    }

    private String actorName(String name) {
        return DigestUtils.md5DigestAsHex(name.getBytes());
    }

}
