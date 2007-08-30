/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import jskat.control.JSkatMaster;

/**
 * The action for exiting JSkat
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net>
 */
public class ExitJSkatAction implements ActionListener {
    
	static Logger log = Logger.getLogger(jskat.gui.main.ExitJSkatAction.class);

	/**
     * Creates a new instance of AboutDialogAction
     * @param jskatMaster The JSkatMaster that controls the game
     */
    public ExitJSkatAction(JSkatMaster jskatMaster) {
        
        this.jskatMaster = jskatMaster;
        
        log.debug("ExitJSkatAction is ready.");
    }
    
    /**
     * The action that should be performed
     * @param e The event that fires the action
     */    
    public void actionPerformed(ActionEvent e) {
        
        jskatMaster.exitJSkat();
    }
    
    private JSkatMaster jskatMaster;
}
