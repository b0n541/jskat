/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;

/**
 * Chat panel for ISS
 */
class ChatPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public ChatPanel() {
		
		super();
		initPanel();
	}
	
	private void initPanel() {
		
		
		setLayout(new MigLayout("fill"));
		
		setBackground(Color.YELLOW);
		
		this.inputLine = new JTextField(20);
		this.inputLine.addActionListener(this);

        this.chat = new JTextArea(7, 50);
        this.chat.setEditable(false);
        this.chat.setLineWrap(true);
        this.chat.setText("### You joined table X");
        JScrollPane scrollPane = new JScrollPane(this.chat);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, "span 3, growx, wrap");
        add(this.inputLine, "growx");
        add(new JButton("Send"));
        add(new JButton("Talk on/off"));
	}

	public void actionPerformed(ActionEvent evt) {
		
        String text = this.inputLine.getText();
        this.chat.append('\n' + text);
        this.inputLine.setText(null);

        this.chat.setCaretPosition(this.chat.getDocument().getLength());
    }
	
	private JTextField inputLine;
	private JTextArea chat;
}
