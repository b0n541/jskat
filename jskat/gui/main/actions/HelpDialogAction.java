/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main.actions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jskat.data.JSkatDataModel;
import jskat.gui.help.JSkatHelpDialog;

/**
 * The action for showing the Help dialog
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net>
 */
public class HelpDialogAction implements ActionListener {

	private Log log = LogFactory.getLog(HelpDialogAction.class);

    /**
     * Creates a new instance of HelpDialogAction
     * @param dataModel The JSkatDataModel that holds all data
     * @param parent The parent JFrame
     */
    public HelpDialogAction(JSkatDataModel dataModel, JFrame parent) {
        
        jskatHelpDialog = new JSkatHelpDialog(dataModel, parent, true);
        
        log.debug("HelpDialogAction is ready.");
    }
    
    /**
     * The action that should be performed
     * @param e The Event that fires the action
     */    
    public void actionPerformed(ActionEvent e) {
        
        jskatHelpDialog.setVisible(true);
    }
    
    private JSkatHelpDialog jskatHelpDialog;
}
