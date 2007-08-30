/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import jskat.control.JSkatMaster;
import jskat.data.JSkatDataModel;
import jskat.gui.options.JSkatOptionsDialog;

/**
 * The OptionsDialogAction for showing the Options dialog
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net>
 */
public class OptionsDialogAction implements ActionListener {
    
    static Logger log = Logger.getLogger(jskat.gui.main.OptionsDialogAction.class);

    /**
     * Creates a new instance of OptionsDialogAction
     * @param jskatMaster The JSkatMaster that controls the game
     * @param dataModel The JSkatDataModel that holds all data
     * @param parent The parent JFrame
     */
    public OptionsDialogAction(JSkatMaster jskatMaster, 
                               JSkatDataModel dataModel, 
                               JFrame parent) {
        
        jskatOptionsDialog = new JSkatOptionsDialog(jskatMaster, dataModel, parent, true);
        
        log.debug("OptionsDialogAction is ready.");
    }
    
    /**
     * The action that should be performed
     * @param e The Event that fires the action
     */    
    public void actionPerformed(ActionEvent e) {
        
        jskatOptionsDialog.setVisible(true);
    }
    
    private JSkatOptionsDialog jskatOptionsDialog;
}
