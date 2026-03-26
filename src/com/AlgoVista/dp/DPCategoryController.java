package com.AlgoVista.dp;

import com.AlgoVista.graphs.GraphSubCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DPCategoryController {
    @FXML private FlowPane cardContainer;

    @FXML
    public void initialize() {
        loadCards();
    }

    private void loadCards() {
        addAlgorithmCard("Fibonacci", "#38bdf8", "/com/AlgoVista/images/fibonacci.png", this::openFibonacciView);
        addAlgorithmCard("LCS", "#10b981", "/com/AlgoVista/images/lcs.png", this::openLCSView);
        addAlgorithmCard("Knapsack", "#f59e0b", "/com/AlgoVista/images/knapsack.png", this::openKnapsackView);
    }

    private void addAlgorithmCard(String name, String accentColor, String bgPath, Runnable onClick) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox card = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData(name);

            String imageUrl = getClass().getResource(bgPath) != null ? 
                             getClass().getResource(bgPath).toExternalForm() : "";
            
            String normalStyle =
                    "-fx-background-image: url('" + imageUrl + "');" +
                    "-fx-background-size: cover;" +
                    "-fx-background-position: center;" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-color: rgba(255,255,255,0.2);" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 15;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 15, 0, 0, 5);";

            String hoverStyle = normalStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, " + accentColor + ", 20, 0, 0, 0);";

            card.setStyle(normalStyle);
            
            card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
            card.setOnMouseExited(e -> card.setStyle(normalStyle));
            card.setOnMouseClicked(e -> onClick.run());

            cardContainer.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToDashboard() {
        switchScene("/fxml/dashboard.fxml");
    }

    @FXML
    private void openFibonacciView() {
        switchScene("/fxml/Fibonacci.fxml");
    }

    @FXML
    private void openLCSView() {
        switchScene("/fxml/LCS.fxml");
    }

    @FXML
    private void openKnapsackView() {
        switchScene("/fxml/Knapsack.fxml");
    }

    private void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) cardContainer.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setX(x);
            stage.setY(y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
