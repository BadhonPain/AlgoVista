package com.AlgoVista.stack;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackController {

    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private TextField valueInput;
    @FXML private VBox stackContainer;
    @FXML private VBox stepLogContainer;
    @FXML private Label outputLabel;

    @FXML private Slider speedSlider;
    @FXML private Button playPauseBtn;
    @FXML private Label dynamicComplexityLabel;

    private StackModel model;

    // Animation Engine state
    private List<StackSnapshot> snapshots = new ArrayList<>();
    private int currentStep = 0;
    private boolean isPlaying = false;
    private Thread animationThread;
    private final Object playLock = new Object();

    @FXML
    public void initialize() {
        model = new StackModel(8); // Default capacity 8
        capacitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 8));

        if (dynamicComplexityLabel != null) {
            dynamicComplexityLabel.setText("O(1)");
        }

        updateVisualization();
        logMessage("Stack Visualizer initialized. Ready for simulation.");
    }

    private void setComplexity(String comp) {
        Platform.runLater(() -> {
            if (dynamicComplexityLabel != null) {
                dynamicComplexityLabel.setText(comp);
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

    @FXML
    private void createStack() {
        if (isPlaying) resetAnimation();
        int cap = capacitySpinner.getValue();
        model = new StackModel(cap);
        clearSnapshots();
        updateVisualization();
        logMessage("Created empty stack with capacity " + cap + ".");
        stepLogContainer.getChildren().clear();
    }

    @FXML
    private void generateRandom() {
        if (isPlaying) resetAnimation();
        int cap = capacitySpinner.getValue();
        model = new StackModel(cap);    // reinitialise with new capacity
        model.generateSample(cap, 1, 99);
        clearSnapshots();
        updateVisualization();
        logMessage("Generated random stack of size " + cap + ".");
        stepLogContainer.getChildren().clear();
    }

    @FXML
    private void push() {
        if (isPlaying) resetAnimation();
        Integer val = getInputValue();
        if (val == null) return;

        clearSnapshots();
        setComplexity("O(1)");
        stepLogContainer.getChildren().clear();

        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "PUSH OP initiating for value " + val + "...", "O(1)");
        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Step 1: Checking if stack is full...", "O(1)");

        if (model.isFull()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Stack is full! Cannot push.", "O(1)");
            startAnimation();
            logMessage("Push failed. Stack Overflow condition.");
            return;
        }

        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Stack is not full. Proceeding.", "O(1)");

        boolean success = model.push(val);
        if (success) {
            Map<Integer, String> highlights = new HashMap<>();
            highlights.put(model.getTopIndex(), "#2ecc71"); // Green for inserted element
            recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), highlights, "Step 2: Incrementing TOP pointer and inserting value " + val, "O(1)");
            recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Step 3: Pushed value successfully.", "O(1)");
            startAnimation();
            valueInput.clear();
            logMessage("Pushed " + val + " onto the stack.");
        }
    }

    @FXML
    private void pop() {
        if (isPlaying) resetAnimation();
        clearSnapshots();
        setComplexity("O(1)");
        stepLogContainer.getChildren().clear();

        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "POP OP initiating...", "O(1)");
        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Step 1: Checking if stack is empty...", "O(1)");

        if (model.isEmpty()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Stack is empty! Cannot pop.", "O(1)");
            startAnimation();
            logMessage("Pop failed. Stack Underflow condition.");
            return;
        }

        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Stack has elements. Proceeding.", "O(1)");

        Map<Integer, String> highToRemove = new HashMap<>();
        highToRemove.put(model.getTopIndex(), "#e74c3c"); // Red for element about to be removed
        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), highToRemove, "Step 2: Identifying current TOP element (" + model.peek() + ") for removal.", "O(1)");

        Integer popped = model.pop();
        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Step 3: Value " + popped + " removed. TOP pointer decremented.", "O(1)");
        startAnimation();
        logMessage("Popped " + popped + " from the stack.");
    }

    @FXML
    private void peek() {
        if (isPlaying) resetAnimation();
        clearSnapshots();
        setComplexity("O(1)");
        stepLogContainer.getChildren().clear();

        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "PEEK OP initiating...", "O(1)");

        if (model.isEmpty()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Stack is empty. No top element.", "O(1)");
            startAnimation();
            logMessage("Peek failed: Stack is empty.");
            return;
        }

        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Accessing top element pointer...", "O(1)");

        Map<Integer, String> peekHigh = new HashMap<>();
        peekHigh.put(model.getTopIndex(), "#9b59b6"); // Purple for peek
        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), peekHigh, "Examining element at TOP...", "O(1)");
        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Top element is " + model.peek() + ".", "O(1)");

        startAnimation();
        logMessage("Top element is " + model.peek() + ".");
    }

    @FXML
    private void search() {
        if (isPlaying) resetAnimation();
        Integer target = getInputValue();
        if (target == null) return;

        clearSnapshots();
        setComplexity("O(n)");
        stepLogContainer.getChildren().clear();

        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "SEARCH OP initiating for value " + target + "...", "O(n)");

        if (model.isEmpty()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Stack is empty. Search termination immediately.", "O(n)");
            startAnimation();
            logMessage("Value not found (Empty Stack).");
            return;
        }

        Integer[] currentElements = model.getElements();
        boolean found = false;

        for (int i = model.getTopIndex(); i >= 0; i--) {
            Map<Integer, String> checkColor = new HashMap<>();
            checkColor.put(i, "#f1c40f"); // Yellow for checking
            recordSnapshot(currentElements, model.getCapacity(), model.getTopIndex(), checkColor, "Checking index " + i + " from bottom (Value: " + currentElements[i] + ")", "O(n)");

            if (currentElements[i] != null && currentElements[i].equals(target)) {
                found = true;
                Map<Integer, String> matchColor = new HashMap<>();
                matchColor.put(i, "#2ecc71"); // Green for match
                recordSnapshot(currentElements, model.getCapacity(), model.getTopIndex(), matchColor, "Match found! Distance from top = " + (model.getTopIndex() - i + 1), "O(n)");
                break;
            } else {
                recordSnapshot(currentElements, model.getCapacity(), model.getTopIndex(), new HashMap<>(), "No match at index " + i + ". Moving down.", "O(n)");
            }
        }

        if (!found) {
            recordSnapshot(currentElements, model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Reached bottom. Element not found.", "O(n)");
            logMessage("Search failed. Element " + target + " not present.");
        } else {
            logMessage("Found value " + target + ".");
        }
        startAnimation();
    }

    @FXML
    private void traverse() {
        if (isPlaying) resetAnimation();
        clearSnapshots();
        setComplexity("O(n)");
        stepLogContainer.getChildren().clear();

        recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "TRAVERSE OP initiating...", "O(n)");

        if (model.isEmpty()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Empty Stack.", "O(n)");
            startAnimation();
            logMessage("Stack is empty.");
            return;
        }

        Integer[] currentElements = model.getElements();
        recordSnapshot(currentElements, model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Traversing from TOP down to BOTTOM...", "O(n)");

        for (int i = model.getTopIndex(); i >= 0; i--) {
            Map<Integer, String> travColor = new HashMap<>();
            travColor.put(i, "#9b59b6"); // Purple
            recordSnapshot(currentElements, model.getCapacity(), model.getTopIndex(), travColor, "Visiting element at index " + i + " (Value: " + currentElements[i] + ")", "O(n)");
        }

        recordSnapshot(currentElements, model.getCapacity(), model.getTopIndex(), new HashMap<>(), "Traversal complete.", "O(n)");
        startAnimation();
        logMessage("Traversing from TOP to BOTTOM:\n" + model.traverse());
    }

    @FXML
    private void checkEmpty() {
        if (isPlaying) resetAnimation();
        stepLogContainer.getChildren().clear();
        boolean empty = model.isEmpty();
        addLogStep("Checked isEmpty(). Result: " + empty);
        logMessage("Is Stack Empty? -> " + String.valueOf(empty).toUpperCase());
    }

    @FXML
    private void checkFull() {
        if (isPlaying) resetAnimation();
        stepLogContainer.getChildren().clear();
        boolean full = model.isFull();
        addLogStep("Checked isFull(). Result: " + full);
        logMessage("Is Stack Full? -> " + String.valueOf(full).toUpperCase());
    }

    @FXML
    private void getSize() {
        if (isPlaying) resetAnimation();
        stepLogContainer.getChildren().clear();
        int size = model.size();
        addLogStep("Top pointer is at index " + model.getTopIndex() + ". Size is Top+1.");
        logMessage("Current Size: " + size);
    }

    @FXML
    private void clear() {
        if (isPlaying) resetAnimation();
        stepLogContainer.getChildren().clear();
        addLogStep("CLEAR OP Executed.");
        model.clear();
        clearSnapshots();
        updateVisualization();
        logMessage("Stack fully cleared.");
    }

    // --- Animation Handling ---

    private void clearSnapshots() {
        snapshots.clear();
    }

    private void recordSnapshot(Integer[] elements, int capacity, int topIndex, Map<Integer, String> nodeColors, String message, String timeComplexity) {
        snapshots.add(new StackSnapshot(elements, capacity, topIndex, nodeColors, message, timeComplexity));
    }

    private void startAnimation() {
        if (snapshots.isEmpty()) return;
        isPlaying = true;
        currentStep = 0;
        playPauseBtn.setText("Pause");

        animationThread = new Thread(() -> {
            try {
                while (currentStep < snapshots.size() && isPlaying) {
                    final int stepToRender = currentStep;
                    Platform.runLater(() -> renderSnapshot(snapshots.get(stepToRender)));

                    long sleepTime = (long) (1000 / speedSlider.getValue());
                    Thread.sleep(sleepTime);

                    synchronized (playLock) {
                        if (isPlaying) {
                            currentStep++;
                        }
                    }
                }

                Platform.runLater(() -> {
                    isPlaying = false;
                    playPauseBtn.setText("Play");
                    if (currentStep >= snapshots.size()) {
                        updateVisualization(); // Final render state at end
                    }
                });
            } catch (InterruptedException e) {
                // Interrupted
            }
        });
        animationThread.setDaemon(true);
        animationThread.start();
    }

    @FXML
    private void togglePlayPause() {
        if (snapshots.isEmpty()) return;
        synchronized (playLock) {
            isPlaying = !isPlaying;
            playPauseBtn.setText(isPlaying ? "Pause" : "Play");
            if (isPlaying) {
                if (currentStep >= snapshots.size()) {
                    currentStep = 0;
                }
                startAnimation();
            } else {
                if (animationThread != null && animationThread.isAlive()) {
                    animationThread.interrupt();
                }
            }
        }
    }

    @FXML
    private void stepForward() {
        if (snapshots.isEmpty()) return;
        synchronized (playLock) {
            if (isPlaying) togglePlayPause();
            if (currentStep < snapshots.size() - 1) {
                currentStep++;
                renderSnapshot(snapshots.get(currentStep));
            }
        }
    }

    @FXML
    private void stepBackward() {
        if (snapshots.isEmpty()) return;
        synchronized (playLock) {
            if (isPlaying) togglePlayPause();
            if (currentStep > 0) {
                currentStep--;
                renderSnapshot(snapshots.get(currentStep));
            }
        }
    }

    @FXML
    private void resetAnimation() {
        synchronized (playLock) {
            isPlaying = false;
            if (animationThread != null && animationThread.isAlive()) {
                animationThread.interrupt();
            }
            playPauseBtn.setText("Play");
            currentStep = 0;
            if (!snapshots.isEmpty()) {
                renderSnapshot(snapshots.get(0));
            } else {
                updateVisualization();
            }
        }
    }

    // --- Visualization ---

    private void updateVisualization() {
        renderArray(model.getElements(), model.getCapacity(), model.getTopIndex(), new HashMap<>());
    }

    private void renderSnapshot(StackSnapshot snapshot) {
        if (snapshot == null) return;
        renderArray(snapshot.getElements(), snapshot.getCapacity(), snapshot.getTopIndex(), snapshot.getNodeColors());
        if (snapshot.getMessage() != null && !snapshot.getMessage().isEmpty()) {
            addLogStep(snapshot.getMessage());
        }
        if (snapshot.getTimeComplexity() != null) {
            setComplexity(snapshot.getTimeComplexity());
        }
    }

    private void renderArray(Integer[] elements, int capacity, int topIndex, Map<Integer, String> nodeColors) {
        stackContainer.getChildren().clear();

        for (int i = capacity - 1; i >= 0; i--) {
            boolean isActive = (i <= topIndex && elements[i] != null);
            boolean isTop = (i == topIndex);
            String valStr = isActive ? String.valueOf(elements[i]) : "";
            String highlightColor = nodeColors.getOrDefault(i, null);

            HBox row = createStackCell(valStr, i, isActive, isTop, highlightColor);
            stackContainer.getChildren().add(row);
        }
    }

    private HBox createStackCell(String valStr, int index, boolean isActive, boolean isTop, String highlightColor) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER);

        // Top marker
        Label topMarker = new Label(isTop ? "TOP →" : "     ");
        topMarker.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c; -fx-min-width: 45;");

        // Main Box
        VBox box = new VBox(0);
        box.setAlignment(Pos.CENTER);

        String bgStyle;
        if (highlightColor != null) {
            bgStyle = "-fx-background-color: " + highlightColor + ";";
            ScaleTransition st = new ScaleTransition(Duration.millis(300), box);
            st.setFromX(1.0); st.setToX(1.1);
            st.setFromY(1.0); st.setToY(1.1);
            st.setCycleCount(2);
            st.setAutoReverse(true);
            st.play();
        } else {
            bgStyle = isActive ? "-fx-background-color: #34495e;" : "-fx-background-color: #ecf0f1;";
        }
        String textFill = isActive ? "white" : "transparent";

        Label dataLabel = new Label(valStr);
        dataLabel.setStyle(
                bgStyle +
                "-fx-border-color: #2c3e50;" +
                "-fx-border-width: 2;" +
                "-fx-min-width: 130;" +
                "-fx-max-width: 130;" +
                "-fx-min-height: 40;" +
                "-fx-max-height: 40;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + textFill + ";"
        );
        box.getChildren().add(dataLabel);

        // Index marker
        Label idxMarker = new Label("[" + index + "]");
        idxMarker.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13; -fx-min-width: 25;");

        row.getChildren().addAll(topMarker, box, idxMarker);
        return row;
    }

    private void addLogStep(String stepDescription) {
        Platform.runLater(() -> {
            Label l = new Label("• " + stepDescription);
            l.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15;");
            l.setWrapText(true);
            stepLogContainer.getChildren().add(l);
        });
    }

    private void logMessage(String msg) {
        Platform.runLater(() -> {
            if (outputLabel != null) {
                outputLabel.setText(msg);
            }
        });
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void backToMenu() {
        if (isPlaying) resetAnimation();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) stackContainer.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
