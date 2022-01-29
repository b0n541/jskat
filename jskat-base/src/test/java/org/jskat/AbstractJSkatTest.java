
package org.jskat;

import org.jskat.data.DesktopSavePathResolver;
import org.jskat.data.JSkatOptions;
import org.junit.jupiter.api.BeforeAll;

/**
 * Abstract class for all JSkat unit tests
 */
public abstract class AbstractJSkatTest {

    /**
     * Creates the logger
     */
    @BeforeAll
    public static void createLogger() {
        final JSkatOptions options = JSkatOptions.instance(new DesktopSavePathResolver());
        options.resetToDefault();
    }
}
