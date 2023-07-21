package org.jskat.player;

import java.util.Set;

public class JSkatPlayerResolver {

    public static final String HUMAN_PLAYER_CLASS = "org.jskat.gui.human.SwingHumanPlayer";

    /**
     * Gets all class names including package names of AI player implementations.
     *
     * @return A set of all player implementation classes
     */
    public static Set<String> getAllAIPlayerImplementations() {
        return Set.of(
                "org.jskat.ai.rnd.AIPlayerRND",
                "org.jskat.ai.alex.AIPlayerAlex",
                "org.jskat.ai.jens.AIPlayerJens",
                "org.jskat.ai.sascha.AIPlayerSascha",
                "org.jskat.ai.newalgorithm.AlgorithmAI"
        );
    }
}
