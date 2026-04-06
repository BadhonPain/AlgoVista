package com.AlgoVista.utils;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

public class CustomAlert {

    private static AudioClip errorSound;

    static {
        try {
            URL resource = CustomAlert.class.getResource("/com/AlgoVista/sounds/error_click.wav");
            if (resource != null) {
                errorSound = new AudioClip(resource.toExternalForm());
                errorSound.setVolume(1.0);
            } else {
                System.out.println("Audio resource not found: /com/AlgoVista/sounds/error_click.wav");
            }
        } catch (Exception e) {
            System.out.println("Error Audio could not be loaded: " + e.getMessage());
        }
    }

    public static void showError(String title, String msg) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showError(title, msg));
            return;
        }

        if (errorSound != null) {
            try { 
                errorSound.play(); 
            } catch(Exception e) {
                System.out.println("Could not play error sound: " + e.getMessage());
            }
        }
        
        displayAlert(title, msg, true);
    }
    
    public static void showInfo(String title, String msg) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showInfo(title, msg));
            return;
        }
        displayAlert(title, msg, false);
    }

    private static void displayAlert(String title, String msg, boolean isError) {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        
        String borderColor = isError ? "#e74c3c" : "#38bdf8"; // Red for Error, Cyan for Info
        String glowColor = isError ? "rgba(231, 76, 60, 0.4)" : "rgba(56, 189, 248, 0.4)";

        root.setStyle(
            "-fx-background-color: rgba(30, 41, 59, 0.95);" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 20;" +
            "-fx-border-radius: 20;" +
            "-fx-padding: 30 40 30 40;"
        );
        root.setEffect(new DropShadow(40, Color.web(glowColor)));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: " + borderColor + "; -fx-font-size: 22px; -fx-font-weight: bold; -fx-font-family: 'Inter', sans-serif;");

        Label msgLabel = new Label(msg);
        msgLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 14px; -fx-font-family: 'Inter', sans-serif; -fx-padding: 0 0 10 0;");
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(300);
        msgLabel.setTextAlignment(TextAlignment.CENTER);

        Button btn = new Button("Dismiss");
        String btnStyleNormal = "-fx-background-color: transparent; -fx-border-color: " + borderColor + "; -fx-text-fill: " + borderColor + "; -fx-border-radius: 10; -fx-padding: 8 30; -fx-font-weight: bold; -fx-cursor: hand;";
        String btnStyleHover = "-fx-background-color: " + borderColor + "; -fx-text-fill: #0f172a; -fx-border-radius: 10; -fx-padding: 8 30; -fx-font-weight: bold; -fx-cursor: hand;";
        
        btn.setStyle(btnStyleNormal);
        btn.setOnAction(e -> stage.close());
        btn.setOnMouseEntered(e -> btn.setStyle(btnStyleHover));
        btn.setOnMouseExited(e -> btn.setStyle(btnStyleNormal));

        root.getChildren().addAll(titleLabel, msgLabel, btn);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        
        // Disable window minimizing interactions outside the dialogue
        stage.showAndWait();
    }
}
