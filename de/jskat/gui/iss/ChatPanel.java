/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.iss.ISSChatMessage;
import de.jskat.gui.JSkatTabPanel;
import de.jskat.gui.action.JSkatAction;

/**
 * Chat panel for ISS
 */
class ChatPanel extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(ChatPanel.class);

	private JTextField inputLine;
	private Map<String, JTextArea> chats;
	private JTabbedPane chatTabs;
	
	private String activeChatName;

	/**
	 * Constructor
	 */
	ChatPanel(JSkatTabPanel parent) {

		super();
		initPanel(parent.getActionMap());
	}

	private void initPanel(ActionMap actions) {

		setLayout(new MigLayout("fill")); //$NON-NLS-1$

		this.chats = new HashMap<String, JTextArea>();
		this.chatTabs = new JTabbedPane();
		this.chatTabs.setTabPlacement(JTabbedPane.BOTTOM);
		this.chatTabs.setAutoscrolls(true);
		this.chatTabs.addChangeListener(this);
		add(this.chatTabs, "growx, wrap"); //$NON-NLS-1$
		
		addNewChat("ISS debug");
		addNewChat("Lobby");
		
		this.inputLine = new JTextField(20);
		this.inputLine.setAction(actions.get(JSkatAction.SEND_CHAT_MESSAGE));
		this.inputLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String message = ChatPanel.this.inputLine.getText();
				log.debug("Chat message: " + message); //$NON-NLS-1$

				ISSChatMessage chatMessage = new ISSChatMessage(ChatPanel.this.activeChatName, message);
				e.setSource(chatMessage);  
				// fire event again
				ChatPanel.this.inputLine.dispatchEvent(e);
				
				ChatPanel.this.inputLine.setText(null);
			}
		});
		add(this.inputLine, "growx"); //$NON-NLS-1$
	}

	JTextArea addNewChat(String name) {

		JTextArea chat = getChat();
		this.chats.put(name, chat);
		JScrollPane scrollPane = new JScrollPane(chat);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setName(name);

		this.chatTabs.add(name, scrollPane);
		this.activeChatName = name;
		
		return chat;
	}

	private JTextArea getChat() {

		JTextArea chat = new JTextArea(7, 50);
		chat.setEditable(false);
		chat.setLineWrap(true);

		return chat;
	}
	
	void addMessage(ISSChatMessage message) {
		
		log.debug("addMessage");
		
		JTextArea chat = this.chats.get(message.getChatName());
		
		if (chat == null) {
			// new chat --> create chat text area first
			chat = this.addNewChat(message.getChatName());
		}
		
		chat.append(message.getMessage() + '\n');
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() instanceof JTabbedPane) {
			
			JTabbedPane tabs = (JTabbedPane) e.getSource();
			Component tab = tabs.getSelectedComponent();
			
			this.activeChatName = tab.getName();
			log.debug("Chat " + this.activeChatName + " activated.");
		}
	}
}
