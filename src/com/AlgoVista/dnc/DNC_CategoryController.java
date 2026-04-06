package com.AlgoVista.dnc;

import com.AlgoVista.graphs.GraphSubCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DNC_CategoryController {
    @FXML private FlowPane cardContainer;

    @FXML
    public void initialize() {
        loadCards();
    }

    private void loadCards() {
        addAlgorithmCard("Binary Search", "#38bdf8", "binary_search_bg.png", this::openBinarySearchView);
        addAlgorithmCard("Merge Sort", "#9b59b2", "merge_sort_bg.png", this::openMergeSortView);
        addAlgorithmCard("Quick Sort", "#f39c12", "quick_sort_bg.png", this::openQuickSortView);
    }

    private void addAlgorithmCard(String name, String accentColor, String bgImage, Runnable onClick) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSubCard.fxml"));
            VBox card = loader.load();
            GraphSubCardController controller = loader.getController();
            controller.setData(name);

            String selectedBg;
            switch (name) {
                case "Binary Search": 
                    selectedBg = "/com/AlgoVista/images/binary_search.png"; 
                    break;
                case "Merge Sort": 
                    selectedBg = "/com/AlgoVista/images/merge_sort.png";
                    break;
                case "Quick Sort": 
                    selectedBg = "/com/AlgoVista/images/quick_sort.png";
                    break;
                default: 
                    selectedBg = "/com/AlgoVista/images/divide_conquer.png";
            }

            String imageUrl = getClass().getResource(selectedBg) != null ? 
                             getClass().getResource(selectedBg).toExternalForm() : "";
            
            String size = "cover";
            String pos = "center";
            
            if (name.equals("Binary Search")) {
                size = "100% 100%"; // Stretching to fit perfectly if cover was leaving gaps or cropping wrong
            }

            String normalStyle =
                    "-fx-background-image: url('" + imageUrl + "');" +
                    "-fx-background-size: " + size + ";" +
                    "-fx-background-position: " + pos + ";" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-color: rgba(255,255,255,0.2);" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 15;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 15, 0, 0, 5);";

            String hoverStyle = normalStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(gaussian, #38bdf8, 20, 0, 0, 0);";

            card.setStyle(normalStyle);
            
            // Adjust title label style for better visibility on images
            controller.setData(name);
            // Assuming card structure allows targeting the label if we want more custom styling
            
            card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
            card.setOnMouseExited(e -> card.setStyle(normalStyle));
            card.setOnMouseClicked(e -> onClick.run());

            cardContainer.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToDashboard() {
        switchScene("/fxml/dashboard.fxml");
    }

    private void openBinarySearchView() {
        switchScene("/fxml/BinarySearch.fxml");
    }

    private void openMergeSortView() {
        switchScene("/fxml/MergeSort.fxml");
    }

    private void openQuickSortView() {
        switchScene("/fxml/QuickSort.fxml");
    }

    private void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
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
