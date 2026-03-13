package com.AlgoVista.dnc;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuickSortController {

    @FXML private VBox treeContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Label statusLabel;
    @FXML private Label stepLabel;
    @FXML private Label complexityLabel;
    @FXML private TextField customArrayInput;
    @FXML private ComboBox<String> pivotSelector;

    private List<Integer> data;
    private boolean sorting = false;
    private SequentialTransition masterAnimation;

    @FXML
    public void initialize() {
        pivotSelector.setItems(FXCollections.observableArrayList("FIRST", "LAST", "MIDDLE", "RANDOM"));
        pivotSelector.setValue("FIRST");
        
        // Auto-scroll to bottom as new rows are added
        treeContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0);
        });
        
        generateNewArray();
    }

    @FXML
    private void generateNewArray() {
        if (sorting) return;
        treeContainer.getChildren().clear();
        data = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 8; i++) {
            data.add(rand.nextInt(90) + 10);
        }

        renderInitialArray();
        statusLabel.setText("Click Start Quick Sort to begin!");
        stepLabel.setText("");
    }

    @FXML
    private void handleCustomArray() {
        if (sorting) return;
        String input = customArrayInput.getText();
        if (input.isEmpty()) return;

        try {
            String[] parts = input.split(",");
            List<Integer> newData = new ArrayList<>();
            for (String p : parts) {
                newData.add(Integer.parseInt(p.trim()));
            }

            data = newData;
            renderInitialArray();
            statusLabel.setText("Custom array loaded.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid format!");
        }
    }

    private void renderInitialArray() {
        treeContainer.getChildren().clear();
        HBox row = createHBoxRow(data, -1, -1, -1);
        treeContainer.getChildren().add(wrapInContainer(row, "Initial Unsorted Array"));
    }

    private StackPane createSimpleNode(int value, Color color) {
        Rectangle rect = new Rectangle(50, 50);
        rect.setFill(color);
        rect.setStroke(Color.web("#334155"));
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        Label label = new Label(String.valueOf(value));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        return new StackPane(rect, label);
    }

    private HBox createHBoxRow(List<Integer> list, int low, int high, int pivotIndex) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        for (int i = 0; i < list.size(); i++) {
            Color c;
            if (i == pivotIndex) c = Color.RED; // Pivot highlight
            else if (i >= low && i <= high) c = Color.web("#334155"); // Active range
            else c = Color.web("#0f172a"); // Dimmed/Background range
            
            row.getChildren().add(createSimpleNode(list.get(i), c));
        }
        return row;
    }
    
    private VBox wrapInContainer(HBox row, String title) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        Label label = new Label(title);
        label.setTextFill(Color.web("#94a3b8"));
        label.setStyle("-fx-font-size: 12;");
        container.getChildren().addAll(label, row);
        return container;
    }

    @FXML
    private void startSort() {
        if (sorting) return;
        sorting = true;
        treeContainer.getChildren().clear();
        renderInitialArray();
        
        detectAndShowComplexity();
        
        masterAnimation = new SequentialTransition();
        List<Integer> workingData = new ArrayList<>(data);
        
        
        runQuickSort(0, workingData.size() - 1, workingData);
        
        // Add a final task to show the complete sorted array in green
        masterAnimation.getChildren().add(createFinalSortedTask(workingData));
        
        masterAnimation.setOnFinished(e -> {
            sorting = false;
            statusLabel.setText("Array is Sorted !");
        });
        masterAnimation.play();
    }

    private void detectAndShowComplexity() {
        String strategy = pivotSelector.getValue();
        boolean sorted = true;
        for (int i = 0; i < data.size() - 1; i++) {
            if (data.get(i) > data.get(i + 1)) {
                sorted = false;
                break;
            }
        }

        complexityLabel.setVisible(true);
        if (sorted && "FIRST".equals(strategy)) {
            complexityLabel.setText("Worst Case : Time complexity O(n²)");
            complexityLabel.setStyle("-fx-background-color: rgba(231, 76, 60, 0.2); -fx-text-fill: #e74c3c; -fx-padding: 3 15 3 15; -fx-background-radius: 12; -fx-font-weight: bold;");
        } else if (isPerfectlyBalanced(data) && "MIDDLE".equals(strategy)) {
            complexityLabel.setText("Best Case : Time complexity O(n log n)");
            complexityLabel.setStyle("-fx-background-color: rgba(46, 204, 113, 0.2); -fx-text-fill: #2ecc71; -fx-padding: 3 15 3 15; -fx-background-radius: 12; -fx-font-weight: bold;");
        } else {
            complexityLabel.setText("Average Case : Time complexity O(n log n)");
            complexityLabel.setStyle("-fx-background-color: rgba(52, 152, 219, 0.2); -fx-text-fill: #3498db; -fx-padding: 3 15 3 15; -fx-background-radius: 12; -fx-font-weight: bold;");
        }
    }

    private boolean isPerfectlyBalanced(List<Integer> list) {
        // A simple heuristic for "Best Case" demonstration
        // If middle element is roughly the median, we call it best case for UI purposes
        if (list.size() < 3) return false;
        List<Integer> sorted = new ArrayList<>(list);
        Collections.sort(sorted);
        int median = sorted.get(sorted.size() / 2);
        int middleVal = list.get(list.size() / 2);
        return Math.abs(median - middleVal) < 5;
    }

    private void runQuickSort(int low, int high, List<Integer> workingData) {
        if (low <= high) {
            int pIndex = partitionVertical(low, high, workingData);
            runQuickSort(low, pIndex - 1, workingData);
            runQuickSort(pIndex + 1, high, workingData);
        }
    }

    private int partitionVertical(int low, int high, List<Integer> workingData) {
        if (low >= high) {
            if (low == high) {
                masterAnimation.getChildren().add(createFinalizeTask(low, workingData));
            }
            return low;
        }

        int pivotIndex = choosePivot(low, high);
        int pivotValue = workingData.get(pivotIndex);
        
        // Task to show current state and pivot
        masterAnimation.getChildren().add(createRowUpdateTask(workingData, low, high, pivotIndex, 
            "Range [" + low + "-" + high + "] - Pivot selected: " + pivotValue));
        
        // Partition logic
        Collections.swap(workingData, pivotIndex, high);
        int i = low;
        for (int j = low; j < high; j++) {
            if (workingData.get(j) < pivotValue) {
                Collections.swap(workingData, i, j);
                i++;
            }
        }
        Collections.swap(workingData, i, high);
        
        // Task to show result of partition
        masterAnimation.getChildren().add(createRowUpdateTask(workingData, i, i, i, 
            "Pivot " + pivotValue + " is now in its sorted position."));
        
        return i;
    }

    private int choosePivot(int low, int high) {
        String strategy = pivotSelector.getValue();
        switch (strategy) {
            case "FIRST": return low;
            case "MIDDLE": return low + (high - low) / 2;
            case "RANDOM": return low + new Random().nextInt(high - low + 1);
            default: return high; // LAST
        }
    }

    private Transition createRowUpdateTask(List<Integer> currentData, int low, int high, int pivotIndex, String msg) {
        PauseTransition pt = new PauseTransition(Duration.seconds(1.2));
        List<Integer> snapData = new ArrayList<>(currentData);
        pt.setOnFinished(e -> {
            HBox row = createHBoxRow(snapData, low, high, pivotIndex);
            treeContainer.getChildren().add(wrapInContainer(row, msg));
            statusLabel.setText("Partitioning: " + msg);
        });
        return pt;
    }

    private Transition createFinalizeTask(int index, List<Integer> currentData) {
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        List<Integer> snapData = new ArrayList<>(currentData);
        pt.setOnFinished(e -> {
            HBox row = createHBoxRow(snapData, index, index, -1);
            // Color the finalized node green for visual feedback
            StackPane node = (StackPane) row.getChildren().get(index);
            ((Rectangle)node.getChildren().get(0)).setFill(Color.web("#2ecc71"));
            
            treeContainer.getChildren().add(wrapInContainer(row, "Finalized position at index " + index));
        });
        return pt;
    }

    private Transition createFinalSortedTask(List<Integer> finalData) {
        PauseTransition pt = new PauseTransition(Duration.seconds(1.0));
        List<Integer> snapData = new ArrayList<>(finalData);
        pt.setOnFinished(e -> {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER);
            for (int val : snapData) {
                row.getChildren().add(createSimpleNode(val, Color.web("#2ecc71")));
            }
            treeContainer.getChildren().add(wrapInContainer(row, "Final Sorted Array"));
        });
        return pt;
    }

    @FXML
    private void backToCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DNC_Category.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) treeContainer.getScene().getWindow();
            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
