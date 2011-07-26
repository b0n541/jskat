/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui;

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
