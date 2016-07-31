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
package net.tenorite.channel.config;

import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import net.tenorite.channel.Channel;
import net.tenorite.channel.Channels;
import net.tenorite.channel.ChannelsRegistry;
import net.tenorite.channel.actors.ChannelsActors;
import net.tenorite.channel.commands.ListChannels;
import net.tenorite.game.GameModes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author Johan Siebens
 */
@Configuration
public class ChannelsConfig {

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private GameModes gameModes;

    @Bean
    public ChannelsActors channelsActors() {
        return new ChannelsActors(actorSystem, gameModes);
    }

    @Bean
    public ChannelsRegistry channelsRegistry() {
        ChannelsActors channelsActors = channelsActors();
        return tempo -> {
            Future<Object> result = Patterns.ask(channelsActors.get(tempo), ListChannels.instance(), 1000);
            CompletionStage<Object> stage = FutureConverters.toJava(result);
            return stage.thenCompose(o -> {
                if (o instanceof Channels) {
                    return CompletableFuture.completedFuture(((Channels) o).getChannels());
                }
                else {
                    CompletableFuture<List<Channel>> f = new CompletableFuture<>();
                    f.completeExceptionally(new IllegalStateException());
                    return f;
                }
            });
        };
    }

}
