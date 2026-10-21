package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class TournamentScheduleTest {
    @Test
    void twoCompetitorsHaveCanonicalSixMixedAssignmentsAndOneDeck() {
        var a = UUID.fromString("11111111-1111-4111-8111-111111111111");
        var b = UUID.fromString("ffffffff-ffff-4fff-8fff-ffffffffffff");
        var games = TournamentSchedules.generate(List.of(b, a), 1, 0);
        assertThat(games).hasSize(6);
        assertThat(
                        games.stream()
                                .map(
                                        g ->
                                                g.seats().stream()
                                                        .map(
                                                                s ->
                                                                        s.competitorId().equals(a)
                                                                                ? "A"
                                                                                : "B")
                                                        .reduce("", String::concat)))
                .containsExactly("AAB", "ABA", "BAA", "ABB", "BAB", "BBA");
        assertThat(games.stream().map(ScheduledTournamentGame::deck).distinct()).hasSize(1);
        assertThat(games.stream().map(ScheduledTournamentGame::gameId).distinct()).hasSize(6);
    }

    @Test
    void fourCompetitorsCoverEveryTripleWithOneSharedDeckAndBalancedSeats() {
        var ids = java.util.stream.IntStream.rangeClosed(1, 4).mapToObj(i -> new UUID(0, i)).toList();
        var schedule = TournamentSchedules.generate(ids.reversed(), 2, 42);
        assertThat(schedule).hasSize(48);
        assertThat(schedule.stream().map(ScheduledTournamentGame::gameId).distinct()).hasSize(48);
        for (long epoch : List.of(0L, 1L)) {
            var games = schedule.stream().filter(g -> g.epoch() == epoch).toList();
            assertThat(games.stream().map(ScheduledTournamentGame::deck).distinct()).hasSize(1);
            assertThat(games.stream().map(ScheduledTournamentGame::blockId).distinct()).hasSize(4);
            for (var id : ids) {
                var seats = games.stream().flatMap(g -> g.seats().stream()).filter(s -> s.competitorId().equals(id)).toList();
                assertThat(seats).hasSize(18);
                for (var seat : org.jskat.util.Player.getOrderedList()) assertThat(seats.stream().filter(s -> s.seat() == seat)).hasSize(6);
            }
            for (var block : games.stream().collect(java.util.stream.Collectors.groupingBy(ScheduledTournamentGame::blockId)).values()) {
                assertThat(block).hasSize(6);
                assertThat(block.stream().map(g -> g.seats().stream().map(TournamentSeat::competitorId).toList()).distinct()).hasSize(6);
            }
        }
        assertThat(schedule.getFirst().deck()).isNotEqualTo(schedule.getLast().deck());
        assertThat(schedule.stream().flatMap(g -> g.seats().stream()).map(TournamentSeat::seed).distinct()).hasSize(144);
    }

    @Test
    void exactGoldenSchedulesUseIndependentPythonFixtures() throws Exception {
        var ids =
                List.of(
                        UUID.fromString("11111111-1111-4111-8111-111111111111"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888"),
                        UUID.fromString("ffffffff-ffff-4fff-8fff-ffffffffffff"));
        for (int n : List.of(2, 3)) {
            var roster = n == 2 ? List.of(ids.get(0), ids.get(2)) : ids;
            try (var input = getClass().getResourceAsStream("schedule-" + n + "-v1.json")) {
                assertThat(
                                CanonicalJson.encode(
                                        TournamentRecords.tree(
                                                TournamentSchedules.generate(roster, 1, 0))))
                        .isEqualTo(
                                new String(
                                        input.readAllBytes(),
                                        java.nio.charset.StandardCharsets.UTF_8));
            }
        }
    }
}
