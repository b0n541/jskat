package org.jskat.tournament;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.jskat.util.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Regenerable views from a single verified committed observation; never completion authority. */
public final class TournamentReports {
    private TournamentReports() {}

    /** Structured failed-attempt fallback evidence is permanent across resumed sessions. */
    public static List<JsonNode> retainedFallbacks(String historyJson) {
        var result = new ArrayList<JsonNode>();
        for (var diagnostic : CanonicalJson.parse(historyJson).path("diagnostics"))
            for (var fallback : diagnostic.path("payload").path("detail").path("fallbacks"))
                result.add(fallback.deepCopy());
        return List.copyOf(result);
    }

    public static final String VERSION = "jskat-rating-export-v1";
    public static final String DIAGNOSTICS_VERSION = "jskat-loeo-ten-bins-v1";

    public record Report(String summaryJson, String leaderboardCsv, String gameScoresCsv) {
        public Map<String, byte[]> exports() {
            return Map.of(
                    "summary.json",
                    summaryJson.getBytes(StandardCharsets.UTF_8),
                    "leaderboard.csv",
                    leaderboardCsv.getBytes(StandardCharsets.UTF_8),
                    "game-scores.csv",
                    gameScoresCsv.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static DurableTournamentRunStore.ReportGeneration publish(
            DurableTournamentRunStore store, JsonNode metadata) throws IOException {
        var set = store.captureCommittedSet();
        var report =
                calculate(
                        store.configuration(),
                        store.schedule(),
                        set,
                        store.historyJson(),
                        metadata);
        return store.publishReports(set, report.exports(), metadata);
    }

    /** Resolves current generation once through the durable store. */
    public static Report read(DurableTournamentRunStore store) throws IOException {
        var files = store.readCurrentReports().exports();
        return new Report(
                text(files, "summary.json"),
                text(files, "leaderboard.csv"),
                text(files, "game-scores.csv"));
    }

    private static String text(Map<String, byte[]> files, String name) {
        return new String(files.get(name), StandardCharsets.UTF_8);
    }

    public static Report calculate(
            ResolvedTournamentConfiguration configuration,
            List<ScheduledTournamentGame> schedule,
            DurableTournamentRunStore.CommittedSet set,
            String historyJson,
            JsonNode metadata) {
        var ids =
                configuration.competitors().stream()
                        .map(c -> c.requested().id())
                        .sorted(Comparator.comparing(UUID::toString))
                        .toList();
        var games = new HashMap<String, TournamentGameResult>();
        for (var g : set.games())
            if (games.put(g.gameId(), g) != null)
                throw new IllegalArgumentException("Duplicate committed game");
        var expected = new TreeMap<Long, Integer>();
        var actual = new TreeMap<Long, Integer>();
        var blocks = new TreeMap<Long, Map<String, Map<UUID, Long>>>();
        var ordered =
                schedule.stream()
                        .sorted(
                                Comparator.comparingLong(ScheduledTournamentGame::epoch)
                                        .thenComparing(ScheduledTournamentGame::blockId)
                                        .thenComparing(ScheduledTournamentGame::gameId))
                        .toList();
        var stats = new LinkedHashMap<UUID, ObjectNode>();
        for (var id : ids) stats.put(id, newStats());
        StringBuilder scores =
                new StringBuilder(
                        "gameId,epoch,blockId,seat,competitorId,score,role,won,allPassed,contract\n");
        var fallback = CanonicalJson.object().putArray("records");
        for (var game : ordered) {
            expected.merge(game.epoch(), 1, Integer::sum);
            var result = games.remove(game.gameId());
            if (result == null) continue;
            actual.merge(game.epoch(), 1, Integer::sum);
            var totals =
                    blocks.computeIfAbsent(game.epoch(), k -> new TreeMap<>())
                            .computeIfAbsent(
                                    game.blockId(),
                                    k -> new TreeMap<>(Comparator.comparing(UUID::toString)));
            var outcome = result.outcome();
            for (var seat : game.seats()) {
                UUID id = seat.competitorId();
                long score = result.seatScores().get(seat.seat());
                totals.merge(id, score, Math::addExact);
                var s = stats.get(id);
                increment(s, "seatAppearances", 1);
                increment(s, "scoreTotal", score);
                if (outcome.allPassed()) increment(s, "allPassAppearances", 1);
                String role =
                        outcome.allPassed()
                                ? "all-pass"
                                : seat.seat() == outcome.declarer() ? "declarer" : "defender";
                boolean won =
                        !outcome.allPassed() && (role.equals("declarer") == outcome.declarerWon());
                if (!outcome.allPassed()) {
                    increment(s, role + "Appearances", 1);
                    if (role.equals("declarer"))
                        increment(
                                (ObjectNode) s.get("declarerContracts"),
                                outcome.contract().gameType().name(),
                                1);
                    increment(s, role + (won ? "Wins" : "Losses"), 1);
                    increment(
                            (ObjectNode) s.get("contracts"),
                            outcome.contract().gameType().name(),
                            1);
                    increment(
                            (ObjectNode) s.get("declarations"),
                            "hand",
                            outcome.contract().hand() ? 1 : 0);
                    increment(
                            (ObjectNode) s.get("declarations"),
                            "ouvert",
                            outcome.contract().ouvert() ? 1 : 0);
                    increment(
                            (ObjectNode) s.get("declarations"),
                            "schneiderAnnounced",
                            outcome.contract().schneiderAnnounced() ? 1 : 0);
                    increment(
                            (ObjectNode) s.get("declarations"),
                            "schwarzAnnounced",
                            outcome.contract().schwarzAnnounced() ? 1 : 0);
                    increment(s, role + "ScoreTotal", score);
                    increment(s, "cardPoints", outcome.cardPoints().get(seat.seat()));
                    increment(s, "tricks", outcome.tricks().get(seat.seat()));
                }
                csv(
                        scores,
                        game.gameId(),
                        game.epoch(),
                        game.blockId(),
                        seat.seat(),
                        id,
                        score,
                        role,
                        outcome.allPassed() ? "" : won,
                        outcome.allPassed(),
                        outcome.allPassed() ? "" : outcome.contract().gameType());
            }
            for (var event : result.events())
                if (Set.of("BidEvent", "HoldBidEvent", "PassBidEvent").contains(event.type())) {
                    var payload = CanonicalJson.parse(event.payloadJson());
                    Player seat = Player.valueOf(payload.path("seat").asText());
                    UUID id =
                            game.seats().stream()
                                    .filter(s -> s.seat() == seat)
                                    .findFirst()
                                    .orElseThrow()
                                    .competitorId();
                    var s = stats.get(id);
                    increment((ObjectNode) s.get("bidding"), event.type(), 1);
                    if (!event.type().equals("PassBidEvent"))
                        s.put(
                                "maximumAcceptedBid",
                                Math.max(
                                        s.path("maximumAcceptedBid").asLong(),
                                        payload.path("bid").asLong()));
                }
            for (var f : result.fallbacks()) fallback.add(TournamentRecords.tree(f));
        }
        if (!games.isEmpty()) throw new IllegalArgumentException("Committed game outside schedule");
        var epochs =
                expected.keySet().stream()
                        .filter(e -> expected.get(e).equals(actual.get(e)))
                        .toList();
        if (!epochs.equals(set.completeEpochs()))
            throw new IllegalArgumentException(
                    "Committed epoch observation disagrees with schedule");
        var observations = new ArrayList<TournamentRatings.Observation>();
        var blockRows = CanonicalJson.object().putArray("rows");
        for (long epoch : epochs)
            for (var entry : blocks.get(epoch).entrySet()) {
                var totals = entry.getValue();
                var row = blockRows.addObject();
                row.put("epoch", epoch);
                row.put("blockId", entry.getKey());
                row.set("totals", TournamentRecords.tree(totals));
                var competitors = new ArrayList<>(totals.keySet());
                for (int i = 0; i < competitors.size(); i++)
                    for (int j = i + 1; j < competitors.size(); j++) {
                        UUID a = competitors.get(i), b = competitors.get(j);
                        int comparison = Long.compare(totals.get(a), totals.get(b));
                        observations.add(
                                new TournamentRatings.Observation(
                                        epoch,
                                        entry.getKey(),
                                        a,
                                        b,
                                        comparison > 0 ? 1 : comparison < 0 ? 0 : .5));
                    }
            }
        var ratings = TournamentRatings.fit(ids, observations);
        long bootstrapSeed =
                TournamentIdentities.deriveSeed(
                        configuration.effectiveSeed(), "bootstrap", "ratings-v1");
        var intervals = TournamentRatings.intervals(ids, observations, epochs, bootstrapSeed);
        var summary = CanonicalJson.object();
        summary.put("schemaVersion", 1);
        summary.put("exportVersion", VERSION);
        summary.put("runId", set.runId().toString());
        summary.put("committedSetDigest", set.digest());
        summary.set("gameChecksums", TournamentRecords.tree(set.gameChecksums()));
        summary.put(
                "requestedDigest", TournamentIdentities.requestedDigest(configuration.requested()));
        summary.put("resolvedDigest", TournamentIdentities.resolvedDigest(configuration));
        summary.set(
                "configuration",
                CanonicalJson.parse(TournamentIdentities.resolvedCanonicalJson(configuration)));
        summary.set(
                "requestedConfiguration",
                CanonicalJson.parse(
                        TournamentIdentities.requestedCanonicalJson(configuration.requested())));
        summary.put("originalConfiguration", configuration.requested().originalJson());
        summary.put("configurationFile", configuration.requested().configurationFile().toString());
        summary.set("lifecycle", metadata.deepCopy());
        summary.set("history", CanonicalJson.parse(historyJson));
        boolean complete = set.games().size() == schedule.size();
        summary.put("provisional", !complete);
        summary.put("hasRatedEvidence", !epochs.isEmpty());
        summary.put("committedGames", set.games().size());
        summary.put("scheduledGames", schedule.size());
        summary.put(
                "partialEpochGames",
                set.games().size() - epochs.stream().mapToInt(expected::get).sum());
        summary.set("includedEpochs", TournamentRecords.tree(epochs));
        retainedFallbacks(historyJson).forEach(fallback::add);
        summary.put("reproducible", fallback.isEmpty());
        summary.set("inferenceFallbacks", fallback);
        var params = summary.putObject("ratingParameters");
        params.put("algorithm", TournamentRatings.VERSION);
        params.put("gradientInfinityTolerance", 1e-10);
        params.put("newtonStepInfinityTolerance", 1e-12);
        params.put("maximumNewtonIterations", 100);
        params.put("maximumBacktracks", 50);
        params.put("armijoCoefficient", 1e-4);
        params.put("objectiveRoundoffAllowance", 1e-14);
        params.put("objectiveRoundoffUlps", 16);
        params.put("bootstrapPercentileConvention", "type-7-linear-interpolation");
        params.put("bootstrapEpochCount", epochs.size());
        params.put("calibrationBins", 10);
        params.put("center", 1500);
        params.put("eloScale", 400);
        params.put("priorDeviationStandardDeviation", 400);
        params.put("bootstrapAlgorithm", TournamentRatings.BOOTSTRAP_VERSION);
        params.put("bootstrapSeed", Long.toUnsignedString(bootstrapSeed));
        params.put("bootstrapReplicates", TournamentRatings.REPLICATES);
        params.put("intervalLevel", .95);
        params.put("diagnosticsAlgorithm", DIAGNOSTICS_VERSION);
        summary.set("blockTotals", blockRows);
        summary.set("observations", TournamentRecords.tree(observations));
        var leaders = summary.putArray("leaderboard");
        StringBuilder leaderboard =
                new StringBuilder(
                        "rank,competitorId,name,implementation,rating,intervalLower,intervalUpper,provisional,hasRatedEvidence\n");
        var sorted =
                ids.stream()
                        .sorted(
                                Comparator.<UUID>comparingDouble(ratings::get)
                                        .reversed()
                                        .thenComparing(UUID::toString))
                        .toList();
        int rank = 0, position = 0;
        Double previous = null;
        for (var id : sorted) {
            position++;
            double rating = ratings.get(id);
            if (previous == null || Double.compare(previous, rating) != 0) rank = position;
            previous = rating;
            var competitor =
                    configuration.competitors().stream()
                            .filter(c -> c.requested().id().equals(id))
                            .findFirst()
                            .orElseThrow();
            var row = leaders.addObject();
            row.put("rank", rank);
            row.put("competitorId", id.toString());
            row.put("name", competitor.requested().name());
            row.put("implementation", competitor.requested().implementation().id());
            if (epochs.isEmpty()) row.putNull("rating");
            else row.put("rating", rating);
            if (intervals.isPresent())
                row.set("interval", TournamentRecords.tree(intervals.get().get(id)));
            else row.putNull("interval");
            row.put(
                    "intervalUnavailableReason",
                    epochs.size() < 2 ? "fewer-than-two-complete-epochs" : "");
            csv(
                    leaderboard,
                    rank,
                    id,
                    competitor.requested().name(),
                    competitor.requested().implementation().id(),
                    epochs.isEmpty() ? "" : rating,
                    intervals.isEmpty() ? "" : intervals.get().get(id).get(0),
                    intervals.isEmpty() ? "" : intervals.get().get(id).get(1),
                    !complete,
                    !epochs.isEmpty());
        }
        var descriptive = summary.putArray("descriptive");
        for (var id : ids) {
            var s = stats.get(id);
            s.put("competitorId", id.toString());
            rate(s, "declarer");
            rate(s, "defender");
            ratio(
                    s,
                    "meanScore",
                    s.path("scoreTotal").asLong(),
                    s.path("seatAppearances").asLong());
            descriptive.add(s);
        }
        summary.put(
                "descriptivePopulation",
                "all committed seat appearances, including partial epochs; contracts count seat"
                        + " appearances, bidding counts accepted audit actions");
        var pairs = summary.putArray("pairwise");
        long ties = 0;
        for (int i = 0; i < ids.size(); i++)
            for (int j = i + 1; j < ids.size(); j++) {
                UUID a = ids.get(i), b = ids.get(j);
                var row = pairs.addObject();
                row.put("first", a.toString());
                row.put("second", b.toString());
                long wins = 0, losses = 0, tied = 0;
                for (var o : observations)
                    if (o.first().equals(a) && o.second().equals(b)) {
                        if (o.outcome() == 1) wins++;
                        else if (o.outcome() == 0) losses++;
                        else tied++;
                    }
                row.put("comparisons", wins + losses + tied);
                row.put("wins", wins);
                row.put("losses", losses);
                row.put("ties", tied);
                if (epochs.isEmpty()) row.putNull("estimatedWinProbability");
                else
                    row.put(
                            "estimatedWinProbability",
                            TournamentRatings.probability(ratings.get(a), ratings.get(b)));
                ties += tied;
            }
        long tiedBlocks =
                blockRows.findValues("totals").stream()
                        .filter(
                                t -> {
                                    var values = new ArrayList<Long>();
                                    t.elements().forEachRemaining(v -> values.add(v.asLong()));
                                    return new HashSet<>(values).size() < values.size();
                                })
                        .count();
        ratio(summary, "blockTieRate", tiedBlocks, blockRows.size());
        summary.put("blockTieDenominator", blockRows.size());
        summary.put("tiedBlocks", tiedBlocks);
        ratio(summary, "blockPairTieRate", ties, observations.size());
        summary.put("blockPairTieDenominator", observations.size());
        summary.set("predictive", predictive(ids, observations, epochs));
        summary.putArray("interpretationLimits")
                .add(
                        "Ratings are roster-relative ordinal comparisons: score margins are"
                                + " discarded.")
                .add(
                        "Partner effects and nontransitive matchups may not be represented by one"
                                + " scalar rating.")
                .add("Bootstrap intervals resample whole deals and are not a stopping criterion.")
                .add("Role and bidding breakdowns are descriptive, not separate ratings.")
                .add("Final means every scheduled game committed, not narrow uncertainty.");
        return new Report(CanonicalJson.encode(summary), leaderboard.toString(), scores.toString());
    }

    private static ObjectNode predictive(
            List<UUID> ids, List<TournamentRatings.Observation> observations, List<Long> epochs) {
        var result = CanonicalJson.object();
        result.put("available", epochs.size() >= 2);
        result.put("version", DIAGNOSTICS_VERSION);
        var bins = result.putArray("calibration");
        if (epochs.size() < 2) {
            result.putNull("logLoss");
            result.put("unavailableReason", "fewer-than-two-complete-epochs");
            result.put("observations", 0);
            return result;
        }
        double loss = 0;
        long count = 0;
        long[] n = new long[10];
        double[] predicted = new double[10], observed = new double[10];
        for (long epoch : epochs) {
            var fit =
                    TournamentRatings.fit(
                            ids, observations.stream().filter(o -> o.epoch() != epoch).toList());
            for (var o : observations)
                if (o.epoch() == epoch) {
                    double p =
                            TournamentRatings.probability(fit.get(o.first()), fit.get(o.second()));
                    if (!(p > 0 && p < 1))
                        throw new IllegalStateException("Nonfinite predictive log loss");
                    loss -=
                            o.outcome() * StrictMath.log(p)
                                    + (1 - o.outcome()) * StrictMath.log1p(-p);
                    count++;
                    int bin = Math.min(9, (int) (p * 10));
                    n[bin]++;
                    predicted[bin] += p;
                    observed[bin] += o.outcome();
                }
        }
        result.put("observations", count);
        ratio(result, "logLoss", loss, count);
        for (int i = 0; i < 10; i++) {
            var b = bins.addObject();
            b.put("lowerInclusive", i / 10.0);
            b.put("upper", (i + 1) / 10.0);
            b.put("upperInclusive", i == 9);
            b.put("count", n[i]);
            ratio(b, "meanPredicted", predicted[i], n[i]);
            ratio(b, "meanObserved", observed[i], n[i]);
        }
        return result;
    }

    private static ObjectNode newStats() {
        var n = CanonicalJson.object();
        for (String key :
                List.of(
                        "seatAppearances",
                        "scoreTotal",
                        "allPassAppearances",
                        "declarerAppearances",
                        "declarerWins",
                        "declarerLosses",
                        "defenderAppearances",
                        "defenderWins",
                        "defenderLosses",
                        "declarerScoreTotal",
                        "defenderScoreTotal",
                        "maximumAcceptedBid",
                        "cardPoints",
                        "tricks")) n.put(key, 0);
        n.putObject("declarerContracts");
        n.putObject("contracts");
        n.putObject("declarations");
        n.putObject("bidding");
        return n;
    }

    private static void increment(ObjectNode n, String field, long amount) {
        n.put(field, Math.addExact(n.path(field).asLong(), amount));
    }

    private static void rate(ObjectNode n, String role) {
        ratio(
                n,
                role + "WinRate",
                n.path(role + "Wins").asLong(),
                n.path(role + "Appearances").asLong());
    }

    private static void ratio(ObjectNode n, String field, double total, long count) {
        if (count == 0) n.putNull(field);
        else n.put(field, total / count);
    }

    private static void csv(StringBuilder out, Object... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) out.append(',');
            String value = Objects.toString(values[i], "");
            out.append('"').append(value.replace("\"", "\"\"")).append('"');
        }
        out.append('\n');
    }
}
