package com.AlgoVista.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class CardController {
    @FXML private Label algoName;
    @FXML private ImageView algoIcon;

    public void setData(String name) {
        algoName.setText(name);
        
        // Load image based on algorithm name
        String imgPath = getImagePath(name);
        if (imgPath != null) {
            try {
                Image img = new Image(getClass().getResourceAsStream(imgPath));
                algoIcon.setImage(img);
            } catch (Exception e) {
                // Silently skip missing images
            }
        }
    }

    private String getImagePath(String name) {
        switch (name.toLowerCase()) {
            case "array": return "/com/AlgoVista/images/array.png";
            case "linked list": return "/com/AlgoVista/images/linkedList.png";
            case "stack": return "/com/AlgoVista/images/stack.png";
            case "queue": return "/com/AlgoVista/images/queue.png";
            case "sorting": return "/com/AlgoVista/images/sorting.png";
            case "graph": return "/com/AlgoVista/images/graph.png";
            case "bst": return "/com/AlgoVista/images/BST.png";
            case "recursion": return "/com/AlgoVista/images/recursion.png";
            case "heap": return "/com/AlgoVista/images/heap.png";
            case "d & c": return "/com/AlgoVista/images/dnc.png";
            case "dp": return "/com/AlgoVista/images/dp.png";
            default: return null;
        }
    }

    @FXML
    private void handleCardClick() {
        String algo = algoName.getText();
        try {
            String fxmlPath = null;

            if (algo.equals("Graph")) fxmlPath = "/fxml/GraphCategory.fxml";
            else if (algo.equals("Heap")) fxmlPath = "/fxml/Heap.fxml";
            else if (algo.equals("BST")) fxmlPath = "/fxml/BSTCategory.fxml";
            else if (algo.equals("Recursion")) fxmlPath = "/fxml/RecursionCategory.fxml";
            else if (algo.equals("D & C")) fxmlPath = "/fxml/DNC_Category.fxml";
            else if (algo.equals("DP")) fxmlPath = "/fxml/DP_Category.fxml";


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
            } else if (algo.equals("Queue")) {
                fxmlPath = "/fxml/QueueVisualizer.fxml";
            } else if (algo.equals("Sorting")) {
                fxmlPath = "/fxml/SortingCategory.fxml";
            }


            if (fxmlPath != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Stage stage = (Stage) algoName.getScene().getWindow();
                Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
                stage.setScene(scene);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}