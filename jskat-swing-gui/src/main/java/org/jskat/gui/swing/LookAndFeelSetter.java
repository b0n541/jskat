package org.jskat.gui.swing;

import javafx.stage.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Sets the look and feel
 */
public class LookAndFeelSetter {

    private static Logger log = LoggerFactory.getLogger(LookAndFeelSetter.class);

    /**
     * Sets the look and feel.
     *
     * @param targetScreen Target screen for main window
     */
    public static void setLookAndFeel(final Screen targetScreen) {

        try {
            LookAndFeel laf = null;

            try {
                laf = (LookAndFeel) Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance();
            } catch (final ClassNotFoundException e) {
                laf = (LookAndFeel) Class.forName("javax.swing.plaf.nimbus.NimbusLookAndFeel").newInstance();
            }

            laf.getDefaults().put("control", new Color(226, 217, 202)); //$NON-NLS-1$
            laf.getDefaults().put("text", new Color(0, 0, 0)); //$NON-NLS-1$
            laf.getDefaults().put("nimbusFocus", new Color(255, 245, 193)); //$NON-NLS-1$
            laf.getDefaults().put("nimbusLightBackground", new Color(241, 238, 229)); //$NON-NLS-1$
            laf.getDefaults().put("nimbusBase", new Color(96, 65, 34)); //$NON-NLS-1$

            if (ScreenResolution.isVeryBigScreen(targetScreen)) {
                laf.getDefaults().put("defaultFont", new Font(Font.SANS_SERIF, 0, 14));
            }

            UIManager.setLookAndFeel(laf);
            log.debug("NimbusLookAndFeel successfully applied..."); //$NON-NLS-1$

        } catch (final UnsupportedLookAndFeelException e) {
            log.debug(e.toString());
        } catch (final InstantiationException e) {
            log.debug(e.toString());
        } catch (final IllegalAccessException e) {
            log.debug(e.toString());
        } catch (final ClassNotFoundException e) {
            log.info("NimbusLookAndFeel not found (probably not installed/wrong JDK)!"); //$NON-NLS-1$
        }
    }
}
