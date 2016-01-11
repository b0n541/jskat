package org.jskat.gui.javafx.dialog.firststeps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class FirstStepsDialogController {
	@FXML
	private WebView firstStepsContent;

	@FXML
	public void initialize() {
		WebEngine engine = firstStepsContent.getEngine();
		try {
			String contentUrl = Paths.get("org/jskat/gui/help/de/gettingStarted.html").toUri().toURL().toExternalForm();
			System.out.println(contentUrl);
			engine.loadContent(new String(Files.readAllBytes(Paths.get("org/jskat/gui/help/de/gettingStarted.html"))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
