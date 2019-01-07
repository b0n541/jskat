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
package org.jskat.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.security.CodeSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves the default save path for desktop applications
 */
public class DesktopSavePathResolver implements SavePathResolver {
	
	private final static Logger log = LoggerFactory.getLogger(DesktopSavePathResolver.class);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDefaultSavePath() {
		return System.getProperty("user.home") //$NON-NLS-1$
				+ System.getProperty("file.separator") + ".jskat" //$NON-NLS-1$ //$NON-NLS-2$
				+ System.getProperty("file.separator"); //$NON-NLS-1$
	}

	@Override
	public String getCurrentWorkingDirectory() {
		
		CodeSource codeSource = DesktopSavePathResolver.class.getProtectionDomain().getCodeSource();
		
		String workingDir;
		try {
			File jarFile= new File(codeSource.getLocation().toURI().getPath());
			workingDir = jarFile.getParentFile().getPath() + File.separator;
		} catch (URISyntaxException e) {
			workingDir = getDefaultSavePath();
		}
		
		return workingDir;
	}
}
