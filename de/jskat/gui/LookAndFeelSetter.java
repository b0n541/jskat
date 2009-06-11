package de.jskat.gui;

import java.awt.Color;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LookAndFeelSetter {

	private static Log log = LogFactory.getLog(LookAndFeelSetter.class);
	
	public static void setLookAndFeel() {
		
		try {
			
			LookAndFeel laf = (LookAndFeel) Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance();
			
			laf.getDefaults().put("control", new Color(226, 217, 202));
			laf.getDefaults().put("text", new Color(0, 0, 0));
			laf.getDefaults().put("nimbusFocus", new Color(255, 245, 193));
			laf.getDefaults().put("nimbusLightBackground", new Color(241, 238, 229));
			laf.getDefaults().put("nimbusBase", new Color(96, 65, 34));
			
			UIManager.setLookAndFeel(laf);
			log.debug("NimbusLookAndFeel successfully applied...");
			
		} catch (UnsupportedLookAndFeelException e) {
			log.debug(e);
		} catch (InstantiationException e) {
			log.debug(e);
		} catch (IllegalAccessException e) {
			log.debug(e);
		} catch (ClassNotFoundException e) {
			log.info("NimbusLookAndFeel not found (probably not installed/wrong JDK)!");
		}
	}
}
