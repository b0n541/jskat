package org.jskat.util;

import org.jskat.AbstractJSkatTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for class Card
 */
public class SkatConstantsTest extends AbstractJSkatTest {

    private static Stream<Arguments> testCasesForTournamentGameValue() {
        return Stream.of(
                Arguments.of(true, 18, 3, 68),
                Arguments.of(true, 18, 4, 68),
                Arguments.of(true, -36, 3, -86),
                Arguments.of(true, -36, 4, -86),
                Arguments.of(true, 20, 3, 70),
                Arguments.of(true, 20, 4, 70),
                Arguments.of(true, -40, 3, -90),
                Arguments.of(true, -40, 4, -90),
                Arguments.of(false, 18, 3, 0),
                Arguments.of(false, 18, 4, 0),
                Arguments.of(false, -36, 3, 40),
                Arguments.of(false, -36, 4, 30),
                Arguments.of(false, 20, 3, 0),
                Arguments.of(false, 20, 4, 0),
                Arguments.of(false, -40, 3, 40),
                Arguments.of(false, -40, 4, 30));
    }

    /**
     * Tests calculation of game values after tournament rules
     */
    @ParameterizedTest
    @MethodSource("testCasesForTournamentGameValue")
    public void tournamentGameValue(boolean isDeclarer, int gameValue, int numberOfPlayers, int expectedTournamentValue) {
        assertThat(SkatConstants.getTournamentGameValue(isDeclarer, gameValue, numberOfPlayers)).isEqualTo(expectedTournamentValue);
    }

    private static Stream<Arguments> testCasesForNextBidValue() {
        return Stream.of(
                Arguments.of(0, 18),
                Arguments.of(18, 20),
                Arguments.of(20, 22),
                Arguments.of(22, 23),
                Arguments.of(216, 240),
                Arguments.of(240, 264),
                Arguments.of(264, 264));
    }

    @ParameterizedTest
    @MethodSource("testCasesForNextBidValue")
    public void nextBidValue(int currBidValue, int expectedNextBidValue) {
        assertThat(SkatConstants.getNextBidValue(currBidValue)).isEqualTo(expectedNextBidValue);
    }
}
