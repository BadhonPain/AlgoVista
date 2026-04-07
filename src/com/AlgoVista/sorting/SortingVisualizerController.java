package com.AlgoVista.sorting;

import com.AlgoVista.sorting.algorithms.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SortingVisualizerController {

    @FXML private Label algoTitleLabel;
    @FXML private Label complexityLabel;
    @FXML private Button playPauseBtn;
    @FXML private HBox arrayContainer;
    @FXML private VBox codeContainer;
    @FXML private Label statusLabel;
    @FXML private TextField customArrayInput;
    @FXML private Slider speedSlider;

    private String currentAlgorithmType;
    private SortingAlgorithm algorithm;
    private List<StateSnapshot> snapshots;
    private int currentFrame = 0;
    private Timeline timeline;
    private boolean isPlaying = false;

    @FXML
    public void initialize() {
        if (speedSlider != null) {
            speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (timeline != null) {
                    timeline.setRate(newVal.doubleValue());
                }
            });
        }
    }

    public void initAlgorithm(String algoType) {
        this.currentAlgorithmType = algoType;
        algoTitleLabel.setText(algoType);
        
        switch (algoType) {
            case "Selection Sort": algorithm = new SelectionSortAlgorithm(); break;
            case "Insertion Sort": algorithm = new InsertionSortAlgorithm(); break;
            case "Counting Sort": algorithm = new CountingSortAlgorithm(); break;
            case "Radix Sort": algorithm = new RadixSortAlgorithm(); break;
            case "Bucket Sort": algorithm = new BucketSortAlgorithm(); break;
            case "Bubble Sort":
            default: algorithm = new BubbleSortAlgorithm(); break;
        }

        complexityLabel.setText("Time Complexity: " + algorithm.getTimeComplexity());
        setupTimeline();
        generateArray();
    }

    private void setupTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(600), e -> stepForward(null)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        if (speedSlider != null) {
            timeline.setRate(speedSlider.getValue());
        }
    }

    @FXML
    private void setCustomArray(ActionEvent event) {
        try {
            String[] parts = customArrayInput.getText().split(",");
            int[] arr = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                arr[i] = Integer.parseInt(parts[i].trim());
            }
            if (arr.length > 0) {
                snapshots = algorithm.generateSnapshots(arr);
                currentFrame = 0;
                if (isPlaying) playPause(null);
                renderFrame();
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid array input! Use comma-separated numbers.");
        }
    }

    @FXML
    private void generateArray() {
        Random rand = new Random();
        int[] initialArray = new int[8];
        for (int i = 0; i < initialArray.length; i++) {
            initialArray[i] = rand.nextInt(90) + 10;
        }
        
        snapshots = algorithm.generateSnapshots(initialArray);
        currentFrame = 0;
        
        if (isPlaying) playPause(null);
        renderFrame();
    }

    @FXML
    private void playPause(ActionEvent event) {
        if (isPlaying) {
            timeline.pause();
            playPauseBtn.setText("▶");
            isPlaying = false;
        } else {
            if (currentFrame >= snapshots.size() - 1) currentFrame = 0;
            timeline.play();
            playPauseBtn.setText("||");
            isPlaying = true;
        }
    }

    @FXML
    private void stepForward(ActionEvent event) {
        if (currentFrame < snapshots.size() - 1) {
            currentFrame++;
            renderFrame();
        } else if (isPlaying) playPause(null);
    }

    @FXML
    private void stepBackward(ActionEvent event) {
        if (currentFrame > 0) {
            currentFrame--;
            renderFrame();
        }
    }

    @FXML
    private void reset(ActionEvent event) {
        if (isPlaying) playPause(null);
        currentFrame = 0;
        renderFrame();
    }

    @FXML
    private void backToMenu(ActionEvent event) {
        if (isPlaying) timeline.stop();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SortingCategory.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void renderFrame() {
        if (snapshots == null || snapshots.isEmpty()) return;
        StateSnapshot snapshot = snapshots.get(currentFrame);
        renderArray(snapshot);
        renderCode(snapshot);
        statusLabel.setText(snapshot.getStatusMessage());
    }

    private void renderArray(StateSnapshot snapshot) {
        arrayContainer.getChildren().clear();
        int[] arr = snapshot.getArray();
        int[] active = snapshot.getActiveIndices();
        int[] sorted = snapshot.getSortedIndices();

        int maxVal = 100;
        for (int maxSearch : arr) {
            if (maxSearch > maxVal) maxVal = maxSearch;
        }

        for (int i = 0; i < arr.length; i++) {
            VBox barWrapper = new VBox(5);
            barWrapper.setAlignment(Pos.BOTTOM_CENTER);

            javafx.scene.layout.StackPane barStack = new javafx.scene.layout.StackPane();
            barStack.setAlignment(Pos.BOTTOM_CENTER);

            VBox bar = new VBox();
            bar.getStyleClass().add("array-bar");
            bar.setPrefWidth(40);
            
            double height = Math.max(25, ((double) arr[i] / maxVal) * 200);
            bar.setPrefHeight(height);
            bar.setStyle("-fx-background-color: #3498db; -fx-background-radius: 4 4 0 0;"); // Default Color

            boolean isActive = false;
            for(int a : active) if(a == i) isActive = true;
            boolean isSorted = false;
            for(int s : sorted) if(s == i) isSorted = true;

            if (isActive) {
                bar.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 4 4 0 0;");
            } else if (isSorted) {
                bar.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 4 4 0 0;");
            }

            Label valLabel = new Label(String.valueOf(arr[i]));
            valLabel.getStyleClass().add("array-bar-value");
            valLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5;");
            
            barStack.getChildren().addAll(bar, valLabel);

            Label indexLabel = new Label(String.valueOf(i));
            indexLabel.getStyleClass().add("array-bar-index");
            indexLabel.setStyle("-fx-text-fill: #94a3b8;");

            barWrapper.getChildren().addAll(barStack, indexLabel);
            arrayContainer.getChildren().add(barWrapper);
        }
    }

    private void renderCode(StateSnapshot snapshot) {
        codeContainer.getChildren().clear();
        String[] codeLines = algorithm.getCodeSnippet("C++"); // Force single generic pseudo code snippet
        int activeLine = snapshot.getActiveCodeLine();

        for (int i = 0; i < codeLines.length; i++) {
            HBox lineHBox = new HBox();
            lineHBox.getStyleClass().add("code-line");
            if (i == activeLine) lineHBox.getStyleClass().add("active");

            Label lineNum = new Label(String.valueOf(i + 1));
            lineNum.getStyleClass().add("code-line-number");

            Label codeText = new Label(codeLines[i].replace(" ", "  "));
            codeText.getStyleClass().add("code-line-text");

            lineHBox.getChildren().addAll(lineNum, codeText);
            codeContainer.getChildren().add(lineHBox);
        }
    }

}
