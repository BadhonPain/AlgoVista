package com.AlgoVista.graphs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphCategoryController {
    @FXML private HBox cardContainer;

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

            // Normal style
            String structureNormalStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/structure.png');" +
                            "-fx-background-size: 95%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: #FFFFFF;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-effect: drop shadow(Gaussian, RGBA(0,0,0,0.3), 10, 0, 0, 2);";

            // Hover style
            String structureHoverStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/structure.png');" +
                            "-fx-background-size: 95%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: #FFFFFF;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-scale-x: 1.05;" +
                            "-fx-scale-y: 1.05;" +
                            "-fx-effect: dropshadow(gaussian, rgba(56, 189, 248, 0.8), 15, 0, 0, 3);";

            structureCard.setStyle(structureNormalStyle);

            // Hover effects
            structureCard.setOnMouseEntered(e -> structureCard.setStyle(structureHoverStyle));
            structureCard.setOnMouseExited(e -> structureCard.setStyle(structureNormalStyle));

            // Click handler
            structureCard.setOnMouseClicked(e -> openStructureView());

            cardContainer.getChildren().add(structureCard);
            System.out.println("Structure card loaded successfully!");
        } catch (IOException e) {
            System.out.println("Error loading Structure card:");
            e.printStackTrace();
        }

        // Traversal card
        try {
            System.out.println("Loading Traversal card...");
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox traversalCard = loader2.load();
            GraphSubCardController controller2 = loader2.getController();
            controller2.setData("Traversal");

            // Normal style
            String traversalNormalStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/traversal.png');" +
                            "-fx-background-size: 95%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: #FFFFFF;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";

            // Hover style
            String traversalHoverStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/traversal.png');" +
                            "-fx-background-size: 95%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: #FFFFFF;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-scale-x: 1.05;" +
                            "-fx-scale-y: 1.05;" +
                            "-fx-effect: dropshadow(gaussian, rgba(56, 189, 248, 0.8), 15, 0, 0, 3);";

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
        System.out.println("Traversal card clicked - TODO: Open traversal view");
        // TODO: Create GraphTraversal.fxml and load it here
    }
}