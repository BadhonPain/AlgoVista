package com.AlgoVista.queue;

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

public class QueueController {

    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private TextField valueInput;
    @FXML private FlowPane queueContainer;
    @FXML private VBox stepLogContainer;
    @FXML private Label outputLabel;

    @FXML private Slider speedSlider;
    @FXML private Button playPauseBtn;
    @FXML private Label dynamicComplexityLabel;

    private QueueModel model;

    // Animation Engine state
    private List<QueueSnapshot> snapshots = new ArrayList<>();
    private int currentStep = 0;
    private boolean isPlaying = false;
    private Thread animationThread;
    private final Object playLock = new Object();

    @FXML
    public void initialize() {
        model = new QueueModel(8); // Default capacity 8
        capacitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 8));

        if (dynamicComplexityLabel != null) {
            dynamicComplexityLabel.setText("O(1)");
        }

        updateVisualization();
        logMessage("Queue Visualizer initialized. Ready for simulation.");
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
            showAlert("Invalid Input", "Please enter a valid integer.");
            return null;
        }
    }

    @FXML
    private void createQueue() {
        if (isPlaying) resetAnimation();
        int cap = capacitySpinner.getValue();
        model = new QueueModel(cap);
        clearSnapshots();
        updateVisualization();
        logMessage("Created empty circular queue with capacity " + cap + ".");
        stepLogContainer.getChildren().clear();
    }

    @FXML
    private void generateRandom() {
        if (isPlaying) resetAnimation();
        int cap = capacitySpinner.getValue();
        model = new QueueModel(cap);    // reinitialise with new capacity
        model.generateSample(cap, 1, 99);
        clearSnapshots();
        updateVisualization();
        logMessage("Generated random queue of size " + cap + ".");
        stepLogContainer.getChildren().clear();
    }

    @FXML
    private void enqueue() {
        if (isPlaying) resetAnimation();
        Integer val = getInputValue();
        if (val == null) return;

        clearSnapshots();
        setComplexity("O(1)");
        stepLogContainer.getChildren().clear();

        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "ENQUEUE OP initiating for value " + val + "...", "O(1)");
        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Step 1: Checking if queue is full...", "O(1)");

        if (model.isFull()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Queue is full! Cannot enqueue.", "O(1)");
            startAnimation();
            logMessage("Enqueue failed. Queue Overflow condition.");
            return;
        }

        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Queue is not full. Proceeding.", "O(1)");

        boolean success = model.enqueue(val);
        if (success) {
            Map<Integer, String> highlights = new HashMap<>();
            highlights.put(model.getRear(), "#2ecc71"); // Green for inserted
            recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), highlights, "Step 2: Calculating new REAR index via (rear + 1) % capacity and inserting value " + val, "O(1)");
            recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Step 3: Enqueued successfully.", "O(1)");
            startAnimation();
            valueInput.clear();
            logMessage("Enqueued " + val + " to the queue.");
        }
    }

    @FXML
    private void dequeue() {
        if (isPlaying) resetAnimation();
        clearSnapshots();
        setComplexity("O(1)");
        stepLogContainer.getChildren().clear();

        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "DEQUEUE OP initiating...", "O(1)");
        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Step 1: Checking if queue is empty...", "O(1)");

        if (model.isEmpty()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Queue is empty! Cannot dequeue.", "O(1)");
            startAnimation();
            logMessage("Dequeue failed. Queue Underflow condition.");
            return;
        }

        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Queue has elements. Proceeding.", "O(1)");

        Map<Integer, String> highToRemove = new HashMap<>();
        highToRemove.put(model.getFront(), "#e74c3c"); // Red for element to be removed
        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), highToRemove, "Step 2: Identifying current FRONT element (" + model.peek() + ") for removal.", "O(1)");

        Integer dequeued = model.dequeue();
        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Step 3: Value " + dequeued + " removed. FRONT pointer incremented via (front + 1) % capacity.", "O(1)");
        startAnimation();
        logMessage("Dequeued " + dequeued + " from the queue.");
    }

    @FXML
    private void peek() {
        if (isPlaying) resetAnimation();
        clearSnapshots();
        setComplexity("O(1)");
        stepLogContainer.getChildren().clear();

        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "PEEK OP initiating...", "O(1)");

        if (model.isEmpty()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Queue is empty. No front element.", "O(1)");
            startAnimation();
            logMessage("Peek failed: Queue is empty.");
            return;
        }

        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Accessing FRONT pointer...", "O(1)");

        Map<Integer, String> peekHigh = new HashMap<>();
        peekHigh.put(model.getFront(), "#9b59b6"); // Purple for peek
        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), peekHigh, "Examining element at FRONT...", "O(1)");
        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Front element is " + model.peek() + ".", "O(1)");

        startAnimation();
        logMessage("Front element is " + model.peek() + ".");
    }

    @FXML
    private void search() {
        if (isPlaying) resetAnimation();
        Integer target = getInputValue();
        if (target == null) return;

        clearSnapshots();
        setComplexity("O(n)");
        stepLogContainer.getChildren().clear();

        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "SEARCH OP initiating for value " + target + "...", "O(n)");

        if (model.isEmpty()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Queue is empty. Search termination immediately.", "O(n)");
            startAnimation();
            logMessage("Value not found (Empty Queue).");
            return;
        }

        Integer[] currentElements = model.getElements();
        boolean found = false;
        int front = model.getFront();
        int capacity = model.getCapacity();

        for (int count = 0; count < model.getSize(); count++) {
            int currIndex = (front + count) % capacity;
            Map<Integer, String> checkColor = new HashMap<>();
            checkColor.put(currIndex, "#f1c40f"); // Yellow for checking
            recordSnapshot(currentElements, model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), checkColor, "Checking index " + currIndex + " from front (Value: " + currentElements[currIndex] + ")", "O(n)");

            if (currentElements[currIndex] != null && currentElements[currIndex].equals(target)) {
                found = true;
                Map<Integer, String> matchColor = new HashMap<>();
                matchColor.put(currIndex, "#2ecc71"); // Green for match
                recordSnapshot(currentElements, model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), matchColor, "Match found! Distance from front = " + count, "O(n)");
                break;
            } else {
                recordSnapshot(currentElements, model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "No match at index " + currIndex + ". Moving next.", "O(n)");
            }
        }

        if (!found) {
            recordSnapshot(currentElements, model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Reached end of queue. Element not found.", "O(n)");
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

        recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "TRAVERSE OP initiating...", "O(n)");

        if (model.isEmpty()) {
            recordSnapshot(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Empty Queue.", "O(n)");
            startAnimation();
            logMessage("Queue is empty.");
            return;
        }

        Integer[] currentElements = model.getElements();
        recordSnapshot(currentElements, model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Traversing from FRONT to REAR...", "O(n)");

        int front = model.getFront();
        int capacity = model.getCapacity();

        for (int count = 0; count < model.getSize(); count++) {
            int currIndex = (front + count) % capacity;
            Map<Integer, String> travColor = new HashMap<>();
            travColor.put(currIndex, "#9b59b6"); // Purple
            recordSnapshot(currentElements, model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), travColor, "Visiting element at index " + currIndex + " (Value: " + currentElements[currIndex] + ")", "O(n)");
        }

        recordSnapshot(currentElements, model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>(), "Traversal complete.", "O(n)");
        startAnimation();
        logMessage("Traversing from FRONT to REAR:\n" + model.traverse());
    }

    @FXML
    private void checkEmpty() {
        if (isPlaying) resetAnimation();
        stepLogContainer.getChildren().clear();
        boolean empty = model.isEmpty();
        addLogStep("Checked isEmpty(). Result: " + empty);
        logMessage("Is Queue Empty? -> " + String.valueOf(empty).toUpperCase());
    }

    @FXML
    private void checkFull() {
        if (isPlaying) resetAnimation();
        stepLogContainer.getChildren().clear();
        boolean full = model.isFull();
        addLogStep("Checked isFull(). Result: " + full);
        logMessage("Is Queue Full? -> " + String.valueOf(full).toUpperCase());
    }

    @FXML
    private void getSize() {
        if (isPlaying) resetAnimation();
        stepLogContainer.getChildren().clear();
        int size = model.getSize();
        addLogStep("Counted nodes from Front to Rear.");
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
        logMessage("Queue fully cleared.");
    }

    // --- Animation Handling ---

    private void clearSnapshots() {
        snapshots.clear();
    }

    private void recordSnapshot(Integer[] elements, int capacity, int front, int rear, int size, Map<Integer, String> nodeColors, String message, String timeComplexity) {
        snapshots.add(new QueueSnapshot(elements, capacity, front, rear, size, nodeColors, message, timeComplexity));
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

                    double currentSpeed = speedSlider != null ? speedSlider.getValue() : com.AlgoVista.utils.SettingsManager.getSpeed();
                    if (speedSlider == null) {
                        currentSpeed = com.AlgoVista.utils.SettingsManager.getSpeed();
                    } else {
                        com.AlgoVista.utils.SettingsManager.setSpeed(currentSpeed);
                    }
                    long sleepTime = (long) (1000 / currentSpeed);
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
        renderArray(model.getElements(), model.getCapacity(), model.getFront(), model.getRear(), model.getSize(), new HashMap<>());
    }

    private void renderSnapshot(QueueSnapshot snapshot) {
        if (snapshot == null) return;
        renderArray(snapshot.getElements(), snapshot.getCapacity(), snapshot.getFront(), snapshot.getRear(), snapshot.getSize(), snapshot.getNodeColors());
        if (snapshot.getMessage() != null && !snapshot.getMessage().isEmpty()) {
            addLogStep(snapshot.getMessage());
        }
        if (snapshot.getTimeComplexity() != null) {
            setComplexity(snapshot.getTimeComplexity());
        }
    }

    private void renderArray(Integer[] elements, int capacity, int front, int rear, int size, Map<Integer, String> nodeColors) {
        queueContainer.getChildren().clear();
        boolean isEmpty = (size == 0);

        for (int i = 0; i < capacity; i++) {
            boolean isActive = false;
            
            if (!isEmpty) {
                if (front <= rear) {
                    isActive = (i >= front && i <= rear);
                } else {
                    isActive = (i >= front || i <= rear);
                }
            }
            
            boolean isFront = (!isEmpty && i == front);
            boolean isRear = (!isEmpty && i == rear);
            
            String valStr = (isActive && elements[i] != null) ? String.valueOf(elements[i]) : "";
            String highlightColor = nodeColors.getOrDefault(i, null);

            VBox cell = createQueueCell(valStr, i, isActive, isFront, isRear, highlightColor);
            queueContainer.getChildren().add(cell);
        }
    }

    private VBox createQueueCell(String valStr, int index, boolean isActive, boolean isFront, boolean isRear, String highlightColor) {
        VBox box = new VBox(5);
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
                "-fx-min-width: 50;" +
                "-fx-max-width: 50;" +
                "-fx-min-height: 50;" +
                "-fx-max-height: 50;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 18;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + textFill + ";"
        );

        Label idxLabel = new Label("[" + index + "]");
        idxLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11;");

        HBox tags = new HBox(5);
        tags.setAlignment(Pos.CENTER);
        if (isFront) {
            Label fTag = new Label("F");
            fTag.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 2 5; -fx-background-radius: 3; -fx-font-size: 10; -fx-font-weight: bold;");
            tags.getChildren().add(fTag);
        }
        if (isRear) {
            Label rTag = new Label("R");
            rTag.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 2 5; -fx-background-radius: 3; -fx-font-size: 10; -fx-font-weight: bold;");
            tags.getChildren().add(rTag);
        }

        box.getChildren().addAll(dataLabel, idxLabel, tags);
        return box;
    }

    private void addLogStep(String stepDescription) {
        Platform.runLater(() -> {
            Label l = new Label("• " + stepDescription);
            l.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13;");
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

            Stage stage = (Stage) queueContainer.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
