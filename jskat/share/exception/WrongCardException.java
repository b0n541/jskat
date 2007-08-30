/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share.exception;

/**
 * Is raised when a player plays a card that he doesn't own
 * 
 * @author Jan Sch&auml;fer
 *
 */
public class WrongCardException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7111950744832918538L;

	/**
     * 
     */
    public WrongCardException(String msg) {
        super(msg);
    }
}
