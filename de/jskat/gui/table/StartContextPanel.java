/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.action.main.StartSkatSeriesAction;

class StartContextPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private StartSkatSeriesAction action;

	public StartContextPanel(StartSkatSeriesAction newAction) {

		this.action = newAction;
		initPanel();
	}

	public void initPanel() {

		this.setLayout(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel panel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		panel.add(new JButton(this.action), "center"); //$NON-NLS-1$
		panel.setOpaque(false);
		this.add(panel, "center"); //$NON-NLS-1$

		setOpaque(false);
	}
}
