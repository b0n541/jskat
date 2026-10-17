package org.jskat.player;

import java.util.Set;

public class JSkatPlayerResolver {

    public static final String HUMAN_PLAYER_CLASS = "org.jskat.gui.human.SwingHumanPlayer";
    public static final String ML_PLAYER_CLASS = "org.jskat.ai.ml.MLPlayer";
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
                "org.jskat.ai.ml.MLPlayer",
                "org.jskat.ai.ml.MLPlayerPro"
        );
    }

    public static boolean isMLPlayer(final String playerType) {
        return ML_PLAYER_CLASS.equals(playerType) || ML_PLAYER_PRO_CLASS.equals(playerType);
    }
}
