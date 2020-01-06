package org.jskat.ai.mcts;

public class NodeStatistics {

    private final static double EXPLORATION_PARAMETER = Math.sqrt(2.0);

    private double rewards = 0.0;
    private long visits = 0L;

    public double getUctValue(final long parentNodeSimulationCount) {
        if (visits == 0) {
            return Double.MAX_VALUE;
        }
        return (rewards / visits) + EXPLORATION_PARAMETER * Math.sqrt(Math.log(parentNodeSimulationCount) / visits);
    }

    public synchronized void addReward(final double reward) {
        rewards += reward;
        visits++;
    }
}
