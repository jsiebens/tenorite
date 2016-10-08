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
package net.tenorite.screenplay.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.screenplay.actions.Open;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * @author Johan Siebens
 */
public class StartWith implements Task {

    private final String path;

    public StartWith(String path) {
        this.path = path;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.browserOn(path));
    }

    public static StartWith anEmptyTournamentList(Tempo tempo, GameModeId gameMode) {
        return instrumented(StartWith.class, String.format("/t/%s/m/%s/tournaments", tempo, gameMode));
    }

}
