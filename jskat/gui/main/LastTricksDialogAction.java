/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import jskat.data.JSkatDataModel;
import jskat.gui.JSkatGraphicRepository;

/**
 * Action for showing the LastTrickDialog
 */
public class LastTricksDialogAction implements ActionListener {

	static Logger log = Logger
			.getLogger(jskat.gui.main.LastTricksDialogAction.class);

	/**
	 * Creates a new instance of LastTricksDialogAction
	 * 
	 * @param dataModel
	 *            The JSkatDataModel that holds all data
	 * @param jskatBitmaps
	 *            The JSkatGraphicRepository that holds all images used by JSkat
	 * @param parent
	 *            The parent JFrame
	 */
	public LastTricksDialogAction(JSkatDataModel dataModel, JSkatGraphicRepository jskatBitmaps,
			JFrame parent) {

		lastTricksDialog = LastTricksDialog.createInstance(dataModel,
				jskatBitmaps, parent);

		log.debug("LastTricksDialogAction is ready.");
	}

	/**
	 * The action that should be performed
	 * 
	 * @param e
	 *            The Event that fires the action
	 */
	public void actionPerformed(ActionEvent e) {

		lastTricksDialog.setVisible(true);
	}

	private LastTricksDialog lastTricksDialog;
}
