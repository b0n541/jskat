/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import java.util.ResourceBundle;

import jskat.data.JSkatDataModel;

/**
 * The action for showing the About dialog
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net>
 */
public class AboutDialogAction implements ActionListener {

    private static final Logger log = Logger.getLogger(jskat.gui.main.AboutDialogAction.class);
    
    /**
     * Creates a new instance of AboutDialogAction
     * @param dataModel The JSkatDataModel that holds all data
     * @param parent The parent JFrame
     */
    public AboutDialogAction(JSkatDataModel dataModel, JSkatFrame parent) {
        
        jskatStrings = dataModel.getResourceBundle();
        this.parent = parent;
        
        log.debug("AboutDialogAction is ready.");
    }
    
    /**
     * The action that should be performed
     * @param e Event that fires the action
     */    
    public void actionPerformed(ActionEvent e) {

        JLabel jskatLabel = new JLabel("JSkat 0.6");
        jskatLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        JPanel jskatPanel = new JPanel();
        jskatPanel.setLayout(new GridLayout(9, 1));
        jskatPanel.add(jskatLabel);
        jskatLabel = new JLabel(jskatStrings.getString("about_message1"));
        jskatPanel.add(jskatLabel);
        jskatLabel = new JLabel(jskatStrings.getString("about_message2"));
        jskatPanel.add(jskatLabel);
        jskatLabel = new JLabel(jskatStrings.getString("about_message8"));
        jskatPanel.add(jskatLabel);
        jskatLabel = new JLabel(jskatStrings.getString("about_message3"));
        jskatPanel.add(jskatLabel);
        jskatLabel = new JLabel(jskatStrings.getString("about_message4"));
        jskatPanel.add(jskatLabel);
        jskatLabel = new JLabel(jskatStrings.getString("about_message5"));
        jskatPanel.add(jskatLabel);
        jskatLabel = new JLabel(jskatStrings.getString("about_message6"));
        jskatPanel.add(jskatLabel);
        jskatLabel = new JLabel(jskatStrings.getString("about_message7"));
        jskatPanel.add(jskatLabel);

        JOptionPane.showMessageDialog(parent,
					                jskatPanel,
					                jskatStrings.getString("about_jskat"),
					                JOptionPane.INFORMATION_MESSAGE,
					                new ImageIcon(parent.getJSkatBitmaps().getJSkatLogoImage()));
    }
    
    private ResourceBundle jskatStrings;
    private JSkatFrame parent;
}
