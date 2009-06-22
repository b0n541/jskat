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
import de.jskat.gui.action.main.ContinueSkatSeriesAction;

class GameOverPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public GameOverPanel(ContinueSkatSeriesAction newAction) {
		
		this.action = newAction;
		initPanel();
	}
	
	public void initPanel() {
		
		this.setLayout(new MigLayout("fill", "fill", "fill"));
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		panel.add(new JButton(this.action), "center");
		this.add(panel, "center, grow");
	}

	private ContinueSkatSeriesAction action;
}
