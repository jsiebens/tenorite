/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tenorite.util;

import akka.actor.UntypedActor;
import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Johan Siebens
 */
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
