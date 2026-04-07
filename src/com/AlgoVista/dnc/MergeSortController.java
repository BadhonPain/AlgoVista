package com.AlgoVista.dnc;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.AlgoVista.utils.ShortcutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Controller for Merge Sort Visualization.
 * Implements downward arrows for splitting and step-by-step merging.
 */
public class MergeSortController {

    @FXML private VBox treeContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Label statusLabel;
    @FXML private TextField customArrayInput;
    @FXML private javafx.scene.control.Slider speedSlider;
    @FXML private Label speedLabel;

    // Complexity Boxes
    @FXML private VBox bestCaseBox;
    @FXML private VBox worstCaseBox;
    @FXML private VBox spaceCaseBox;

    private double animationSpeed = 1.0;

    private List<Integer> initialData;
    private boolean sorting = false;
    private SequentialTransition currentAnimation;

    @FXML
    public void initialize() {
        if (speedSlider != null) {
            double initSpeed = com.AlgoVista.utils.SettingsManager.getSpeed();
            speedSlider.setValue(initSpeed);
            animationSpeed = com.AlgoVista.utils.SettingsManager.getTimelineRate(initSpeed);
            animationSpeed = com.AlgoVista.utils.SettingsManager.getTimelineRate(initSpeed);
            if (speedLabel != null) { speedLabel.setText(String.format("%.2fx", initSpeed)); }
        }
        // Auto-scroll to bottom when new layers are added
        treeContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0);
        });

        if (speedSlider != null) {
            speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                animationSpeed = com.AlgoVista.utils.SettingsManager.getTimelineRate(newVal.doubleValue());
                if (speedLabel != null) {
                    speedLabel.setText(String.format("%.2fx", newVal.doubleValue()));
                }
            });
        }
        
        generateNewArray();

        treeContainer.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ShortcutManager.register(newVal,
                    this::playPauseToggle,
                    null,
                    this::generateNewArray,
                    this::backToCategory
                );
            }
        });
    }

    private void highlightComplexity(VBox activeBox) {
        if (activeBox != null) {
            activeBox.setStyle("-fx-background-color: rgba(245, 158, 11, 0.15); " +
                             "-fx-border-color: #f59e0b; " +
                             "-fx-border-width: 1.5; " +
                             "-fx-border-radius: 5; " +
                             "-fx-background-radius: 5;");
        }
    }

    private void clearComplexityHighlights() {
        String baseStyle = "-fx-padding: 2 5; -fx-background-radius: 5;";
        if (bestCaseBox != null) bestCaseBox.setStyle(baseStyle);
        if (worstCaseBox != null) worstCaseBox.setStyle(baseStyle);
        if (spaceCaseBox != null) spaceCaseBox.setStyle(baseStyle);
    }

    private void playPauseToggle() {
        if (currentAnimation != null) {
            if (currentAnimation.getStatus() == Animation.Status.RUNNING) {
                currentAnimation.pause();
            } else {
                currentAnimation.play();
            }
        } else if (!sorting) {
            startSort();
        }
    }

    @FXML
    private void generateNewArray() {
        if (sorting) return;
        treeContainer.getChildren().clear();
        clearComplexityHighlights();
        initialData = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 8; i++) {
            initialData.add(rand.nextInt(90) + 10);
        }
        
        displayInitialLayer(initialData);
        statusLabel.setText("Click Start Merge Sort to see the tree splitting!");
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
            
            if (newData.size() > 8) {
                newData = newData.subList(0, 8);
                statusLabel.setText("Limited to first 8 elements.");
            }
            
            initialData = newData;
            treeContainer.getChildren().clear();
            displayInitialLayer(initialData);
            statusLabel.setText("Custom array loaded.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid format!");
        }
    }

    private void displayInitialLayer(List<Integer> data) {
        HBox layer = new HBox(createArrayBox(data, Color.web("#1e293b")));
        layer.setAlignment(Pos.CENTER);
        treeContainer.getChildren().add(layer);
    }

    private HBox createArrayBox(List<Integer> data, Color color) {
        HBox hbox = new HBox(5);
        hbox.setAlignment(Pos.CENTER);
        hbox.setStyle("-fx-padding: 10; -fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 5;");
        for (Integer val : data) {
            hbox.getChildren().add(createNode(val, color));
        }
        return hbox;
    }

    private StackPane createNode(int value, Color color) {
        Rectangle rect = new Rectangle(35, 35);
        rect.setFill(color);
        rect.setStroke(Color.web("#334155"));
        rect.setArcWidth(5);
        rect.setArcHeight(5);

        Label label = new Label(String.valueOf(value));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        return new StackPane(rect, label);
    }

    @FXML
    private void startSort() {
        if (sorting) return;
        sorting = true;
        
        // Merge sort is always O(n log n), highlight both Time boxes
        highlightComplexity(bestCaseBox);
        highlightComplexity(worstCaseBox);
        
        treeContainer.getChildren().clear();
        animateMergeSort(new ArrayList<>(initialData));
    }

    private void animateMergeSort(List<Integer> data) {
        statusLabel.setText("Phase 1: Splitting the array recursively...");
        
        // 1. Root
        HBox rootLayer = new HBox(createArrayBox(data, Color.web("#38bdf8")));
        rootLayer.setAlignment(Pos.CENTER);
        treeContainer.getChildren().add(rootLayer);

        // Sequence of steps
        SequentialTransition masterSeq = new SequentialTransition();

        // SPLITTING STEPS
        // Level 1: children center-to-center = 140 + 60 = 200
        // Level 2: children center-to-center = 70 + 30 = 100. Parent center-to-center = 200.
        // Level 3: children center-to-center = 35 + 15 = 50. Parent center-to-center = 100.
        
        masterSeq.getChildren().add(createStepTask(() -> {
            HBox arrowLayer = new HBox(createTreeBranch(200)); 
            arrowLayer.setAlignment(Pos.CENTER);
            HBox layer = new HBox(60, 
                createArrayBox(data.subList(0, 4), Color.web("#38bdf8")), 
                createArrayBox(data.subList(4, 8), Color.web("#38bdf8"))
            );
            layer.setAlignment(Pos.CENTER);
            treeContainer.getChildren().addAll(arrowLayer, layer);
            return List.of(arrowLayer, layer);
        }, 1.0));

        masterSeq.getChildren().add(createStepTask(() -> {
            // Level 2 Forks: Width 100. Group width = 120. Spacing = 200 - 120 = 80
            HBox arrowLayer = new HBox(80, createTreeBranch(100), createTreeBranch(100)); 
            arrowLayer.setAlignment(Pos.CENTER);
            List<Integer> l1 = data.subList(0, 4);
            List<Integer> r1 = data.subList(4, 8);
            HBox layer = new HBox(30, 
                createArrayBox(l1.subList(0, 2), Color.web("#38bdf8")), 
                createArrayBox(l1.subList(2, 4), Color.web("#38bdf8")),
                createArrayBox(r1.subList(0, 2), Color.web("#38bdf8")),
                createArrayBox(r1.subList(2, 4), Color.web("#38bdf8"))
            );
            layer.setAlignment(Pos.CENTER);
            treeContainer.getChildren().addAll(arrowLayer, layer);
            return List.of(arrowLayer, layer);
        }, 1.0));

        masterSeq.getChildren().add(createStepTask(() -> {
            // Level 3 Forks: Width 50. Group width = 70. Spacing = 100 - 70 = 30
            HBox arrowLayer = new HBox(30, createTreeBranch(50), createTreeBranch(50), createTreeBranch(50), createTreeBranch(50));
            arrowLayer.setAlignment(Pos.CENTER);
            HBox layer = new HBox(15);
            layer.setAlignment(Pos.CENTER);
            for (int v : data) layer.getChildren().add(createNode(v, Color.web("#38bdf8")));
            treeContainer.getChildren().addAll(arrowLayer, layer);
            return List.of(arrowLayer, layer);
        }, 1.0));

        // MERGING STEPS
        masterSeq.getChildren().add(createStepTask(() -> {
            statusLabel.setText("Phase 2: Merging pairs...");
            List<Integer> l1 = data.subList(0, 4);
            List<Integer> r1 = data.subList(4, 8);
            HBox layer = new HBox(30, 
                createArrayBox(sort(l1.subList(0, 2)), Color.web("#fbbf24")), 
                createArrayBox(sort(l1.subList(2, 4)), Color.web("#fbbf24")),
                createArrayBox(sort(r1.subList(0, 2)), Color.web("#fbbf24")),
                createArrayBox(sort(r1.subList(2, 4)), Color.web("#fbbf24"))
            );
            layer.setAlignment(Pos.CENTER);
            treeContainer.getChildren().add(layer);
            return List.of(layer);
        }, 1.5));

        masterSeq.getChildren().add(createStepTask(() -> {
            statusLabel.setText("Merging halves...");
            HBox layer = new HBox(50, 
                createArrayBox(sort(data.subList(0, 4)), Color.web("#fbbf24")), 
                createArrayBox(sort(data.subList(4, 8)), Color.web("#fbbf24"))
            );
            layer.setAlignment(Pos.CENTER);
            treeContainer.getChildren().add(layer);
            return List.of(layer);
        }, 1.5));

        masterSeq.getChildren().add(createStepTask(() -> {
            statusLabel.setText("Final merge complete!");
            HBox finalLayer = new HBox(createArrayBox(sort(data), Color.web("#2ecc71")));
            finalLayer.setAlignment(Pos.CENTER);
            treeContainer.getChildren().add(finalLayer);
            return List.of(finalLayer);
        }, 1.5));

        currentAnimation = masterSeq;
        currentAnimation.setOnFinished(e -> {
            statusLabel.setText("Array is Sorted !");
            sorting = false;
            currentAnimation = null;
        });
        currentAnimation.play();
    }

    private PauseTransition createStepTask(java.util.function.Supplier<List<Node>> action, double pauseSeconds) {
        PauseTransition pt = new PauseTransition(Duration.seconds(pauseSeconds / animationSpeed));
        pt.setOnFinished(e -> {
            List<Node> nodes = action.get();
            for (Node n : nodes) {
                n.setOpacity(0);
                FadeTransition ft = new FadeTransition(Duration.seconds(0.8 / animationSpeed), n);
                ft.setToValue(1.0);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.8 / animationSpeed), n);
                tt.setFromY(-20); tt.setToY(0);
                ft.play(); tt.play();
            }
        });
        return pt;
    }

    private Group createTreeBranch(double width) {
        Group group = new Group();
        double half = width / 2;
        
        Line leftLine = new Line(0, 0, -half, 30);
        Line rightLine = new Line(0, 0, half, 30);
        leftLine.setStroke(Color.web("#94a3b8"));
        rightLine.setStroke(Color.web("#94a3b8"));
        leftLine.setStrokeWidth(4);
        rightLine.setStrokeWidth(4);
        
        // Larger arrows at the bottom
        Polygon leftArrow = new Polygon(-half-10, 30, -half+10, 30, -half, 45);
        Polygon rightArrow = new Polygon(half-10, 30, half+10, 30, half, 45);
        leftArrow.setFill(Color.web("#94a3b8"));
        rightArrow.setFill(Color.web("#94a3b8"));
        
        group.getChildren().addAll(leftLine, rightLine, leftArrow, rightArrow);
        return group;
    }

    private List<Integer> sort(List<Integer> data) {
        List<Integer> s = new ArrayList<>(data);
        java.util.Collections.sort(s);
        return s;
    }

    @FXML
    private void backToCategory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/DNC_Category.fxml"));
            Stage stage = (Stage) treeContainer.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
