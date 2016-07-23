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
package net.tenorite;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import org.junit.After;
import org.junit.Before;

import java.util.function.Predicate;

/**
 * @author Johan Siebens
 */
public abstract class AbstractActorTestCase extends AbstractTestCase {

    protected ActorSystem system;

    @Before
    public final void setup() {
        system = ActorSystem.create();
    }

    @After
    public final void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    protected final JavaTestKit newTestKit() {
        return newTestKit(p -> true);
    }

    protected final JavaTestKit newTestKit(Predicate<Object> predicate) {
        return new JavaTestKit(system) {{
            new IgnoreMsg() {

                @Override
                protected boolean ignore(Object o) {
                    return !predicate.test(o);
                }

            };
        }};
    }

    protected final Predicate<Object> accept(Class<?> type) {
        return type::isInstance;
    }

    protected final Predicate<Object> ignore(Class<?> type) {
        return accept(type).negate();
    }

}
