/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share.exception;

/**
 * RuntimeException for wrong skat handling
 *
 */
public class SkatHandlingException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6557556861265549558L;

	/**
	 * Creates a special RuntimeException for wrong skat handling
	 * 
	 * @param msg Message
	 */
	public SkatHandlingException(String msg) {
        super(msg);
    }

}
