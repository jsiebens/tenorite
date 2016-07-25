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
