package org.jskat.control;


import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.iss.IssPlayerDataUpdatedEvent;
import org.jskat.data.JSkatApplicationData;
import org.jskat.data.iss.PlayerData;
import org.jskat.gui.UnitTestView;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link JSkatMaster}
 */
public class JSkatMasterTest extends AbstractJSkatTest {

    /**
     * Tests the creation of tables
     */
    @Test
    public void createTable() {
        final UnitTestView view = new UnitTestView();
        JSkatMaster.INSTANCE.setView(view);

        JSkatMaster.INSTANCE.createTable();

        assertThat(view.tables.size()).isEqualTo(1);
        assertTrue(view.tables.contains("UnitTestTable 1"));

        JSkatMaster.INSTANCE.createTable();

        assertThat(view.tables.size()).isEqualTo(2);
        assertTrue(view.tables.contains("UnitTestTable 1"));
        assertTrue(view.tables.contains("UnitTestTable 2"));
    }

    @Test
    public void preservesISSPlayerMetadataForInvitations() {
        final String playerName = "invitation-test-player";

        try {
            JSkatMaster.INSTANCE.updateISSPlayerOn(
                    new IssPlayerDataUpdatedEvent(playerName, "DE", 42, 1.75));

            final PlayerData player = JSkatApplicationData.INSTANCE.getAvailableISSPlayers().stream()
                    .filter(candidate -> playerName.equals(candidate.getLogin()))
                    .findFirst()
                    .orElseThrow();

            assertThat(player.getLanguages()).isEqualTo("DE");
            assertThat(player.getGamesPlayed()).isEqualTo(42);
            assertThat(player.getStrength()).isEqualTo(1.75);
        } finally {
            JSkatApplicationData.INSTANCE.removeAvailableISSPlayer(playerName);
        }
    }

    @Test
    public void recognizesAIISSPlayersForInvitations() {
        final String playerName = "invitation-test-ai-player";

        try {
            JSkatMaster.INSTANCE.updateISSPlayerOn(
                    new IssPlayerDataUpdatedEvent(playerName, "-", 42, 1.75));

            final PlayerData player = JSkatApplicationData.INSTANCE.getAvailableISSPlayers().stream()
                    .filter(candidate -> playerName.equals(candidate.getLogin()))
                    .findFirst()
                    .orElseThrow();

            assertTrue(player.isKIPlayer());
        } finally {
            JSkatApplicationData.INSTANCE.removeAvailableISSPlayer(playerName);
        }
    }
}
