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
package net.tenorite.game.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModes;
import net.tenorite.game.GameRepository;
import net.tenorite.game.actors.GamesActor;
import net.tenorite.game.repository.MongoGameRepository;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Johan Siebens
 */
@Configuration
public class GameConfig {

    @Autowired
    private Jongo jongo;

    @Autowired
    private ActorSystem system;

    @Bean
    public GameRepository gameRepository() {
        return new MongoGameRepository(jongo);
    }

    @Bean
    public ActorRef gameActor() {
        return system.actorOf(GamesActor.props(gameRepository()));
    }

    @Bean
    public GameModes gameModes(List<GameMode> modes) {
        return new GameModes(modes);
    }

}
