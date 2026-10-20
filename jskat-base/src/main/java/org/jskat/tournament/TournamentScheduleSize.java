package org.jskat.tournament;

/** Version one retains an int-indexed schedule; reject unaddressable sizes before allocation. */
public final class TournamentScheduleSize {
    private TournamentScheduleSize() {}

    public static long gameCount(int competitors, long epochs) {
        if (competitors < 2 || epochs < 1)
            throw new IllegalArgumentException(
                    "At least two competitors and positive epochs required");
        long perEpoch =
                competitors == 2
                        ? 6
                        : Math.multiplyExact(
                                Math.multiplyExact((long) competitors, competitors - 1),
                                competitors - 2);
        long count = Math.multiplyExact(perEpoch, epochs);
        if (count > Integer.MAX_VALUE)
            throw new IllegalArgumentException(
                    "Schedule exceeds signed-int indexed representation");
        return count;
    }
}
