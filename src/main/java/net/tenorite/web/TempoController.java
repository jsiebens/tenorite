package net.tenorite.web;

import net.tenorite.badges.*;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameModes;
import net.tenorite.game.GameRepository;
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

    private BadgeRepository badgeRepository;

    @Autowired
    public TempoController(GameModes gameModes, WinlistRepository winlistRepository, BadgeRepository badgeRepository) {
        this.gameModes = gameModes;
        this.winlistRepository = winlistRepository;
        this.badgeRepository = badgeRepository;
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

        List<Badge> badges = gameMode.getBadgeValidators().stream().map(BadgeValidator::getBadge).collect(toList());

        return
            new ModelAndView("badges")
                .addObject("tempo", tempo)
                .addObject("gameModes", gameModes)
                .addObject("gameMode", gameMode)
                .addObject("badges", badges);
    }

    @RequestMapping("/t/{tempo}/m/{mode}/badges/{type}")
    public ModelAndView badge(@PathVariable("tempo") Tempo tempo, @PathVariable("mode") String mode, @PathVariable("type") String type) {
        GameModeId gameModeId = GameModeId.of(mode);
        BadgeType badgeType = BadgeType.of(type);

        GameMode gameMode = gameModes.get(GameModeId.of(mode));
        Badge badge = Badge.of(gameModeId, badgeType);

        List<BadgeLevel> badgeLevels = badgeRepository.badgeOps(tempo).badgeLevels(badge);

        return
            new ModelAndView("badge")
                .addObject("tempo", tempo)
                .addObject("gameModes", gameModes)
                .addObject("gameMode", gameMode)
                .addObject("badge", badge)
                .addObject("badgeLevels", badgeLevels);
    }

    @RequestMapping("/t/{tempo}/g/{id}/replay")
    public ModelAndView replay(@PathVariable("tempo") Tempo tempo, @PathVariable("id") String gameId) {
        return
            new ModelAndView("replay")
                .addObject("tempo", tempo)
                .addObject("id", gameId);
    }

}
