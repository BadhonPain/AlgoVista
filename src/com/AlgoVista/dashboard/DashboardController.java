package com.AlgoVista.dashboard;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DashboardController {

    @FXML private StackPane rootPane;
    @FXML private TilePane algoContainer;
    @FXML private TextField searchField;
    @FXML private StackPane overlayPane;
    @FXML private VBox modalContent;

    private final List<String> algorithms = Arrays.asList(
            "Array", "Linked List", "Stack", "Queue", "Sorting", "Graph",
            "BST", "Recursion", "Heap", "D & C", "DP");

    @FXML
    public void initialize() {
        loadCards(""); // Load all cards initially
        
        // Ensure overlay is hidden but interactive setup
        overlayPane.setOnMouseClicked(e -> closeOverlay());
        modalContent.setOnMouseClicked(e -> e.consume()); // Prevent closing when clicking inside modal
    }

    @FXML
    private void onSearch() {
        loadCards(searchField.getText().toLowerCase());
    }

    @FXML
    private void onSettings() {
        showOverlay("Settings", "Customization options coming soon in the next logic modules...");
    }

    @FXML
    private void onAbout() {
        showOverlay("About AlgoVista", 
            "AlgoVista is a next-generation algorithm visualizer designed for professional clarity.\n\n" +
            "Version: 2.1.0 (Pro)\n" +
            "Lead UI/UX: Lead Engineer Role\n" +
            "Build: 2026.03.27");
    }

    private void showOverlay(String title, String content) {
        modalContent.getChildren().clear();
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #38bdf8; -fx-padding: 0 0 15 0;");
        
        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #94a3b8; -fx-wrap-text: true; -fx-text-alignment: center;");
        contentLabel.setWrapText(true);
        
        VBox spacer = new VBox();
        spacer.setPrefHeight(30);
        
        Label closeBtn = new Label("CLOSE");
        closeBtn.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b; -fx-cursor: hand;");
        closeBtn.setOnMouseClicked(e -> closeOverlay());

        modalContent.getChildren().addAll(titleLabel, contentLabel, spacer, closeBtn);
        
        overlayPane.setMouseTransparent(false);
        FadeTransition ft = new FadeTransition(Duration.millis(300), overlayPane);
        ft.setToValue(1.0);
        ft.play();
    }

    private void closeOverlay() {
        FadeTransition ft = new FadeTransition(Duration.millis(300), overlayPane);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> overlayPane.setMouseTransparent(true));
        ft.play();
    }

    private void loadCards(String filter) {
        algoContainer.getChildren().clear();
        for (String name : algorithms) {
            String fullName = getFullName(name);
            if (name.toLowerCase().contains(filter) || fullName.toLowerCase().contains(filter)) {
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

    private String getFullName(String algoName) {
        switch (algoName.toLowerCase()) {
            case "bst": return "Binary Search Tree";
            case "d & c": return "Divide and Conquer";
            case "dp": return "Dynamic Programming";
            default: return algoName;
        }
    }

    private String getStyleClass(String algoName) {
        switch (algoName.toLowerCase()) {
            case "d & c": return "divide-conquer-card";
            case "recursion": return "recursion-card";
            case "array": return "array-card";
            case "linked list": return "linked-list-card";
            case "stack": return "stack-card";
            case "queue": return "queue-card";
            case "graph": return "graph-card";
            case "bst": return "bst-card";
            case "heap": return "heap-card";
            case "sorting": return "sorting-card";
            case "dp": return "dp-card";
            default: return "";
        }
    }
}