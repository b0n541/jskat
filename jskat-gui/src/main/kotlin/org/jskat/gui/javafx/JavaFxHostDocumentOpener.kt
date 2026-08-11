package org.jskat.gui.javafx

import org.slf4j.LoggerFactory

class JavaFxHostDocumentOpener(private val showDocument: (String) -> Unit) {

    private val log = LoggerFactory.getLogger(JavaFxHostDocumentOpener::class.java)

    fun open(link: String) {
        try {
            showDocument(link)
        } catch (e: Exception) {
            log.error("Error opening web page: $link", e)
        }
    }

    fun openIfExternal(link: String): Boolean {
        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            return false
        }

        open(link)
        return true
    }
}
