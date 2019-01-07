/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.util.version;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Checks the version of JSkat
 */
public class VersionChecker {

	/**
	 * Gets the latest version of JSkat from JSkat website
	 * 
	 * @return Latest version
	 */
	public static String getLatestVersion() {

		String result = ""; //$NON-NLS-1$

		try {
			VersionHandler handler = new VersionHandler();
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(new URL(
					"http://jskat.org/pad/jskat.xml").openStream())); //$NON-NLS-1$
			result = handler.versionString;
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

	/**
	 * Checks whether a new version is available or not
	 * 
	 * @param localVersion
	 *            Local JSkat version
	 * @param remoteVersion
	 *            Remote JSkat version from the JSkat website
	 * 
	 * @return TRUE, if a new version is available
	 */
	public static boolean isHigherVersionAvailable(final String localVersion,
			final String remoteVersion) {
		boolean result = false;

		List<Integer> localVersionParts = getVersionParts(localVersion);
		List<Integer> remoteVersionParts = getVersionParts(remoteVersion);

		int previousLocalPart = 0;
		int previousRemotePart = 0;
		int index = 0;
		for (Integer localVersionPart : localVersionParts) {
			if (remoteVersionParts.size() > index) {
				int remoteVersionPart = remoteVersionParts.get(index);
				if (previousLocalPart == previousRemotePart
						&& localVersionPart < remoteVersionPart) {
					result = true;
				}
				previousLocalPart = localVersionPart;
				previousRemotePart = remoteVersionPart;
			}
			index++;
		}

		return result;
	}

	private static List<Integer> getVersionParts(final String version) {
		List<Integer> result = new ArrayList<Integer>();
		StringTokenizer token = new StringTokenizer(version, "."); //$NON-NLS-1$
		while (token.hasMoreTokens()) {
			result.add(Integer.valueOf(token.nextToken()));
		}
		return result;
	}
}