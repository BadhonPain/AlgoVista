package com.AlgoVista.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class CardController {
    @FXML
    private Label algoName;

    public void setData(String name) {
        algoName.setText(name);
    }

    @FXML
    private void handleCardClick() {
        String algo = algoName.getText();

        try {
            String fxmlPath = null;

            if (algo.equals("Graph")) {
                fxmlPath = "/fxml/GraphCategory.fxml";
            } else if (algo.equals("Heap")) {
                fxmlPath = "/fxml/Heap.fxml";
            } else if (algo.equals("BST")) {
                fxmlPath = "/fxml/BSTCategory.fxml";
            } else if (algo.equals("Recursion")) {
                fxmlPath = "/fxml/RecursionCategory.fxml";
            } else if (algo.equals("D & C")) {
                fxmlPath = "/fxml/DNC_Category.fxml";
            } else if (algo.equals("Linked List")) {
                fxmlPath = "/fxml/LinkedListMenu.fxml";
            } else if (algo.equals("Array")) {
                fxmlPath = "/fxml/ArrayVisualizer.fxml";
            } else if (algo.equals("Stack")) {
                fxmlPath = "/fxml/StackVisualizer.fxml";
            }                                         // ← this } was missing!

            if (fxmlPath != null) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                Stage stage = (Stage) algoName.getScene().getWindow();
                double width = stage.getWidth();
                double height = stage.getHeight();
                double x = stage.getX();
                double y = stage.getY();

                Scene scene = new Scene(root, width, height);
                stage.setScene(scene);
                stage.setX(x);
                stage.setY(y);
            } else {
                System.out.println(algo + " clicked - Not implemented yet");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}