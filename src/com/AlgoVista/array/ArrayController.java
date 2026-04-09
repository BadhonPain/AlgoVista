package com.AlgoVista.array;

import com.AlgoVista.utils.ShortcutManager;
import javafx.application.Platform;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayController {

    @FXML private Spinner<Integer> sizeSpinner;
    @FXML private TextField valueInput;
    @FXML private TextField indexInput;
    @FXML private FlowPane arrayContainer;
    @FXML private Label outputLabel;
    @FXML private VBox stepLogContainer;
    
    @FXML private Slider speedSlider;
    @FXML private Button playPauseBtn;
    @FXML private Label dynamicComplexityLabel;

    private ArrayModel model;
    
    // Animation Engine state
    private List<ArraySnapshot> snapshots = new ArrayList<>();
    private int currentStep = 0;
    private boolean isPlaying = false;
    private Thread animationThread;
    private final Object playLock = new Object();

    @FXML
    public void initialize() {
        model = new ArrayModel(10);
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 5));
        
        if (dynamicComplexityLabel != null) {
            dynamicComplexityLabel.setText("Time Complexity: System Ready");
        }
        
        arrayContainer.getChildren().clear(); // Keep the canvas explicitly blank at the beginning
        logMessage("Array initialized. Operations are ready.");

        // Register shortcuts when scene is available
        arrayContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ShortcutManager.register(newScene, 
                    () -> togglePlayPause(), 
                    () -> stepForward(), 
                    () -> resetAnimation(), 
                    () -> backToMenu()
                );
            }
        });
    }

    private void setComplexity(String comp) {
        Platform.runLater(() -> {
            if (dynamicComplexityLabel != null) {
                dynamicComplexityLabel.setText("Time Complexity: " + comp);
            }
        });
    }

    private Integer getInputValue() {
        try {
            return Integer.parseInt(valueInput.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for the value.");
            return null;
        }
    }

    private Integer getInputIndex() {
        try {
            return Integer.parseInt(indexInput.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for the index.");
            return null;
        }
    }

    @FXML
    private void createArray() {
        if (isPlaying) resetAnimation();
        int size = sizeSpinner.getValue();
        model = new ArrayModel(size); 
        clearSnapshots();
        updateVisualization();
        logMessage("Created empty array with capacity " + size + ".");
    }

    @FXML
    private void generateRandom() {
        if (isPlaying) resetAnimation();
        int size = sizeSpinner.getValue();
        model = new ArrayModel(size);   // reinitialise with new capacity
        model.generateSample(size, 1, 99);
        clearSnapshots();
        updateVisualization();
        logMessage("Generated random array of size " + size + ".");
    }

    @FXML
    private void insertAtEnd() {
        if (isPlaying) resetAnimation();
        Integer val = getInputValue();
        if (val == null) return;
        
        clearSnapshots();
        setComplexity("O(1)*"); // amortized
        
        boolean success = model.insertAtEnd(val);
        if (success) {
            Map<Integer, String> h = new HashMap<>();
            h.put(model.getSize() - 1, "#2ecc71"); // green
            recordSnapshot(model.getElements(), model.getCapacity(), h, "Inserted " + val + " at end (index " + (model.getSize()-1) + ")", "O(1)");
            startAnimation();
            valueInput.clear();
        } else {
            showAlert("Insert Failed", "Array constraint reached or full.");
        }
    }

    @FXML
    private void insertAtIndex() {
        if (isPlaying) resetAnimation();
        Integer val = getInputValue();
        Integer idx = getInputIndex();
        if (val == null || idx == null) return;
        
        if (idx < 0 || idx > model.getSize()) {
            showAlert("Invalid Index", "Index out of bounds");
            return;
        }

        clearSnapshots();
        setComplexity("O(n)");
        
        Integer[] currentElements = model.getElements();
        
        // Visualize shifting visually later, for now we just show insertion
        boolean success = model.insertAtIndex(idx, val);
        if (success) {
            Map<Integer, String> h = new HashMap<>();
            h.put(idx, "#2ecc71");
            recordSnapshot(model.getElements(), model.getCapacity(), h, "Inserted " + val + " at index " + idx, "O(n)");
            startAnimation();
            valueInput.clear();
            indexInput.clear();
        }
    }

    @FXML
    private void updateValue() {
        if (isPlaying) resetAnimation();
        Integer val = getInputValue();
        Integer idx = getInputIndex();
        if (val == null || idx == null) return;

        if (idx < 0 || idx >= model.getSize()) {
             showAlert("Invalid Index", "Index out of bounds");
             return;
        }
        
        clearSnapshots();
        setComplexity("O(1)");
        
        model.updateValue(idx, val);
        
        Map<Integer, String> h = new HashMap<>();
        h.put(idx, "#3498db"); // blue
        recordSnapshot(model.getElements(), model.getCapacity(), h, "Updated index " + idx + " to " + val, "O(1)");
        
        startAnimation();
        valueInput.clear();
        indexInput.clear();
    }

    @FXML
    private void deleteAtIndex() {
        if (isPlaying) resetAnimation();
        Integer idx = getInputIndex();
        if (idx == null || idx < 0 || idx >= model.getSize()) {
             if (idx != null) showAlert("Invalid", "Index out of bounds");
             return;
        }

        clearSnapshots();
        setComplexity("O(n)");
        
        Integer[] currentElements = model.getElements();
        int val = currentElements[idx];
        
        // Traverse to find index
        for (int i = 0; i <= idx; i++) {
            Map<Integer, String> h = new HashMap<>();
            h.put(i, "#f1c40f");
            recordSnapshot(currentElements, model.getCapacity(), h, "Traversing to index " + idx + " (val at i=" + i + ": " + currentElements[i] + ")", "O(n)");
        }
        
        // Found and removing
        Map<Integer, String> removeHighlight = new HashMap<>();
        removeHighlight.put(idx, "#e74c3c");
        recordSnapshot(currentElements, model.getCapacity(), removeHighlight, "Removing value " + val + " at index " + idx, "O(n)");
        
        model.deleteAtIndex(idx);
        recordSnapshot(model.getElements(), model.getCapacity(), new HashMap<>(), "Deleted " + val + ". Elements shifted left.", "O(n)");
        
        startAnimation();
        indexInput.clear();
    }

    @FXML
    private void deleteByValue() {
        if (isPlaying) resetAnimation();
        Integer val = getInputValue();
        if (val == null) return;

        clearSnapshots();
        setComplexity("O(n)");
        
        Integer[] currentElements = model.getElements();
        int foundIdx = -1;
        
        for (int i = 0; i < model.getSize(); i++) {
            Map<Integer, String> h = new HashMap<>();
            h.put(i, "#f1c40f");
            
            if (currentElements[i] == val) {
                h.put(i, "#e74c3c"); 
                recordSnapshot(currentElements, model.getCapacity(), h, "Found value " + val + " at index " + i + ". Removing...", "O(n)");
                foundIdx = i;
                break;
            } else {
                recordSnapshot(currentElements, model.getCapacity(), h, "Index " + i + " != " + val + ". Continuing...", "O(n)");
            }
        }
        
        if (foundIdx != -1) {
            model.deleteAtIndex(foundIdx);
            recordSnapshot(model.getElements(), model.getCapacity(), new HashMap<>(), "Deleted first occurrence of " + val + ".", "O(n)");
            valueInput.clear();
        } else {
            recordSnapshot(currentElements, model.getCapacity(), new HashMap<>(), "Value " + val + " not found to delete.", "O(n)");
        }
        
        startAnimation();
    }

    @FXML
    private void search() {
        if (isPlaying) resetAnimation();
        Integer val = getInputValue();
        if (val == null) return;

        clearSnapshots();
        setComplexity("O(n)");
        
        Integer[] currentElements = model.getElements();
        boolean found = false;
        
        for (int i = 0; i < model.getSize(); i++) {
            Map<Integer, String> h = new HashMap<>();
            h.put(i, "#f1c40f");
            
            if (currentElements[i] == val) {
                h.put(i, "#2ecc71");
                recordSnapshot(currentElements, model.getCapacity(), h, "Value " + val + " found at index " + i + "!", "O(n)");
                found = true;
                break;
            } else {
                recordSnapshot(currentElements, model.getCapacity(), h, "Checking index " + i + "...", "O(n)");
            }
        }
        
        if (!found) {
            recordSnapshot(currentElements, model.getCapacity(), new HashMap<>(), "Value " + val + " not found.", "O(n)");
        }
        
        startAnimation();
    }

    @FXML
    private void traverse() {
        if (isPlaying) resetAnimation();
        if (model.isEmpty()) {
            showAlert("Empty", "Array is empty.");
            return;
        }

        clearSnapshots();
        setComplexity("O(n)");
        
        Integer[] currentElements = model.getElements();
        for (int i = 0; i < model.getSize(); i++) {
            Map<Integer, String> h = new HashMap<>();
            h.put(i, "#f1c40f"); // Yellow traversal color
            recordSnapshot(currentElements, model.getCapacity(), h, "Visiting index " + i + " (Value: " + currentElements[i] + ")", "O(n)");
        }
        
        recordSnapshot(currentElements, model.getCapacity(), new HashMap<>(), "Traversal complete.", "O(n)");
        startAnimation();
    }

    @FXML
    private void reverse() {
        if (isPlaying) resetAnimation();
        if (model.isEmpty()) return;

        clearSnapshots();
        setComplexity("O(n)");
        
        int left = 0;
        int right = model.getSize() - 1;
        
        while (left < right) {
            Map<Integer, String> h = new HashMap<>();
            h.put(left, "#e67e22"); 
            h.put(right, "#e67e22");
            recordSnapshot(model.getElements(), model.getSize(), h, "Highlighting elements at " + left + " and " + right + " to swap.", "O(n)");
            
            model.swap(left, right);
            
            recordSnapshot(model.getElements(), model.getSize(), h, "Swapped elements.", "O(n)");
            left++;
            right--;
        }
        
        recordSnapshot(model.getElements(), model.getSize(), new HashMap<>(), "Array reversal complete.", "O(n)");
        startAnimation();
    }

    @FXML
    private void sort() {
        if (isPlaying) resetAnimation();
        if (model.isEmpty()) return;

        clearSnapshots();
        setComplexity("O(n²)");
        
        int size = model.getSize();
        boolean swapped;
        Map<Integer, String> sortedNodes = new HashMap<>();
        
        for (int i = 0; i < size - 1; i++) {
            swapped = false;
            for (int j = 0; j < size - i - 1; j++) {
                Map<Integer, String> h = new HashMap<>(sortedNodes);
                h.put(j, "#3498db"); // comparing
                h.put(j+1, "#3498db");
                
                int val1 = model.getElements()[j];
                int val2 = model.getElements()[j+1];
                recordSnapshot(model.getElements(), model.getSize(), h, "Comparing " + val1 + " & " + val2, "O(n²)");
                
                if (val1 > val2) {
                    h.put(j, "#e74c3c"); // swapping
                    h.put(j+1, "#e74c3c");
                    recordSnapshot(model.getElements(), model.getSize(), h, val1 + " > " + val2 + ". Swapping!", "O(n²)");
                    
                    model.swap(j, j+1);
                    recordSnapshot(model.getElements(), model.getSize(), h, "Swapped.", "O(n²)");
                    swapped = true;
                }
            }
            sortedNodes.put(size - 1 - i, "#2ecc71");
            recordSnapshot(model.getElements(), model.getSize(), new HashMap<>(sortedNodes), "Marked index " + (size - 1 - i) + " as sorted.", "O(n²)");
        }
        
        for(int i=0; i<size; i++) sortedNodes.put(i, "#2ecc71");
        recordSnapshot(model.getElements(), model.getSize(), sortedNodes, "Array fully sorted.", "O(n²)");
        
        startAnimation();
    }

    @FXML
    private void clear() {
        if (isPlaying) resetAnimation();
        model.clear();
        clearSnapshots();
        updateVisualization();
        logMessage("Cleared array layout");
    }

    // --- Animation Engine ---
    
    private void clearSnapshots() {
        snapshots.clear();
        currentStep = 0;
        if (stepLogContainer != null) {
            Platform.runLater(() -> stepLogContainer.getChildren().clear());
        }
    }

    private void recordSnapshot(Integer[] state, int size, Map<Integer, String> nodeColors, String message, String complexity) {
        snapshots.add(new ArraySnapshot(state, size, nodeColors, message, complexity));
    }

    private void startAnimation() {
        if (snapshots.isEmpty()) return;
        currentStep = 0;
        isPlaying = true;
        updatePlayPauseButton();
        
        if (animationThread != null && animationThread.isAlive()) {
            animationThread.interrupt();
        }

        animationThread = new Thread(() -> {
            try {
                while (currentStep < snapshots.size()) {
                    synchronized (playLock) {
                        while (!isPlaying) {
                            playLock.wait();
                        }
                    }
                    
                    final int step = currentStep;
                    Platform.runLater(() -> renderSnapshot(snapshots.get(step)));
                    
                    responsiveSleep();
                    currentStep++;
                }
            } catch (InterruptedException e) {
                // Thread interrupted
            } finally {
                isPlaying = false;
                Platform.runLater(this::updatePlayPauseButton);
                
                // Keep the final view without terminating early layout if it was just reached end
                if (currentStep >= snapshots.size() && snapshots.size() > 0) {
                    Platform.runLater(() -> renderSnapshot(snapshots.get(snapshots.size() - 1)));
                }
            }
        });
        animationThread.setDaemon(true);
        animationThread.start();
    }

    @FXML
    private void togglePlayPause() {
        if (snapshots.isEmpty() && currentStep >= snapshots.size()) return;
        
        isPlaying = !isPlaying;
        updatePlayPauseButton();
        
        if (isPlaying) {
            synchronized (playLock) {
                playLock.notify();
            }
            if (currentStep >= snapshots.size()) {
                startAnimation(); 
            }
        }
    }

    @FXML
    private void stepForward() {
        if (snapshots.isEmpty()) return;
        
        if (isPlaying) {
            isPlaying = false; // Pause if playing
            updatePlayPauseButton();
        }
        
        if (currentStep < snapshots.size() - 1) {
            currentStep++;
            renderSnapshot(snapshots.get(currentStep));
        }
    }

    @FXML
    private void stepBack() {
        if (snapshots.isEmpty()) return;
        
        if (isPlaying) {
            isPlaying = false; // Pause if playing
            updatePlayPauseButton();
        }
        
        if (currentStep > 0) {
            currentStep--;
            // On step back, we might want to drop recent logs, but for simplicity we render the snapshot
            renderSnapshot(snapshots.get(currentStep));
        }
    }

    @FXML
    private void resetAnimation() {
        if (animationThread != null) {
            animationThread.interrupt();
        }
        isPlaying = false;
        currentStep = 0;
        updatePlayPauseButton();
        
        if (!snapshots.isEmpty()) {
            renderSnapshot(snapshots.get(0));
            // Or we could let it remain at the finished state and just jump. 
            // Better to show the very first snapshot of the operation
            if (stepLogContainer != null) {
                Platform.runLater(() -> stepLogContainer.getChildren().clear());
            }
        }
    }

    private void renderSnapshot(ArraySnapshot snap) {
        arrayContainer.getChildren().clear();
        
        logMessage(snap.getMessage());
        logStep(snap.getMessage());
        
        // Instead of getting elements from active model, use the snapshot's elements perfectly
        Integer[] elems = snap.getElements();
        
        for (int i = 0; i < snap.getSize(); i++) {
            boolean hasColor = snap.getNodeColors().containsKey(i);
            String customColor = hasColor ? snap.getNodeColors().get(i) : "#9b59b6"; 
            
            String valStr = (elems[i] != null) ? String.valueOf(elems[i]) : "";
            VBox nodeBox = createArrayBox(valStr, i, customColor);
            arrayContainer.getChildren().add(nodeBox);
            
            if (hasColor) {
               animateNode(nodeBox);
            }
        }
    }

    // Fallback UI update for non-animated initializations mapping to regular
    private void updateVisualization() {
        arrayContainer.getChildren().clear();
        
        Integer[] currentElements = model.getElements();
        for (int i = 0; i < model.getCapacity(); i++) { 
            String valStr = (currentElements[i] != null) ? String.valueOf(currentElements[i]) : "";
            VBox nodeBox = createArrayBox(valStr, i, "#9b59b6");
            arrayContainer.getChildren().add(nodeBox);
        }
    }

    private VBox createArrayBox(String value, int index, String colorHex) {
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                "-fx-border-color: #2c3e50;" +
                "-fx-border-width: 2 2 0 2;" +
                "-fx-min-width: 55;" +
                "-fx-max-width: 55;" +
                "-fx-min-height: 55;" +
                "-fx-max-height: 55;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label indexLabel = new Label(String.valueOf(index));
        indexLabel.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #2c3e50;" +
                "-fx-border-width: 2;" +
                "-fx-min-width: 55;" +
                "-fx-max-width: 55;" +
                "-fx-min-height: 25;" +   // Increased to 25 to prevent clipping
                "-fx-max-height: 25;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 16;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2c3e50;"
        );

        container.getChildren().addAll(valueLabel, indexLabel);
        return container;
    }

    private void animateNode(VBox nodeContainer) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), nodeContainer);
        st.setFromX(1.0); st.setToX(1.1);
        st.setFromY(1.0); st.setToY(1.1);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    private void logStep(String stepDescription) {
        Label l = new Label("• " + stepDescription);
        l.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15;");
        l.setWrapText(true);
        if (stepLogContainer != null) {
            stepLogContainer.getChildren().add(l);
        }
    }

    private void logMessage(String message) {
        if (outputLabel != null) {
            outputLabel.setText(message);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePlayPauseButton() {
        if (playPauseBtn != null) {
            playPauseBtn.setText(isPlaying ? "⏸" : "▶");
        }
    }
    
    /** Reads local slider first; falls back to global. Does NOT write to SettingsManager. */
    private long getDelay() {
        double sliderVal = (speedSlider != null) ? speedSlider.getValue()
                                                 : com.AlgoVista.utils.SettingsManager.getSpeed();
        double rate = com.AlgoVista.utils.SettingsManager.getTimelineRate(sliderVal);
        return (long)(1000.0 / Math.max(rate, 0.01));
    }

    /** Sleeps in 50ms chunks, re-reading the delay each chunk for live responsiveness. */
    private void responsiveSleep() throws InterruptedException {
        long target = getDelay();
        long elapsed = 0;
        final long chunk = 50;
        while (elapsed < target && isPlaying) {
            Thread.sleep(Math.min(chunk, target - elapsed));
            elapsed += chunk;
            target = getDelay();
        }
    }

    @FXML
    private void backToMenu() {
        if (animationThread != null) animationThread.interrupt();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) arrayContainer.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
