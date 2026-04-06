package com.AlgoVista.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700);

        try {
            URL clickUrl = getClass().getResource("/com/AlgoVista/sounds/mouse_click.mp3");
            if (clickUrl != null) {
                AudioClip clickSound = new AudioClip(clickUrl.toExternalForm());
                clickSound.setVolume(0.3); // Soft volume for UI clicks
                
                // Add a global listener directly to the window (Stage)
                stage.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    try {
                        clickSound.play();
                    } catch (Exception ex) {
                        // Ignore rapid click errors
                    }
                });
            }
        } catch (Exception e) {
            System.out.println("Global click sound failed to load: " + e.getMessage());
        }

        stage.setTitle("AlgoVista");
        stage.setScene(scene);
        stage.show();
    }
}