/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.util.Vector;

import jskat.control.JSkatMaster;
import jskat.data.JSkatDataModel;
import jskat.gui.JSkatGraphicRepository;

/**
 * Holds all actions for the JSkat GUI
 * 
 * @author Jan Schäfer <jan.schaefer@b0n541.net>
 */
public class JSkatActions {

    /**
     * Creates all JSkat actions
     */
    public JSkatActions(JSkatMaster jskatMaster,
                        JSkatDataModel dataModel,
                        JSkatFrame mainFrame,
                        JSkatGraphicRepository jskatBitmaps,
                        Vector aiPlayer) {

        // Actions definition
        aboutDialogAction = new AboutDialogAction(dataModel, mainFrame);
        helpDialogAction = new HelpDialogAction(dataModel, mainFrame);
        exitJSkatAction = new ExitJSkatAction(jskatMaster);
        optionsDialogAction = new OptionsDialogAction(jskatMaster, dataModel, mainFrame);
		newSkatTableDialogAction = new NewSkatSeriesDialogAction(jskatMaster,
				dataModel, mainFrame, aiPlayer);
        flipCardsAction = new FlipCardsAction(mainFrame);
        lastTricksDialogAction = new LastTricksDialogAction(dataModel, jskatBitmaps, mainFrame);
    }

    public AboutDialogAction getAboutDialogAction() {
        return aboutDialogAction;
    }
    
    public HelpDialogAction getHelpDialogAction() {
        return helpDialogAction;
    }
    
    public ExitJSkatAction getExitJSkatAction() {
        return exitJSkatAction;
    }
    
    public OptionsDialogAction getOptionsDialogAction() {
        return optionsDialogAction;
    }
    
    public NewSkatSeriesDialogAction getNewSkatRoundDialogAction(){
        return newSkatTableDialogAction;
    }
    
    public FlipCardsAction getFlipCardsAction(){
        return flipCardsAction;
    }
    
    public LastTricksDialogAction getLastTricksDialogAction(){
        return lastTricksDialogAction;
    }
    
    private AboutDialogAction aboutDialogAction;
    private HelpDialogAction helpDialogAction;
    private ExitJSkatAction exitJSkatAction;
    private OptionsDialogAction optionsDialogAction;
    private NewSkatSeriesDialogAction newSkatTableDialogAction;
    private FlipCardsAction flipCardsAction;
    private LastTricksDialogAction lastTricksDialogAction;
}
