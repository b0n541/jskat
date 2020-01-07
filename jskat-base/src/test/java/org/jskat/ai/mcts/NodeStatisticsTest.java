/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.mcts;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NodeStatisticsTest {

    @Test
    public void initialUctValue() {

        final NodeStatistics statistics = new NodeStatistics();

        assertThat(statistics.getUctValue(0L), is(Double.MAX_VALUE));
    }

    @Test
    public void addReward() {

        final NodeStatistics statistics = new NodeStatistics();

        statistics.addReward(1.0);

        assertThat(statistics.getUctValue(1L), is(1.0));

        statistics.addReward(1.0);

        assertThat(statistics.getUctValue(2L), is(1.8325546111576978));
    }

    @Test
    public void addNoReward() {

        final NodeStatistics statistics = new NodeStatistics();

        statistics.addReward(0.0);

        assertThat(statistics.getUctValue(1L), is(0.0));

        statistics.addReward(1.0);

        assertThat(statistics.getUctValue(2L), is(1.3325546111576978));
    }

    @Test
    public void addNoRewardOtherOrder() {

        final NodeStatistics statistics = new NodeStatistics();

        statistics.addReward(1.0);

        assertThat(statistics.getUctValue(1L), is(1.0));

        statistics.addReward(0.0);

        assertThat(statistics.getUctValue(2L), is(1.3325546111576978));
    }
}
