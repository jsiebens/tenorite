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

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Johan Siebens
 */
public class AddATournament implements Task {

    public static Target ADD_TOURNAMENT_BUTTON = Target.the("Add Tournament Button").locatedBy("#addTournament");

    public static Target NAME_FIELD = Target.the("Name Field").locatedBy("#inputName");

    public static Target PARTICIPANTS_FIELD = Target.the("Participants Field").locatedBy("#inputParticipants");

    public static Target SUBMIT_BUTTON = Target.the("Submit Button").locatedBy("#submitButton");

    private final String name;

    private final List<String> participants;

    public AddATournament(String name, List<String> participants) {
        this.name = name;
        this.participants = participants;
    }

    @Override
    @Step("{0} adds a new tournament")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(ADD_TOURNAMENT_BUTTON));

        actor.attemptsTo(
            Enter.theValue(name).into(NAME_FIELD),
            Enter.theValue(participants.stream().collect(Collectors.joining("\n"))).into(PARTICIPANTS_FIELD)
        );

        actor.attemptsTo(Click.on(SUBMIT_BUTTON));
    }

    public static AddATournament with(String name, List<String> participants) {
        return Instrumented.instanceOf(AddATournament.class).withProperties(name, participants);
    }

}
