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
package org.jskat.util;

import org.jskat.AbstractJSkatTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for class Card
 */
public class SkatConstantsTest extends AbstractJSkatTest {

    /**
     * Tests calculation of game values after tournament rules
     */
    @Test
    public void getTournamentGameValue001() {

        assertThat(SkatConstants.getTournamentGameValue(true, 18, 3)).isEqualTo(68);
        assertThat(SkatConstants.getTournamentGameValue(true, 18, 4)).isEqualTo(68);
        assertThat(SkatConstants.getTournamentGameValue(true, -36, 3)).isEqualTo(-86);
        assertThat(SkatConstants.getTournamentGameValue(true, -36, 4)).isEqualTo(-86);
        assertThat(SkatConstants.getTournamentGameValue(true, 20, 3)).isEqualTo(70);
        assertThat(SkatConstants.getTournamentGameValue(true, 20, 4)).isEqualTo(70);
        assertThat(SkatConstants.getTournamentGameValue(true, -40, 3)).isEqualTo(-90);
        assertThat(SkatConstants.getTournamentGameValue(true, -40, 4)).isEqualTo(-90);
        assertThat(SkatConstants.getTournamentGameValue(false, 18, 3)).isEqualTo(0);
        assertThat(SkatConstants.getTournamentGameValue(false, 18, 4)).isEqualTo(0);
        assertThat(SkatConstants.getTournamentGameValue(false, -36, 3)).isEqualTo(40);
        assertThat(SkatConstants.getTournamentGameValue(false, -36, 4)).isEqualTo(30);
        assertThat(SkatConstants.getTournamentGameValue(false, 20, 3)).isEqualTo(0);
        assertThat(SkatConstants.getTournamentGameValue(false, 20, 4)).isEqualTo(0);
        assertThat(SkatConstants.getTournamentGameValue(false, -40, 3)).isEqualTo(40);
        assertThat(SkatConstants.getTournamentGameValue(false, -40, 4)).isEqualTo(30);
    }
}
