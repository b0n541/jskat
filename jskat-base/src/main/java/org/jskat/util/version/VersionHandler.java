/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-10
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
