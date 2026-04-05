package com.AlgoVista.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700);

        stage.setTitle("AlgoVista");
        stage.setScene(scene);
        stage.show();
    }
}