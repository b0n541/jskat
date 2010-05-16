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

class GameStartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public GameStartPanel(StartSkatSeriesAction newAction) {
		
		this.action = newAction;
		initPanel();
	}
	
	public void initPanel() {
		
		this.setLayout(new MigLayout("fill", "fill", "fill"));
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		panel.add(new JButton(this.action), "center");
		this.add(panel, "center, grow");
	}

	private StartSkatSeriesAction action;
}
