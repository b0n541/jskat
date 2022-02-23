package org.jskat.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

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
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            workingDir = jarFile.getParentFile().getPath() + File.separator;
        } catch (URISyntaxException e) {
            workingDir = getDefaultSavePath();
        }

        return workingDir;
    }
}
