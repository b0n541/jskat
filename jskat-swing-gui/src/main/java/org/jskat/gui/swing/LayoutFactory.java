package org.jskat.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Layout factory for creation of layout managers
 */
public class LayoutFactory {

    private final static Logger LOG = LoggerFactory.getLogger(LayoutFactory.class);

    /**
     * Gets layout manager for {@link MigLayout}<br>
     * Sets smaller insets on Mac OS platform
     *
     * @return Layout manager
     */
    public static MigLayout getMigLayout() {

        return getMigLayout(null, null, null);
    }

    /**
     * Gets layout manager for {@link MigLayout}<br>
     * Sets smaller insets on Mac OS platform
     *
     * @param layoutConstraints Layout constraints for MigLayout
     * @return Layout manager
     */
    public static MigLayout getMigLayout(String layoutConstraints) {

        return getMigLayout(layoutConstraints, null, null);
    }

    /**
     * Gets layout manager for {@link MigLayout}<br>
     * Sets smaller insets on Mac OS platform
     *
     * @param layoutConstraints Layout constraints for MigLayout
     * @param columnConstraints Column constraints for MigLayout
     * @return Layout manager
     */
    public static MigLayout getMigLayout(String layoutConstraints,
                                         String columnConstraints) {

        return getMigLayout(layoutConstraints, columnConstraints, null);
    }

    /**
     * Gets layout manager for {@link MigLayout}<br>
     * Sets smaller insets on Mac OS platform
     *
     * @param layoutConstraints Layout constraints for MigLayout
     * @param columnConstraints Column constraints for MigLayout
     * @param rowConstraints    Row constraints for MigLayout
     * @return Layout manager
     */
    public static MigLayout getMigLayout(String layoutConstraints,
                                         String columnConstraints,
                                         String rowConstraints) {

        String finalLayoutConstraints = layoutConstraints;

        if (layoutConstraints != null && isMacOS()) {
            finalLayoutConstraints = injectMacOSLayoutConstraints(layoutConstraints);
        }

        return new MigLayout(finalLayoutConstraints, columnConstraints, rowConstraints);
    }

    private static String injectMacOSLayoutConstraints(String layoutConstraints) {

        String result = layoutConstraints;

        if (layoutConstraints == null || layoutConstraints.length() == 0) {
            // no layout constraints set
            result = getMacOSInsets();
        } else if (!layoutConstraints.contains("ins") && !layoutConstraints.contains("insets")) {
            // set Mac OS specific insets only if insets are not set already
            result = getMacOSInsets() + ", " + layoutConstraints;
        }

        LOG.debug("Layout constraints: " + result);

        return result;
    }

    private static boolean isMacOS() {
        return System.getProperty("os.name").toUpperCase().contains("MAC");
    }

    private static String getMacOSInsets() {
        return "insets 5";
    }
}
