package org.jskat.tournament;

/** Version-one SplitMix64 with unbiased bounded selection. Callers derive purpose-specific seeds. */
final class TournamentRandomV1 {
    private long state;

    TournamentRandomV1(long seed) {
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
