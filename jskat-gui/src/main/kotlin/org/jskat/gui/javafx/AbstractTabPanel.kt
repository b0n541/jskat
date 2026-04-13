package org.jskat.gui.javafx

import javafx.scene.layout.BorderPane
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.JSkatResourceBundle

/**
 * Generic Tab Panel for JSkat
 */
abstract class AbstractTabPanel(
    val tabName: String,
    protected val actions: Map<JSkatAction, AbstractJSkatAction>?
) : BorderPane() {

    /**
     * JSkat bitmaps
     */
    protected val bitmaps: JSkatGraphicRepository = JSkatGraphicRepository.INSTANCE

    /**
     * JSkat strings
     */
    protected val strings: JSkatResourceBundle = JSkatResourceBundle.INSTANCE

    /**
     * JSkat options
     */
    protected val options: JSkatOptions = JSkatOptions.instance()

    init {
        // In JavaFX, we don't set the name on the component like in Swing.
        // The tabName property holds the name.
        // initPanel() is called by subclasses or we can call it here if it's safe.
        // However, calling open methods in constructor is risky in Kotlin.
        // But the original Java code did it.
        // For now, I'll leave it to the subclass to call initPanel if needed, 
        // or call it here if I'm sure subclasses are ready.
        // SkatTablePanel calls initPanel in its init block.
    }

    /**
     * Initializes the tab panel.
     */
    protected abstract fun initPanel()

    /**
     * Sets the focus.
     */
    abstract fun setFocus()
}
