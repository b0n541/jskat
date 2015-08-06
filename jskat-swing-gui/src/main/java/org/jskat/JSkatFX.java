package org.jskat;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.PropertyConfigurator;
import org.jskat.control.JSkatMaster;
import org.jskat.data.DesktopSavePathResolver;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.gui.swing.LookAndFeelSetter;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JSkatFX extends Application {
	
	public static void main(String[] args) {
		
        PropertyConfigurator.configure(ClassLoader
                .getSystemResource("org/jskat/config/log4j.properties")); //$NON-NLS-1$
        JSkatOptions.instance(new DesktopSavePathResolver());
		
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Hello World!");
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				LookAndFeelSetter.setLookAndFeel();
			}
		});
		
		JSkatViewImpl jskatView = new JSkatViewImpl();
		JSkatGraphicRepository.INSTANCE.toString();
		JSkatMaster.INSTANCE.setView(jskatView);
		
		SwingNode swingNode = new SwingNode();
		swingNode.setContent(jskatView.mainPanel);
		
		StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);
        
		primaryStage.setScene(new Scene(pane, 1000, 1000));
		primaryStage.show();
	}
}
