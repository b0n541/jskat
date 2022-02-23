package org.jskat.gui.swing;

import javafx.stage.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Determines the screen resolution
 */
public class ScreenResolution {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenResolution.class);

    public static final boolean isBigScreen(Screen targetScreen) {
        return getScreenHeight(targetScreen) > 800;
    }

    private static int getScreenHeight(Screen targetScreen) {
        double height = targetScreen.getBounds().getHeight();
        LOG.debug("Screen height JavaFX: " + height);
        return (int) height;
    }

    public static final boolean isVeryBigScreen(Screen targetScreen) {
        return getScreenHeight(targetScreen) > 1300;
    }
}
