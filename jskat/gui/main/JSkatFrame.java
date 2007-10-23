/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;

import java.util.Vector;
import java.util.ResourceBundle;

import jskat.control.JSkatMaster;
import jskat.data.JSkatDataModel;
import jskat.gui.JSkatGraphicRepository;

/**
 * The main window for JSkat
 * 
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net>
 */
public class JSkatFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4583028823455278964L;

	static Logger log = Logger.getLogger(jskat.gui.main.JSkatFrame.class);
	
    /**
     * Creates new JSkatFrame
     * 
     * @param jskatMaster
     *            The JSkatMaster that controls the game
     * @param dataModel
     *            The JSkatDataModel that holds all data
     * @param jskatBitmaps
     *            The JSkatGraphicsRepository that holds all images used in
     *            JSkat
     * @param aiPlayer
     *            All AIPlayer classes that were found during startup
     */
    public JSkatFrame(JSkatMaster jskatMaster, JSkatDataModel dataModel,
            JSkatGraphicRepository jskatBitmaps, Vector<String> aiPlayer) {

        this.dataModel = dataModel;
        jskatStrings = dataModel.getResourceBundle();
        this.jskatBitmaps = jskatBitmaps;

        this.jskatMaster = jskatMaster;

        this.jskatActions = new JSkatActions(jskatMaster, dataModel, this, jskatBitmaps, aiPlayer);

        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {

        // Frame definition
        setTitle("JSkat");

        // MenuBar definition
        jSkatMenuBar = new JSkatMenuBar(dataModel, jskatBitmaps, jskatActions);
        setJMenuBar(jSkatMenuBar);

        // Toolbar definition
        jSkatToolBar = new JSkatToolBar(dataModel, jskatBitmaps, jskatActions);
        getContentPane().add(jSkatToolBar, BorderLayout.NORTH);

        // PlayArea definition
        playAreaPanel = new JSkatPlayArea(dataModel, jskatBitmaps);
        getContentPane().add(playAreaPanel, BorderLayout.CENTER);

        // WindowListener for handling the closing of the main window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                jskatMaster.exitJSkat();
            }
        });

        // TODO This should go to own classes
        infoPanel = new JPanel();

        infoPanel.setLayout(new GridBagLayout());

        GameScore gameScore = new GameScore(dataModel);
        //dataModel.getCurrentRound().addObserver(gameScore);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);

        infoPanel.add(gameScore, gridBagConstraints);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(1, 0));

        JTable table = new JTable(new ScoreTableModel(dataModel));
        table.setDefaultRenderer(String.class, new ScoreTableCellRenderer());
        table.setDefaultRenderer(Integer.class, new ScoreTableCellRenderer());
        table.setShowHorizontalLines(false);

        TableColumn column = null;

        for (int i = 0; i < 5; i++) {

            column = table.getColumnModel().getColumn(i);

            if (i == 4) {

                column.setPreferredWidth(5);

            } else {

                column.setPreferredWidth(30);
            }
        }

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(200, 400));
        scrollPane.setMinimumSize(new Dimension(200, 0));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //Add the scroll pane to this panel.
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        infoPanel.add(scrollPane, gridBagConstraints);

        getContentPane().add(infoPanel, BorderLayout.WEST);

        statusBarPanel = new JPanel();
        statusBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusBarPanel.setBorder(new EtchedBorder());
        statusBarLabel = new JLabel();
        statusBarLabel.setFont(new Font("Dialog", 0, 12));
        statusBarLabel.setText(jskatStrings.getString("greetings"));
        statusBarPanel.add(statusBarLabel);
        // TODO_end

        getContentPane().add(statusBarPanel, BorderLayout.SOUTH);

        pack();

        setLocationRelativeTo(null);
    }

    /**
     * Gets the JSkatPlayArea
     * 
     * @return The JSkatPlayArea
     */
    public JSkatPlayArea getPlayArea() {

        return playAreaPanel;
    }
    
    /**
     * Gets the score panel
     * 
     * @return The score panel
     */
    public GameScore getGameScore() {
    	
    	return (GameScore) infoPanel.getComponent(0);
    }

    /**
     * Gets the bitmaps for JSkat
     * 
     * @return JSkatGraphicRepository The bitmaps for JSkat
     */
    public JSkatGraphicRepository getJSkatBitmaps() {

        return jskatBitmaps;
    }
    
    /** Variables */
    private JSkatDataModel dataModel;

    private ResourceBundle jskatStrings;

    private JSkatGraphicRepository jskatBitmaps;

    private JSkatMaster jskatMaster;

    private JSkatActions jskatActions;

    private JSkatToolBar jSkatToolBar;

    private JSkatMenuBar jSkatMenuBar;

    private JSkatPlayArea playAreaPanel;

    private JPanel infoPanel;
    
    private JPanel statusBarPanel;

    private JLabel statusBarLabel;
}