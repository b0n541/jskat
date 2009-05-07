/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.data.GameAnnouncement;
import de.jskat.gui.action.JSkatActions;
import de.jskat.util.GameType;

/**
 * Holds widgets for announcing a game
 */
class GameAnnouncePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Parent panel
	 */
	protected SkatTablePanel parent = null;
	private JComboBox gameTypeList = null;
	private JCheckBox ouvertBox = null;
	private JCheckBox schneiderBox = null;
	private JCheckBox schwarzBox = null;

	/**
	 * Constructor
	 * 
	 * @param newParent Parent panel
	 */
	public GameAnnouncePanel(SkatTablePanel newParent) {
		
		this.parent = newParent;
		initPanel();
	}
	
	private void initPanel() {
		
		this.setLayout(new MigLayout("fill"));
		
		ActionMap actions = this.parent.getActionMap();
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		
		this.gameTypeList = new JComboBox(GameType.values());
		this.gameTypeList.setSelectedIndex(-1);

		this.ouvertBox = new JCheckBox("ouvert");
		this.schneiderBox = new JCheckBox("schneider");
		this.schwarzBox = new JCheckBox("schwarz");
		
		panel.add(this.gameTypeList, "wrap");
		panel.add(this.ouvertBox, "wrap");
		panel.add(this.schneiderBox, "wrap");
		panel.add(this.schwarzBox, "wrap");

		final JButton playButton = new JButton(actions.get(JSkatActions.ANNOUNCE_GAME));
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (gameTypeList.getSelectedItem() != null) {
					
					GameAnnouncement ann = new GameAnnouncement();
					ann.setGameType((GameType) gameTypeList.getSelectedItem());
					
					ann.setOuvert(ouvertBox.isSelected());
					ann.setSchneider(schneiderBox.isSelected());
					ann.setSchwarz(schwarzBox.isSelected());
					
					e.setSource(ann);  
					// fire event again
					playButton.dispatchEvent(e);
				}
			}
		});
		panel.add(playButton);
		this.add(panel, "center");
	}
	
	public void resetPanel() {
		
		this.gameTypeList.setSelectedIndex(-1);
		this.ouvertBox.setSelected(false);
		this.schneiderBox.setSelected(false);
		this.schwarzBox.setSelected(false);
	}
}
