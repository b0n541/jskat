
package org.jskat.data;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.JSkatOptions.Option;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.data.SkatTableOptions.ContraCallingTime;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.control.gui.img.CardFace;
import org.jskat.control.gui.img.CardSet;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for JSkat options
 */
public class JSkatOptionsTest extends AbstractJSkatTest {

    /**
     * Tests the default values
     */
    @Test
    public void testDefaultValues() {

        final JSkatOptions options = JSkatOptions.instance();
        options.setDefaultProperties();

        assertFalse(options.isCheatDebugMode());

        assertTrue(options.getBoolean(Option.SHOW_TIPS_AT_START_UP));
        assertFalse(options.getBoolean(Option.CHECK_FOR_NEW_VERSION_AT_START_UP));
        assertFalse(options.getBoolean(Option.HIDE_TOOLBAR));

        if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
            assertThat(options.getLanguage()).isEqualTo(SupportedLanguage.GERMAN);
        } else {
            assertThat(options.getLanguage()).isEqualTo(SupportedLanguage.ENGLISH);
        }
        assertThat(options.getWaitTimeAfterTrick()).isEqualTo(0);
        assertThat(options.getCardSet()).isEqualTo(CardSet.ISS_TOURNAMENT);
        assertThat(options.getCardSet().getCardFace()).isEqualTo(CardFace.TOURNAMENT);
        assertThat(options.getSavePath()).isEqualTo(new DesktopSavePathResolver().getDefaultSavePath());

        // rule defaults
        assertThat(options.getRules()).isEqualTo(RuleSet.ISPA);

        assertFalse(options.isPlayContra());
        assertFalse(options.isPlayContra(true));
        assertTrue(options.isPlayContra(false));

        assertFalse(options.isContraAfterBid18());
        assertFalse(options.isContraAfterBid18(true));
        assertTrue(options.isContraAfterBid18(false));

        assertThat(options.getContraCallingTime()).isEqualTo(ContraCallingTime.BEFORE_FIRST_CARD);

        assertFalse(options.isPlayBock());
        assertFalse(options.isPlayBock(true));
        assertTrue(options.isPlayBock(false));

        assertFalse(options.isBockEventAllPlayersPassed());
        assertFalse(options.isBockEventAllPlayersPassed(true));
        assertFalse(options.isBockEventContraReCalled());
        assertFalse(options.isBockEventContraReCalled(true));
        assertFalse(options.isBockEventLostAfterContra());
        assertFalse(options.isBockEventLostAfterContra(true));
        assertFalse(options.isBockEventLostGrand());
        assertFalse(options.isBockEventLostGrand(true));
        assertFalse(options.isBockEventLostWith60());
        assertFalse(options.isBockEventLostWith60(true));
        assertFalse(options.isBockEventMultipleOfHundredScore());
        assertFalse(options.isBockEventMultipleOfHundredScore(true));

        assertFalse(options.isPlayRamsch());
        assertFalse(options.isPlayRamsch(true));
        assertTrue(options.isPlayRamsch(false));

        assertFalse(options.isPlayRevolution());
        assertFalse(options.isPlayRevolution(true));
        assertFalse(options.isPlayRevolution(false));

        // ISS defaults
        assertThat(options.getString(Option.ISS_ADDRESS)).isEqualTo("skatgame.net");
        assertThat(options.getInteger(Option.ISS_PORT)).isEqualTo(7000);
    }

    @Test
    public void testPropertyName() {
        assertThat(JSkatOptions.Option.RULES.propertyName()).isEqualTo("rules");
        assertThat(JSkatOptions.Option.CARD_SET.propertyName()).isEqualTo("cardSet");
        assertThat(JSkatOptions.Option.BOCK_EVENT_NO_BID.propertyName()).isEqualTo("bockEventNoBid");
        assertThat(JSkatOptions.Option.CONTRA_AFTER_BID_18.propertyName()).isEqualTo("contraAfterBid18");
    }

    @Test
    public void testValueOfProperty() {
        assertThat(JSkatOptions.Option.valueOfProperty("rules")).isEqualTo(JSkatOptions.Option.RULES);
        assertThat(JSkatOptions.Option.valueOfProperty("cardSet")).isEqualTo(JSkatOptions.Option.CARD_SET);
        assertThat(JSkatOptions.Option.valueOfProperty("bockEventNoBid")).isEqualTo(JSkatOptions.Option.BOCK_EVENT_NO_BID);
        assertThat(JSkatOptions.Option.valueOfProperty("contraAfterBid18")).isEqualTo(JSkatOptions.Option.CONTRA_AFTER_BID_18);
    }
}
