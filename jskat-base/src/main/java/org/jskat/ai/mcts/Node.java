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
