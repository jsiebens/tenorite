package net.tenorite.web;

import net.tenorite.core.Tempo;
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

@Controller
public class WinlistController {

    private GameModes gameModes;

    private WinlistRepository winlistRepository;

    @Autowired
    public WinlistController(GameModes gameModes, WinlistRepository winlistRepository) {
        this.gameModes = gameModes;
        this.winlistRepository = winlistRepository;
    }

    @RequestMapping("/t/{tempo}/winlist/{mode}")
    public ModelAndView recentGames(@PathVariable("tempo") Tempo tempo, @PathVariable("mode") String gameMode) {
        GameModeId id = GameModeId.of(gameMode);

        List<WinlistItem> winlistItems = winlistRepository.winlistOps(tempo).loadWinlist(id);

        return
            new ModelAndView("winlist")
                .addObject("tempo", tempo)
                .addObject("gameModes", gameModes)
                .addObject("gameMode", gameModes.get(id))
                .addObject("winlist", winlistItems);
    }

}
