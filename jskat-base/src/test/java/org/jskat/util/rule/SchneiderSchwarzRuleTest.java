package org.jskat.util.rule;


import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameContract;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests schneider and schwarz rules
 */
public class SchneiderSchwarzRuleTest extends AbstractJSkatTest {

    private static final CardList DISCARDED_CARDS = CardList.of(Card.C7, Card.S7);
    private SkatGameData data;
    private GameContract contract;

    private static final SuitGrandRule clubRules = (SuitGrandRule) SkatRuleFactory.getSkatRules(GameType.CLUBS);

    @BeforeEach
    public void setUp() {
        data = new SkatGameData();
        contract = new GameContract(GameType.CLUBS);
        data.setDeclarer(Player.FOREHAND);
    }

    /**
     * Test case 000 for schneider rule
     */
    @Test
    public void testSchneider000() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));

        assertThat(SuitGrandRule.isSchneider(data)).isTrue();
    }

    /**
     * Test case 000 for schwarz rule
     */
    @Test
    public void testSchwarz000() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));

        assertThat(SuitGrandRule.isSchwarz(data)).isTrue();
    }

    /**
     * Test for casting null rules into suit/grand rules
     */
    @Test
    public void testCast001() {
        assertThrows(ClassCastException.class, () -> {
            data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
            final SuitGrandRule nullRules = (SuitGrandRule) SkatRuleFactory.getSkatRules(GameType.NULL);
        });
    }

    /**
     * Test for casting ramsch rules into suit/grand rules
     */
    @Test
    public void testCast002() {
        assertThrows(ClassCastException.class, () -> {
            data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
            final SuitGrandRule nullRules = (SuitGrandRule) SkatRuleFactory.getSkatRules(GameType.RAMSCH);
        });
    }
}
