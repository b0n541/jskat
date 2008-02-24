/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share.exception;

/**
 * RuntimeException for wrong card play
 */
public class WrongCardException extends RuntimeException {

	private static final long serialVersionUID = 7111950744832918538L;

	/**
	 * Creates a special RuntimeException for wrong card play
	 * 
	 * @param msg Message
	 */
    public WrongCardException(String msg) {
        super(msg);
    }
}
