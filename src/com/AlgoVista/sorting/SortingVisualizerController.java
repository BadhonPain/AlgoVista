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
    @FXML private VBox variablesContainer;
    @FXML private Label statusLabel;
    @FXML private TextField customArrayInput;
    @FXML private Slider speedSlider;

    private String currentAlgorithmType;
    private SortingAlgorithm algorithm;
    private List<StateSnapshot> snapshots;
    private int currentFrame = 0;
    private Timeline timeline;
    private boolean isPlaying = false;
    private String currentLanguage = "C++";

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
            case "Merge Sort": algorithm = new MergeSortAlgorithm(); break;
            case "Quick Sort": algorithm = new QuickSortAlgorithm(); break;
            case "Heap Sort": algorithm = new HeapSortAlgorithm(); break;
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
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void setLanguageCpp(MouseEvent event) {
        currentLanguage = "C++";
        updateLanguageLabels((Node)event.getSource());
        renderFrame();
    }

    @FXML
    private void setLanguageJava(MouseEvent event) {
        currentLanguage = "Java";
        updateLanguageLabels((Node)event.getSource());
        renderFrame();
    }

    private void updateLanguageLabels(Node activeNode) {
        HBox parent = (HBox) activeNode.getParent();
        for (Node child : parent.getChildren()) child.getStyleClass().remove("active");
        activeNode.getStyleClass().add("active");
    }

    private void renderFrame() {
        if (snapshots == null || snapshots.isEmpty()) return;
        StateSnapshot snapshot = snapshots.get(currentFrame);
        renderArray(snapshot);
        renderCode(snapshot);
        renderVariables(snapshot);
        statusLabel.setText(snapshot.getStatusMessage());
    }

    private void renderArray(StateSnapshot snapshot) {
        arrayContainer.getChildren().clear();
        int[] arr = snapshot.getArray();
        int[] active = snapshot.getActiveIndices();
        int[] sorted = snapshot.getSortedIndices();

        int maxVal = 100;
        for (int i = 0; i < arr.length; i++) {
            VBox barWrapper = new VBox(5);
            barWrapper.setAlignment(Pos.BOTTOM_CENTER);

            VBox bar = new VBox();
            bar.getStyleClass().add("array-bar");
            bar.setPrefWidth(40);
            
            double height = Math.max(20, ((double) arr[i] / maxVal) * 200);
            bar.setPrefHeight(height);

            Label valLabel = new Label(String.valueOf(arr[i]));
            valLabel.getStyleClass().add("array-bar-value");
            bar.getChildren().add(valLabel);

            boolean isActive = false;
            for(int a : active) if(a == i) isActive = true;
            boolean isSorted = false;
            for(int s : sorted) if(s == i) isSorted = true;

            if (isActive) bar.getStyleClass().add("active");
            else if (isSorted) bar.getStyleClass().add("sorted");

            Label indexLabel = new Label(String.valueOf(i));
            indexLabel.getStyleClass().add("array-bar-index");

            barWrapper.getChildren().addAll(bar, indexLabel);
            arrayContainer.getChildren().add(barWrapper);
        }
    }

    private void renderCode(StateSnapshot snapshot) {
        codeContainer.getChildren().clear();
        String[] codeLines = algorithm.getCodeSnippet(currentLanguage);
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

    private void renderVariables(StateSnapshot snapshot) {
        variablesContainer.getChildren().clear();
        Map<String, String> vars = snapshot.getVariables();

        for (Map.Entry<String, String> entry : vars.entrySet()) {
            HBox row = new HBox();
            row.getStyleClass().add("variable-row");
            
            Label nameLabel = new Label(entry.getKey());
            nameLabel.getStyleClass().add("variable-name");
            
            Label valueLabel = new Label(entry.getValue());
            valueLabel.getStyleClass().add("variable-value");

            HBox spacer = new HBox();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            row.getChildren().addAll(nameLabel, spacer, valueLabel);
            variablesContainer.getChildren().add(row);
        }
    }
}
