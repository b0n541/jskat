/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.data.GameAnnouncement;
import de.jskat.gui.action.JSkatAction;
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
	JCheckBox ouvertBox = null;
	JCheckBox schneiderBox = null;
	JCheckBox schwarzBox = null;

	/**
	 * Constructor
	 * 
	 * @param newParent Parent panel
	 */
	public GameAnnouncePanel(SkatTablePanel newParent, ResourceBundle strings) {
		
		this.parent = newParent;
		initPanel(strings);
	}
	
	private void initPanel(ResourceBundle strings) {
		
		this.setLayout(new MigLayout("fill")); //$NON-NLS-1$
		
		ActionMap actions = this.parent.getActionMap();
		
		JPanel panel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		
		this.gameTypeList = new JComboBox();
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		// FIXME change entries to internationalized strings
		model.addElement(GameType.CLUBS);
		model.addElement(GameType.SPADES);
		model.addElement(GameType.HEARTS);
		model.addElement(GameType.DIAMONDS);
		model.addElement(GameType.NULL);
		model.addElement(GameType.GRAND);
		this.gameTypeList.setModel(model);
		this.gameTypeList.setSelectedIndex(-1);

		this.ouvertBox = new JCheckBox(strings.getString("ouvert")); //$NON-NLS-1$
		this.schneiderBox = new JCheckBox(strings.getString("schneider")); //$NON-NLS-1$
		this.schwarzBox = new JCheckBox(strings.getString("schwarz")); //$NON-NLS-1$
		
		panel.add(this.gameTypeList, "wrap"); //$NON-NLS-1$
		panel.add(this.ouvertBox, "wrap"); //$NON-NLS-1$
		panel.add(this.schneiderBox, "wrap"); //$NON-NLS-1$
		panel.add(this.schwarzBox, "wrap"); //$NON-NLS-1$

		final JButton playButton = new JButton(actions.get(JSkatAction.ANNOUNCE_GAME));
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (gameTypeList.getSelectedItem() != null) {
					
					GameAnnouncement ann = new GameAnnouncement();
					ann.setGameType((GameType) gameTypeList.getSelectedItem());
					
					ann.setOuvert(GameAnnouncePanel.this.ouvertBox.isSelected());
					ann.setSchneider(GameAnnouncePanel.this.schneiderBox.isSelected());
					ann.setSchwarz(GameAnnouncePanel.this.schwarzBox.isSelected());
					
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
