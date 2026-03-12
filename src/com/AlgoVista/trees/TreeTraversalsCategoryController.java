package com.AlgoVista.trees;

import com.AlgoVista.graphs.GraphSubCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class TreeTraversalsCategoryController {
    @FXML
    private FlowPane cardContainer;

    @FXML
    public void initialize() {
        loadCards();
    }

    private void loadCards() {
        // Pre Order Card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox preOrderCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("Pre Order");

            String cardNormalStyle = "-fx-background-color: transparent;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/pre_order.png');" +
                    "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-color: #8b5cf6;" +
                    "-fx-border-width: 4;" +
                    "-fx-border-radius: 15;" +
                    "-fx-effect: dropshadow(gaussian, rgba(139, 92, 246, 0.4), 15, 0, 0, 5);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(139, 92, 246, 0.8), 25, 0, 0, 8);";

            preOrderCard.setStyle(cardNormalStyle);
            preOrderCard.setOnMouseEntered(e -> preOrderCard.setStyle(cardHoverStyle));
            preOrderCard.setOnMouseExited(e -> preOrderCard.setStyle(cardNormalStyle));
            preOrderCard.setOnMouseClicked(e -> {
                System.out.println("Pre Order Clicked!");
            });

            cardContainer.getChildren().add(preOrderCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // In Order Card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox inOrderCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("In Order");

            String cardNormalStyle = "-fx-background-color: transparent;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/in_order.png');" +
                    "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-color: #10b981;" +
                    "-fx-border-width: 4;" +
                    "-fx-border-radius: 15;" +
                    "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.4), 15, 0, 0, 5);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.8), 25, 0, 0, 8);";

            inOrderCard.setStyle(cardNormalStyle);
            inOrderCard.setOnMouseEntered(e -> inOrderCard.setStyle(cardHoverStyle));
            inOrderCard.setOnMouseExited(e -> inOrderCard.setStyle(cardNormalStyle));
            inOrderCard.setOnMouseClicked(e -> {
                System.out.println("In Order Clicked!");
            });

            cardContainer.getChildren().add(inOrderCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Post Order Card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox postOrderCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("Post Order");

            String cardNormalStyle = "-fx-background-color: transparent;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/post_order.png');" +
                    "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-color: #ec4899;" +
                    "-fx-border-width: 4;" +
                    "-fx-border-radius: 15;" +
                    "-fx-effect: dropshadow(gaussian, rgba(236, 72, 153, 0.4), 15, 0, 0, 5);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(236, 72, 153, 0.8), 25, 0, 0, 8);";

            postOrderCard.setStyle(cardNormalStyle);
            postOrderCard.setOnMouseEntered(e -> postOrderCard.setStyle(cardHoverStyle));
            postOrderCard.setOnMouseExited(e -> postOrderCard.setStyle(cardNormalStyle));
            postOrderCard.setOnMouseClicked(e -> {
                System.out.println("Post Order Clicked!");
            });

            cardContainer.getChildren().add(postOrderCard);
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
