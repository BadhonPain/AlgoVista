package com.AlgoVista.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DashboardController {
    @FXML private TilePane algoContainer;
    @FXML private TextField searchField;

    private final List<String> algorithms = Arrays.asList(
            "Array", "Linked List", "Stack", "Queue", "Graph",
            "BST", "Heap", "Sorting", "DP", "Advanced"
    );

    @FXML
    public void initialize() {
        loadCards(""); // Load all cards initially

        // Add listener for the Search Bar
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadCards(newValue.toLowerCase());
        });
    }

    private void loadCards(String filter) {
        algoContainer.getChildren().clear();
        for (String name : algorithms) {
            if (name.toLowerCase().contains(filter)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AlgoCard.fxml"));
                    VBox card = loader.load();
                    CardController controller = loader.getController();
                    controller.setData(name);

                    // Add CSS class based on algorithm name
                    String styleClass = getStyleClass(name);
                    card.getStyleClass().add(styleClass);

                    algoContainer.getChildren().add(card);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Helper method to map algorithm names to CSS classes
    private String getStyleClass(String algoName) {
        switch (algoName.toLowerCase()) {
            case "array": return "array-card";
            case "linked list": return "linked-list-card";
            case "stack": return "stack-card";
            case "queue": return "queue-card";
            case "graph": return "graph-card";
            case "bst": return "bst-card";
            case "heap": return "heap-card";
            case "sorting": return "sorting-card";
            case "dp": return "dp-card";
            case "advanced": return "advanced-card";
            default: return "";
        }
    }
}