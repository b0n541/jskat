/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
