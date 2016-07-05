package net.tenorite.web;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameModes;
import net.tenorite.winlist.WinlistItem;
import net.tenorite.winlist.WinlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
public class TempoController {

    private GameModes gameModes;

    private WinlistRepository winlistRepository;

    @Autowired
    public TempoController(GameModes gameModes, WinlistRepository winlistRepository) {
        this.gameModes = gameModes;
        this.winlistRepository = winlistRepository;
    }

    @RequestMapping("/t/{tempo}/m/{mode}/winlist")
    public ModelAndView winlist(@PathVariable("tempo") Tempo tempo, @PathVariable("mode") String gameMode) {
        GameModeId id = GameModeId.of(gameMode);

        List<WinlistItem> winlistItems = winlistRepository.winlistOps(tempo).loadWinlist(id);

        return
            new ModelAndView("winlist")
                .addObject("tempo", tempo)
                .addObject("gameModes", gameModes)
                .addObject("gameMode", gameModes.get(id))
                .addObject("winlist", winlistItems);
    }

    @RequestMapping("/t/{tempo}/m/{mode}/badges")
    public ModelAndView badges(@PathVariable("tempo") Tempo tempo, @PathVariable("mode") String gameModeId) {
        GameMode gameMode = gameModes.get(GameModeId.of(gameModeId));

        List<Badge> badges = gameMode.getBadgeValidators().stream().map(BadgeValidator::getType).collect(toList());

        return
            new ModelAndView("badges")
                .addObject("tempo", tempo)
                .addObject("gameModes", gameModes)
                .addObject("gameMode", gameMode)
                .addObject("badges", badges);
    }

}
