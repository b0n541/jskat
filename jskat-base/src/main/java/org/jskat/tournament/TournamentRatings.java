package org.jskat.tournament;

import java.util.*;

/** Batch MAP Bradley–Terry; all public inputs use canonical competitor identities. */
public final class TournamentRatings {
    private TournamentRatings() {}

    public static final String VERSION = "jskat-bt-map-newton-v1";
    public static final String BOOTSTRAP_VERSION = "jskat-epoch-splitmix64-percentile7-v1";
    public static final int REPLICATES = 5000;

    public record Observation(long epoch, String blockId, UUID first, UUID second, double outcome) {
        public Observation {
            if (epoch < 0
                    || first.equals(second)
                    || !(outcome == 0 || outcome == .5 || outcome == 1))
                throw new IllegalArgumentException("Invalid pair observation");
        }
    }

    public static double probability(double first, double second) {
        return logistic((first - second) * StrictMath.log(10) / 400);
    }

    private static double logistic(double z) {
        if (z >= 0) return 1 / (1 + StrictMath.exp(-z));
        double e = StrictMath.exp(z);
        return e / (1 + e);
    }

    public static Map<UUID, Double> fit(List<UUID> roster, List<Observation> observations) {
        var ids = roster.stream().sorted(Comparator.comparing(UUID::toString)).toList();
        if (new HashSet<>(ids).size() != ids.size())
            throw new IllegalArgumentException("Duplicate roster");
        var index = new HashMap<UUID, Integer>();
        for (int i = 0; i < ids.size(); i++) index.put(ids.get(i), i);
        // Aggregate in fixed order to make input completion order immaterial.
        var pairs = new TreeMap<String, double[]>();
        for (var o : observations) {
            Integer a = index.get(o.first()), b = index.get(o.second());
            if (a == null || b == null) throw new IllegalArgumentException("Unknown competitor");
            double outcome = o.outcome();
            if (a > b) {
                int t = a;
                a = b;
                b = t;
                outcome = 1 - outcome;
            }
            var p = pairs.computeIfAbsent(a + ":" + b, k -> new double[4]);
            p[0] = a;
            p[1] = b;
            p[2]++;
            p[3] += outcome;
        }
        int n = ids.size();
        double[] x = new double[n];
        double precision = 1 / (StrictMath.log(10) * StrictMath.log(10));
        boolean converged = false;
        for (int iteration = 0; iteration < 100; iteration++) {
            double[] g = new double[n];
            double[][] h = new double[n][n];
            for (int i = 0; i < n; i++) {
                g[i] = precision * x[i];
                h[i][i] = precision;
            }
            for (var p : pairs.values()) {
                int a = (int) p[0], b = (int) p[1];
                double probability = logistic(x[a] - x[b]);
                double residual = p[2] * probability - p[3],
                        w = p[2] * probability * (1 - probability);
                g[a] += residual;
                g[b] -= residual;
                h[a][a] += w;
                h[b][b] += w;
                h[a][b] -= w;
                h[b][a] -= w;
            }
            double max = 0;
            for (double v : g) max = Math.max(max, Math.abs(v));
            if (max < 1e-10) {
                converged = true;
                break;
            }
            double[] step = solve(h, g);
            double before = objective(x, pairs.values(), precision), directional = 0;
            for (int i = 0; i < n; i++) directional += g[i] * step[i];
            boolean accepted = false;
            for (int backtrack = 0; backtrack < 50; backtrack++) {
                double alpha = Math.scalb(1.0, -backtrack);
                double[] candidate = x.clone();
                for (int i = 0; i < n; i++) candidate[i] -= alpha * step[i];
                if (objective(candidate, pairs.values(), precision)
                        <= before - 1e-4 * alpha * directional + 1e-14) {
                    x = candidate;
                    accepted = true;
                    break;
                }
            }
            if (!accepted) throw new IllegalStateException("Rating Newton line search failed");
        }
        if (!converged)
            throw new IllegalStateException(
                    "Rating Newton convergence failed after 100 iterations");
        double mean = Arrays.stream(x).average().orElse(0);
        var result = new LinkedHashMap<UUID, Double>();
        for (int i = 0; i < n; i++) {
            double r = 1500 + (x[i] - mean) * 400 / StrictMath.log(10);
            if (!Double.isFinite(r)) throw new IllegalStateException("Nonfinite rating");
            result.put(ids.get(i), r);
        }
        return Collections.unmodifiableMap(result);
    }

