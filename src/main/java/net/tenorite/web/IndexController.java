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
