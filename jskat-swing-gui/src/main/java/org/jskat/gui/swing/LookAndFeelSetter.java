/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.Screen;

/**
 * Sets the look and feel
 */
public class LookAndFeelSetter {

	private static Logger log = LoggerFactory.getLogger(LookAndFeelSetter.class);

	/**
	 * Sets the look and feel.
	 *
	 * @param targetScreen
	 *            Target screen for main window
	 */
	public static void setLookAndFeel(final Screen targetScreen) {

		try {
			LookAndFeel laf = null;

			try {
				laf = (LookAndFeel) Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance();
			} catch (final ClassNotFoundException e) {
				laf = (LookAndFeel) Class.forName("javax.swing.plaf.nimbus.NimbusLookAndFeel").newInstance();
			}

			laf.getDefaults().put("control", new Color(226, 217, 202)); //$NON-NLS-1$
			laf.getDefaults().put("text", new Color(0, 0, 0)); //$NON-NLS-1$
			laf.getDefaults().put("nimbusFocus", new Color(255, 245, 193)); //$NON-NLS-1$
			laf.getDefaults().put("nimbusLightBackground", new Color(241, 238, 229)); //$NON-NLS-1$
			laf.getDefaults().put("nimbusBase", new Color(96, 65, 34)); //$NON-NLS-1$

			if (ScreenResolution.isVeryBigScreen(targetScreen)) {
				laf.getDefaults().put("defaultFont", new Font(Font.SANS_SERIF, 0, 14));
			}

			UIManager.setLookAndFeel(laf);
			log.debug("NimbusLookAndFeel successfully applied..."); //$NON-NLS-1$

		} catch (final UnsupportedLookAndFeelException e) {
			log.debug(e.toString());
		} catch (final InstantiationException e) {
			log.debug(e.toString());
		} catch (final IllegalAccessException e) {
			log.debug(e.toString());
		} catch (final ClassNotFoundException e) {
			log.info("NimbusLookAndFeel not found (probably not installed/wrong JDK)!"); //$NON-NLS-1$
		}
	}
}
