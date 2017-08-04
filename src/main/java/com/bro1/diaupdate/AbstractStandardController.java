package com.bro1.diaupdate;

import javafx.stage.Stage;

abstract public class AbstractStandardController implements StandardControllerInterface {

	private Stage myStage;
	
	public Stage getStage() {
		return myStage;
	}

	@Override
	public void setStage(Stage stage) {
		this.myStage = stage;
	}

}
