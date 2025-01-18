package org.jskat.gui.javafx.dialog.firststeps.model

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import org.jskat.data.JSkatOptions
import org.jskat.data.JSkatOptions.Option

class FirstStepsDialogModel {
    val isShowTipsOnStartUp: BooleanProperty = SimpleBooleanProperty(
        JSkatOptions.instance().getBoolean(Option.SHOW_TIPS_AT_START_UP)
    )
} 