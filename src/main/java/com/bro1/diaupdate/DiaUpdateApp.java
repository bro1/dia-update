package com.bro1.diaupdate;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DiaUpdateApp extends Application {

	public static Stage myStage = null;

	public static void main(String[] args) {
		Application.launch(DiaUpdateApp.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		myStage = stage;

		URL res = getClass().getResource("DiaUpdater.fxml");
		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(res);
		loader.setBuilderFactory(new JavaFXBuilderFactory());

		DiaUpdateController controller = new DiaUpdateController();
		controller.myStage = stage;
		loader.setController(controller);

		Parent root = (Parent) loader.load(res.openStream());
		
		Scene scene = new Scene(root);
		
		stage.setScene(scene);

		stage.setTitle("Dia Updater");
		stage.show();
		
	}
}
