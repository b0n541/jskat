/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.Color;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Sets the look and feel
 */
public class LookAndFeelSetter {

	private static Log log = LogFactory.getLog(LookAndFeelSetter.class);
	
	/**
	 * Sets the look and feel
	 */
	public static void setLookAndFeel() {
		
		try {
			
			LookAndFeel laf = (LookAndFeel) Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance(); //$NON-NLS-1$
			
			laf.getDefaults().put("control", new Color(226, 217, 202)); //$NON-NLS-1$
			laf.getDefaults().put("text", new Color(0, 0, 0)); //$NON-NLS-1$
			laf.getDefaults().put("nimbusFocus", new Color(255, 245, 193)); //$NON-NLS-1$
			laf.getDefaults().put("nimbusLightBackground", new Color(241, 238, 229)); //$NON-NLS-1$
			laf.getDefaults().put("nimbusBase", new Color(96, 65, 34)); //$NON-NLS-1$
			
			UIManager.setLookAndFeel(laf);
			log.debug("NimbusLookAndFeel successfully applied..."); //$NON-NLS-1$
			
		} catch (UnsupportedLookAndFeelException e) {
			log.debug(e);
		} catch (InstantiationException e) {
			log.debug(e);
		} catch (IllegalAccessException e) {
			log.debug(e);
		} catch (ClassNotFoundException e) {
			log.info("NimbusLookAndFeel not found (probably not installed/wrong JDK)!"); //$NON-NLS-1$
		}
	}
}
