package org.jskat.gui.javafx.dialog.help

import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.web.WebView
import org.jskat.util.JSkatResourceBundle
import org.jskat.gui.javafx.JavaFxHostDocumentOpener
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader

class JSkatHelpDialog @JvmOverloads constructor(
    title: String,
    contentPath: String,
    private val documentOpener: JavaFxHostDocumentOpener? = null
) : Dialog<ButtonType>() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val webView = WebView()
    private val log = LoggerFactory.getLogger(JSkatHelpDialog::class.java)

    init {
        this.title = title
        dialogPane.content = webView
        dialogPane.buttonTypes.add(ButtonType.CLOSE)
        
        webView.prefWidth = 800.0
        webView.prefHeight = 600.0

        dialogPane.stylesheets.add("/org/jskat/gui/javafx/jskat.css")

        webView.engine.locationProperty().addListener { _, _, location ->
            documentOpener?.openIfExternal(location)
        }

        setFile(contentPath)
    }

    private fun setFile(filename: String) {
        val content = getResource(filename)
        val template = getResource("org/jskat/gui/help/frame.html")

        webView.engine.loadContent(prepareHelpContent(content, template) { link ->
            ClassLoader.getSystemResource(link)?.toExternalForm()
        })
    }

    private fun getResource(url: String): String {
        val message = StringBuilder()
        try {
            val `is` = ClassLoader.getSystemResourceAsStream(url)
            if (`is` != null) {
                val isr = InputStreamReader(`is`)
                val bfr = BufferedReader(isr)
                var line = bfr.readLine()
                while (line != null) {
                    message.append(line).append("\n")
                    line = bfr.readLine()
                }
            } else {
                log.warn("Resource not found: $url")
            }
        } catch (e: Exception) {
            log.warn("Error in loading message: ", e)
        }
        return message.toString()
    }
}

internal fun prepareHelpContent(
    content: String,
    template: String,
    localLinkUrl: (String) -> String?
): String {
    val css = """
        <style>
            body {
                font-family: sans-serif;
                font-size: 16px;
                padding: 20px;
            }
        </style>
    """.trimIndent()
    val resolvedLocalLinks = LOCAL_HELP_LINK.replace(content) { match ->
        localLinkUrl(match.groupValues[1])?.let { "href=\"$it\"" } ?: match.value
    }
    val withContent = template.replace("@@insert@@", resolvedLocalLinks)

    return withContent.replace("</head>", "$css</head>")
}

private val LOCAL_HELP_LINK = Regex("""href=\"(org/jskat/gui/help/[^\"]+)\"""")
