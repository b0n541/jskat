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

/**
 * The action for flipping the cards
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net>
 */
public class FlipCardsAction implements ActionListener {
    
	static Logger log = Logger.getLogger(jskat.gui.main.FlipCardsAction.class);

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
