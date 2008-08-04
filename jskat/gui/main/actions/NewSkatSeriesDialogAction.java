/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main.actions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Vector;

import jskat.control.JSkatMaster;
import jskat.data.JSkatDataModel;
import jskat.gui.options.NewSkatSeriesDialog;

/**
 * The OptionsDialogAction for showing the Options dialog
 * 
 * @author Jan Sch&auml;fer
 */
public class NewSkatSeriesDialogAction implements ActionListener {

	private Log log = LogFactory.getLog(NewSkatSeriesDialogAction.class);

	/**
	 * Creates a new instance of OptionsDialogAction
	 * 
	 * @param jskatMaster
	 *            The JSkatMaster that controls the game
	 * @param dataModel
	 *            The JSkatDataModel that holds all data
	 * @param parent
	 *            The parent JFrame
	 * @param aiPlayer
	 *            The AIPlayer class names that were found during startup
	 */
	public NewSkatSeriesDialogAction(JSkatMaster jskatMaster,
			JSkatDataModel dataModel, JFrame parent, Vector<String> aiPlayer) {

		newSkatTableDialog = new NewSkatSeriesDialog(jskatMaster, dataModel,
				aiPlayer, parent, true);

		log.debug("NewSkatRoundDialogAction is ready.");
	}

	/**
	 * The action that should be performed
	 * 
	 * @param e
	 *            The Event that fires the action
	 */
	public void actionPerformed(ActionEvent e) {

		newSkatTableDialog.setVisible(true);
	}

	private NewSkatSeriesDialog newSkatTableDialog;
}
