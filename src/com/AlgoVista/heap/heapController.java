package com.AlgoVista.heap;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

public class heapController {
    @FXML private Canvas heapCanvas;
    @FXML private RadioButton rbMaxHeap, rbMinHeap;
    @FXML private TextField valueInput, indexInput, newValueInput;
    @FXML private Spinner<Integer> sizeSpinner, minSpinner, maxSpinner;
    @FXML private Slider speedSlider;
    @FXML private Button playButton, pauseButton, stepButton;
    @FXML private Label statusLabel, complexityLabel;
    @FXML private HBox arrayBox;
    @FXML private Label sortedResultLabel;

    private heapModel heapModel;
    private heapVisualizer visualizer;
    private ToggleGroup heapTypeGroup;

    private Timeline animation;
    private List<heapModel.HeapOperation> currentOperations;
    private int currentStep;

    @FXML
    public void initialize() {
        // Initialize heap type
        heapTypeGroup = new ToggleGroup();
        rbMaxHeap.setToggleGroup(heapTypeGroup);
        rbMinHeap.setToggleGroup(heapTypeGroup);
        rbMaxHeap.setSelected(true);

        heapModel = new heapModel(true);
        visualizer = new heapVisualizer(heapCanvas);

        // Initialize spinners
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 10));
        minSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));
        maxSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 99));

        // Heap type listener
        heapTypeGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                heapModel.setHeapType(rbMaxHeap.isSelected());
                if (!heapModel.isEmpty()) {
                    rebuildCurrentHeap();
                }
            }
        });

        updateVisualization();
        statusLabel.setText("Ready. Select an operation.");
    }

    private void rebuildCurrentHeap() {
        List<Integer> values = heapModel.getHeap();
        if (!values.isEmpty()) {
            currentOperations = heapModel.buildHeapOptimal(values);
            currentStep = 0;
            animateOperations();
        }
    }

    @FXML
    private void insertValue() {
        try {
            int value = Integer.parseInt(valueInput.getText().trim());
            currentOperations = heapModel.insert(value);
            currentStep = 0;
            animateOperations();
            valueInput.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void extractRoot() {
        if (heapModel.isEmpty()) {
            showAlert("Empty Heap", "Cannot extract from empty heap.");
            return;
        }

        currentOperations = heapModel.extract();
        currentStep = 0;
        animateOperations();
    }

    @FXML
    private void buildHeapNlogN() {
        try {
            int size = sizeSpinner.getValue();
            int min = minSpinner.getValue();
            int max = maxSpinner.getValue();

            if (min > max) {
                showAlert("Invalid Range", "Min must be ≤ Max");
                return;
            }

            Random rand = new Random();
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                values.add(rand.nextInt(max - min + 1) + min);
            }

            currentOperations = heapModel.buildHeapNaive(values);
            currentStep = 0;
            animateOperations();

        } catch (Exception e) {
            showAlert("Error", "Failed to generate heap.");
        }
    }

    @FXML
    private void buildHeapN() {
        try {
            int size = sizeSpinner.getValue();
            int min = minSpinner.getValue();
            int max = maxSpinner.getValue();

            if (min > max) {
                showAlert("Invalid Range", "Min must be ≤ Max");
                return;
            }

            Random rand = new Random();
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                values.add(rand.nextInt(max - min + 1) + min);
            }

            currentOperations = heapModel.buildHeapOptimal(values);
            currentStep = 0;
            animateOperations();

        } catch (Exception e) {
            showAlert("Error", "Failed to generate heap.");
        }
    }

    @FXML
    private void updateKey() {
        try {
            int index = Integer.parseInt(indexInput.getText().trim());
            int newValue = Integer.parseInt(newValueInput.getText().trim());

            currentOperations = heapModel.updateKey(index, newValue);
            currentStep = 0;
            animateOperations();

            indexInput.clear();
            newValueInput.clear();

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers for index and value.");
        }
    }

    @FXML
    private void deleteByIndex() {
        try {
            int index = Integer.parseInt(indexInput.getText().trim());

            currentOperations = heapModel.deleteByIndex(index);
            currentStep = 0;
            animateOperations();

            indexInput.clear();

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid index.");
        }
    }


    @FXML
    private void heapSort() {
        if (heapModel.isEmpty()) {
            showAlert("Empty Heap", "Cannot sort empty heap.");
            return;
        }

        currentOperations = heapModel.heapSort();
        currentStep = 0;
        animateOperations();
    }

    @FXML
    private void clearHeap() {
        heapModel.clear();
        visualizer.clearColors();
        updateVisualization();
        statusLabel.setText("Heap cleared.");
        complexityLabel.setText("");
    }

    private void animateOperations() {
        if (currentOperations == null || currentOperations.isEmpty()) {
            updateVisualization();
            return;
        }

        if (animation != null) {
            animation.stop();
        }

        double speed = speedSlider.getValue();
        Duration duration = Duration.millis(1000 / speed);

        animation = new Timeline(new KeyFrame(duration, e -> {
            if (currentStep < currentOperations.size()) {
                heapModel.HeapOperation op = currentOperations.get(currentStep);
                executeStep(op);
                currentStep++;
            } else {
                animation.stop();
                visualizer.resetColors(heapModel);
                updateVisualization();
                statusLabel.setText("Animation complete.");
            }
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void executeStep(heapModel.HeapOperation op) {
        visualizer.clearColors();

        switch (op.type) {
            case "insert":
                visualizer.setNodeColor(op.index1, "inserted");
                break;
            case "extract":
                if (op.index1 >= 0 && op.index1 < heapModel.size()) {
                    visualizer.setNodeColor(op.index1, "extracted");
                }
                break;
            case "compare":
                if (op.index1 >= 0 && op.index1 < heapModel.size()) {
                    visualizer.setNodeColor(op.index1, "comparing");
                }
                if (op.index2 >= 0 && op.index2 < heapModel.size()) {
                    visualizer.setNodeColor(op.index2, "comparing");
                }
                break;
            case "swap":
                if (op.index1 >= 0 && op.index1 < heapModel.size()) {
                    visualizer.setNodeColor(op.index1, "swapping");
                }
                if (op.index2 >= 0 && op.index2 < heapModel.size()) {
                    visualizer.setNodeColor(op.index2, "swapping");
                }
                break;
            case "update":
            case "delete":
                if (op.index1 >= 0 && op.index1 < heapModel.size()) {
                    visualizer.setNodeColor(op.index1, "swapping");
                }
                break;
            case "complete":
                // Show sorted result if it's HeapSort completion
                if (op.description.contains("Sorted")) {
                    statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 13;");
                }
                break;
        }

        statusLabel.setText(op.description);
        if (!op.complexity.isEmpty()) {
            complexityLabel.setText("Complexity: " + op.complexity);
        }

        updateVisualization();
    }

    @FXML
    private void playAnimation() {
        if (animation != null && animation.getStatus() == Timeline.Status.PAUSED) {
            animation.play();
        }
    }

    @FXML
    private void pauseAnimation() {
        if (animation != null) {
            animation.pause();
        }
    }

    @FXML
    private void nextStep() {
        if (currentOperations != null && currentStep < currentOperations.size()) {
            heapModel.HeapOperation op = currentOperations.get(currentStep);
            executeStep(op);
            currentStep++;

            if (currentStep >= currentOperations.size()) {
                visualizer.resetColors(heapModel);
                updateVisualization();
                statusLabel.setText("Animation complete.");
            }
        }
    }

    private void updateVisualization() {
        visualizer.drawHeap(heapModel);
        updateArrayView();
    }

    private void updateArrayView() {
        arrayBox.getChildren().clear();

        List<Integer> heap = heapModel.getHeap();

        if (heap.isEmpty()) {
            Label emptyLabel = new Label("No elements in heap");
            emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 12; -fx-padding: 10;");
            arrayBox.getChildren().add(emptyLabel);
            return;
        }

        for (int i = 0; i < heap.size(); i++) {
            VBox cellBox = new VBox(0);
            cellBox.setStyle("-fx-alignment: center;");

            // Value box
            Label valueLabel = getLabel(heap, i);

            // Index box (below value)
            Label indexLabel = new Label(String.valueOf(i));
            indexLabel.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: #2c3e50;" +
                            "-fx-border-width: 0 2 2 2;" +
                            "-fx-min-width: 45;" +
                            "-fx-max-width: 45;" +
                            "-fx-min-height: 20;" +
                            "-fx-alignment: center;" +
                            "-fx-text-fill: red;" +
                            "-fx-font-size: 11;" +
                            "-fx-font-weight: bold;"
            );

            cellBox.getChildren().addAll(valueLabel, indexLabel);
            arrayBox.getChildren().add(cellBox);
        }
    }

    private static Label getLabel(List<Integer> heap, int i) {
        Label valueLabel = new Label(String.valueOf(heap.get(i)));
        valueLabel.setStyle(
                "-fx-background-color: #87CEEB;" +
                        "-fx-border-color: #2c3e50;" +
                        "-fx-border-width: 2;" +
                        "-fx-min-width: 45;" +
                        "-fx-max-width: 45;" +
                        "-fx-min-height: 45;" +
                        "-fx-max-height: 45;" +
                        "-fx-alignment: center;" +
                        "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: black;"
        );
        return valueLabel;
    }

    @FXML
    private void backToDashboard() {
        try {
            if (animation != null) {
                animation.stop();
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) heapCanvas.getScene().getWindow();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}