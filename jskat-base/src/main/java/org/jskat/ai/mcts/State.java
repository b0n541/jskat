package org.jskat.ai.mcts;

import org.jskat.data.Trick;

import java.util.List;

public final class State {

    private final List<Trick> tricks;

    public State(final List<Trick> tricks) {
        this.tricks = tricks;
    }

    public static List<State> getAllPossibleStates() {
        return List.of();
    }

    public void randomPlay() {

    }
}
