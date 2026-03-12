package com.AlgoVista.recursion;

import com.AlgoVista.graphs.GraphSubCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class RecursionCategoryController {
    @FXML
    private FlowPane cardContainer;

    @FXML
    public void initialize() {
        loadCards();
    }

    private void loadCards() {
        // Call Stack card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox callStackCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("Call Stack");

            String cardNormalStyle = "-fx-background-color: #1A1A1A;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/call_stack_bg.png');"
                    +
                    "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #FFFFFF;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(56, 189, 248, 0.8), 15, 0, 0, 3);";

            callStackCard.setStyle(cardNormalStyle);
            callStackCard.setOnMouseEntered(e -> callStackCard.setStyle(cardHoverStyle));
            callStackCard.setOnMouseExited(e -> callStackCard.setStyle(cardNormalStyle));
            callStackCard.setOnMouseClicked(e -> openCallStackView());

            cardContainer.getChildren().add(callStackCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Recursion Stack card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox recursionStackCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("Call Stack with Recursion");

            String cardNormalStyle = "-fx-background-color: #1A1A1A;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/factorial_recursion_bg.png');"
                    +
                    "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #FFFFFF;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(56, 189, 248, 0.8), 15, 0, 0, 3);";

            recursionStackCard.setStyle(cardNormalStyle);
            recursionStackCard.setOnMouseEntered(e -> recursionStackCard.setStyle(cardHoverStyle));
            recursionStackCard.setOnMouseExited(e -> recursionStackCard.setStyle(cardNormalStyle));
            recursionStackCard.setOnMouseClicked(e -> openRecursionStackView());

            cardContainer.getChildren().add(recursionStackCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Infinite Recursion card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox infiniteCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("Infinite Recursion");

            String cardNormalStyle = "-fx-background-color: #1A1A1A;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/infinite_recursion_bg.png');"
                    +
                    "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #FFFFFF;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.8), 15, 0, 0, 3);";

            infiniteCard.setStyle(cardNormalStyle);
            infiniteCard.setOnMouseEntered(e -> infiniteCard.setStyle(cardHoverStyle));
            infiniteCard.setOnMouseExited(e -> infiniteCard.setStyle(cardNormalStyle));
            infiniteCard.setOnMouseClicked(e -> openInfiniteRecursionView());

            cardContainer.getChildren().add(infiniteCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tail Recursion card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox tailCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("Tail Recursion");

            String cardNormalStyle = "-fx-background-color: #1A1A1A;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/tail_recursion_bg.png');"
                    +
                    "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #FFFFFF;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.8), 15, 0, 0, 3);";

            tailCard.setStyle(cardNormalStyle);
            tailCard.setOnMouseEntered(e -> tailCard.setStyle(cardHoverStyle));
            tailCard.setOnMouseExited(e -> tailCard.setStyle(cardNormalStyle));
            tailCard.setOnMouseClicked(e -> openTailRecursionView());

            cardContainer.getChildren().add(tailCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Graph Algorithms card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox graphCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("Graph Algorithms");

            String cardNormalStyle = "-fx-background-color: #1A1A1A;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/graph_algorithms.png');"
                    + "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #FFFFFF;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(56, 189, 248, 0.8), 15, 0, 0, 3);";

            graphCard.setStyle(cardNormalStyle);
            graphCard.setOnMouseEntered(e -> graphCard.setStyle(cardHoverStyle));
            graphCard.setOnMouseExited(e -> graphCard.setStyle(cardNormalStyle));
            graphCard.setOnMouseClicked(e -> openGraphAlgorithmsView());

            cardContainer.getChildren().add(graphCard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tree Traversals card
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox treesCard = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData("Tree Traversals");

            String cardNormalStyle = "-fx-background-color: #1A1A1A;" +
                    "-fx-background-image: url('file:///E:/JavaFx%20Project/Java_Fx/resources/com/AlgoVista/images/tree_traversals.png');"
                    + "-fx-background-size: 95%;" +
                    "-fx-background-position: center 25%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #FFFFFF;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);";

            String cardHoverStyle = cardNormalStyle
                    + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, rgba(56, 189, 248, 0.8), 15, 0, 0, 3);";

            treesCard.setStyle(cardNormalStyle);
            treesCard.setOnMouseEntered(e -> treesCard.setStyle(cardHoverStyle));
            treesCard.setOnMouseExited(e -> treesCard.setStyle(cardNormalStyle));
            treesCard.setOnMouseClicked(e -> openTreeTraversalsView());

            cardContainer.getChildren().add(treesCard);
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

    private void openCallStackView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CallStack.fxml"));
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

    private void openRecursionStackView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/RecursionStack.fxml"));
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

    private void openInfiniteRecursionView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/InfiniteRecursion.fxml"));
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

    private void openTailRecursionView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/TailRecursion.fxml"));
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

    private void openGraphAlgorithmsView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/GraphAlgorithmsCategory.fxml"));
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

    private void openTreeTraversalsView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/TreeTraversal.fxml"));
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
