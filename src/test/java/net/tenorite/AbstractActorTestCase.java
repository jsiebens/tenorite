package net.tenorite;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import org.junit.After;
import org.junit.Before;

import java.util.function.Predicate;

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

}
