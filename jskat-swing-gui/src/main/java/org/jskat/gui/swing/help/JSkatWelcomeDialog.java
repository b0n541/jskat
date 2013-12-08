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
package org.jskat.gui.swing.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.Option;

/**
 * Welcome dialog for helping new player to get started
 */
public class JSkatWelcomeDialog extends JSkatHelpDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param parentFrame
	 *            Main frame
	 * @param title
	 *            Title
	 * @param contentPath
	 *            Path to the content to be shown
	 */
	public JSkatWelcomeDialog(JFrame parentFrame, String title,
			String contentPath) {
		super(parentFrame, title, contentPath);
	}

	@Override
	protected JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();

		final JCheckBox showTips = new JCheckBox(
				strings.getString("show_tips_at_startup")); //$NON-NLS-1$
		buttonPanel.add(showTips);

		final JSkatOptions options = JSkatOptions.instance();
		if (options.getBoolean(Option.SHOW_TIPS_AT_START_UP)) {
			showTips.setSelected(true);
		}

		showTips.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				options.setShowTipsAtStartUp(showTips.isSelected());
			}
		});

		JButton closeButton = new JButton(strings.getString("close")); //$NON-NLS-1$
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				closeDialog();
			}
		});
		buttonPanel.add(closeButton);
		JButton openExternal = new JButton(openExternalAction);

		buttonPanel.add(openExternal);
		return buttonPanel;
	}
}
