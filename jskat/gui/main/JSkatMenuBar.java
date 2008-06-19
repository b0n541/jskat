/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/
package jskat.gui.main;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;

import jskat.data.JSkatDataModel;
import jskat.gui.img.JSkatGraphicRepository;

/**
 * Menu bar for JSkat
 */
public class JSkatMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1296953839801016503L;

	/**
	 * Creates a new instance of JSkatMenu
	 * 
	 * @param dataModel
	 *            The JSkatDataModel that holds all data
	 * @param jskatBitmaps
	 *            The JSkatGraphicRepository that holds all images used in JSkat
	 * @param jskatActions
	 *            The actions for JSkat
	 */
	public JSkatMenuBar(JSkatDataModel dataModel,
			JSkatGraphicRepository jskatBitmaps, JSkatActions jskatActions) {

		jskatStrings = dataModel.getResourceBundle();
		this.jskatBitmaps = jskatBitmaps;

		initComponents();

		newRoundMenuItem.addActionListener(jskatActions
				.getNewSkatSeriesDialogAction());
		aboutJSkatMenuItem.addActionListener(jskatActions
				.getAboutDialogAction());
		helpMenuItem.addActionListener(jskatActions.getHelpDialogAction());
		exitJSkatMenuItem.addActionListener(jskatActions.getExitJSkatAction());
		optionsMenuItem
				.addActionListener(jskatActions.getOptionsDialogAction());
	}

	private void initComponents() {

		// File menu
		fileMenu = new JMenu();
		fileMenu.setText(jskatStrings.getString("skat_round"));
		// MenuItems
		newRoundMenuItem = new JMenuItem();
		newRoundMenuItem.setText(jskatStrings.getString("new_skat_round"));
		newRoundMenuItem.setIcon(new ImageIcon(jskatBitmaps.getIconImage(
				JSkatGraphicRepository.NEW_ICON,
				JSkatGraphicRepository.SMALL_ICON)));
		fileMenu.add(newRoundMenuItem);
		loadRoundMenuItem = new JMenuItem();
		loadRoundMenuItem.setText(jskatStrings.getString("open_skat_round"));
		loadRoundMenuItem.setIcon(new ImageIcon(jskatBitmaps.getIconImage(
				JSkatGraphicRepository.OPEN_ICON,
				JSkatGraphicRepository.SMALL_ICON)));
		fileMenu.add(loadRoundMenuItem);
		saveRoundMenuItem = new JMenuItem();
		saveRoundMenuItem.setText(jskatStrings.getString("save_skat_round"));
		saveRoundMenuItem.setIcon(new ImageIcon(jskatBitmaps.getIconImage(
				JSkatGraphicRepository.SAVE_ICON,
				JSkatGraphicRepository.SMALL_ICON)));
		fileMenu.add(saveRoundMenuItem);

		fileMenu.addSeparator();

		exitJSkatMenuItem = new JMenuItem();
		exitJSkatMenuItem.setText(jskatStrings.getString("exit_jskat"));
		exitJSkatMenuItem.setIcon(new ImageIcon(jskatBitmaps.getIconImage(
				JSkatGraphicRepository.EXIT_ICON,
				JSkatGraphicRepository.SMALL_ICON)));
		fileMenu.add(exitJSkatMenuItem);
		add(fileMenu);

		gameMenu = new JMenu();
		gameMenu.setText(jskatStrings.getString("game"));
		// MenuItems
		optionsMenuItem = new JMenuItem();
		optionsMenuItem.setText(jskatStrings.getString("preferences"));
		optionsMenuItem.setIcon(new ImageIcon(jskatBitmaps.getIconImage(
				JSkatGraphicRepository.PREFERENCES_ICON,
				JSkatGraphicRepository.SMALL_ICON)));
		gameMenu.add(optionsMenuItem);

		add(gameMenu);

		// Help menu
		helpMenu = new JMenu();
		helpMenu.setText(jskatStrings.getString("help"));

		aboutJSkatMenuItem = new JMenuItem();
		aboutJSkatMenuItem.setText(jskatStrings.getString("about_jskat"));
		aboutJSkatMenuItem.setIcon(new ImageIcon(jskatBitmaps.getIconImage(
				JSkatGraphicRepository.ABOUT_ICON,
				JSkatGraphicRepository.SMALL_ICON)));
		helpMenu.add(aboutJSkatMenuItem);

		helpMenu.addSeparator();

		helpMenuItem = new JMenuItem();
		helpMenuItem.setText(jskatStrings.getString("help"));
		helpMenuItem.setIcon(new ImageIcon(jskatBitmaps.getIconImage(
				JSkatGraphicRepository.HELP_ICON,
				JSkatGraphicRepository.SMALL_ICON)));
		helpMenu.add(helpMenuItem);
		add(helpMenu);
	}

	// Variables declaration
	private ResourceBundle jskatStrings;

	private JSkatGraphicRepository jskatBitmaps;

	private JMenu fileMenu;

	private JMenuItem newRoundMenuItem;

	private JMenuItem loadRoundMenuItem;

	private JMenuItem saveRoundMenuItem;

	private JMenuItem exitJSkatMenuItem;

	private JMenu gameMenu;

	private JMenu helpMenu;

	private JMenuItem optionsMenuItem;

	private JMenuItem aboutJSkatMenuItem;

	private JMenuItem helpMenuItem;
}
