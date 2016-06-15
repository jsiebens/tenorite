package net.tenorite.clients.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import net.tenorite.clients.ClientRegistrationException;
import net.tenorite.clients.ClientsRegistry;
import net.tenorite.clients.actors.ClientsActor;
import net.tenorite.clients.commands.RegisterClient;
import net.tenorite.clients.events.ClientRegistered;
import net.tenorite.clients.events.ClientRegistrationFailed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Configuration
public class ClientsConfig {

    @Autowired
    private ActorSystem actorSystem;

    @Bean(name = "clients")
    public ActorRef clients() {
        return actorSystem.actorOf(ClientsActor.props());
    }

    @Bean
    public ClientsRegistry clientsRegistry() {
        ActorRef clients = clients();
        return (tempo, name, channel) -> {
            RegisterClient registerClient = RegisterClient.of(tempo, name, channel);
            Future<Object> result = Patterns.ask(clients, registerClient, 1000);
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
