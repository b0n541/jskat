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
        val classpathRoot = checkNotNull(JSkatHelpDialog::class.java.getResource("/")) {
            "Could not find the application classpath root"
        }.toExternalForm()

        webView.engine.loadContent(prepareHelpContent(content, template, classpathRoot))
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

internal fun prepareHelpContent(content: String, template: String, classpathRoot: String): String {
    val css = """
        <style>
            body {
                font-family: sans-serif;
                font-size: 16px;
                padding: 20px;
            }
        </style>
    """.trimIndent()
    val withContent = template.replace("@@insert@@", content)

    return withContent.replace("</head>", "<base href=\"$classpathRoot\">$css</head>")
}
