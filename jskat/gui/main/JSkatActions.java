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
 */
public class JSkatActions {

    /**
     * Creates all actions used in JSkat
     * 
     * @param jskatMaster JSkatMaster that controls the whole application
     * @param dataModel Data model
     * @param mainFrame Main frame
     * @param jskatBitmaps Graphic repository that holds all graphics
     * @param aiPlayer Names of all possible AI player
     */
    public JSkatActions(JSkatMaster jskatMaster,
                        JSkatDataModel dataModel,
                        JSkatFrame mainFrame,
                        JSkatGraphicRepository jskatBitmaps,
                        Vector<String> aiPlayer) {

        // Actions definition
        aboutDialogAction = new AboutDialogAction(dataModel, mainFrame);
        helpDialogAction = new HelpDialogAction(dataModel, mainFrame);
        exitJSkatAction = new ExitJSkatAction(jskatMaster);
        optionsDialogAction = new OptionsDialogAction(jskatMaster, dataModel, mainFrame);
		newSkatSeriesDialogAction = new NewSkatSeriesDialogAction(jskatMaster,
				dataModel, mainFrame, aiPlayer);
        flipCardsAction = new FlipCardsAction(mainFrame);
        lastTricksDialogAction = new LastTricksDialogAction(dataModel, jskatBitmaps, mainFrame);
    }

    /**
     * Gets the AboutDialogAction
     * 
     * @return Reference to the AboutDialogAction
     */
    public AboutDialogAction getAboutDialogAction() {
        return aboutDialogAction;
    }
    
    /**
     * Gets the HelpDialogAction
     * 
     * @return Reference to the HelpDialogAction
     */
    public HelpDialogAction getHelpDialogAction() {
        return helpDialogAction;
    }
    
    /**
     * Gets the ExitJSkatAction
     * 
     * @return Reference to the ExitJSkatAction
     */
    public ExitJSkatAction getExitJSkatAction() {
        return exitJSkatAction;
    }
    
    /**
     * Gets the OptionsDialogAction
     * 
     * @return Reference to the OptionsDialogAction
     */
    public OptionsDialogAction getOptionsDialogAction() {
        return optionsDialogAction;
    }
    
    /**
     * Gets the NewSkatSeriesDialogAction
     * 
     * @return Reference to the NewSkatSeriesDialogAction
     */
    public NewSkatSeriesDialogAction getNewSkatSeriesDialogAction(){
        return newSkatSeriesDialogAction;
    }
    
    /**
     * Gets the FlipCardsAction
     * 
     * @return Reference to the FlipCardsAction
     */
    public FlipCardsAction getFlipCardsAction(){
        return flipCardsAction;
    }
    
    /**
     * Gets the LastTricksDialogAction
     * 
     * @return Reference to the LastTricksDialogAction
     */
    public LastTricksDialogAction getLastTricksDialogAction(){
        return lastTricksDialogAction;
    }
    
    private AboutDialogAction aboutDialogAction;
    private HelpDialogAction helpDialogAction;
    private ExitJSkatAction exitJSkatAction;
    private OptionsDialogAction optionsDialogAction;
    private NewSkatSeriesDialogAction newSkatSeriesDialogAction;
    private FlipCardsAction flipCardsAction;
    private LastTricksDialogAction lastTricksDialogAction;
}
