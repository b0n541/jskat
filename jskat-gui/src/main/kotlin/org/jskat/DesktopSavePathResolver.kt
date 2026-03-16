package org.jskat

import org.jskat.data.SavePathResolver
import java.nio.file.Paths

class DesktopSavePathResolver : SavePathResolver {
    override fun getDefaultSavePath(): String {
        return Paths.get(System.getProperty("user.home"), ".jskat").toString()
    }

    override fun getCurrentWorkingDirectory(): String {
        return System.getProperty("user.dir")
    }
}
