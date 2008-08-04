/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main.actions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import jskat.gui.main.JSkatFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The action for flipping the cards
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net>
 */
public class FlipCardsAction implements ActionListener {
    
	private Log log = LogFactory.getLog(FlipCardsAction.class);

	/**
     * Creates a new instance of FlipCardsAction
     * @param parent The parent JFrame
     */
    public FlipCardsAction(JSkatFrame parent) {
        
        this.parent = parent;
        
        log.debug("FlipCardsAction is ready.");
    }
    
    /**
     * The action that should be performed
     * @param e Event that fires the action
     */    
    public void actionPerformed(ActionEvent e) {
        
        parent.getPlayArea().flipCards();
    }
    
    private JSkatFrame parent;
}
