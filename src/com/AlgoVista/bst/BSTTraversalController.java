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
import com.AlgoVista.utils.ShortcutManager;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.List;

public class BSTTraversalController {

    @FXML private Canvas bstCanvas;
    @FXML private TextField valueInput, customBSTInput;
    @FXML private Slider speedSlider;
    @FXML private Label statusLabel, traversalTypeLabel, speedLabel;
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
        if (speedSlider != null) {
            double initSpeed = com.AlgoVista.utils.SettingsManager.getSpeed();
            speedSlider.setValue(initSpeed);
            
            if (speedLabel != null) { speedLabel.setText(String.format("%.2fx", initSpeed)); }
        }
        model = new BSTModel();
        visualizer = new BSTVisualizer(bstCanvas);
        
        // Initialize spinners
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 15));

        // Ensure canvas redraws correctly
        bstCanvas.widthProperty().addListener(evt -> updateVisualization());
        bstCanvas.heightProperty().addListener(evt -> updateVisualization());

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (speedLabel != null) {
                speedLabel.setText(String.format("%.2fx", newVal.doubleValue()));
            }
        });

        updateVisualization();
        statusLabel.setText("Ready. Build a tree then select a traversal.");
        traversalTypeLabel.setText("All Traversals — O(n)");
        traversalTypeLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #94a3b8; " +
                "-fx-background-color: rgba(148,163,184,0.12); -fx-padding: 6 14; -fx-background-radius: 20;");

        // Register Keyboard Shortcuts
        bstCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ShortcutManager.register(newScene, 
                    this::playPauseToggle, 
                    this::nextStep, 
                    this::clearTree, 
                    this::backToCategory
                );
            }
        });
    }

    private void playPauseToggle() {
        if (animation == null) return;
        if (animation.getStatus() == Timeline.Status.RUNNING) {
            pauseAnimation();
        } else {
            playAnimation();
        }
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
        if (model.isEmpty()) { showAlert("Empty Tree", "The tree is empty."); return; }
        traversalTypeLabel.setText("In-order  |  O(n)");
        traversalTypeLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #93c5fd; " +
                "-fx-background-color: rgba(29,78,216,0.2); -fx-padding: 6 14; -fx-background-radius: 20;");
        currentOperations = model.inorderTraversal();
        startAnimation();
    }

    @FXML
    private void preorderTraversal() {
        if (model.isEmpty()) { showAlert("Empty Tree", "The tree is empty."); return; }
        traversalTypeLabel.setText("Pre-order  |  O(n)");
        traversalTypeLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #d8b4fe; " +
                "-fx-background-color: rgba(147,51,234,0.2); -fx-padding: 6 14; -fx-background-radius: 20;");
        currentOperations = model.preorderTraversal();
        startAnimation();
    }

    @FXML
    private void postorderTraversal() {
        if (model.isEmpty()) { showAlert("Empty Tree", "The tree is empty."); return; }
        traversalTypeLabel.setText("Post-order  |  O(n)");
        traversalTypeLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #fcd34d; " +
                "-fx-background-color: rgba(180,83,9,0.2); -fx-padding: 6 14; -fx-background-radius: 20;");
        currentOperations = model.postorderTraversal();
        startAnimation();
    }

    @FXML
    private void levelOrderTraversal() {
        if (model.isEmpty()) { showAlert("Empty Tree", "The tree is empty."); return; }
        traversalTypeLabel.setText("Level-order (BFS)  |  O(n)");
        traversalTypeLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #5eead4; " +
                "-fx-background-color: rgba(15,118,110,0.25); -fx-padding: 6 14; -fx-background-radius: 20;");
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
        double speed = com.AlgoVista.utils.SettingsManager.getTimelineRate(speedSlider.getValue());
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
        for (int i = 0; i < queueState.size(); i++) {
            int value = queueState.get(i);
            Label front = new Label(i == 0 ? "Front" : "");
            Label qLabel = new Label(String.valueOf(value));
            qLabel.setStyle(
                    "-fx-background-color: #134e4a;" +
                    "-fx-text-fill: #5eead4;" +
                    "-fx-border-color: #0f766e;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-radius: 8;" +
                    "-fx-min-width: 42;" +
                    "-fx-min-height: 42;" +
                    "-fx-pref-width: 42;" +
                    "-fx-pref-height: 42;" +
                    "-fx-alignment: center;" +
                    "-fx-font-size: 14;" +
                    "-fx-font-weight: bold;"
            );
            qLabel.setMinWidth(Region.USE_PREF_SIZE);
            queueBox.getChildren().add(qLabel);
            if (i < queueState.size() - 1) {
                Label arrow = new Label("›");
                arrow.setStyle("-fx-text-fill: #334155; -fx-font-size: 18;");
                queueBox.getChildren().add(arrow);
            }
        }
    }
    
    private void addTraversalNode(int value) {
        int index = traversalBox.getChildren().size();
        // Alternating position indicator
        if (index > 0) {
            Label arrow = new Label("→");
            arrow.setStyle("-fx-text-fill: #475569; -fx-font-size: 14;");
            traversalBox.getChildren().add(arrow);
        }
        Label chip = new Label(String.valueOf(value));
        chip.setStyle(
                "-fx-background-color: #0c4a6e;" +
                "-fx-text-fill: #38bdf8;" +
                "-fx-border-color: #0369a1;" +
                "-fx-border-width: 1.5;" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-min-width: 40;" +
                "-fx-min-height: 40;" +
                "-fx-pref-width: 40;" +
                "-fx-pref-height: 40;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 14;" +
                "-fx-font-weight: bold;"
        );
        chip.setMinWidth(Region.USE_PREF_SIZE);
        traversalBox.getChildren().add(chip);
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

        private void showAlert(String title, String message) {
        if (title != null && (title.toLowerCase().contains("complete") || title.toLowerCase().contains("ready") || title.toLowerCase().contains("custom mode"))) {
            com.AlgoVista.utils.CustomAlert.showInfo(title, message);
        } else {
            com.AlgoVista.utils.CustomAlert.showError(title, message);
        }
    }
}
