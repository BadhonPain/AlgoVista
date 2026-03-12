package com.AlgoVista.graphs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphAlgorithmsCategoryController {
    @FXML
    private FlowPane cardContainer;

    @FXML
    public void initialize() {
        loadCards();
    }

    private void loadCards() {
        // DFS Card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox dfsCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("DFS");

            String cardNormalStyle = "-fx-background-color: #0f172a;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/dfs_card.png');" +
                    "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 25;" +
                    "-fx-border-color: #fce7f3;" +
                    "-fx-border-width: 3;" +
                    "-fx-border-radius: 25;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.08; -fx-scale-y: 1.08; -fx-effect: dropshadow(gaussian, rgba(236, 72, 153, 0.9), 25, 0, 0, 5);";

            dfsCard.setStyle(cardNormalStyle);
            dfsCard.setOnMouseEntered(e -> dfsCard.setStyle(cardHoverStyle));
            dfsCard.setOnMouseExited(e -> dfsCard.setStyle(cardNormalStyle));
            dfsCard.setOnMouseClicked(e -> {
                try {
                    FXMLLoader dfsLoader = new FXMLLoader(getClass().getResource("/fxml/DFS.fxml"));
                    Parent dfsRoot = dfsLoader.load();
                    Stage stage = (Stage) cardContainer.getScene().getWindow();
                    double w = stage.getWidth(), h = stage.getHeight();
                    double x = stage.getX(), y = stage.getY();
                    stage.setScene(new Scene(dfsRoot, w, h));
                    stage.setX(x); stage.setY(y);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            cardContainer.getChildren().add(dfsCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Topological Sort Card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox topoCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("Topological Sort");

            String cardNormalStyle = "-fx-background-color: #0f172a;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/topo_sort_card.png');" +
                    "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 25;" +
                    "-fx-border-color: #e0f2fe;" +
                    "-fx-border-width: 3;" +
                    "-fx-border-radius: 25;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.08; -fx-scale-y: 1.08; -fx-effect: dropshadow(gaussian, rgba(14, 165, 233, 0.9), 25, 0, 0, 5);";

            topoCard.setStyle(cardNormalStyle);
            topoCard.setOnMouseEntered(e -> topoCard.setStyle(cardHoverStyle));
            topoCard.setOnMouseExited(e -> topoCard.setStyle(cardNormalStyle));
            topoCard.setOnMouseClicked(e -> {
                try {
                    FXMLLoader topoLoader = new FXMLLoader(getClass().getResource("/fxml/TopologicalSort.fxml"));
                    Parent topoRoot = topoLoader.load();
                    Stage stage = (Stage) cardContainer.getScene().getWindow();
                    double w = stage.getWidth(), h = stage.getHeight();
                    double x = stage.getX(), y = stage.getY();
                    stage.setScene(new Scene(topoRoot, w, h));
                    stage.setX(x); stage.setY(y);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            cardContainer.getChildren().add(topoCard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/RecursionCategory.fxml"));
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
