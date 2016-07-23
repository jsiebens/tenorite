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
package net.tenorite.badges.actors;

import akka.actor.Props;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.game.GameModes;
import net.tenorite.game.events.GameFinished;
import net.tenorite.util.AbstractActor;

import java.util.stream.StreamSupport;

/**
 * @author Johan Siebens
 */
public final class BadgesActor extends AbstractActor {

    public static Props props(GameModes gameModes, BadgeRepository badgeRepository) {
        return Props.create(BadgesActor.class, gameModes, badgeRepository);
    }

    private final BadgeRepository badgeRepository;

    private final GameModes gameModes;

    public BadgesActor(GameModes gameModes, BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
        this.gameModes = gameModes;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        StreamSupport.stream(gameModes.spliterator(), false).flatMap(g -> g.getBadgeValidators().stream()).forEach(b -> context().actorOf(badge(b)));
    }

    @Override
    public void onReceive(Object message) throws Exception {

    }

    private Props badge(BadgeValidator validator) {
        return Props.create(BadgeActor.class, validator, badgeRepository).withDispatcher("badges-dispatcher");
    }

    private static class BadgeActor extends AbstractActor {

        private final BadgeValidator validator;

        private final BadgeRepository badgeRepository;

        public BadgeActor(BadgeValidator validator, BadgeRepository badgeRepository) {
            this.validator = validator;
            this.badgeRepository = badgeRepository;
        }

        @Override
        public void preStart() throws Exception {
            super.preStart();
            subscribe(GameFinished.class);
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof GameFinished) {
                validator.process((GameFinished) message, badgeRepository, this::publish);
            }
        }

    }

}
