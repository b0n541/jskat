
package org.jskat.data;


import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SkatGameDataTest {

    SkatGameData gameData;

    @BeforeEach
    public void createGameData() {
        gameData = new SkatGameData();
    }

    @Test
    public void hand() {

        assertTrue(gameData.isHand());

        gameData.addSkatToPlayer(Player.FOREHAND);

        assertFalse(gameData.isHand());
    }

    @Test
    public void schneiderSchwarz() {

        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(0);

        assertTrue(gameData.isSchneider());
        assertTrue(gameData.isSchwarz());

        gameData.setDeclarerScore(15);

        assertTrue(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(30);

        assertTrue(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(31);

        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(60);

        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(89);

        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(90);

        assertTrue(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(120);

        assertTrue(gameData.isSchneider());
        assertTrue(gameData.isSchwarz());
    }
}
