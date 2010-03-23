/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.action.human.ContinueSkatSeriesAction;

class GameOverPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public GameOverPanel(ContinueSkatSeriesAction newAction) {
		
		this.action = newAction;
		initPanel();
	}
	
	public void initPanel() {
		
		this.setLayout(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		JPanel panel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		panel.add(new JButton(this.action), "center"); //$NON-NLS-1$
		this.add(panel, "center, grow"); //$NON-NLS-1$
	}

	private ContinueSkatSeriesAction action;
}
