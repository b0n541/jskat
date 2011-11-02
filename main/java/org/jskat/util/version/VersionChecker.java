package org.jskat.util.version;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Checks the version of JSkat
 */
public class VersionChecker {

	/**
	 * Gets the latest version of JSkat from sourceforge.net
	 * 
	 * @return Latest version
	 */
	public static double getLatestVersion() {

		double result = 0.0;

		try {
			VersionHandler handler = new VersionHandler();
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(new URL("http://jskat.org/pad/jskat.xml").openStream())); //$NON-NLS-1$
			result = handler.versionNumber;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
