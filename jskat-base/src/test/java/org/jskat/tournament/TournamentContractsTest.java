package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.jskat.util.Card;
import org.jskat.util.Player;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;

class TournamentContractsTest {
    @Test
    void retainedGameOwnsItsInputsAndRequiresThreeDistinctSeatsAndFullDeck() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        var deck = new ArrayList<>(List.of(Card.values()));
        var seats =
                List.of(
                        new TournamentSeat(Player.FOREHAND, id, 1),
                        new TournamentSeat(Player.MIDDLEHAND, id, 2),
                        new TournamentSeat(Player.REARHAND, id, 3));
        var game = new ScheduledTournamentGame("e0/b0/a0", 0, "e0/b0", seats, deck, 9);
        deck.clear();
        assertThat(game.deck()).hasSize(32);
        assertThatThrownBy(() -> game.deck().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(
                        () ->
                                new ScheduledTournamentGame(
                                        "invalid",
                                        0,
                                        "b0",
                                        List.of(
                                                seats.getFirst(),
                                                seats.getFirst(),
                                                seats.getLast()),
                                        game.deck(),
                                        9))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sessionOptionsAndCancellationAreInvocationState() {
        assertThat(TournamentSessionOptions.defaults())
                .isEqualTo(new TournamentSessionOptions(1, false, Duration.ofSeconds(60)));
        assertThatThrownBy(() -> new TournamentSessionOptions(0, false, Duration.ofSeconds(60)))
                .isInstanceOf(IllegalArgumentException.class);
        var signal = new TournamentCancellation();
        assertThat(signal.isCancellationRequested()).isFalse();
        signal.requestCancellation();
        assertThat(signal.isCancellationRequested()).isTrue();
    }

    @Test
    void directAdapterModelsRejectInvalidRosterAndModelVariants() {
        var id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        assertThatThrownBy(
                        () -> new TournamentCompetitor(id, "  ", PlayerImplementation.RANDOM, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TournamentCompetitor(id, "ML", PlayerImplementation.ML, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ModelSource.Installed("../candidate"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eventPayloadRetainsExactDecimalsAndRejectsAmbiguousJson() {
        var event = new TournamentGameEvent(0, "bid", "{\"probability\":0.123456789012345678901}");
        assertThat(event.payloadJson()).isEqualTo("{\"probability\":0.123456789012345678901}");
        assertThatThrownBy(() -> new TournamentGameEvent(0, "bid", "{\"x\":1,\"x\":2}"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TournamentGameEvent(0, "bid", "{} {}"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
