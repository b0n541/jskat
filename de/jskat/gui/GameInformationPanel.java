/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * Panel for showing game informations
 */
class GameInformationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel label;

	/**
	 * Constructor
	 */
	GameInformationPanel() {
		
		super();
		initPanel();
	}
	
	private void initPanel() {
		
		setLayout(new MigLayout("fill"));
		
		setBackground(Color.GREEN);
		setOpaque(false);
		
		this.label = new JLabel();
		this.label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		setText(" ");
		
		add(this.label);
	}
	
	/**
	 * Sets the text of the game information label
	 * 
	 * @param newText Text to be set
	 */
	void setText(String newText) {
		
		this.label.setText(newText);
	}
	
	void clear() {
		
		this.label.setText(" ");
	}
}
