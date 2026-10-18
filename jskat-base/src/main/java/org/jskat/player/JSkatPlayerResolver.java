package org.jskat.player;

import java.util.Set;

/**
 * Defines the j skat player resolver type.
 */
public class JSkatPlayerResolver {
    /**
     * Creates an instance of this type.
     */
    public JSkatPlayerResolver() {
    }

    /**
     * Fully qualified class name for the human player implementation.
     */
    public static final String HUMAN_PLAYER_CLASS = "org.jskat.gui.human.SwingHumanPlayer";
    /**
     * Fully qualified class name for the standard ML player implementation.
     */
    public static final String ML_PLAYER_CLASS = "org.jskat.ai.ml.MLPlayer";
    /**
     * Fully qualified class name for the stronger ML player implementation.
     */
    public static final String ML_PLAYER_PRO_CLASS = "org.jskat.ai.ml.MLPlayerPro";

    /**
     * Gets all class names including package names of AI player implementations.
     *
     * @return A set of all player implementation classes
     */
    public static Set<String> getAllAIPlayerImplementations() {
        return Set.of(
                "org.jskat.ai.rnd.AIPlayerRND",
                "org.jskat.ai.newalgorithm.AlgorithmAI",
                ML_PLAYER_CLASS,
                ML_PLAYER_PRO_CLASS
        );
    }

    /**
     * Returns or indicates m l player.
     * @param playerType player type value.
     * @return is m l player result.
     */
    public static boolean isMLPlayer(final String playerType) {
        return ML_PLAYER_CLASS.equals(playerType) || ML_PLAYER_PRO_CLASS.equals(playerType);
    }
}
