package jskat.gui;

import jskat.control.SkatGame;
import jskat.control.SkatSeries;
import jskat.control.SkatTable;

public interface JSkatView {

	public void startTable(SkatTable table);
	
	public void closeTable(SkatTable table);
	
	public void startSeries(SkatSeries series);

	public void pauseSeries(SkatSeries series);
	
	public void finishSeries(SkatSeries series);
	
	public void startGame(SkatGame game);
	
	public void startDealing(SkatGame game);
	
	public void startBidding(SkatGame game);
	
	public void startCardPlaying(SkatGame game);
	
	public void showLastTrick(SkatGame game);
	
	public void showResults(SkatGame game);

	public void finishGame(SkatGame game);
}
