package net.tenorite.clients.config;

import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import net.tenorite.channel.actors.ChannelsActors;
import net.tenorite.clients.ClientRegistrationException;
import net.tenorite.clients.ClientsRegistry;
import net.tenorite.clients.actors.ClientsActors;
import net.tenorite.clients.commands.RegisterClient;
import net.tenorite.clients.events.ClientRegistered;
import net.tenorite.clients.events.ClientRegistrationFailed;
import net.tenorite.game.GameModes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author Johan Siebens
 */
@Configuration
public class ClientsConfig {

    @Autowired
    private GameModes gameModes;

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private ChannelsActors channelsActors;

    @Bean
    public ClientsActors clientsActors() {
        return new ClientsActors(actorSystem, gameModes, channelsActors);
    }

    @Bean
    public ClientsRegistry clientsRegistry() {
        ClientsActors clientsActors = clientsActors();
        return (tempo, name, channel) -> {
            RegisterClient registerClient = RegisterClient.of(name, channel);
            Future<Object> result = Patterns.ask(clientsActors.get(tempo), registerClient, 1000);
            CompletionStage<Object> stage = FutureConverters.toJava(result);
            return stage.thenCompose(o -> {
                if (o instanceof ClientRegistered) {
                    return CompletableFuture.completedFuture((ClientRegistered) o);
                }
                else if (o instanceof ClientRegistrationFailed) {
                    CompletableFuture<ClientRegistered> f = new CompletableFuture<>();
                    f.completeExceptionally(new ClientRegistrationException((ClientRegistrationFailed) o));
                    return f;
                }
                else {
                    CompletableFuture<ClientRegistered> f = new CompletableFuture<>();
                    f.completeExceptionally(new IllegalStateException());
                    return f;
                }
            });
        };
    }

}
