/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.text.html.HTMLEditorKit;
import java.util.ResourceBundle;

import jskat.data.JSkatDataModel;

/**
 * Help dialog for JSkat
 */
public class JSkatHelpDialog extends JDialog {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5198433859538350877L;
	
	/**
     * Creates new form JSkatHelpDialog
     * @param dataModel The JSkatDataModel that holds all data
     * @param parent The parent JFrame
     * @param modal TRUE if the dialog is modal
     */
    public JSkatHelpDialog(JSkatDataModel dataModel, JFrame parent, boolean modal) {
        
        super(parent, modal);
        
        this.jskatStrings = dataModel.getResourceBundle();
        this.parent = parent;
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        JButton closeButton = new JButton(jskatStrings.getString("close"));
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeDialog();
            }
        });
        southPanel.add(closeButton);
        JPanel westPanel = new JPanel();
        JPanel eastPanel = new JPanel();
        
        scrollPane = new JScrollPane();
        textPane = new JTextPane();
        
        setTitle(jskatStrings.getString("help"));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                closeDialog();
            }
        });
        
        getContentPane().add(northPanel, BorderLayout.NORTH);
        
        textPane.setEditorKit(new HTMLEditorKit());
        textPane.setEditable(false);
        
        String oneLine = new String();
        String gplText = new String();
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("jskat/gui/help/gpl.txt");
            InputStreamReader isr = new java.io.InputStreamReader(is);
            BufferedReader bfr = new java.io.BufferedReader(isr);
            
            while ( bfr.ready() ) {
                // could probably do this by reading
                // a char array...I have no idea
                // if it would be faster...experiment!
                oneLine = bfr.readLine();
                gplText = gplText + oneLine + "\n";
            }
            
        } catch (java.io.IOException e) {
        }
        
        if (gplText != null)
            textPane.setText(gplText);
        
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setViewportView(textPane);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        getContentPane().add(eastPanel, BorderLayout.EAST);
        getContentPane().add(westPanel, BorderLayout.WEST);
        
        
        pack();
    }
    
    private void setToInitialState() {
        
        scrollPane.getVerticalScrollBar().setValue(0);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Shows the Help dialog
     * 
     * @param visible Shows the dialog if set to TRUE 
     */    
    public void setVisible(boolean visible) {
        
		if (visible) {
			setToInitialState();
		}
    		
        super.setVisible(visible);
    }
    
    /** Closes the dialog */
    private void closeDialog() {
        setVisible(false);
        dispose();
    }
    
    // Variables declaration
    private ResourceBundle jskatStrings;
    private JFrame parent;
    private JScrollPane scrollPane;
    private JTextPane textPane;
}
