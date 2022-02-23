
package org.jskat.gui.javafx.dialog.firststeps.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.Option;

public class FirstStepsDialogModel {

    public BooleanProperty isShowTipsOnStartUp = new SimpleBooleanProperty(
            JSkatOptions.instance().getBoolean(Option.SHOW_TIPS_AT_START_UP));
}
