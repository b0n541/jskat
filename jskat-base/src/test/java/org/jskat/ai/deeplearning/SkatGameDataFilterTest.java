package org.jskat.ai.deeplearning;

import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameResult;
import org.jskat.util.Player;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SkatGameDataFilterTest {

    private static final SkatGameData KERMIT_FOREHAND;
    private static final SkatGameData KERMIT_MIDDLEHAND;
    private static final SkatGameData KERMIT_REARHAND;
    private static final SkatGameData ZOOT_FOREHAND;
    private static final SkatGameData KERMIT_LOST;

    static {
        final var wonGame = new SkatGameResult();
        wonGame.setWon(true);
        final var lostGame = new SkatGameResult();
        lostGame.setWon(false);

        KERMIT_FOREHAND = new SkatGameData();
        KERMIT_FOREHAND.setPlayerName(Player.FOREHAND, "kermit");
        KERMIT_FOREHAND.setDeclarer(Player.FOREHAND);
        KERMIT_FOREHAND.setResult(wonGame);

        KERMIT_MIDDLEHAND = new SkatGameData();
        KERMIT_MIDDLEHAND.setPlayerName(Player.MIDDLEHAND, "kermit:1");
        KERMIT_MIDDLEHAND.setDeclarer(Player.MIDDLEHAND);
        KERMIT_MIDDLEHAND.setResult(wonGame);

        KERMIT_REARHAND = new SkatGameData();
        KERMIT_REARHAND.setPlayerName(Player.REARHAND, "kermit:2");
        KERMIT_REARHAND.setDeclarer(Player.REARHAND);
        KERMIT_REARHAND.setResult(wonGame);

        ZOOT_FOREHAND = new SkatGameData();
        ZOOT_FOREHAND.setPlayerName(Player.FOREHAND, "zoot");
        ZOOT_FOREHAND.setDeclarer(Player.FOREHAND);
        ZOOT_FOREHAND.setResult(wonGame);

        KERMIT_LOST = new SkatGameData();
        KERMIT_LOST.setPlayerName(Player.FOREHAND, "kermit");
        KERMIT_LOST.setDeclarer(Player.FOREHAND);
        KERMIT_LOST.setResult(lostGame);
    }

    @Test
    public void isDeclarer() {
        assertThat(
                Stream.of(KERMIT_FOREHAND, KERMIT_MIDDLEHAND, KERMIT_REARHAND,
                                ZOOT_FOREHAND, KERMIT_LOST)
                        .filter(SkatGameDataFilter.KERMIT_WON_GAMES)
                        .collect(Collectors.toList()))
                .containsExactlyInAnyOrder(KERMIT_FOREHAND, KERMIT_MIDDLEHAND, KERMIT_REARHAND);
    }
}
