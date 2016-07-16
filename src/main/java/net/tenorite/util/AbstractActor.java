package net.tenorite.util;

import akka.actor.UntypedActor;
import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;

import java.util.function.Consumer;
import java.util.function.Function;

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

    protected static <T, R> Mapper<T, R> mapper(Function<T, R> func) {
        return new Mapper<T, R>() {

            @Override
            public R apply(T parameter) {
                return func.apply(parameter);
            }

        };
    }

    protected static <T> OnSuccess<T> onSuccess(Consumer<T> consumer) {
        return new OnSuccess<T>() {

            @Override
            public void onSuccess(T t) throws Throwable {
                consumer.accept(t);
            }

        };
    }

}
