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

public class BSTTraversalController {

    @FXML private Canvas bstCanvas;
    @FXML private TextField valueInput, customBSTInput;
    @FXML private Slider speedSlider;
    @FXML private Label statusLabel;
    @FXML private HBox traversalBox;
    @FXML private HBox queueBox;
    @FXML private Spinner<Integer> sizeSpinner;

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

        // Ensure canvas redraws correctly
        bstCanvas.widthProperty().addListener(evt -> updateVisualization());
        bstCanvas.heightProperty().addListener(evt -> updateVisualization());

        updateVisualization();
        statusLabel.setText("Ready. Build a tree then select a traversal.");
    }

    @FXML
    private void insertValue() {
        try {
            int value = Integer.parseInt(valueInput.getText().trim());
            model.insert(value); // Ignore operations list, no animation for manual build here
            updateVisualization();
            valueInput.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void generateRandomBST() {
        try {
            int size = sizeSpinner.getValue();
            model.clear(); // Clear existing
            visualizer.clearColors();
            traversalBox.getChildren().clear();
            queueBox.getChildren().clear();

            java.util.Random rand = new java.util.Random();
            for (int i = 0; i < size; i++) {
                int val = rand.nextInt(100) + 1; // 1 to 100 max
                model.insert(val);
            }

            updateVisualization();
            statusLabel.setText("Random tree generated.");
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
            traversalBox.getChildren().clear();
            queueBox.getChildren().clear();

            String[] parts = input.split(",");
            for (String part : parts) {
                int val = Integer.parseInt(part.trim());
                model.insert(val); // Ignore ops for silent build
            }

            updateVisualization();
            statusLabel.setText("Custom tree generated.");
            customBSTInput.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please ensure all values are valid integers separated by commas.");
        }
    }

    @FXML
    private void inorderTraversal() {
        if (model.isEmpty()) {
            showAlert("Empty Tree", "The tree is empty.");
            return;
        }
        currentOperations = model.inorderTraversal();
        startAnimation();
    }

    @FXML
    private void preorderTraversal() {
        if (model.isEmpty()) {
            showAlert("Empty Tree", "The tree is empty.");
            return;
        }
        currentOperations = model.preorderTraversal();
        startAnimation();
    }

    @FXML
    private void postorderTraversal() {
        if (model.isEmpty()) {
            showAlert("Empty Tree", "The tree is empty.");
            return;
        }
        currentOperations = model.postorderTraversal();
        startAnimation();
    }

    @FXML
    private void levelOrderTraversal() {
        if (model.isEmpty()) {
            showAlert("Empty Tree", "The tree is empty.");
            return;
        }
        currentOperations = model.levelOrderTraversal();
        startAnimation();
    }

    @FXML
    private void clearTree() {
        model.clear();
        visualizer.clearColors();
        updateVisualization();
        traversalBox.getChildren().clear();
        queueBox.getChildren().clear();
        statusLabel.setText("Tree cleared.");
    }

    private void startAnimation() {
        if (currentOperations == null || currentOperations.isEmpty()) {
            updateVisualization();
            return;
        }

        if (animation != null) {
            animation.stop();
        }

        traversalBox.getChildren().clear();
        queueBox.getChildren().clear();

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
                visualizer.clearColors();
                updateVisualization();
                statusLabel.setText("Traversal complete.");
            }
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void executeStep(BSTModel.BSTOperation op) {
        visualizer.clearColors();

        if (op.targetNode != null) {
            visualizer.setNodeColor(op.targetNode, op.type);
        }

        statusLabel.setText(op.description);

        if (op.type.equals("traversing") && op.targetNode != null) {
            addTraversalNode(op.targetNode.value);
        }

        if (op.queueState != null) {
            updateQueueUI(op.queueState);
        } else {
            queueBox.getChildren().clear();
        }

        updateVisualization();
    }
    
    private void updateQueueUI(List<Integer> queueState) {
        queueBox.getChildren().clear();
        for (int value : queueState) {
            Label qLabel = new Label(String.valueOf(value));
            qLabel.setStyle(
                    "-fx-background-color: #e67e22;" +
                    "-fx-text-fill: white;" +
                    "-fx-border-color: #d35400;" +
                    "-fx-border-width: 2;" +
                    "-fx-background-radius: 5;" +
                    "-fx-border-radius: 5;" +
                    "-fx-min-width: 40;" +
                    "-fx-min-height: 40;" +
                    "-fx-alignment: center;" +
                    "-fx-font-size: 16;" +
                    "-fx-font-weight: bold;"
            );
            queueBox.getChildren().add(qLabel);
        }
    }
    
    private void addTraversalNode(int value) {
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.setStyle(
                "-fx-background-color: #00cec9;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #00b894;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-min-width: 40;" +
                "-fx-min-height: 40;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 16;" +
                "-fx-font-weight: bold;"
        );
        traversalBox.getChildren().add(valueLabel);
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
                statusLabel.setText("Traversal complete.");
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
