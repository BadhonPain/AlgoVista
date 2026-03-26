package com.AlgoVista.bst;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class BSTOperationsController {

    @FXML private Canvas bstCanvas;
    @FXML private TextField valueInput, customBSTInput, oldValueInput, newValueInput;
    @FXML private Slider speedSlider;
    @FXML private Label statusLabel, complexityLabel, speedLabel;
    @FXML private Spinner<Integer> sizeSpinner, minSpinner, maxSpinner;

    private BSTModel model;
    private BSTVisualizer visualizer;

    private Timeline animation;
    private List<BSTModel.BSTOperation> currentOperations;
    private int currentStep;

    @FXML
    public void initialize() {
        model = new BSTModel();
        visualizer = new BSTVisualizer(bstCanvas);
        
        // Initialize spinners
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 10));
        minSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        maxSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 100));

        // Ensure canvas redraws correctly
        bstCanvas.widthProperty().addListener(evt -> updateVisualization());
        bstCanvas.heightProperty().addListener(evt -> updateVisualization());

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (speedLabel != null) {
                speedLabel.setText(String.format("%.1fx", newVal.doubleValue()));
            }
        });

        updateVisualization();
        statusLabel.setText("Ready. Enter a value to insert.");
    }

    @FXML
    private void insertValue() {
        try {
            int value = Integer.parseInt(valueInput.getText().trim());
            // Capture tree state BEFORE insert so canvas shows it during animation
            BSTNode snapshot = model.deepCopyRoot();
            currentOperations = model.insert(value);
            model.setTransientRoot(snapshot); // animation draws from pre-insert tree
            startAnimation();
            valueInput.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void findValue() {
        try {
            int value = Integer.parseInt(valueInput.getText().trim());
            // Find doesn't modify tree; snapshot is same as real tree, but set for consistency
            BSTNode snapshot = model.deepCopyRoot();
            currentOperations = model.find(value);
            model.setTransientRoot(snapshot);
            startAnimation();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void deleteValue() {
        try {
            int value = Integer.parseInt(valueInput.getText().trim());
            // Capture tree BEFORE delete — so we see the node being highlighted before disappearing
            BSTNode snapshot = model.deepCopyRoot();
            currentOperations = model.delete(value);
            model.setTransientRoot(snapshot);
            startAnimation();
            valueInput.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void generateRandomBST() {
        try {
            int size = sizeSpinner.getValue();
            int min = minSpinner.getValue();
            int max = maxSpinner.getValue();

            if (min > max) {
                showAlert("Invalid Range", "Min must be ≤ Max");
                return;
            }

            model.clear(); // Clear existing
            visualizer.clearColors();

            java.util.Random rand = new java.util.Random();
            currentOperations = new java.util.ArrayList<>();
            for (int i = 0; i < size; i++) {
                int val = rand.nextInt(max - min + 1) + min;
                currentOperations.addAll(model.insert(val));
            }

            startAnimation();
        } catch (Exception e) {
            showAlert("Error", "Failed to generate BST.");
        }
    }

    @FXML
    private void buildCustomBST() {
        try {
            String input = customBSTInput.getText().trim();
            if (input.isEmpty()) {
                showAlert("Empty Input", "Please enter a comma-separated list of values.");
                return;
            }

            model.clear();
            visualizer.clearColors();
            currentOperations = new java.util.ArrayList<>();

            String[] parts = input.split(",");
            for (String part : parts) {
                int val = Integer.parseInt(part.trim());
                currentOperations.addAll(model.insert(val));
            }

            startAnimation();
            customBSTInput.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please ensure all values are valid integers separated by commas.");
        }
    }

    @FXML
    private void updateValue() {
        try {
            int oldVal = Integer.parseInt(oldValueInput.getText().trim());
            int newVal = Integer.parseInt(newValueInput.getText().trim());
            BSTNode snapshot = model.deepCopyRoot();
            currentOperations = model.update(oldVal, newVal);
            model.setTransientRoot(snapshot);
            startAnimation();
            oldValueInput.clear();
            newValueInput.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers for both old and new values.");
        }
    }

    @FXML
    private void clearTree() {
        model.clear();
        visualizer.clearColors();
        updateVisualization();
        statusLabel.setText("Tree cleared.");
        complexityLabel.setText("");
        statusLabel.setText("Steps reset.");
    }

    private void startAnimation() {
        if (currentOperations == null || currentOperations.isEmpty()) {
            model.clearTransientRoot();
            visualizer.clearColors();
            updateVisualization();
            return;
        }

        if (animation != null) {
            animation.stop();
        }

        currentStep = 0;
        double speed = speedSlider.getValue();
        Duration duration = Duration.millis(1200 / speed);

        animation = new Timeline(new KeyFrame(duration, e -> {
            if (currentStep < currentOperations.size()) {
                BSTModel.BSTOperation op = currentOperations.get(currentStep);
                executeStep(op);
                currentStep++;
            } else {
                animation.stop();
                // Clear snapshot — final real tree is now shown
                model.clearTransientRoot();
                visualizer.clearColors();
                updateVisualization();
                statusLabel.setText("Animation complete.");
            }
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void executeStep(BSTModel.BSTOperation op) {
        if (op.targetNode != null) {
            visualizer.setNodeColor(op.targetNode, op.type);
        }

        statusLabel.setText(op.description);

        // Only update complexity badge for operation-level steps, not per-step sub-steps
        if (!op.complexity.contains("per step")) {
            complexityLabel.setText("Complexity: " + op.complexity);
            complexityLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; " +
                    "-fx-background-color: rgba(245,158,11,0.15); -fx-padding: 6 14; -fx-background-radius: 20;");
        }

        // On "inserted" step: switch canvas to real tree so new node appears
        if ("inserted".equals(op.type)) {
            model.clearTransientRoot();
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
            if (animation != null && animation.getStatus() == Timeline.Status.RUNNING) {
                animation.pause();
            }
            BSTModel.BSTOperation op = currentOperations.get(currentStep);
            executeStep(op);
            currentStep++;

            if (currentStep >= currentOperations.size()) {
                visualizer.clearColors();
                updateVisualization();
                statusLabel.setText("Animation complete.");
            }
        }
    }

    private void updateVisualization() {
        visualizer.drawTree(model);
    }

    @FXML
    private void backToCategory() {
        try {
            if (animation != null) {
                animation.stop();
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/BSTCategory.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) bstCanvas.getScene().getWindow();
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
