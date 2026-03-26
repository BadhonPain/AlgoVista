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
        // DFS card
        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox dfsCard = loader1.load();
            GraphSubCardController controller1 = loader1.getController();
            controller1.setData("DFS");

            // Use standard dimensions (matching BST's "normal" size)
            dfsCard.setPrefWidth(388);
            dfsCard.setPrefHeight(400);
            dfsCard.setMaxWidth(388);
            dfsCard.setMaxHeight(400);

            // Normal style - restored for standard fit
            String dfsNormalStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/dfs_card.png');" +
                            "-fx-background-size: 85%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.4);" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 20;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);";

            // Hover style - Rose glow for DFS
            String dfsHoverStyle =
                    "-fx-background-color: #1E293B;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/dfs_card.png');" +
                            "-fx-background-size: 88%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: #f43f5e;" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 20;" +
                            "-fx-scale-x: 1.03;" +
                            "-fx-scale-y: 1.03;" +
                            "-fx-effect: dropshadow(gaussian, rgba(244, 63, 94, 0.6), 20, 0.4, 0, 0);";

            dfsCard.setStyle(dfsNormalStyle);

            // Hover effects
            dfsCard.setOnMouseEntered(e -> dfsCard.setStyle(dfsHoverStyle));
            dfsCard.setOnMouseExited(e -> dfsCard.setStyle(dfsNormalStyle));

            // Click handler
            dfsCard.setOnMouseClicked(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DFS.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) cardContainer.getScene().getWindow();
                    stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            cardContainer.getChildren().add(dfsCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Topological Sort card
        try {
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox topoCard = loader2.load();
            GraphSubCardController controller2 = loader2.getController();
            controller2.setData("Topological Sort");

            // Use standard dimensions
            topoCard.setPrefWidth(388);
            topoCard.setPrefHeight(400);
            topoCard.setMaxWidth(388);
            topoCard.setMaxHeight(400);

            // Normal style
            String topoNormalStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/topo_sort_card.png');" +
                            "-fx-background-size: 85%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.4);" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 20;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);";

            // Hover style
            String topoHoverStyle =
                    "-fx-background-color: #1E293B;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/topo_sort_card.png');" +
                            "-fx-background-size: 88%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: #38bdf8;" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 20;" +
                            "-fx-scale-x: 1.03;" +
                            "-fx-scale-y: 1.03;" +
                            "-fx-effect: dropshadow(gaussian, rgba(56, 189, 248, 0.6), 20, 0.4, 0, 0);";

            topoCard.setStyle(topoNormalStyle);

            // Hover effects
            topoCard.setOnMouseEntered(e -> topoCard.setStyle(topoHoverStyle));
            topoCard.setOnMouseExited(e -> topoCard.setStyle(topoNormalStyle));

            // Click handler
            topoCard.setOnMouseClicked(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TopologicalSort.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) cardContainer.getScene().getWindow();
                    stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
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
            loader.setLocation(getClass().getResource("/fxml/GraphCategory.fxml"));
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
