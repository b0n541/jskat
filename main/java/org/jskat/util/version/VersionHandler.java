package org.jskat.util.version;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Extracts the version number from the PAD XML file
 */
class VersionHandler extends DefaultHandler {

	String versionString = null;
	double versionNumber = 0.0;

	@Override
	public void characters(char[] ch, int start, int length) {

		versionString = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) {

		if ("Program_Version".equals(localName)) { //$NON-NLS-1$
			versionNumber = Double.parseDouble(versionString);
		}
	}
}
