/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.util.Vector;

import jskat.control.SkatTable;
import jskat.data.JSkatDataModel;
import jskat.gui.JSkatGraphicRepository;

/**
 * Holds all actions for the JSkatTable
 * 
 * @author Jan Sch&auml;fer
 */
public class JSkatTableActions {

	/**
	 * Creates all JSkat actions
	 */
	public JSkatTableActions(SkatTable skatTable, JSkatDataModel dataModel,
			JSkatFrame mainFrame, JSkatGraphicRepository jskatBitmaps,
			Vector aiPlayer) {

		// Actions definition
		flipCardsAction = new FlipCardsAction(mainFrame);
		lastTricksDialogAction = new LastTricksDialogAction(dataModel, jskatBitmaps, mainFrame);
	}

	public NewSkatSeriesDialogAction getNewSkatRoundDialogAction() {
		return newSkatRoundDialogAction;
	}

	public FlipCardsAction getFlipCardsAction() {
		return flipCardsAction;
	}

	public LastTricksDialogAction getLastTricksDialogAction() {
		return lastTricksDialogAction;
	}

	private NewSkatSeriesDialogAction newSkatRoundDialogAction;

	private FlipCardsAction flipCardsAction;

	private LastTricksDialogAction lastTricksDialogAction;
}
