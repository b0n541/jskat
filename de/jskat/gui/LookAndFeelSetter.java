package de.jskat.gui;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

public class LookAndFeelSetter {
	
	public static void setLookAndFeel() {
		
		try {
			
			NimbusLookAndFeel nlaf = new NimbusLookAndFeel();
			
			nlaf.getDefaults().put("control", new Color(226, 217, 202));
			nlaf.getDefaults().put("text", new Color(0, 0, 0));
			nlaf.getDefaults().put("nimbusFocus", new Color(255, 245, 193));
			nlaf.getDefaults().put("nimbusLightBackground", new Color(241, 238, 229));
			nlaf.getDefaults().put("nimbusBase", new Color(96, 65, 34));
			
			UIManager.setLookAndFeel(nlaf);
			
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
