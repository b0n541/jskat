package org.jskat.util.version;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Extracts the version number from the PAD XML file
 */
class VersionHandler extends DefaultHandler {

    String versionString = "";
    String workingString = null;

    @Override
    public void characters(char[] ch, int start, int length) {

        workingString = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        if ("Program_Version".equals(localName)) {
            versionString = workingString;
        }
    }
}
