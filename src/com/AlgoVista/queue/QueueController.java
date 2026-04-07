package com.AlgoVista.queue;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
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

public class QueueController {

    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private TextField valueInput;
    @FXML private FlowPane queueContainer;
    @FXML private VBox stepLogContainer;
    @FXML private Label outputLabel;
    @FXML private VBox complexitiesContainer;

    private QueueModel model;
    private boolean isAnimating = false;

    @FXML
    public void initialize() {
        model = new QueueModel(8); // Default capacity 8
        capacitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 8));
        
        setupComplexities();
        renderQueueInstantly();
        logResult("Queue Visualizer initialized. Ready for simulation.");
    }

    private void setupComplexities() {
        addComplexityRow("Enqueue", "Add an element to the rear of the queue", "O(1)");
        addComplexityRow("Dequeue", "Remove the front element", "O(1)");
        addComplexityRow("Peek / Front", "Access front element without removing", "O(1)");
        addComplexityRow("Is Empty", "Check whether the queue has no elements", "O(1)");
        addComplexityRow("Is Full", "Check whether the queue reached capacity", "O(1)");
        addComplexityRow("Size", "Return the current number of elements", "O(1)");
        addComplexityRow("Search", "Scan queue elements to find a value", "O(n)");
        addComplexityRow("Traverse / Display", "Visit and display all elements", "O(n)");
        addComplexityRow("Clear", "Remove all elements", "O(n)");
        addComplexityRow("Space Complexity", "Memory used to store queue elements", "O(n)");
    }

    private void addComplexityRow(String operation, String description, String complexity) {
        HBox row = new HBox(10);
        row.getStyleClass().add("complexity-row");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox textContainer = new VBox(2);
        HBox.setHgrow(textContainer, Priority.ALWAYS);
        
        Label nameLbl = new Label(operation);
        nameLbl.getStyleClass().add("complexity-op-name");
        
        Label descLbl = new Label(description);
        descLbl.getStyleClass().add("complexity-op-desc");
        descLbl.setWrapText(true);

        textContainer.getChildren().addAll(nameLbl, descLbl);

        Label badge = new Label(complexity);
        if (complexity.equals("O(1)") || complexity.startsWith("O(1)")) {
            badge.getStyleClass().add("complexity-badge-o1");
        } else {
            badge.getStyleClass().add("complexity-badge-on");
        }

        row.getChildren().addAll(textContainer, badge);
        complexitiesContainer.getChildren().add(row);
    }

    private Integer getInputValue() {
        try {
            return Integer.parseInt(valueInput.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
            return null;
        }
    }

    // --- Core Operations ---
    
    @FXML
    private void createQueue() {
        if (isAnimating) return;
        int cap = capacitySpinner.getValue();
        model = new QueueModel(cap);
        stepLogContainer.getChildren().clear();
        logStep("Created empty circular queue with capacity " + cap);
        renderQueueInstantly();
        logResult("New queue created successfully.");
    }

    @FXML
    private void generateRandom() {
        if (isAnimating) return;
        int cap = capacitySpinner.getValue();
        model.generateSample(cap, 1, 99);
        stepLogContainer.getChildren().clear();
        logStep("Generated random elements up to capacity " + cap);
        renderQueueInstantly();
        logResult("Random queue generated successfully.");
    }

    @FXML
    private void enqueue() {
        if (isAnimating) return;
        Integer val = getInputValue();
        if (val == null) return;

        stepLogContainer.getChildren().clear();
        logStep("ENQUEUE OP initiating for value " + val + "...");
        
        isAnimating = true;

        new Thread(() -> {
            try {
                Thread.sleep((long)((800) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                logStep("Step 1: Checking if queue is full...");
                
                if (model.isFull()) {
                    logStep("Queue is full! Cannot enqueue.");
                    logResult("Enqueue failed. Queue Overflow condition.");
                    showAlert("Queue Overflow", "Cannot enqueue, the queue is full.");
                    return;
                }
                
                logStep("Queue is not full. Proceeding.");
                Thread.sleep((long)((1000) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                
                boolean success = model.enqueue(val);
                if (success) {
                    logStep("Step 2: Calculating new REAR index via (rear + 1) % capacity and inserting value " + val);
                    Platform.runLater(this::renderQueueInstantly);
                    
                    Platform.runLater(() -> {
                        int rearIndex = model.getRear();
                        if (rearIndex >= 0 && rearIndex < queueContainer.getChildren().size()) {
                            VBox cellBox = (VBox) queueContainer.getChildren().get(rearIndex);
                            Label dataLabel = (Label) cellBox.getChildren().get(0);
                            String oldStyle = dataLabel.getStyle();
                            dataLabel.setStyle(oldStyle.replace("-fx-background-color: #34495e;", "-fx-background-color: #2ecc71;"));
                            
                            PauseTransition restore = new PauseTransition(Duration.millis(800));
                            restore.setOnFinished(ev -> dataLabel.setStyle(oldStyle));
                            restore.play();
                        }
                    });
                    
                    logStep("Step 3: Enqueued successfully.");
                    logResult("Enqueued " + val + " to the queue.");
                    Platform.runLater(() -> valueInput.clear());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isAnimating = false;
            }
        }).start();
    }

    @FXML
    private void dequeue() {
        if (isAnimating) return;
        
        stepLogContainer.getChildren().clear();
        logStep("DEQUEUE OP initiating...");
        isAnimating = true;

        new Thread(() -> {
            try {
                Thread.sleep((long)((800) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                logStep("Step 1: Checking if queue is empty...");
                
                if (model.isEmpty()) {
                    logStep("Queue is empty! Cannot dequeue.");
                    logResult("Dequeue failed. Queue Underflow condition.");
                    showAlert("Queue Underflow", "Cannot dequeue, the queue is empty.");
                    return;
                }
                
                logStep("Queue has elements. Proceeding.");
                
                Platform.runLater(() -> {
                    int frontIndex = model.getFront();
                    if (frontIndex >= 0 && frontIndex < queueContainer.getChildren().size()) {
                        VBox cellBox = (VBox) queueContainer.getChildren().get(frontIndex);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        dataLabel.setStyle(dataLabel.getStyle().replace("-fx-background-color: #34495e;", "-fx-background-color: #e74c3c;"));
                        logStep("Step 2: Identifying current FRONT element (" + model.peek() + ") for removal.");
                    }
                });
                
                Thread.sleep((long)((1000) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                if (model.isEmpty()) return;
                
                Integer dequeued = model.dequeue();
                logStep("Step 3: Value " + dequeued + " removed. FRONT pointer incremented via (front + 1) % capacity.");
                Platform.runLater(this::renderQueueInstantly);
                logResult("Dequeued " + dequeued + " from the queue.");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isAnimating = false;
            }
        }).start();
    }

    @FXML
    private void peek() {
        if (isAnimating) return;
        
        stepLogContainer.getChildren().clear();
        logStep("PEEK OP initiating...");
        
        if (model.isEmpty()) {
            logStep("Queue is empty. No front element.");
            logResult("Peek failed: Queue is empty.");
            return;
        }

        isAnimating = true;
        
        new Thread(() -> {
            try {
                logStep("Accessing FRONT pointer...");
                
                String[] savedStyle = {null};
                
                Platform.runLater(() -> {
                    int frontIndex = model.getFront();
                    if (frontIndex >= 0 && frontIndex < queueContainer.getChildren().size()) {
                        VBox cellBox = (VBox) queueContainer.getChildren().get(frontIndex);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        
                        savedStyle[0] = dataLabel.getStyle();
                        dataLabel.setStyle(savedStyle[0].replace("-fx-background-color: #34495e;", "-fx-background-color: #9b59b6;"));
                    }
                });
                
                Thread.sleep((long)((1200) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                
                Platform.runLater(() -> {
                    int frontIndex = model.getFront();
                    if (frontIndex >= 0 && frontIndex < queueContainer.getChildren().size()) {
                        VBox cellBox = (VBox) queueContainer.getChildren().get(frontIndex);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        if(savedStyle[0] != null) dataLabel.setStyle(savedStyle[0]);
                    }
                    logStep("Front element is " + model.peek() + ".");
                    logResult("Front element is " + model.peek());
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isAnimating = false;
            }
        }).start();
    }

    @FXML
    private void search() {
        if (isAnimating) return;
        Integer target = getInputValue();
        if (target == null) return;

        stepLogContainer.getChildren().clear();
        logStep("SEARCH OP initiating for value " + target + "...");
        
        if (model.isEmpty()) {
            logStep("Queue is empty. Search termination immediately.");
            logResult("Value not found (Empty Queue).");
            return;
        }

        isAnimating = true;
        
        new Thread(() -> {
            try {
                int[] elements = model.getElements();
                int front = model.getFront();
                int size = model.getSize();
                int capacity = model.getCapacity();
                boolean[] foundTracker = {false};

                for (int count = 0; count < size; count++) {
                    if (foundTracker[0]) break;
                    
                    int currIndex = (front + count) % capacity;
                    Thread.sleep((long)((800) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                    
                    logStep("Checking index " + currIndex + " from front (Value: " + elements[currIndex] + ")");
                    
                    final int finalCount = count;
                    Platform.runLater(() -> {
                        VBox cellBox = (VBox) queueContainer.getChildren().get(currIndex);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        String oldStyle = dataLabel.getStyle();
                        
                        dataLabel.setStyle(oldStyle.replace("-fx-background-color: #34495e;", "-fx-background-color: #f1c40f;")); // yellow
                        
                        if (elements[currIndex] == target) {
                            foundTracker[0] = true;
                            logStep("Match found! Distance from front = " + finalCount);
                            logResult("Found value " + target + " at array index " + currIndex + ".");
                            PauseTransition end = new PauseTransition(Duration.millis(1500));
                            end.setOnFinished(ev -> dataLabel.setStyle(oldStyle));
                            end.play();
                        } else {
                            PauseTransition end = new PauseTransition(Duration.millis(300));
                            end.setOnFinished(ev -> dataLabel.setStyle(oldStyle));
                            end.play();
                        }
                    });
                    
                    if (elements[currIndex] == target) {
                        Thread.sleep((long)((1500) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                    } else {
                        Thread.sleep((long)((300) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                    }
                }
                
                Thread.sleep((long)((200) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                if (!foundTracker[0]) {
                    logStep("Reached end of queue. Element not found.");
                    logResult("Search failed. Element " + target + " not present.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isAnimating = false;
            }
        }).start();
    }

    @FXML
    private void traverse() {
        if (isAnimating) return;
        
        stepLogContainer.getChildren().clear();
        logStep("TRAVERSE OP initiating...");
        
        if (model.isEmpty()) {
            logStep("Empty Queue.");
            logResult("Queue is empty.");
            return;
        }
        
        isAnimating = true;

        new Thread(() -> {
            try {
                int[] elements = model.getElements();
                int front = model.getFront();
                int size = model.getSize();
                int capacity = model.getCapacity();
                
                logStep("Traversing from FRONT to REAR...");
                
                for (int count = 0; count < size; count++) {
                    int currIndex = (front + count) % capacity;
                    Thread.sleep((long)((800) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                    
                    logStep("Visiting element at index " + currIndex + " (Value: " + elements[currIndex] + ")");
                    
                    Platform.runLater(() -> {
                        VBox cellBox = (VBox) queueContainer.getChildren().get(currIndex);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        String oldStyle = dataLabel.getStyle();
                        
                        dataLabel.setStyle(oldStyle.replace("-fx-background-color: #34495e;", "-fx-background-color: #9b59b6;")); // purple
                        
                        PauseTransition end = new PauseTransition(Duration.millis(800));
                        end.setOnFinished(ev -> dataLabel.setStyle(oldStyle));
                        end.play();
                    });
                    
                    Thread.sleep((long)((1000) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier())); 
                }
                
                logStep("Traversal complete.");
                logResult("Traversing from FRONT to REAR:\n" + model.traverse());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isAnimating = false;
            }
        }).start();
    }

    @FXML
    private void checkEmpty() {
        if (isAnimating) return;
        stepLogContainer.getChildren().clear();
        boolean empty = model.isEmpty();
        logStep("Checked isEmpty(). Result: " + empty);
        logResult("Is Queue Empty? -> " + String.valueOf(empty).toUpperCase());
    }

    @FXML
    private void checkFull() {
        if (isAnimating) return;
        stepLogContainer.getChildren().clear();
        boolean full = model.isFull();
        logStep("Checked isFull(). Result: " + full);
        logResult("Is Queue Full? -> " + String.valueOf(full).toUpperCase());
    }

    @FXML
    private void getSize() {
        if (isAnimating) return;
        stepLogContainer.getChildren().clear();
        int size = model.getSize();
        logStep("Counted nodes from Front to Rear.");
        logResult("Current Size: " + size + " / " + model.getCapacity());
    }

    @FXML
    private void clear() {
        if (isAnimating) return;
        stepLogContainer.getChildren().clear();
        logStep("CLEAR OP Executed.");
        model.clear();
        renderQueueInstantly();
        logResult("Queue fully cleared.");
    }

    // --- Visualization & Helpers ---

    private void renderQueueInstantly() {
        queueContainer.getChildren().clear();
        
        int capacity = model.getCapacity();
        int[] elements = model.getElements();
        int front = model.getFront();
        int rear = model.getRear();
        boolean isEmpty = model.isEmpty();
        int size = model.getSize();

        // Render entire array capacity
        for (int i = 0; i < capacity; i++) {
            boolean isActive = false;
            
            // Check if current index i is within the active logical queue range
            if (!isEmpty) {
                if (front <= rear) {
                    isActive = (i >= front && i <= rear);
                } else {
                    isActive = (i >= front || i <= rear);
                }
            }
            
            boolean isFront = (!isEmpty && i == front);
            boolean isRear = (!isEmpty && i == rear);
            
            String val = isActive ? String.valueOf(elements[i]) : "";
            
            VBox cell = createQueueCell(val, i, isActive, isFront, isRear);
            queueContainer.getChildren().add(cell);
        }
    }

    private VBox createQueueCell(String valStr, int index, boolean isActive, boolean isFront, boolean isRear) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        
        String bgStyle = isActive ? "-fx-background-color: #34495e;" : "-fx-background-color: #ecf0f1;";
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
                "-fx-font-size: 18;" + // Enhanced text size
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

    private void logStep(String stepDescription) {
        Label l = new Label("• " + stepDescription);
        l.setStyle("-fx-text-fill: #34495e; -fx-font-size: 13;");
        l.setWrapText(true);
        Platform.runLater(() -> stepLogContainer.getChildren().add(l));
    }

    private void logResult(String msg) {
        Platform.runLater(() -> outputLabel.setText(msg));
    }

        private void showAlert(String title, String message) {
        if (title != null && (title.toLowerCase().contains("complete") || title.toLowerCase().contains("ready") || title.toLowerCase().contains("custom mode"))) {
            com.AlgoVista.utils.CustomAlert.showInfo(title, message);
        } else {
            com.AlgoVista.utils.CustomAlert.showError(title, message);
        }
    }

    @FXML
    private void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) queueContainer.getScene().getWindow();
            double width = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
