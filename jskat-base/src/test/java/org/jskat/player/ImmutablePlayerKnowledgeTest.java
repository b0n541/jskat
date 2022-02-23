package org.jskat.player;

import org.jskat.data.GameAnnouncement;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImmutablePlayerKnowledgeTest {

    private ImmutablePlayerKnowledge knowledge;

    @BeforeEach
    void setUp() {
        knowledge = new ImmutablePlayerKnowledge();
    }

    @Test
    void getPlayerPartyMembers_PlayerIsDeclarer() {

        knowledge.playerPosition = Player.FOREHAND;
        knowledge.declarer = Player.FOREHAND;

        assertThat(knowledge.getPlayerPartyMembers()).containsExactly(Player.FOREHAND);
    }

    @Test
    void getPlayerPartyMembers_PlayerIsNotDeclarer() {

        knowledge.playerPosition = Player.FOREHAND;
        knowledge.declarer = Player.MIDDLEHAND;

        assertThat(knowledge.getPlayerPartyMembers()).contains(Player.FOREHAND, Player.REARHAND);
    }

    @Test
    void getPlayerPartyMembers_Ramsch() {

        knowledge.playerPosition = Player.FOREHAND;
        GameAnnouncement.GameAnnouncementFactory factory = GameAnnouncement.getFactory();
        factory.setGameType(GameType.RAMSCH);
        knowledge.announcement = factory.getAnnouncement();

        assertThat(knowledge.getPlayerPartyMembers()).contains(Player.FOREHAND);
    }

    @Test
    void getOpponentPartyMembers_PlayerIsDeclarer() {

        knowledge.playerPosition = Player.FOREHAND;
        knowledge.declarer = Player.FOREHAND;

        assertThat(knowledge.getOpponentPartyMembers()).contains(Player.MIDDLEHAND, Player.REARHAND);
    }

    @Test
    void getOpponentPartyMembers_PlayerIsNotDeclarer() {

        knowledge.playerPosition = Player.FOREHAND;
        knowledge.declarer = Player.MIDDLEHAND;

        assertThat(knowledge.getOpponentPartyMembers()).contains(Player.MIDDLEHAND);
    }

    @Test
    void getOpponentPartyMembers_Ramsch() {

        knowledge.playerPosition = Player.FOREHAND;
        GameAnnouncement.GameAnnouncementFactory factory = GameAnnouncement.getFactory();
        factory.setGameType(GameType.RAMSCH);
        knowledge.announcement = factory.getAnnouncement();

        assertThat(knowledge.getOpponentPartyMembers()).contains(Player.MIDDLEHAND, Player.REARHAND);
    }
}
