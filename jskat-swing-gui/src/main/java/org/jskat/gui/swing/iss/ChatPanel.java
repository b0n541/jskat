/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing.iss;

import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jskat.data.iss.ChatMessage;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.swing.AbstractTabPanel;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chat panel for ISS
 */
class ChatPanel extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ChatPanel.class);

	private JTextField inputLine;
	private Map<String, JTextArea> chats;
	private JTabbedPane chatTabs;

	String activeChatName;

	/**
	 * Constructor
	 */
	ChatPanel(final AbstractTabPanel parent) {

		initPanel(parent.getActionMap());
	}

	private void initPanel(final ActionMap actions) {

		JSkatResourceBundle strings = JSkatResourceBundle.instance();

		setLayout(LayoutFactory.getMigLayout("fill", "fill", "[grow][shrink]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		setMinimumSize(new Dimension(100, 100));
		setPreferredSize(new Dimension(100, 100));

		chats = new HashMap<String, JTextArea>();
		chatTabs = new JTabbedPane();
		chatTabs.setTabPlacement(SwingConstants.BOTTOM);
		chatTabs.setAutoscrolls(true);
		chatTabs.addChangeListener(this);
		add(chatTabs, "grow, wrap"); //$NON-NLS-1$

		addNewChat(strings.getString("lobby"), "lobby"); //$NON-NLS-1$ //$NON-NLS-2$

		inputLine = new JTextField(20);
		inputLine.setAction(actions.get(JSkatAction.SEND_CHAT_MESSAGE));
		inputLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {

				String message = ChatPanel.this.inputLine.getText();
				log.debug("Chat message: " + message); //$NON-NLS-1$

				ChatMessage chatMessage = new ChatMessage(
						ChatPanel.this.activeChatName, message);
				e.setSource(chatMessage);
				// fire event again
				ChatPanel.this.inputLine.dispatchEvent(e);

				ChatPanel.this.inputLine.setText(null);
			}
		});
		add(inputLine, "growx"); //$NON-NLS-1$
	}

	JTextArea addNewChat(final String title, final String name) {

		JTextArea chat = getChat();
		chats.put(name, chat);
		JScrollPane scrollPane = new JScrollPane(chat);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setName(name);

		chatTabs.add(title, scrollPane);
		chatTabs.setSelectedIndex(chatTabs.getComponentCount() - 1);
		activeChatName = name;

		return chat;
	}

	private JTextArea getChat() {

		JTextArea chat = new JTextArea(7, 50);
		chat.setEditable(false);
		chat.setLineWrap(true);

		return chat;
	}

	void appendMessage(final ChatMessage message) {

		log.debug("Appending chat message: " + message); //$NON-NLS-1$

		JTextArea chat = chats.get(message.getChatName());

		if (chat == null) {
			// new chat --> create chat text area first
			chat = addNewChat(message.getChatName(), message.getChatName());
		}

		chat.append(message.getMessage() + '\n');
		moveScrollBarToBottom(chat);
	}

	private void moveScrollBarToBottom(JTextArea chat) {
		chat.selectAll();
		int x = chat.getSelectionEnd();
		chat.select(x, x);
	}

	@Override
	public void stateChanged(final ChangeEvent e) {

		if (e.getSource() instanceof JTabbedPane) {

			JTabbedPane tabs = (JTabbedPane) e.getSource();
			Component tab = tabs.getSelectedComponent();

			activeChatName = tab.getName();
			log.debug("Chat " + activeChatName + " activated."); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void setFocus() {
		inputLine.requestFocus();
	}
}
