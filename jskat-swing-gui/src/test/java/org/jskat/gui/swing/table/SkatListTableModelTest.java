package org.jskat.gui.swing.table;

import org.jskat.data.DesktopSavePathResolver;
import org.jskat.data.GameSummary;
import org.jskat.data.JSkatOptions;
import org.jskat.data.SkatGameResult;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SkatListTableModelTest {

    private SkatListTableModel model;

    @BeforeAll
    public static void createLogger() {
        final JSkatOptions options = JSkatOptions.instance(new DesktopSavePathResolver());
        options.resetToDefault();
    }

    @BeforeEach
    void setupEmptyModel() {
        model = new SkatListTableModel();
    }

    @Test
    void emptyTableModel() {
        assertThat(model.getColumnCount()).isEqualTo(4);

        assertThat(model.getColumnClass(-1)).isEqualTo(null);
        assertThat(model.getColumnClass(0)).isEqualTo(Integer.class);
        assertThat(model.getColumnClass(1)).isEqualTo(Integer.class);
        assertThat(model.getColumnClass(2)).isEqualTo(Integer.class);
        assertThat(model.getColumnClass(3)).isEqualTo(Integer.class);
        assertThat(model.getColumnClass(4)).isEqualTo(null);

        assertThat(model.getColumnName(-1)).isEqualTo(null);
        assertThat(model.getColumnName(0)).isEqualTo("P0");
        assertThat(model.getColumnName(1)).isEqualTo("P1");
        assertThat(model.getColumnName(2)).isEqualTo("P2");
        assertThat(model.getColumnName(3)).isEqualTo("Games");
        assertThat(model.getColumnName(4)).isEqualTo(null);

        assertThat(model.getRowCount()).isEqualTo(0);

        assertThat(model.getValueAt(123, 456)).isEqualTo(null);
    }

    @Test
    void setPlayerNames() {
        model.setPlayerNames("Upper Left", "Upper Right", "Lower Player");

        assertThat(model.getColumnName(0)).isEqualTo("Upper Left");
        assertThat(model.getColumnName(1)).isEqualTo("Upper Right");
        assertThat(model.getColumnName(2)).isEqualTo("Lower Player");
        assertThat(model.getColumnName(3)).isEqualTo("Games");
    }

    @Test
    void addWonGame() {

        SkatGameResult result = new SkatGameResult();
        result.setWon(true);
        result.setGameValue(24);

        GameSummary.GameSummaryFactory factory = GameSummary.getFactory();
        factory.setDeclarer(Player.FOREHAND);
        factory.setGameType(GameType.CLUBS);
        factory.setGameResult(result);
        factory.setPlayerPoints(Map.of(Player.FOREHAND, 67, Player.MIDDLEHAND, 23, Player.REARHAND, 40));

        GameSummary summary = factory.getSummary();

        model.addResult(Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND, Player.FOREHAND, summary);

        assertThat(model.getRowCount()).isEqualTo(1);
        assertThat(model.getValueAt(0, 0)).isEqualTo("-");
        assertThat(model.getValueAt(0, 1)).isEqualTo("-");
        assertThat(model.getValueAt(0, 2)).isEqualTo(24);

        result = new SkatGameResult();
        result.setWon(true);
        result.setGameValue(20);

        factory = GameSummary.getFactory();
        factory.setDeclarer(Player.MIDDLEHAND);
        factory.setGameType(GameType.HEARTS);
        factory.setGameResult(result);
        factory.setPlayerPoints(Map.of(Player.FOREHAND, 23, Player.MIDDLEHAND, 87, Player.REARHAND, 20));

        summary = factory.getSummary();

        model.addResult(Player.REARHAND, Player.FOREHAND, Player.MIDDLEHAND, Player.MIDDLEHAND, summary);

        assertThat(model.getRowCount()).isEqualTo(2);
        assertThat(model.getValueAt(0, 0)).isEqualTo("-");
        assertThat(model.getValueAt(0, 1)).isEqualTo("-");
        assertThat(model.getValueAt(0, 2)).isEqualTo(44);
    }

    @Test
    void addLostGame() {

        SkatGameResult result = new SkatGameResult();
        result.setWon(false);
        result.setGameValue(-44);

        GameSummary.GameSummaryFactory factory = GameSummary.getFactory();
        factory.setDeclarer(Player.MIDDLEHAND);
        factory.setGameType(GameType.SPADES);
        factory.setGameResult(result);
        factory.setPlayerPoints(Map.of(Player.FOREHAND, 67, Player.MIDDLEHAND, 23, Player.REARHAND, 40));

        GameSummary summary = factory.getSummary();

        model.addResult(Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND, Player.MIDDLEHAND, summary);

        assertThat(model.getRowCount()).isEqualTo(1);
        assertThat(model.getValueAt(0, 0)).isEqualTo(-44);
        assertThat(model.getValueAt(0, 1)).isEqualTo("-");
        assertThat(model.getValueAt(0, 2)).isEqualTo("-");
    }
}
