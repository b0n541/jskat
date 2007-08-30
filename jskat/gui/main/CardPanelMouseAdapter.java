/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A MouseAdapter for the CardPanels to recognize clicks on the cards
 * @author Jan Schäfer <jan.schaefer@b0n541.net>
 */
public class CardPanelMouseAdapter extends MouseAdapter {
    
    /**
     * Fires when a CardPanel is clicked by the user
     * @param e Mouse event
     */    
    public void mousePressed(MouseEvent e) {
        
        ((CardPanel) e.getSource()).cardClicked();
    }
}
