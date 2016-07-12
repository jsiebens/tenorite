package net.tenorite.web;

import net.tenorite.badges.*;
import net.tenorite.core.NotAvailableException;
import net.tenorite.core.Tempo;
import net.tenorite.game.*;
import net.tenorite.winlist.WinlistItem;
import net.tenorite.winlist.WinlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Controller
public class TempoController {

    private GameModes gameModes;

    private WinlistRepository winlistRepository;

    private GameRepository gameRepository;

    private BadgeRepository badgeRepository;

    @Autowired
    public TempoController(GameModes gameModes, WinlistRepository winlistRepository, GameRepository gameRepository, BadgeRepository badgeRepository) {
        this.gameModes = gameModes;
        this.winlistRepository = winlistRepository;
        this.gameRepository = gameRepository;
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
        List<Badge> badges = gameMode.getBadges();

        int i = badges.indexOf(badge);

        if (i == -1) {
            throw new NotAvailableException();
        }
        else {
            Badge prev = null;
            Badge next = null;

            if (i != 0) {
                prev = badges.get(i - 1);
            }

            if (i != badges.size() - 1) {
                next = badges.get(i + 1);
            }

            List<BadgeLevel> badgeLevels = badgeRepository.badgeOps(tempo).badgeLevels(badge);

            return
                new ModelAndView("badge")
                    .addObject("tempo", tempo)
                    .addObject("gameModes", gameModes)
                    .addObject("gameMode", gameMode)
                    .addObject("badge", badge)
                    .addObject("prev", prev)
                    .addObject("next", next)
                    .addObject("badgeLevels", badgeLevels);
        }

    }

    @RequestMapping("/t/{tempo}/m/{mode}/games")
    public ModelAndView recentGames(@PathVariable("tempo") Tempo tempo, @PathVariable("mode") String mode) {
        GameMode gameMode = gameModes.get(GameModeId.of(mode));

        List<Game> games = gameRepository.gameOps(tempo).recentGames(gameMode.getId());

        return
            new ModelAndView("games")
                .addObject("tempo", tempo)
                .addObject("gameMode", gameMode)
                .addObject("gameModes", gameModes)
                .addObject("games", games);
    }

    @RequestMapping("/t/{tempo}/m/{mode}/games/{id}")
    public ModelAndView replay(@PathVariable("tempo") Tempo tempo, @PathVariable("mode") String mode, @PathVariable("id") String gameId) {
        GameMode gameMode = gameModes.get(GameModeId.of(mode));

        Optional<Game> optGame = gameRepository.gameOps(tempo).loadGame(gameId);

        if (optGame.isPresent()) {
            Game game = optGame.get();
            List<PlayingStats> ranking = new GameRankCalculator().calculate(gameMode, game);

            return
                new ModelAndView("game")
                    .addObject("tempo", tempo)
                    .addObject("gameMode", gameMode)
                    .addObject("gameModes", gameModes)
                    .addObject("ranking", ranking)
                    .addObject("game", optGame.get())
                    .addObject("id", gameId);
        }
        else {
            throw new NotAvailableException();
        }
    }

    @RequestMapping("/t/{tempo}/m/{mode}/p/{name}")
    public ModelAndView player(@PathVariable("tempo") Tempo tempo, @PathVariable("mode") String mode, @PathVariable("name") String name) {
        GameMode gameMode = gameModes.get(GameModeId.of(mode));

        List<Badge> badges = gameMode.getBadges();
        Map<Badge, BadgeLevel> badgeLevels = badgeRepository.badgeOps(tempo).badgeLevels(gameMode.getId(), name);

        return
            new ModelAndView("player")
                .addObject("tempo", tempo)
                .addObject("name", name)
                .addObject("gameMode", gameMode)
                .addObject("gameModes", gameModes)
                .addObject("badges", badges)
                .addObject("badgeLevels", badgeLevels);
    }

}
