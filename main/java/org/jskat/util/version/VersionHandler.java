/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0
 * Copyright (C) 2012-03-13
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
package org.jskat.util.version;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Extracts the version number from the PAD XML file
 */
class VersionHandler extends DefaultHandler {

	String versionString = ""; //$NON-NLS-1$
	String workingString = null;

	@Override
	public void characters(char[] ch, int start, int length) {

		workingString = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) {

		if ("Program_Version".equals(localName)) { //$NON-NLS-1$
			versionString = workingString;
		}
	}
}
