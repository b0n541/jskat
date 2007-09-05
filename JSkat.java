/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import java.util.Vector;
import java.util.StringTokenizer;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;

import jskat.data.JSkatDataModel;
import jskat.data.JSkatOptions;
import jskat.control.JSkatMaster;
import jskat.gui.JSkatGraphicRepository;
import jskat.gui.main.JSkatFrame;
import jskat.share.Tools;

/**
 * Main class of JSkat
 * 
 * @author @JS@
 */
public class JSkat {

	static Logger log = Logger.getLogger(JSkat.class);

	/**
	 * Creates a new instance of JSkat
	 * 
	 * @param aiPlayer
	 * @param splashScreen
	 */
	public JSkat(Vector aiPlayer, JFrame splashScreen) {

		JSkatOptions jskatOptions = JSkatOptions.instance();
		
		// At first the data model
		JSkatDataModel dataModel = new JSkatDataModel(this, jskatOptions);

		// Then the JSkatMaster
		JSkatMaster jskatMaster = new JSkatMaster(dataModel);

		// Preparing all graphics
		JSkatGraphicRepository jskatBitmaps = new JSkatGraphicRepository(
				jskatMaster.getJSkatOptions());

		// Preparing the windows
		JSkatFrame mainWindow = new JSkatFrame(jskatMaster, dataModel,
				jskatBitmaps, aiPlayer);

		dataModel.setMainWindow(mainWindow);
		jskatMaster.addObserver(mainWindow.getPlayArea());
		jskatMaster.addObserver(mainWindow.getGameScore());

		// Show main window
		mainWindow.setVisible(true);

		jskatMaster.createNewSkatTable();

		splashScreen.dispose();
	}

	/**
	 * The main function
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		JFrame splashScreen = showSplashScreen();

		Tools.checkLog();

		log.debug("starting up...");

		// --[DEBUG]--//
		log.debug(System.getProperty("user.dir"));
		log.debug(System.getProperty("user.home"));
		log.debug(System.getProperty("java.class.path"));
		log.debug(System.getProperty("java.version"));

		try {

			UIManager
					.setLookAndFeel(new net.sourceforge.mlf.metouia.MetouiaLookAndFeel());

		} catch (Exception ext) {

			log.error("Look and Feel Metouia not found!");
		}

		new JSkat(getAIPlayer(), splashScreen);
	}

	/**
	 * Gets the AIPlayer classes from the classpath
	 * 
	 * @return Vector of class names
	 */
	private static Vector<String> getAIPlayer() {

		Vector<String> aiPlayer = new Vector<String>();
		URL dirURL = ClassLoader.getSystemResource("jskat/player");
		
		log.debug(dirURL.getProtocol());
		
		// get the path to the player directory
		if (dirURL.getProtocol().equals("file")) {

			// we are in the file system
			log.debug("in file system");
			
			// test whether we are in a file system or in a JAR file
			File playerPath = new File(ClassLoader.getSystemResource("jskat/player").getPath());

			// no exception --> we are in a file system
			aiPlayer.addAll(getAIPlayerFromFileSystem(playerPath));

		} else {

			// we are in the JAR file
			log.debug("in JAR file");

			aiPlayer.addAll(getAIPlayerFromJARFile());
		}

		return aiPlayer;
	}

	private static Vector<String> getAIPlayerFromFileSystem(File playerPath) {

		Vector<String> aiPlayer = new Vector<String>();
//		File currPathFile = null;
/*
		try {

			currPathFile = new File(ClassLoader.getSystemResource(
					"jskat/player").toURI().getPath());

		} catch (URISyntaxException except) {

			except.printStackTrace();
		}
*/
		log.debug(playerPath.isDirectory() + " " + playerPath.isFile());
		log.debug(playerPath.getAbsolutePath());

		// list all files in "*./jskat/player"
		String files[] = playerPath.list();

		for (int i = 0; i < files.length; i++) {

			// test whether it contains
			// "*/jskat/player/AIPlayerName/AIPlayerName.class" or not
			if (new File(playerPath + System.getProperty("file.separator")
					+ files[i]).isDirectory()
					&& new File(playerPath
							+ System.getProperty("file.separator") + files[i]
							+ System.getProperty("file.separator") + files[i]
							+ ".class").isFile()) {

				// AIPlayer found
				log.debug("Searchin AIPlayer in: " + playerPath);
				log.debug("AIPlayer found: " + files[i]);
				// add it to the AIPlayer list
				aiPlayer.add(files[i]);
			}
		}

		return aiPlayer;
	}

	private static Vector<String> getAIPlayerFromJARFile() {

		Vector<String> aiPlayer = new Vector<String>();

		try {

			// take the JAR-File and search there
			java.util.jar.JarFile jarFile = new java.util.jar.JarFile(System
					.getProperty("java.class.path"));
			java.util.Enumeration jarFileEntries = jarFile.entries();

			while (jarFileEntries.hasMoreElements()) {

				String currEntry = jarFileEntries.nextElement().toString();

				StringTokenizer tokenizer = new StringTokenizer(currEntry, "/");
				if (tokenizer.countTokens() == 4
						&& tokenizer.nextToken().compareTo("jskat") == 0
						&& tokenizer.nextToken().compareTo("player") == 0) {

					// entry has the structure "jskat/player/*
					String newAIPlayer = tokenizer.nextToken();

					if (tokenizer.nextToken().compareTo(newAIPlayer + ".class") == 0) {

						// there is a file
						// jskat/player/AIPlayerName/AIPlayer.class
						log.debug(currEntry);
						log.debug("AIPlayer found: " + newAIPlayer);
						aiPlayer.add(newAIPlayer);
					}
				}
			}

		} catch (java.io.IOException e) {

			log.error("No JAR-File!");
		}

		return aiPlayer;
	}

	/**
	 * Shows the splash screen
	 * 
	 * @return The splashscreen JFrame
	 */
	private static JFrame showSplashScreen() {

		Image splashImage = Toolkit
				.getDefaultToolkit()
				.getImage(
						ClassLoader
								.getSystemResource("jskat/gui/img/gui/splashscreen.png"));
		JFrame splashScreen = new JFrame();
		splashScreen.setSize(400, 300);
		splashScreen.setTitle("JSkat...");
		splashScreen.setLocationRelativeTo(null);
		splashScreen.setUndecorated(true);
		splashScreen.getContentPane().add(
				new JLabel(new ImageIcon(splashImage)));
		splashScreen.setVisible(true);

		return splashScreen;
	}
}
