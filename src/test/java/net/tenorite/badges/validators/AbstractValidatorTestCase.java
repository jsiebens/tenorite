package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeRepositoryStub;
import net.tenorite.badges.BadgeType;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.game.GameModeId;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Johan Siebens
 */
public abstract class AbstractValidatorTestCase {

    protected static final GameModeId GAME_MODE_ID = GameModeId.of("JUNIT");

    protected static final BadgeType BADGE_TYPE = BadgeType.of("junit");

    protected static final Badge BADGE = Badge.of(GAME_MODE_ID, BADGE_TYPE);

    protected final BadgeRepositoryStub badgeRepository = new BadgeRepositoryStub();

    protected final List<BadgeEarned> published = new ArrayList<>();

    @Before
    public final void setUp() {
        badgeRepository.clear();
        published.clear();
    }

}
