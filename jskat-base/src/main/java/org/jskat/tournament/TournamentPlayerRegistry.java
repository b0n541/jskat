package org.jskat.tournament;

import org.jskat.ai.algorithmic.AlgorithmicAIPlayer;
import org.jskat.ai.ml.*;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.player.JSkatPlayer;

import java.util.stream.Collectors;

/** Built-in factories; every call returns an independently owned player. */
public final class TournamentPlayerRegistry {
    public JSkatPlayer create(ResolvedTournamentCompetitor competitor) {
        var models =
                competitor.requested().implementation().modelRequired()
                        ? ResolvedModelSet.of(
                                competitor.releaseLabel(),
                                competitor.artifacts().stream()
                                        .collect(
                                                Collectors.toMap(
                                                        TournamentArtifact::name,
                                                        a ->
                                                                competitor
                                                                        .snapshot()
                                                                        .resolve(a.name()))))
                        : null;
        return switch (competitor.requested().implementation()) {
            case RANDOM -> new AIPlayerRND();
            case ALGORITHMIC -> new AlgorithmicAIPlayer();
            case ML -> new MLPlayer(models, ONNXConstructionPolicy.TOURNAMENT);
            case ML_PRO -> new MLPlayerPro(models, ONNXConstructionPolicy.TOURNAMENT);
        };
    }

    public void close(JSkatPlayer player) {
        if (player instanceof AutoCloseable closeable)
            try {
                closeable.close();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException("Player cleanup failed", e);
            }
    }
}
