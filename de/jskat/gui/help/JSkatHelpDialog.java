/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.gui.help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Help dialog for JSkat
 */
public class JSkatHelpDialog extends JDialog {
    
	private static final long serialVersionUID = 1L;

    private JFrame parent;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private String contentURL;
    private String title;

    /**
     * Creates new form JSkatHelpDialog
     * @param parentFrame The parent JFrame
     * @param dialogTitle Dialog title
     * @param contentPath Path to dialog content
     * @param strings i18n strings
     */
    public JSkatHelpDialog(JFrame parentFrame, String dialogTitle, String contentPath,
    		ResourceBundle strings) {
        
        super(parentFrame, true);
        
        this.parent = parentFrame;
        this.title = dialogTitle;
        this.contentURL = contentPath;
        initComponents(strings);
        setLocationRelativeTo(this.parent);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents(ResourceBundle strings) {
        
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        JButton closeButton = new JButton(strings.getString("close")); //$NON-NLS-1$
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeDialog();
            }
        });
        southPanel.add(closeButton);
        JPanel westPanel = new JPanel();
        JPanel eastPanel = new JPanel();
        
        this.scrollPane = new JScrollPane();
        this.textPane = new JTextPane();
        
        setTitle(this.title);
        addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent evt) {
                closeDialog();
            }
        });
        
        getContentPane().add(northPanel, BorderLayout.NORTH);
        
        this.textPane.setEditorKit(new HTMLEditorKit());
        this.textPane.setEditable(false);
        
        StringBuilder message = new StringBuilder();
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream(this.contentURL);
            InputStreamReader isr = new java.io.InputStreamReader(is);
            BufferedReader bfr = new java.io.BufferedReader(isr);
            
            while ( bfr.ready() ) {
                message.append(bfr.readLine()).append("\n"); //$NON-NLS-1$
            }
            
        } catch (java.io.IOException e) {
        	// TODO handle exception
        	e.printStackTrace();
        }
        
        this.textPane.setText(message.toString());
        this.textPane.setCaretPosition(0);
        
        this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setViewportView(this.textPane);
        this.scrollPane.setPreferredSize(new Dimension(600, 300));
        
        getContentPane().add(this.scrollPane, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        getContentPane().add(eastPanel, BorderLayout.EAST);
        getContentPane().add(westPanel, BorderLayout.WEST);
        
        pack();
    }
    
    private void setToInitialState() {
        
        this.scrollPane.getVerticalScrollBar().setValue(0);
        setLocationRelativeTo(this.parent);
    }
    
    /**
     * Shows the Help dialog
     * 
     * @param visible Shows the dialog if set to TRUE 
     */    
    @Override
	public void setVisible(boolean visible) {
        
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
}
