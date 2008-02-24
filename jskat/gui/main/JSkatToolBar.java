/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.border.EtchedBorder;
import java.util.ResourceBundle;

import jskat.gui.JSkatGraphicRepository;
import jskat.data.JSkatDataModel;

/**
 * Tool bar for JSkat
 */
public class JSkatToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4198876387170613726L;

	/**
	 * Creates a new instance of JSkatToolBar
	 * 
	 * @param dataModel
	 *            The JSkatDataModel that holds all data
	 * @param jskatGraphics
	 *            The JSkatGraphicRepository that holds all images used in JSkat
	 * @param jskatActions
	 *            All JSkatActions
	 */
	public JSkatToolBar(JSkatDataModel dataModel,
			JSkatGraphicRepository jskatGraphics, JSkatActions jskatActions) {

		jskatStrings = dataModel.getResourceBundle();
		this.jskatGraphics = jskatGraphics;

		initComponents();

		newSkatRoundButton.addActionListener(jskatActions
				.getNewSkatSeriesDialogAction());
		aboutJSkatButton.addActionListener(jskatActions.getAboutDialogAction());
		helpButton.addActionListener(jskatActions.getHelpDialogAction());
		exitJSkatButton.addActionListener(jskatActions.getExitJSkatAction());
		flipCardsButton.addActionListener(jskatActions.getFlipCardsAction());
		lastTricksButton.addActionListener(jskatActions
				.getLastTricksDialogAction());
		preferencesButton.addActionListener(jskatActions
				.getOptionsDialogAction());
	}

	private void initComponents() {

		// ToolBar definition
		new JToolBar();
		setBorder(new EtchedBorder());
		setFloatable(false);

		// ToolBar Buttons
		newSkatRoundButton = new JButton();
		newSkatRoundButton.setIcon(new ImageIcon(jskatGraphics.getIconImage(
				JSkatGraphicRepository.NEW_ICON,
				JSkatGraphicRepository.BIG_ICON)));
		newSkatRoundButton.setToolTipText(jskatStrings
				.getString("new_skat_round"));
		add(newSkatRoundButton);

		openSkatRoundButton = new JButton();
		openSkatRoundButton.setIcon(new ImageIcon(jskatGraphics.getIconImage(
				JSkatGraphicRepository.OPEN_ICON,
				JSkatGraphicRepository.BIG_ICON)));
		openSkatRoundButton.setToolTipText(jskatStrings
				.getString("open_skat_round"));
		add(openSkatRoundButton);

		saveSkatRoundButton = new JButton();
		saveSkatRoundButton.setIcon(new ImageIcon(jskatGraphics.getIconImage(
				JSkatGraphicRepository.SAVE_ICON,
				JSkatGraphicRepository.BIG_ICON)));
		saveSkatRoundButton.setToolTipText(jskatStrings
				.getString("save_skat_round"));
		add(saveSkatRoundButton);

		addSeparator();

		preferencesButton = new JButton();
		preferencesButton.setIcon(new ImageIcon(jskatGraphics.getIconImage(
				JSkatGraphicRepository.PREFERENCES_ICON,
				JSkatGraphicRepository.BIG_ICON)));
		preferencesButton.setToolTipText(jskatStrings.getString("preferences"));
		add(preferencesButton);

		addSeparator();

		aboutJSkatButton = new JButton();
		aboutJSkatButton.setIcon(new ImageIcon(jskatGraphics.getIconImage(
				JSkatGraphicRepository.ABOUT_ICON,
				JSkatGraphicRepository.BIG_ICON)));
		aboutJSkatButton.setToolTipText(jskatStrings.getString("about_jskat"));
		add(aboutJSkatButton);

		addSeparator();

		helpButton = new JButton();
		helpButton.setIcon(new ImageIcon(jskatGraphics.getIconImage(
				JSkatGraphicRepository.HELP_ICON,
				JSkatGraphicRepository.BIG_ICON)));
		helpButton.setToolTipText(jskatStrings.getString("help"));
		add(helpButton);

		addSeparator();

		exitJSkatButton = new JButton();
		exitJSkatButton.setIcon(new ImageIcon(jskatGraphics.getIconImage(
				JSkatGraphicRepository.EXIT_ICON,
				JSkatGraphicRepository.BIG_ICON)));
		exitJSkatButton.setToolTipText(jskatStrings.getString("exit_jskat"));
		add(exitJSkatButton);

		addSeparator();

		flipCardsButton = new JButton("Flip cards");
		flipCardsButton.setToolTipText(jskatStrings.getString("flip_cards"));
		add(flipCardsButton);

		lastTricksButton = new JButton("Last tricks");
		lastTricksButton.setToolTipText(jskatStrings
				.getString("show_last_tricks"));
		add(lastTricksButton);
	}

	// Variables declaration
	private ResourceBundle jskatStrings;

	private JSkatGraphicRepository jskatGraphics;

	private JButton exitJSkatButton;

	private JButton newSkatRoundButton;

	private JButton preferencesButton;

	private JButton openSkatRoundButton;

	private JButton saveSkatRoundButton;

	private JButton flipCardsButton;

	private JButton lastTricksButton;

	private JButton aboutJSkatButton;

	private JButton helpButton;
}
