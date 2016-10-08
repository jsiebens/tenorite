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
package net.tenorite.web;

import net.tenorite.core.NotAvailableException;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameModes;
import net.tenorite.tournament.Tournament;
import net.tenorite.tournament.TournamentRepository;
import net.tenorite.web.form.TournamentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author Johan Siebens
 */
@Controller
public class TournamentController {

    private final GameModes gameModes;

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentController(GameModes gameModes, TournamentRepository tournamentRepository) {
        this.gameModes = gameModes;
        this.tournamentRepository = tournamentRepository;
    }

    @InitBinder
    public void dataBinding(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "participants", new ParticipantsEditor());
    }

    @RequestMapping(value = "/t/{tempo}/m/{mode}/tournaments", method = RequestMethod.GET)
    public String tournaments(@PathVariable("tempo") Tempo tempo,
                              @PathVariable("mode") String mode,
                              @RequestParam(value = "a", required = false) String create,
                              Model model) {

        GameMode gameMode = gameModes.find(GameModeId.of(mode)).orElseThrow(NotAvailableException::new);

        model.addAttribute("tempo", tempo);
        model.addAttribute("gameMode", gameMode);

        if (create != null) {
            model.addAttribute(new TournamentForm());
            return "tournaments/form";
        }
        else {
            model.addAttribute("tournaments", tournamentRepository.tournamentOps(tempo).listTournaments(gameMode.getId()));
            return "tournaments/overview";
        }

    }

    @RequestMapping(value = "/t/{tempo}/m/{mode}/tournaments", method = RequestMethod.POST)
    public String tournaments(@PathVariable("tempo") Tempo tempo,
                              @PathVariable("mode") String mode,
                              @Valid TournamentForm tournamentForm,
                              BindingResult bindingResult,
                              Model model) {

        GameMode gameMode = gameModes.find(GameModeId.of(mode)).orElseThrow(NotAvailableException::new);

        model.addAttribute("tempo", tempo);
        model.addAttribute("mode", mode);
        model.addAttribute("gameMode", gameMode);
        model.addAttribute("gameModes", gameModes);

        if (bindingResult.hasErrors()) {
            return "tournaments/form";
        }
        else {
            Tournament tournament = Tournament.of(tournamentForm.getName(), gameMode.getId(), tournamentForm.getParticipants());
            tournamentRepository.tournamentOps(tempo).saveTournament(tournament);
            return "redirect:/t/{tempo}/m/{mode}/tournaments";
        }
    }

    private static class ParticipantsEditor extends PropertyEditorSupport {

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            if (StringUtils.hasText(text)) {
                List<String> participants = Arrays.stream(text.split("\n")).map(String::trim).distinct().collect(toList());
                setValue(participants);
            }
            else {
                setValue(Collections.<String>emptyList());
            }
        }

        @Override
        public String getAsText() {
            List<String> value = (List<String>) getValue();
            return value == null ? "" : value.stream().collect(joining("\r\n"));
        }

    }

}
