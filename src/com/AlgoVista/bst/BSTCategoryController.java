package com.AlgoVista.bst;

import com.AlgoVista.graphs.GraphSubCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class BSTCategoryController {
    @FXML private FlowPane cardContainer;

    @FXML
    public void initialize() {
        loadCards();
    }

    private void loadCards() {
        // Operations card
        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox opsCard = loader1.load();
            GraphSubCardController controller1 = loader1.getController();
            controller1.setData("Operations");

            String opsNormalStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/bst_operations_bg.png');" +
                            "-fx-background-size: 95%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: #FFFFFF;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";

            String opsHoverStyle = opsNormalStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(243, 156, 18, 0.8), 15, 0, 0, 3);";

            opsCard.setStyle(opsNormalStyle);
            opsCard.setOnMouseEntered(e -> opsCard.setStyle(opsHoverStyle));
            opsCard.setOnMouseExited(e -> opsCard.setStyle(opsNormalStyle));
            opsCard.setOnMouseClicked(e -> openOperationsView());
            
            cardContainer.getChildren().add(opsCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Traversal card
        try {
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox traversalCard = loader2.load();
            GraphSubCardController controller2 = loader2.getController();
            controller2.setData("Traversals");

            String traversalNormalStyle =
                    "-fx-background-color: #1A1A1A;" +
                            "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/bst_traversals_bg.png');" +
                            "-fx-background-size: 95%;" +
                            "-fx-background-position: center 25%;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: #FFFFFF;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";

            String traversalHoverStyle = traversalNormalStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(155, 89, 182, 0.8), 15, 0, 0, 3);";

            traversalCard.setStyle(traversalNormalStyle);
            traversalCard.setOnMouseEntered(e -> traversalCard.setStyle(traversalHoverStyle));
            traversalCard.setOnMouseExited(e -> traversalCard.setStyle(traversalNormalStyle));
            traversalCard.setOnMouseClicked(e -> openTraversalView());

            cardContainer.getChildren().add(traversalCard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cardContainer.getScene().getWindow();
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
        }
    }

    private void openOperationsView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/BSTOperations.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cardContainer.getScene().getWindow();
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
        }
    }

    private void openTraversalView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/BSTTraversal.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cardContainer.getScene().getWindow();
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
        }
    }
}
