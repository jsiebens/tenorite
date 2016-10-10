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
package net.tenorite.channel.actors;

import akka.actor.Props;
import net.tenorite.badges.BadgeLevel;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.badges.protocol.BadgeEarnedPlineMessage;
import net.tenorite.channel.Channel;
import net.tenorite.channel.commands.ListChannels;
import net.tenorite.channel.events.ChannelJoined;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;
import net.tenorite.protocol.PlineMessage;
import net.tenorite.protocol.WinlistMessage;
import net.tenorite.winlist.events.WinlistUpdated;

import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

/**
 * @author Johan Siebens
 */
final class ChannelActor extends AbstractChannelActor {

    static Props props(Tempo tempo, GameMode gameMode, String name, boolean ephemeral) {
        return Props.create(ChannelActor.class, tempo, gameMode, name, ephemeral);
    }

    private final String name;

    public ChannelActor(Tempo tempo, GameMode gameMode, String name, boolean ephemeral) {
        super(tempo, gameMode, ephemeral);
        this.name = name;
    }

    @Override
    public void preStart() throws Exception {
        subscribe(WinlistUpdated.class);
        subscribe(BadgeEarned.class);
    }

    @Override
    protected void doOnReceive(Object o) {
        if (o instanceof WinlistUpdated) {
            handleWinlistUpdated((WinlistUpdated) o);
        }
        else if (o instanceof BadgeEarned) {
            handleBadgeEarned((BadgeEarned) o);
        }
        else if (o instanceof ListChannels) {
            replyWith(Channel.of(gameMode.getId(), name, slots.size()));
        }
    }

    @Override
    protected void sendWelcomeMessage(Slot slot) {
        slot.send(PlineMessage.of(""));
        slot.send(PlineMessage.of(format("Hello <b>%s</b>, welcome in channel <b>%s</b>", slot.getName(), name)));
        slot.send(PlineMessage.of(""));
    }

    @Override
    protected void onEndGame(Game game, List<PlayingStats> ranking) {
        publish(GameFinished.of(game, ranking));
    }

    @Override
    protected void onPlayerJoined(Slot slot) {
        publish(ChannelJoined.of(tempo, gameMode.getId(), name, slot.getName()));
    }

    private void handleWinlistUpdated(WinlistUpdated winlistUpdated) {
        if (tempo.equals(winlistUpdated.getTempo()) && winlistUpdated.getGameModeId().equals(gameMode.getId())) {
            forEachSlot(s -> s.send(WinlistMessage.of(winlistUpdated.getItems().stream().map(e -> e.getType().getLetter() + e.getName() + ";" + e.getScore()).collect(toList()))));
        }
    }

    private void handleBadgeEarned(BadgeEarned badgeEarned) {
        BadgeLevel level = badgeEarned.getBadge();
        GameModeId gameModeId = level.getBadge().getGameModeId();
        if (tempo.equals(level.getTempo()) && gameMode.getId().equals(gameModeId) && findSlot(level.getName()).isPresent()) {
            BadgeEarnedPlineMessage message = BadgeEarnedPlineMessage.of(level.getName(), level.getBadge().getTitle(), level.getLevel(), badgeEarned.isUpgrade());
            forEachSlot(s -> s.send(message));
        }
    }

}
