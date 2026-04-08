package com.AlgoVista.linkedlist;

import com.AlgoVista.utils.ShortcutManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class LinkedListMenuController {

    @FXML
    private VBox singlyCard;

    @FXML
    private VBox doublyCard;

    @FXML
    public void initialize() {
        // Add hover effects dynamically
        setupHoverEffect(singlyCard);
        setupHoverEffect(doublyCard);

        // Register back shortcut when scene is available
        singlyCard.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ShortcutManager.register(newScene, null, null, null, () -> backToDashboard());
            }
        });
    }

    private void setupHoverEffect(VBox card) {
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 15; -fx-border-color: #38bdf8; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(56, 189, 248, 0.4), 15, 0, 0, 0);");
            card.setScaleX(1.05);
            card.setScaleY(1.05);
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 15;");
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });
    }

    @FXML
    private void openSinglyLinkedList() {
        navigateTo("/fxml/SinglyLinkedList.fxml");
    }

    @FXML
    private void openDoublyLinkedList() {
        navigateTo("/fxml/DoublyLinkedList.fxml");
    }

    @FXML
    private void backToDashboard() {
        navigateTo("/fxml/dashboard.fxml");
    }

    private void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) singlyCard.getScene().getWindow();
            double width = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();
            double x = stage.getX();
            double y = stage.getY();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setX(x);
            stage.setY(y);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load fxml from " + fxmlPath);
        }
    }
}
