package net.tenorite.util;

import akka.actor.UntypedActor;

public abstract class AbstractActor extends UntypedActor {

    protected final void replyWith(Object o) {
        sender().tell(o, self());
    }

    protected final void publish(Object o) {
        getContext().system().eventStream().publish(o);
    }

    protected final void subscribe(Class<?> type) {
        getContext().system().eventStream().subscribe(self(), type);
    }

}
