package org.jskat.gui.dialog

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.ImageView
import javafx.stage.Window
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.JSkatResourceBundle

/**
 * Native JavaFX replacement for the About dialog that was available in v0.23.
 */
class JSkatAboutDialog(
    private val applicationVersion: String,
    owner: Window? = null
) : Alert(AlertType.INFORMATION) {

    private val content = JSkatAboutDialogContent(applicationVersion)

    init {
        if (owner != null) {
            initOwner(owner)
        }

        title = content.title
        headerText = content.headerText
        graphic = ImageView(JSkatGraphicRepository.INSTANCE.jSkatLogoImageFX)
        contentText = content.text
        dialogPane.minWidth = 600.0
        dialogPane.buttonTypes.setAll(ButtonType.CLOSE)
    }
}

internal class JSkatAboutDialogContent(applicationVersion: String) {

    private val strings = JSkatResourceBundle.INSTANCE

    val title = strings.getString("about")
    val headerText = "JSkat ${strings.getString("version")} $applicationVersion"
    val text = """
            https://www.jskat.org
            https://github.com/b0n541/jskat

            ${strings.getString("authors")}:
            Jan Schäfer (jnschfr@gmail.com)
            Markus J. Luzius (jskat@luzius.de)
            Daniel Loreck (daniel.loreck@gmail.com)
            Sascha Laurien
            Slovasim
            Martin Rothe
            Tobias Markus

            ${strings.getString("cards")}: International Skat Server, KDE project, OpenClipart.org

            ${strings.getString("icons")}: Gnome Desktop Icons, Tango project, Elementary icons,
            Silvestre Herrera, Alex Roberts and Icojoy

            ${strings.getString("background_image")}: webtreats

            This program comes with ABSOLUTELY NO WARRANTY;
            for details see licence dialog.
            This is free software, and you are welcome to redistribute it
            under certain conditions; see licence dialog for details.
        """.trimIndent()
}