    private static double objective(double[] x, Collection<double[]> pairs, double precision) {
        double value = 0;
        for (double v : x) value += precision * v * v / 2;
        for (var p : pairs) {
            double z = x[(int) p[0]] - x[(int) p[1]];
            value +=
                    p[2] * (Math.max(z, 0) + StrictMath.log1p(StrictMath.exp(-Math.abs(z))))
                            - p[3] * z;
        }
        return value;
    }

    private static double[] solve(double[][] h, double[] g) {
        int n = g.length;
        double[][] l = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j <= i; j++) {
                double v = h[i][j];
                for (int k = 0; k < j; k++) v -= l[i][k] * l[j][k];
                l[i][j] = i == j ? StrictMath.sqrt(v) : v / l[j][j];
                if (!Double.isFinite(l[i][j]))
                    throw new IllegalStateException("Rating Hessian failure");
            }
        double[] y = g.clone();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) y[i] -= l[i][j] * y[j];
            y[i] /= l[i][i];
        }
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) y[i] -= l[j][i] * y[j];
            y[i] /= l[i][i];
        }
        return y;
    }

    /** Exactly 5000 whole-epoch resamples. Empty Optional means fewer than two epochs. */
    public static Optional<Map<UUID, List<Double>>> intervals(
            List<UUID> ids, List<Observation> observations, List<Long> epochs, long seed) {
        epochs = epochs.stream().sorted().toList();
        if (new HashSet<>(epochs).size() != epochs.size() || epochs.stream().anyMatch(e -> e < 0))
            throw new IllegalArgumentException("Invalid epoch inventory");
        if (epochs.size() < 2) return Optional.empty();
        var byEpoch = new HashMap<Long, List<Observation>>();
        for (long e : epochs) byEpoch.put(e, new ArrayList<>());
        for (var o : observations) {
            var epoch = byEpoch.get(o.epoch());
            if (epoch == null)
                throw new IllegalArgumentException("Observation outside bootstrap epochs");
            epoch.add(o);
        }
        var samples = new HashMap<UUID, double[]>();
        for (var id : ids) samples.put(id, new double[REPLICATES]);
        var rng = new SplitMix(seed);
        for (int replicate = 0; replicate < REPLICATES; replicate++) {
            var sampled = new ArrayList<Observation>();
            for (int j = 0; j < epochs.size(); j++)
                sampled.addAll(byEpoch.get(epochs.get(rng.index(epochs.size()))));
            var ratings = fit(ids, sampled);
            for (var id : ids) samples.get(id)[replicate] = ratings.get(id);
        }
        var result = new LinkedHashMap<UUID, List<Double>>();
        for (var id : ids.stream().sorted(Comparator.comparing(UUID::toString)).toList()) {
            double[] s = samples.get(id);
            Arrays.sort(s);
            result.put(id, List.of(percentile(s, .025), percentile(s, .975)));
        }
        return Optional.of(Collections.unmodifiableMap(result));
    }

    private static double percentile(double[] values, double q) {
        double at = (values.length - 1) * q;
        int lo = (int) at;
        return values[lo] + (values[Math.min(lo + 1, values.length - 1)] - values[lo]) * (at - lo);
    }

    private static final class SplitMix {
        long state;

        SplitMix(long state) {
            this.state = state;
        }

        long next() {
            long z = (state += 0x9e3779b97f4a7c15L);
            z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
            z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
            return z ^ (z >>> 31);
        }

        int index(int bound) {
            long bits, value;
            do {
                bits = next() >>> 1;
                value = bits % bound;
            } while (bits - value + bound - 1 < 0);
            return (int) value;
        }
    }
}
