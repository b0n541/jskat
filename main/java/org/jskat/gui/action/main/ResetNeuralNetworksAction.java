package org.jskat.gui.action.main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for resetting neural networks
 */
public class ResetNeuralNetworksAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public ResetNeuralNetworksAction() {

		putValue(Action.NAME, strings.getString("reset_nn")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION,
				strings.getString("reset_nn_tooltip")); //$NON-NLS-1$

		setIcon(Icon.BLANK);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.resetNeuralNetworks();
	}
}
