package net.tenorite.util;

import akka.actor.UntypedActor;

public abstract class AbstractActor extends UntypedActor {

    protected final void replyWith(Object o) {
        sender().tell(o, self());
    }

}
