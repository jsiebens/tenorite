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

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Johan Siebens
 */
@Controller
public class IndexController {

    private GameModes gameModes;

    @Autowired
    public IndexController(GameModes gameModes) {
        this.gameModes = gameModes;
    }

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index", "hostname", getHostname());
    }

    @RequestMapping("/t/{tempo}")
    public ModelAndView home(@PathVariable("tempo") Tempo tempo) {
        return new ModelAndView("tempo")
            .addObject("gameModes", gameModes)
            .addObject("tempo", tempo);
    }

    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            throw new IllegalStateException("unable to get hostname", e);
        }
    }

}
