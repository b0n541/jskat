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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Help dialog for JSkat
 */
public class JSkatHelpDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(JSkatHelpDialog.class);

	protected final JSkatResourceBundle strings = JSkatResourceBundle.instance();

	private final JFrame parent;
	private JScrollPane scrollPane;
	private JTextPane textPane;
	private final String contentURL;

	/**
	 * Creates new form JSkatHelpDialog
	 * 
	 * @param parentFrame
	 *            The parent JFrame
	 * @param title
	 *            Dialog title
	 * @param contentPath
	 *            Path to dialog content
	 */
	public JSkatHelpDialog(final JFrame parentFrame, final String title, final String contentPath) {

		super(parentFrame, true);

		parent = parentFrame;
		contentURL = contentPath;
		initComponents(title);
		setLocationRelativeTo(parent);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents(final String title) {

		JPanel northPanel = new JPanel();

		JPanel southPanel = getButtonPanel();

		JPanel westPanel = new JPanel();
		JPanel eastPanel = new JPanel();

		scrollPane = new JScrollPane();
		textPane = new JTextPane();

		setTitle(title);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent evt) {
				closeDialog();
			}
		});

		getContentPane().add(northPanel, BorderLayout.NORTH);

		textPane.setEditorKit(new HTMLEditorKit());
		textPane.addHyperlinkListener(hll);
		textPane.setEditable(false);

		setFile(contentURL);

		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(textPane);
		scrollPane.setPreferredSize(new Dimension(600, 300));

		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		getContentPane().add(eastPanel, BorderLayout.EAST);
		getContentPane().add(westPanel, BorderLayout.WEST);

		pack();
	}

	protected JPanel getButtonPanel() {
		JPanel southPanel = new JPanel();
		JButton closeButton = new JButton(strings.getString("close")); //$NON-NLS-1$
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				closeDialog();
			}
		});
		southPanel.add(closeButton);
		JButton openExternal = new JButton(openExternalAction);

		southPanel.add(openExternal);
		return southPanel;
	}

	private String getResource(final String url) {
		StringBuilder message = new StringBuilder();
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(url);
			InputStreamReader isr = new java.io.InputStreamReader(is);
			BufferedReader bfr = new java.io.BufferedReader(isr);

			while (bfr.ready()) {
				message.append(bfr.readLine()).append("\n"); //$NON-NLS-1$
			}

		} catch (java.io.IOException e) {
			log.warn("Error in loading message: ", e);
		}
		return message.toString();
	}

	private void setToInitialState() {

		scrollPane.getVerticalScrollBar().setValue(0);
		setLocationRelativeTo(parent);
	}

	/**
	 * Shows the Help dialog
	 * 
	 * @param visible
	 *            Shows the dialog if set to TRUE
	 */
	@Override
	public void setVisible(final boolean visible) {

		if (visible) {
			setToInitialState();
		}

		super.setVisible(visible);
	}

	/** Closes the dialog */
	void closeDialog() {
		setVisible(false);
		dispose();
	}

	/**
	 * sets a single file, which will be inserted in the general html frame
	 * 
	 * @param filename
	 *            html snippet file
	 */
	public void setFile(final String filename) {
		if (filename != null) {
			setFile(new String[] { filename });
		}
	}

	/**
	 * sets a list of files, which will be concatenated and inserted in the
	 * general html frame
	 * 
	 * @param filenames
	 *            list of html snippet files
	 */
	public void setFile(final String[] filenames) {
		StringBuilder sb = new StringBuilder();
		sb.append(getResource("org/jskat/gui/help/frame.html"));
		int ix = sb.indexOf("@@insert@@");
		if (ix < 0) {
			throw new IllegalArgumentException("frame.html contains no @@insert@@");
		}
		for (String f : filenames) {
			sb.insert(ix, getResource(f));
			ix = sb.indexOf("@@insert@@");
		}
		sb.delete(ix, ix + 10);
		textPane.setText(sb.toString());
		textPane.setCaretPosition(0);
	}

	/**
	 * inserts a html snippet in the general frame of the HTMLDialog
	 * 
	 * @param htmlSnippet
	 *            the html snippet to insert
	 */
	public void insertText(final String htmlSnippet) {
		StringBuilder sb = new StringBuilder();
		sb.append(getResource("org/jskat/gui/help/frame.html"));
		int ix = sb.indexOf("@@insert@@");
		if (ix < 0) {
			throw new IllegalArgumentException("frame.html contains no @@insert@@");
		}
		sb.replace(ix, ix + 10, htmlSnippet);
		textPane.setText(sb.toString());
		textPane.setCaretPosition(0);
	}

	/**
	 * sets a specific html text to display in the dialog
	 * 
	 * @param html
	 *            the html to display in the dialog
	 */
	public void setText(final String html) {
		textPane.setText(html);
		textPane.setCaretPosition(0);
	}

	protected final Action openExternalAction = new AbstractAction(strings.getString("open_external")) {
		private static final long serialVersionUID = 4233152199895964006L;

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			File f = new File(System.getProperty("java.io.tmpdir") + File.separator + "JSkat_doc.html");
			PrintWriter pw;
			try {
				pw = new PrintWriter(f);
			} catch (FileNotFoundException e) {
				log.warn("error in writing external html file:", e);
				return;
			}
			pw.print(externalizeLinks(textPane.getText()));
			pw.flush();
			pw.close();
			try {
				Desktop.getDesktop().browse(new URI("file:///" + f.getAbsolutePath().replace("\\", "/")));
			} catch (IOException e) {
				log.error("IOException", e);
			} catch (URISyntaxException e) {
				log.error("URI Fehler", e);
			}
		}
	};

	String externalizeLinks(final String html) {
		return html.replace("a href=\"org/jskat/gui/help/", "a href=\"http://www.jskat.org/help/");
	}

	private final HyperlinkListener hll = new HyperlinkListener() {
		@Override
		public void hyperlinkUpdate(final HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				String link = e.getDescription();
				if (link.toLowerCase().startsWith("http:")) {
					try {
						Desktop.getDesktop().browse(new URI(link));
					} catch (IOException ex) {
						log.warn("IOException", ex);
						JOptionPane.showMessageDialog(JSkatHelpDialog.this, "Error in loading external link:\n" + link
								+ "\n" + (ex.getMessage() != null ? ex.getMessage() : "<???>"), "Externer Link",
								JOptionPane.ERROR_MESSAGE);
					} catch (URISyntaxException ex) {
						log.warn("URI exception", ex);
						JOptionPane.showMessageDialog(JSkatHelpDialog.this, "Error in loading external link:\n" + link
								+ "\n" + (ex.getMessage() != null ? ex.getMessage() : "<???>"), "Externer Link",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					setFile(link);
				}
			}
		}
	};

}
