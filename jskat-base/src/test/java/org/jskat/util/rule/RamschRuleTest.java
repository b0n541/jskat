package org.jskat.util.rule;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameContract;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RamschRule}
 */
public class RamschRuleTest extends AbstractJSkatTest {

    private SkatGameData data;

    private static final RamschRule ramschRules = (RamschRule) SkatRuleFactory.getSkatRules(GameType.RAMSCH);

    /**
     * {@inheritDoc}
     */
    @BeforeEach
    public void initialize() {
        data = new SkatGameData();
        data.setAnnouncement(new GameAnnouncement(new GameContract(GameType.RAMSCH)));
    }

    @Test
    public void testWrongGameData_NoAnnouncement() {
        assertThrows(IllegalStateException.class, () -> {
            data = new SkatGameData();
            data.getRamschLosers();
        });
    }

    @Test
    public void testWrongGameData_NoRamschAnnouncement() {
        assertThrows(IllegalStateException.class, () -> {
            data = new SkatGameData();
            data.setAnnouncement(new GameAnnouncement(new GameContract(GameType.CLUBS).withHand()));

            data.getRamschLosers();
        });
    }

    /**
     * Tests game value calculation<br>
     * Fore hand made the most points
     */
    @Test
    public void testCalcGameValue_ForeHandMostPoints() {

        data.addPlayerPoints(Player.FOREHAND, 100);
        data.addPlayerPoints(Player.MIDDLEHAND, 15);
        data.addPlayerPoints(Player.REARHAND, 5);

        assertFalse(ramschRules.isGameWon(data));
        assertThat(ramschRules.calcGameResult(data)).isEqualTo(-100);

        data.finishRamschGame();

        assertNull(data.getDeclarer());
        assertThat(data.getRamschLosers()).containsExactlyInAnyOrder(Player.FOREHAND);
    }

    /**
     * Tests game value calculation<br>
     * Middle hand made the most points
     */
    @Test
    public void testCalcGameValue_MiddleHandMostPoints() {

        data.addPlayerPoints(Player.FOREHAND, 15);
        data.addPlayerPoints(Player.MIDDLEHAND, 100);
        data.addPlayerPoints(Player.REARHAND, 5);

        assertFalse(ramschRules.isGameWon(data));
        assertThat(ramschRules.calcGameResult(data)).isEqualTo(-100);

        data.finishRamschGame();

        assertNull(data.getDeclarer());
        assertThat(data.getRamschLosers()).containsExactlyInAnyOrder(Player.MIDDLEHAND);
    }

    /**
     * Tests game value calculation<br>
     * Rear hand made the most points
     */
    @Test
    public void testCalcGameValue_RearHandMostPoints() {

        data.addPlayerPoints(Player.FOREHAND, 15);
        data.addPlayerPoints(Player.MIDDLEHAND, 5);
        data.addPlayerPoints(Player.REARHAND, 100);

        assertFalse(ramschRules.isGameWon(data));
        assertThat(ramschRules.calcGameResult(data)).isEqualTo(-100);

        data.finishRamschGame();

        assertNull(data.getDeclarer());
        assertThat(data.getRamschLosers()).containsExactlyInAnyOrder(Player.REARHAND);
    }

    /**
     * Tests game value calculation<br>
     * Fore hand and middle hand made the most points
     */
    @Test
    public void testCalcGameValue_ForeHandMiddleHandMostPoints() {

        data.addPlayerPoints(Player.FOREHAND, 50);
        data.addPlayerPoints(Player.MIDDLEHAND, 50);
        data.addPlayerPoints(Player.REARHAND, 20);

        assertFalse(ramschRules.isGameWon(data));
        assertThat(ramschRules.calcGameResult(data)).isEqualTo(-50);

        data.finishRamschGame();

        assertNull(data.getDeclarer());
        assertThat(data.getRamschLosers()).containsExactlyInAnyOrder(Player.FOREHAND, Player.MIDDLEHAND);
    }

    /**
     * Tests game value calculation<br>
     * Middle hand and rear hand made the most points
     */
    @Test
    public void testCalcGameValue_MiddleHandRearHandMostPoints() {

        data.addPlayerPoints(Player.FOREHAND, 20);
        data.addPlayerPoints(Player.MIDDLEHAND, 50);
        data.addPlayerPoints(Player.REARHAND, 50);

        assertFalse(ramschRules.isGameWon(data));
        assertThat(ramschRules.calcGameResult(data)).isEqualTo(-50);

        data.finishRamschGame();

        assertNull(data.getDeclarer());
        assertThat(data.getRamschLosers()).containsExactlyInAnyOrder(Player.MIDDLEHAND, Player.REARHAND);
    }

