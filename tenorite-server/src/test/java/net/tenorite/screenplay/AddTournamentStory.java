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
package net.tenorite.screenplay;

import net.tenorite.core.Tempo;
import net.tenorite.modes.classic.Classic;
import net.tenorite.screenplay.questions.TheTournaments;
import net.tenorite.screenplay.tasks.AddATournament;
import net.tenorite.screenplay.tasks.StartWith;
import net.tenorite.tournament.repository.MongoTournamentRepository;
import org.jongo.Jongo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.contains;

/**
 * @author Johan Siebens
 */
public class AddTournamentStory extends AbstractStory {

    @Test
    public void a_user_should_be_able_to_add_a_first_tournament() {
        givenThat(user).wasAbleTo(
            StartWith.anEmptyTournamentList(Tempo.FAST, Classic.ID)
        );

        when(user).attemptsTo(
            AddATournament.with("Tournament A", asList("john", "jane", "nick"))
        );

        then(user).should(
            seeThat(TheTournaments.displayed(), contains("Tournament A"))
        );
    }

}
