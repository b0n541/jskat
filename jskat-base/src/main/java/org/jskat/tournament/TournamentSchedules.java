package org.jskat.tournament;

import org.jskat.util.Card;
import org.jskat.util.Player;

import java.util.*;

/** Version-one canonical tuples and SplitMix64 Fisher–Yates decks. */
public final class TournamentSchedules {
    public static final String VERSION = "jskat-schedule-v1",
            DECK_VERSION = "splitmix64-fisher-yates-v1";

    private TournamentSchedules() {}

    public static List<ScheduledTournamentGame> generate(
            List<UUID> roster, long epochs, long rootSeed) {
        int count = Math.toIntExact(TournamentScheduleSize.gameCount(roster.size(), epochs));
        var ids = roster.stream().sorted(Comparator.comparing(UUID::toString)).toList();
        if (new HashSet<>(ids).size() != ids.size())
            throw new IllegalArgumentException("Duplicate competitor UUID");
        var result = new ArrayList<ScheduledTournamentGame>(count);
        for (long epoch = 0; epoch < epochs; epoch++) {
            long deckSeed = TournamentIdentities.deriveSeed(rootSeed, "deck", "epoch:" + epoch);
            var deck = new ArrayList<>(List.of(Card.values()));
            var random = new SplitMix(deckSeed);
            for (int i = 31; i > 0; i--) Collections.swap(deck, i, random.bounded(i + 1));
            if (ids.size() == 2) {
                UUID a = ids.get(0), b = ids.get(1);
                add(
                        result,
                        epoch,
                        List.of(a, b),
                        List.of(
                                List.of(a, a, b),
                                List.of(a, b, a),
                                List.of(b, a, a),
                                List.of(a, b, b),
                                List.of(b, a, b),
                                List.of(b, b, a)),
                        deck,
                        deckSeed,
                        rootSeed);
            } else
                for (int a = 0; a < ids.size() - 2; a++)
                    for (int b = a + 1; b < ids.size() - 1; b++)
                        for (int c = b + 1; c < ids.size(); c++) {
                            UUID x = ids.get(a), y = ids.get(b), z = ids.get(c);
                            add(
                                    result,
                                    epoch,
                                    List.of(x, y, z),
                                    List.of(
                                            List.of(x, y, z),
                                            List.of(x, z, y),
                                            List.of(y, x, z),
                                            List.of(y, z, x),
                                            List.of(z, x, y),
                                            List.of(z, y, x)),
                                    deck,
                                    deckSeed,
                                    rootSeed);
                        }
        }
        return List.copyOf(result);
    }

    private static void add(
            List<ScheduledTournamentGame> out,
            long epoch,
            List<UUID> group,
            List<List<UUID>> assignments,
            List<Card> deck,
            long deckSeed,
            long seed) {
        var tuple = CanonicalJson.object();
        tuple.put("encoding", VERSION);
        tuple.put("epoch", epoch);
        var grouping = tuple.putArray("group");
        group.forEach(id -> grouping.add(id.toString()));
        String block = CanonicalJson.sha256(CanonicalJson.bytes(tuple));
        for (int i = 0; i < assignments.size(); i++) {
            tuple.put("assignment", i);
            String game = CanonicalJson.sha256(CanonicalJson.bytes(tuple));
            var seats = new ArrayList<TournamentSeat>();
            for (Player p : Player.values())
                if (p.getOrder() < 3)
                    seats.add(
                            new TournamentSeat(
                                    p,
                                    assignments.get(i).get(p.getOrder()),
                                    TournamentIdentities.deriveSeed(
                                            seed,
                                            "player-instance",
                                            game,
                                            "seat:" + p.getOrder())));
            out.add(new ScheduledTournamentGame(game, epoch, block, seats, deck, deckSeed));
        }
    }

    private static final class SplitMix {
        private long state;

        SplitMix(long seed) {
            state = seed;
        }

        long next() {
            long z = (state += 0x9e3779b97f4a7c15L);
            z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
            z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
            return z ^ (z >>> 31);
        }

        int bounded(int bound) {
            long bits, value;
            do {
                bits = next() >>> 1;
                value = bits % bound;
            } while (bits - value + (bound - 1) < 0);
            return (int) value;
        }
    }
}