    /**
     * Tests game value calculation<br>
     * Middle hand and rear hand made the most points
     */
    @Test
    public void testCalcGameValue_MiddleHandRearHandMostPointsForehandJungfrau() {

        data.addPlayerPoints(Player.FOREHAND, 0);
        data.addPlayerPoints(Player.MIDDLEHAND, 60);
        data.addPlayerPoints(Player.REARHAND, 60);

        for (int i = 0; i < 10; i++) {
            final Trick trick = new Trick(0, Player.FOREHAND);
            if (i < 9) {
                trick.setTrickWinner(Player.MIDDLEHAND);
            } else {
                trick.setTrickWinner(Player.REARHAND);
            }
            data.addTrick(trick);
        }

        data.setJungfrauDurchmarsch();

        assertFalse(ramschRules.isGameWon(data));
        assertThat(ramschRules.calcGameResult(data)).isEqualTo(-120);

        data.finishRamschGame();

        assertNull(data.getDeclarer());
        assertThat(data.getRamschLosers()).containsExactlyInAnyOrder(Player.MIDDLEHAND, Player.REARHAND);
    }

    /**
     * Tests game value calculation<br>
     * Fore hand and rear hand made the most points
     */
    @Test
    public void testCalcGameValue_ForeHandRearHandMostPoints() {

        data.addPlayerPoints(Player.FOREHAND, 50);
        data.addPlayerPoints(Player.MIDDLEHAND, 20);
        data.addPlayerPoints(Player.REARHAND, 50);

        assertFalse(ramschRules.isGameWon(data));
        assertThat(ramschRules.calcGameResult(data)).isEqualTo(-50);

        data.finishRamschGame();

        assertNull(data.getDeclarer());
        assertThat(data.getRamschLosers()).containsExactlyInAnyOrder(Player.FOREHAND, Player.REARHAND);
    }

    /**
     * Tests game value calculation<br>
     * all player made same points
     */
    @Test
    public void testCalcGameValue_AllPlayerEqualPoints() {

        data.addPlayerPoints(Player.FOREHAND, 40);
        data.addPlayerPoints(Player.MIDDLEHAND, 40);
        data.addPlayerPoints(Player.REARHAND, 40);

        assertFalse(ramschRules.isGameWon(data));
        assertThat(ramschRules.calcGameResult(data)).isEqualTo(-40);

        data.finishRamschGame();

        assertNull(data.getDeclarer());
        assertThat(data.getRamschLosers()).containsExactlyInAnyOrder(Player.FOREHAND, Player.MIDDLEHAND, Player.REARHAND);
    }

    @Test
    public void testGetMultiplierGeschoben() {

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(1);

        data.addGeschoben();

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(2);

        data.addGeschoben();

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(4);

        data.addGeschoben();

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(8);
    }

    /**
     * Test the calculation of the multiplier
     */
    @Test
    public void testMultiplierJungfrau() {

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(1);

        for (int i = 0; i < 10; i++) {
            final Trick trick = new Trick(0, Player.FOREHAND);
            if (i < 9) {
                trick.setTrickWinner(Player.FOREHAND);
            } else {
                trick.setTrickWinner(Player.MIDDLEHAND);
            }
            data.addTrick(trick);
        }

        data.setJungfrauDurchmarsch();

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(2);
        assertTrue(data.isJungfrau());
        assertFalse(data.isDurchmarsch());
    }

    /**
     * Test the calculation of the multiplier
     */
    @Test
    public void testMultiplierGeschobenJungfrau() {

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(1);

        data.addGeschoben();
        data.addGeschoben();
        data.addGeschoben();

        for (int i = 0; i < 10; i++) {
            final Trick trick = new Trick(0, Player.FOREHAND);
            if (i < 9) {
                trick.setTrickWinner(Player.FOREHAND);
            } else {
                trick.setTrickWinner(Player.MIDDLEHAND);
            }
            data.addTrick(trick);
        }

        data.setJungfrauDurchmarsch();

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(16);
        assertTrue(data.isJungfrau());
        assertFalse(data.isDurchmarsch());
    }

    /**
     * Test the calculation of the multiplier
     */
    @Test
    public void testMultiplierDurchmarsch() {

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(1);

        // all tricks are made by forehand player
        for (int i = 0; i < 10; i++) {
            final Trick trick = new Trick(i, Player.FOREHAND);
            trick.setTrickWinner(Player.FOREHAND);
            data.addTrick(trick);
        }

        data.setJungfrauDurchmarsch();

        assertThat(ramschRules.getBaseMultiplier(data)).isEqualTo(2);
        assertTrue(data.isJungfrau());
        assertTrue(data.isDurchmarsch());
    }
}
