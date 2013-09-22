package org.jskat.gui.swing.table;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.PropertyConfigurator;
import org.jskat.data.DesktopSavePathResolver;
import org.jskat.data.JSkatOptions;
import org.jskat.util.CardDeck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandPanelTest {

	private static Logger log = LoggerFactory.getLogger(HandPanelTest.class);

	public static void main(String[] args) {
		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("org/jskat/config/log4j.properties")); //$NON-NLS-1$
		JSkatOptions.instance(new DesktopSavePathResolver());

		JPanel panel = new JPanel(new MigLayout("fill", "fill", "fill"));

		final ClickableCardPanel cardPanel = new ClickableCardPanel(panel, 1.0,
				false);
		cardPanel.addCards(new CardDeck());

		panel.add(cardPanel);

		JFrame mainWindow = new JFrame();
		mainWindow.add(panel);
		mainWindow.setSize(new Dimension(800, 400));
		mainWindow.setVisible(true);
	}
}
