
package org.jskat.util.rule;


import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests schneider and schwarz rules
 */
public class SchneiderSchwarzRuleTest extends AbstractJSkatTest {

    private SkatGameData data;
    private GameAnnouncementFactory factory;

    private static final SuitGrandRule clubRules = (SuitGrandRule) SkatRuleFactory
            .getSkatRules(GameType.CLUBS);

    @BeforeEach
    public void setUp() {

        data = new SkatGameData();
        factory = GameAnnouncement.getFactory();
        factory.setGameType(GameType.CLUBS);
        data.setDeclarer(Player.FOREHAND);
    }

    /**
     * Test case 000 for schneider rule
     */
    @Test
    public void testSchneider000() {

        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
        assertTrue(clubRules.isSchneider(data));
    }

    /**
     * Test case 000 for schwarz rule
     */
    @Test
    public void testSchwarz000() {

        data.setAnnouncement(factory.getAnnouncement());
        assertTrue(clubRules.isSchwarz(data));
    }

    /**
     * Test for casting null rules into suit/grand rules
     */
    @Test
    public void testCast001() {
        assertThrows(ClassCastException.class, () -> {
            data.setAnnouncement(factory.getAnnouncement());
            final SuitGrandRule nullRules = (SuitGrandRule) SkatRuleFactory.getSkatRules(GameType.NULL);
        });
    }

    /**
     * Test for casting ramsch rules into suit/grand rules
     */
    @Test
    public void testCast002() {
        assertThrows(ClassCastException.class, () -> {
            data.setAnnouncement(factory.getAnnouncement());
            final SuitGrandRule nullRules = (SuitGrandRule) SkatRuleFactory.getSkatRules(GameType.RAMSCH);
        });
    }
}
