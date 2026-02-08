package com.AlgoVista.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // This path must match your resources/fxml folder structure
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700);

        stage.setTitle("AlgoVista - DSA Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}