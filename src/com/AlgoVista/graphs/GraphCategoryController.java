package com.AlgoVista.graphs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphCategoryController {
    @FXML private FlowPane cardContainer;

    @FXML
    public void initialize() {
        System.out.println("GraphCategoryController initialized!");
        loadCards();
    }

    private void loadCards() {
        System.out.println("Loading cards...");

        // Structure card
        try {
            System.out.println("Loading Structure card...");
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox structureCard = loader1.load();
            GraphSubCardController controller1 = loader1.getController();
            controller1.setData("Structure");

            // Use standard dimensions (matching BST's "normal" size)
            structureCard.setPrefWidth(388);
            structureCard.setPrefHeight(400);
            structureCard.setMaxWidth(388);
            structureCard.setMaxHeight(400);

            // Normal style - restored for standard fit
            String structureNormalStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/structure.png');" +
                            "-fx-background-size: 85%;" + 
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.4);" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 20;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);";

            // Hover style - "Best" premium effect
            String structureHoverStyle =
                    "-fx-background-color: #1E293B;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/structure.png');" +
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

            structureCard.setStyle(structureNormalStyle);

            // Hover effects
            structureCard.setOnMouseEntered(e -> structureCard.setStyle(structureHoverStyle));
            structureCard.setOnMouseExited(e -> structureCard.setStyle(structureNormalStyle));

            // Click handler
            structureCard.setOnMouseClicked(e -> openStructureView());

            cardContainer.getChildren().add(structureCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Traversal card
        try {
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox traversalCard = loader2.load();
            GraphSubCardController controller2 = loader2.getController();
            controller2.setData("Traversal");

            // Use standard dimensions
            traversalCard.setPrefWidth(388);
            traversalCard.setPrefHeight(400);
            traversalCard.setMaxWidth(388);
            traversalCard.setMaxHeight(400);

            // Normal style
            String traversalNormalStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/traversal.png');" +
                            "-fx-background-size: 85%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.4);" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 20;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);";

            // Hover style
            String traversalHoverStyle =
                    "-fx-background-color: #1E293B;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/traversal.png');" +
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

            traversalCard.setStyle(traversalNormalStyle);

            // Hover effects
            traversalCard.setOnMouseEntered(e -> traversalCard.setStyle(traversalHoverStyle));
            traversalCard.setOnMouseExited(e -> traversalCard.setStyle(traversalNormalStyle));

            // Click handler
            traversalCard.setOnMouseClicked(e -> openTraversalView());

            cardContainer.getChildren().add(traversalCard);
            System.out.println("Traversal card loaded successfully!");
        } catch (IOException e) {
            System.out.println("Error loading Traversal card:");
            e.printStackTrace();
        }

        System.out.println("Total cards in container: " + cardContainer.getChildren().size());
    }

    @FXML
    private void backToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cardContainer.getScene().getWindow();

            // Save current window properties
            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);

            // Restore position
            stage.setX(x);
            stage.setY(y);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openStructureView() {
        System.out.println("Structure card clicked - Opening structure view");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/GraphStructure.fxml"));
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
            System.out.println("Error loading GraphStructure.fxml:");
            e.printStackTrace();
        }
    }

    private void openTraversalView() {
        System.out.println("Traversal card clicked - Opening traversal view");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/GraphTraversal.fxml"));
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
            System.out.println("Error loading GraphTraversal.fxml:");
            e.printStackTrace();
        }
    }
}