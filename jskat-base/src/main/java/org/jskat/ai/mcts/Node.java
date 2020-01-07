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

import org.jskat.util.Player;

import java.util.List;

public final class Node {

    private final State state;
    private final Player player;
    private final NodeStatistics statistics;

    private final List<Node> children = List.of();

    public Node(final State state, final Player player, final NodeStatistics statistics) {
        this.state = state;
        this.player = player;
        this.statistics = statistics;
    }
}
