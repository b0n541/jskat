package org.jskat.gui.javafx

import javafx.collections.ListChangeListener
import javafx.scene.Scene
import javafx.stage.Window

object JSkatTheme {

    val stylesheetUrl: String = requireNotNull(
        JSkatTheme::class.java.getResource("/org/jskat/gui/javafx/jskat.css")
    ) { "JSkat stylesheet is missing" }.toExternalForm()

    private var installed = false

    /** Installs the theme for every JavaFX window, including dialog windows created later. */
    fun install() {
        if (installed) {
            return
        }

        installed = true
        Window.getWindows().forEach { window -> theme(window) }
        Window.getWindows().addListener(ListChangeListener<Window> { change ->
            while (change.next()) {
                if (change.wasAdded()) {
                    change.addedSubList.forEach { window -> theme(window) }
                }
            }
        })
    }

    private fun theme(window: Window) {
        window.sceneProperty().addListener { _, _, scene -> scene?.let(::theme) }
        window.scene?.let(::theme)
    }

    private fun theme(scene: Scene) {
        if (stylesheetUrl !in scene.stylesheets) {
            scene.stylesheets.add(stylesheetUrl)
        }
    }
}
