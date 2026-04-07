package com.AlgoVista.dnc;

import javafx.animation.FillTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.AlgoVista.utils.ShortcutManager;
import javafx.animation.Animation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BinarySearchController {

    @FXML private HBox arrayContainer;
    @FXML private TextField targetInput;
    @FXML private TextField customArrayInput;
    @FXML private Label statusLabel;
    @FXML private Label stepLabel;
    @FXML private javafx.scene.control.Slider speedSlider;
    @FXML private Label speedLabel;

    private double animationSpeed = 1.0;

    private List<Integer> data;
    private List<StackPane> nodes;
    private int low, high, mid;
    private int searchSteps = 0;
    private boolean searching = false;
    private PauseTransition currentStepAnimation;

    @FXML
    public void initialize() {
        if (speedSlider != null) {
            double initSpeed = com.AlgoVista.utils.SettingsManager.getSpeed();
            speedSlider.setValue(initSpeed);
            animationSpeed = com.AlgoVista.utils.SettingsManager.getTimelineRate(initSpeed);
            animationSpeed = com.AlgoVista.utils.SettingsManager.getTimelineRate(initSpeed);
            if (speedLabel != null) { speedLabel.setText(String.format("%.2fx", initSpeed)); }
        }
        if (speedSlider != null) {
            speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                animationSpeed = com.AlgoVista.utils.SettingsManager.getTimelineRate(newVal.doubleValue());
                if (speedLabel != null) {
                    speedLabel.setText(String.format("%.2fx", newVal.doubleValue()));
                }
            });
        }
        generateNewArray();

        arrayContainer.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ShortcutManager.register(newVal,
                    this::playPauseToggle,
                    null, // Step forward isn't granular here
                    this::generateNewArray,
                    this::backToCategory
                );
            }
        });
    }

    private void playPauseToggle() {
        if (currentStepAnimation != null) {
            if (currentStepAnimation.getStatus() == Animation.Status.RUNNING) {
                currentStepAnimation.pause();
            } else {
                currentStepAnimation.play();
            }
        } else if (!searching) {
            startSearch();
        }
    }

    @FXML
    private void generateNewArray() {
        if (searching) return;
        
        arrayContainer.getChildren().clear();
        data = new ArrayList<>();
        nodes = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 0; i < 10; i++) {
            data.add(rand.nextInt(100));
        }
        Collections.sort(data);

        for (int i = 0; i < data.size(); i++) {
            StackPane node = createNode(data.get(i));
            nodes.add(node);
            arrayContainer.getChildren().add(node);
        }
        
        statusLabel.setText("Ready to search.");
        stepLabel.setText("Random array generated and sorted.");
        resetHighlights();
    }

    @FXML
    private void handleCustomArray() {
        if (searching) return;
        String input = customArrayInput.getText();
        if (input.isEmpty()) {
            statusLabel.setText("Enter values first!");
            return;
        }

        try {
            String[] parts = input.split(",");
            List<Integer> newData = new ArrayList<>();
            for (String p : parts) {
                newData.add(Integer.parseInt(p.trim()));
            }
            Collections.sort(newData);
            
            if (newData.isEmpty()) return;

            data = newData;
            arrayContainer.getChildren().clear();
            nodes.clear();
            for (int val : data) {
                StackPane node = createNode(val);
                nodes.add(node);
                arrayContainer.getChildren().add(node);
            }
            statusLabel.setText("Custom array created!");
            stepLabel.setText("Data sorted for binary search.");
            resetHighlights();
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid format! Use: 1, 2, 3");
        }
    }

    private StackPane createNode(int value) {
        Rectangle rect = new Rectangle(55, 55);
        rect.setFill(Color.web("#1e293b"));
        rect.setStroke(Color.web("#334155"));
        rect.setStrokeWidth(2.5);
        rect.setArcWidth(15);
        rect.setArcHeight(15);

        Label label = new Label(String.valueOf(value));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        StackPane pane = new StackPane(rect, label);
        pane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);");
        return pane;
    }

    @FXML
    private void startSearch() {
        if (searching) return;
        
        String targetStr = targetInput.getText();
        if (targetStr.isEmpty()) {
            statusLabel.setText("Please enter a target!");
            return;
        }

        try {
            int target = Integer.parseInt(targetStr);
            searching = true;
            searchSteps = 0;
            statusLabel.setText("Searching for " + target + "...");
            resetHighlights();
            animateSearch(target);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid target!");
        }
    }

    private void animateSearch(int target) {
        low = 0;
        high = data.size() - 1;
        performStep(target);
    }

    private void performStep(int target) {
        searchSteps++;
        if (low > high) {
            statusLabel.setText("NOT FOUND");
            statusLabel.setTextFill(Color.web("#ef4444"));
            stepLabel.setText("The target " + target + " is not in the array.");
            searching = false;
            return;
        }

        mid = low + (high - low) / 2;
        
        // Visual feedback for search range
        for (int i = 0; i < nodes.size(); i++) {
            Rectangle rect = (Rectangle) nodes.get(i).getChildren().get(0);
            if (i >= low && i <= high) {
                rect.setStroke(Color.web("#38bdf8"));
                rect.setStrokeWidth(3);
                rect.setOpacity(1.0);
            } else {
                rect.setStroke(Color.web("#334155"));
                rect.setStrokeWidth(1);
                rect.setOpacity(0.2);
            }
        }

        // Highlight mid
        Rectangle midRect = (Rectangle) nodes.get(mid).getChildren().get(0);
        midRect.setFill(Color.web("#f39c12")); // Orange for mid
        midRect.setStroke(Color.WHITE);
        
        stepLabel.setText("Checking index " + mid + " (value: " + data.get(mid) + ")");

        currentStepAnimation = new PauseTransition(Duration.seconds(1.2 / animationSpeed));
        currentStepAnimation.setOnFinished(e -> {
            int midVal = data.get(mid);
            if (midVal == target) {
                midRect.setFill(Color.web("#10b981")); // Emerald green
                statusLabel.setText("TARGET FOUND!");
                statusLabel.setTextFill(Color.web("#10b981"));
                stepLabel.setText("Found " + target + " at index " + mid);
                searching = false;
                
                ScaleTransition st = new ScaleTransition(Duration.millis(300 / animationSpeed), nodes.get(mid));
                st.setByX(0.3);
                st.setByY(0.3);
                st.setAutoReverse(true);
                st.setCycleCount(2);
                st.play();                
                
            } else {
                midRect.setFill(Color.web("#1e293b")); // Revert mid color
                midRect.setStroke(Color.web("#334155"));
                if (target < midVal) {
                    statusLabel.setText(target + " < " + midVal + " | Searching LEFT");
                    high = mid - 1;
                } else {
                    statusLabel.setText(target + " > " + midVal + " | Searching RIGHT");
                    low = mid + 1;
                }
                PauseTransition nextPause = new PauseTransition(Duration.seconds(0.8 / animationSpeed));
                nextPause.setOnFinished(ev -> performStep(target));
                nextPause.play();
            }
        });
        currentStepAnimation.play();
    }

    @FXML
    private void resetSearch() {
        if (searching) return;
        resetHighlights();
        statusLabel.setText("Ready to search.");
        statusLabel.setTextFill(Color.web("#94a3b8"));
        stepLabel.setText("Search logic reset.");
    }

    private void resetHighlights() {
        for (StackPane node : nodes) {
            Rectangle rect = (Rectangle) node.getChildren().get(0);
            rect.setFill(Color.web("#1e293b"));
            rect.setStroke(Color.web("#334155"));
            rect.setOpacity(1.0);
        }
    }

    @FXML
    private void backToCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DNC_Category.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) arrayContainer.getScene().getWindow();
            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
