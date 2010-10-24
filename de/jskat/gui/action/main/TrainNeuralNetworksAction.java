/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.main;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Implements the action for showing about dialog
 */
public class TrainNeuralNetworksAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public TrainNeuralNetworksAction(JSkatMaster controller,
			JSkatGraphicRepository bitmaps, ResourceBundle strings) {

		super(controller);

		putValue(Action.NAME, strings.getString("train_nn"));
		putValue(Action.SHORT_DESCRIPTION,
				strings.getString("train_nn_tooltip"));
		putValue(
				Action.SMALL_ICON,
				new ImageIcon(bitmaps.getIconImage(
						JSkatGraphicRepository.Icon.BLANK,
						JSkatGraphicRepository.IconSize.SMALL)));
		putValue(
				Action.LARGE_ICON_KEY,
				new ImageIcon(bitmaps.getIconImage(
						JSkatGraphicRepository.Icon.BLANK,
						JSkatGraphicRepository.IconSize.BIG)));
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		this.jskat.trainNeuralNetworks();
	}
}
