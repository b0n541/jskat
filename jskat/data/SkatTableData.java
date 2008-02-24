/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.data;

import java.util.Observable;
import java.util.Vector;

import jskat.control.SkatGame;
import jskat.control.SkatSeries;

/**
 * Data Class for a Skat table
 *
 */
public class SkatTableData extends Observable {

	/**
	 * Constructor
	 */
	public SkatTableData() {
		
		skatSeries = new Vector<SkatSeries>();
		currSkatSeriesID = -1;
		
	}
	
	/**
	 * Gets the current skat series
	 * 
	 * @return Current skat series
	 */
	public SkatSeries getCurrSkatSeries() {
		
		// 19.05.2007 mjl: return null, if there is no series yet...
		if(currSkatSeriesID<0) return null;
		
		return (SkatSeries) skatSeries.get(currSkatSeriesID);
	}
	
	/**
	 * Returns the current game
	 * 
	 * @return The current game
	 */
	public SkatGame getCurrSkatGame() {
		
		return getCurrSkatSeries().getCurrSkatGame();
	}
	
	/**
	 * Returns the data of the current game
	 * 
	 * @return The data of the current game
	 */
	public SkatGameData getCurrGameData() {
		
		return getCurrSkatSeries().getCurrSkatGameData();
	}
	
	/**
	 * Adds a new SkatSeries to the table data
	 * 
	 * @param newSkatSeries The new SkatSeries
	 * @return The ID of the new SkatSeries
	 */
	public int addSkatSeries(SkatSeries newSkatSeries) {
		
		this.skatSeries.add(newSkatSeries);
		currSkatSeriesID = skatSeries.size() - 1; 
		
		setChanged();
		notifyObservers(newSkatSeries);
		
		return currSkatSeriesID;
	}
	
	private Vector<SkatSeries> skatSeries;
	
	private int currSkatSeriesID;
}
