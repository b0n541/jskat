package org.jskat.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all player positions in a game
 */
public enum Player {
    /**
     * First player
     */
    FOREHAND {
        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public Player getRightNeighbor() {
            return REARHAND;
        }

        @Override
        public Player getLeftNeighbor() {
            return MIDDLEHAND;
        }
    },
    /**
     * Second player
     */
    MIDDLEHAND {
        @Override
        public int getOrder() {
            return 1;
        }

        @Override
        public Player getRightNeighbor() {
            return FOREHAND;
        }

        @Override
        public Player getLeftNeighbor() {
            return REARHAND;
        }
    },
    /**
     * Third player
     */
    REARHAND {
        @Override
        public int getOrder() {
            return 2;
        }

        @Override
        public Player getRightNeighbor() {
            return MIDDLEHAND;
        }

        @Override
        public Player getLeftNeighbor() {
            return FOREHAND;
        }
    };

    public static List<Player> getOrderedList() {
        List<Player> result = new ArrayList<Player>();
        result.add(FOREHAND);
        result.add(MIDDLEHAND);
        result.add(REARHAND);
        return result;
    }

    /**
     * Gets order of a player
     *
     * @return Order of the player
     */
    public abstract int getOrder();

    /**
     * Gets the player right from the player
     *
     * @return Player right from the player
     */
    public abstract Player getRightNeighbor();

    /**
     * Gets the player left from the player
     *
     * @return Player left from the player
     */
    public abstract Player getLeftNeighbor();
}
