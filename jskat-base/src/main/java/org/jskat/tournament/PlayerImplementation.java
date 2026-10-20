package org.jskat.tournament;

/** Closed version-one registry descriptors. Construction belongs to the player-input service. */
public enum PlayerImplementation {
    RANDOM("random", "org.jskat.ai.rnd.AIPlayerRND", false),
    ALGORITHMIC("algorithmic", "org.jskat.ai.algorithmic.AlgorithmicAIPlayer", false),
    ML("ml", "org.jskat.ai.ml.MLPlayer", true),
    ML_PRO("ml-pro", "org.jskat.ai.ml.MLPlayerPro", true);
    private final String id, className;
    private final boolean modelRequired;

    PlayerImplementation(String id, String className, boolean modelRequired) {
        this.id = id;
        this.className = className;
        this.modelRequired = modelRequired;
    }

    public String id() {
        return id;
    }

    public String className() {
        return className;
    }

    public boolean modelRequired() {
        return modelRequired;
    }

    public static PlayerImplementation fromId(String id) {
        for (var entry : values()) if (entry.id.equals(id)) return entry;
        throw new IllegalArgumentException("Unknown implementation: " + id);
    }
}
