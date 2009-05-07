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
import de.jskat.gui.action.main.StartSkatSeriesAction;

class GameStartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public GameStartPanel(StartSkatSeriesAction newAction) {
		
		this.action = newAction;
		initPanel();
	}
	
	public void initPanel() {
		
		this.setLayout(new MigLayout("fill"));
		
		this.add(new JButton(this.action), "center");
	}

	private StartSkatSeriesAction action;
}
