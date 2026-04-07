package com.AlgoVista.stack;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class StackController {

    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private TextField valueInput;
    @FXML private VBox stackContainer;
    @FXML private VBox stepLogContainer;
    @FXML private Label outputLabel;
    @FXML private VBox complexitiesContainer;

    private StackModel model;
    private boolean isAnimating = false;

    @FXML
    public void initialize() {
        model = new StackModel(8); // Default capacity 8
        capacitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 8));
        
        setupComplexities();
        renderStackInstantly();
        logResult("Stack Visualizer initialized. Ready for simulation.");
    }

    private void setupComplexities() {
        addComplexityRow("Push", "Insert an element at the top of the stack", "O(1)");
        addComplexityRow("Pop", "Remove the top element", "O(1)");
        addComplexityRow("Peek / Top", "Access the top element without removing it", "O(1)");
        addComplexityRow("Is Empty", "Check whether the stack has no elements", "O(1)");
        addComplexityRow("Is Full", "Check whether the stack reached capacity", "O(1)");
        addComplexityRow("Size", "Return the current number of elements", "O(1)");
        addComplexityRow("Search", "Scan stack elements to find a value", "O(n)");
        addComplexityRow("Traverse / Display", "Visit and display all elements", "O(n)");
        addComplexityRow("Clear", "Remove all elements", "O(n)");
        addComplexityRow("Generate Random Stack", "Fill stack with multiple random values", "O(n)");
        addComplexityRow("Space Complexity", "Memory used to store stack elements", "O(n)");
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
    private void createStack() {
        if (isAnimating) return;
        int cap = capacitySpinner.getValue();
        model = new StackModel(cap);
        stepLogContainer.getChildren().clear();
        logStep("Created empty stack with capacity " + cap);
        renderStackInstantly();
        logResult("New stack created successfully.");
    }

    @FXML
    private void generateRandom() {
        if (isAnimating) return;
        int cap = capacitySpinner.getValue();
        model.generateSample(cap, 1, 99);
        stepLogContainer.getChildren().clear();
        logStep("Generated random elements up to capacity " + cap);
        renderStackInstantly();
        logResult("Random stack generated successfully.");
    }

    @FXML
    private void push() {
        if (isAnimating) return;
        Integer val = getInputValue();
        if (val == null) return;

        stepLogContainer.getChildren().clear();
        logStep("PUSH OP initiating for value " + val + "...");
        
        isAnimating = true;

        new Thread(() -> {
            try {
                Thread.sleep((long)((800) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                logStep("Step 1: Checking if stack is full...");
                
                if (model.isFull()) {
                    logStep("Stack is full! Cannot push.");
                    logResult("Push failed. Stack Overflow condition.");
                    showAlert("Stack Overflow", "Cannot push, the stack is full.");
                    return;
                }
                
                logStep("Stack is not full. Proceeding.");
                Thread.sleep((long)((1000) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                
                boolean success = model.push(val);
                if (success) {
                    logStep("Step 2: Incrementing TOP pointer and inserting value " + val);
                    Platform.runLater(this::renderStackInstantly); // Redraw with new element
                    
                    Platform.runLater(() -> {
                        int indexInVBox = model.getCapacity() - 1 - model.getTopIndex();
                        if (indexInVBox >= 0 && indexInVBox < stackContainer.getChildren().size()) {
                            HBox row = (HBox) stackContainer.getChildren().get(indexInVBox);
                            VBox cellBox = (VBox) row.getChildren().get(1);
                            Label dataLabel = (Label) cellBox.getChildren().get(0);
                            String oldStyle = dataLabel.getStyle();
                            dataLabel.setStyle(oldStyle.replace("-fx-background-color: #34495e;", "-fx-background-color: #2ecc71;"));
                            
                            PauseTransition restore = new PauseTransition(Duration.millis(800));
                            restore.setOnFinished(ev -> dataLabel.setStyle(oldStyle));
                            restore.play();
                        }
                    });
                    
                    logStep("Step 3: Pushed value successfully.");
                    logResult("Pushed " + val + " onto the stack.");
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
    private void pop() {
        if (isAnimating) return;
        
        stepLogContainer.getChildren().clear();
        logStep("POP OP initiating...");
        isAnimating = true;

        new Thread(() -> {
            try {
                Thread.sleep((long)((800) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                logStep("Step 1: Checking if stack is empty...");
                
                if (model.isEmpty()) {
                    logStep("Stack is empty! Cannot pop.");
                    logResult("Pop failed. Stack Underflow condition.");
                    showAlert("Stack Underflow", "Cannot pop, the stack is empty.");
                    return;
                }
                
                logStep("Stack has elements. Proceeding.");
                
                Platform.runLater(() -> {
                    int indexInVBox = model.getCapacity() - 1 - model.getTopIndex();
                    if (indexInVBox >= 0 && indexInVBox < stackContainer.getChildren().size()) {
                        HBox row = (HBox) stackContainer.getChildren().get(indexInVBox);
                        VBox cellBox = (VBox) row.getChildren().get(1);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        dataLabel.setStyle(dataLabel.getStyle().replace("-fx-background-color: #34495e;", "-fx-background-color: #e74c3c;"));
                        logStep("Step 2: Identifying current TOP element (" + model.peek() + ") for removal.");
                    }
                });
                
                Thread.sleep((long)((1000) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                if (model.isEmpty()) return;
                
                Integer popped = model.pop();
                logStep("Step 3: Value " + popped + " removed. TOP pointer decremented.");
                Platform.runLater(this::renderStackInstantly);
                logResult("Popped " + popped + " from the stack.");
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
            logStep("Stack is empty. No top element.");
            logResult("Peek failed: Stack is empty.");
            return;
        }

        isAnimating = true;
        
        new Thread(() -> {
            try {
                logStep("Accessing top element pointer...");
                
                String[] savedStyle = {null};
                
                Platform.runLater(() -> {
                    int indexInVBox = model.getCapacity() - 1 - model.getTopIndex();
                    if (indexInVBox >= 0 && indexInVBox < stackContainer.getChildren().size()) {
                        HBox row = (HBox) stackContainer.getChildren().get(indexInVBox);
                        VBox cellBox = (VBox) row.getChildren().get(1);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        
                        savedStyle[0] = dataLabel.getStyle();
                        dataLabel.setStyle(savedStyle[0].replace("-fx-background-color: #34495e;", "-fx-background-color: #9b59b6;"));
                    }
                });
                
                Thread.sleep((long)((1200) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                
                Platform.runLater(() -> {
                    int indexInVBox = model.getCapacity() - 1 - model.getTopIndex();
                    if (indexInVBox >= 0 && indexInVBox < stackContainer.getChildren().size()) {
                        HBox row = (HBox) stackContainer.getChildren().get(indexInVBox);
                        VBox cellBox = (VBox) row.getChildren().get(1);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        if(savedStyle[0] != null) dataLabel.setStyle(savedStyle[0]);
                    }
                    logStep("Top element is " + model.peek() + ".");
                    logResult("Top element is " + model.peek());
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
            logStep("Stack is empty. Search termination immediately.");
            logResult("Value not found (Empty Stack).");
            return;
        }

        isAnimating = true;
        
        new Thread(() -> {
            try {
                int[] elements = model.getElements();
                int top = model.getTopIndex();
                boolean[] foundTracker = {false};

                for (int i = top; i >= 0; i--) {
                    if (foundTracker[0]) break;
                    
                    final int currIndex = i;
                    Thread.sleep((long)((800) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                    
                    logStep("Checking index " + currIndex + " from bottom (Value: " + elements[currIndex] + ")");
                    
                    Platform.runLater(() -> {
                        int vBoxIdx = model.getCapacity() - 1 - currIndex;
                        HBox row = (HBox) stackContainer.getChildren().get(vBoxIdx);
                        VBox cellBox = (VBox) row.getChildren().get(1);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        String oldStyle = dataLabel.getStyle();
                        
                        dataLabel.setStyle(oldStyle.replace("-fx-background-color: #34495e;", "-fx-background-color: #f1c40f;")); // yellow
                        
                        if (elements[currIndex] == target) {
                            foundTracker[0] = true;
                            logStep("Match found! Distance from top = " + (top - currIndex + 1));
                            logResult("Found value " + target + " at logical index " + currIndex + ".");
                            PauseTransition end = new PauseTransition(Duration.millis(1500));
                            end.setOnFinished(ev -> dataLabel.setStyle(oldStyle));
                            end.play();
                        } else {
                            PauseTransition end = new PauseTransition(Duration.millis(300));
                            end.setOnFinished(ev -> dataLabel.setStyle(oldStyle));
                            end.play();
                        }
                    });
                    
                    // Wait out the visual highlight phase
                    if (elements[currIndex] == target) {
                        Thread.sleep((long)((1500) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                    } else {
                        Thread.sleep((long)((300) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                    }
                }
                
                Thread.sleep((long)((200) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                if (!foundTracker[0]) {
                    logStep("Reached bottom. Element not found.");
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
            logStep("Empty Stack.");
            logResult("Stack is empty.");
            return;
        }
        
        isAnimating = true;

        new Thread(() -> {
            try {
                int[] elements = model.getElements();
                int top = model.getTopIndex();
                
                logStep("Traversing from TOP down to BOTTOM...");
                
                for (int i = top; i >= 0; i--) {
                    final int currIndex = i;
                    Thread.sleep((long)((800) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()));
                    
                    logStep("Visiting element at index " + currIndex + " (Value: " + elements[currIndex] + ")");
                    
                    Platform.runLater(() -> {
                        int vBoxIdx = model.getCapacity() - 1 - currIndex;
                        HBox row = (HBox) stackContainer.getChildren().get(vBoxIdx);
                        VBox cellBox = (VBox) row.getChildren().get(1);
                        Label dataLabel = (Label) cellBox.getChildren().get(0);
                        String oldStyle = dataLabel.getStyle();
                        
                        dataLabel.setStyle(oldStyle.replace("-fx-background-color: #34495e;", "-fx-background-color: #9b59b6;")); // purple
                        
                        PauseTransition end = new PauseTransition(Duration.millis(800));
                        end.setOnFinished(ev -> dataLabel.setStyle(oldStyle));
                        end.play();
                    });
                    
                    Thread.sleep((long)((1000) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier())); // Wait for highlight to mostly finish
                }
                
                logStep("Traversal complete.");
                logResult("Traversing from TOP to BOTTOM:\n" + model.traverse());
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
        logResult("Is Stack Empty? -> " + String.valueOf(empty).toUpperCase());
    }

    @FXML
    private void checkFull() {
        if (isAnimating) return;
        stepLogContainer.getChildren().clear();
        boolean full = model.isFull();
        logStep("Checked isFull(). Result: " + full);
        logResult("Is Stack Full? -> " + String.valueOf(full).toUpperCase());
    }

    @FXML
    private void getSize() {
        if (isAnimating) return;
        stepLogContainer.getChildren().clear();
        int size = model.size();
        logStep("Top pointer is at index " + model.getTopIndex() + ". Size is Top+1.");
        logResult("Current Size: " + size + " / " + model.getCapacity());
    }

    @FXML
    private void clear() {
        if (isAnimating) return;
        stepLogContainer.getChildren().clear();
        logStep("CLEAR OP Executed.");
        model.clear();
        renderStackInstantly();
        logResult("Stack fully cleared.");
    }

    // --- Visualization & Helpers ---

    private void renderStackInstantly() {
        stackContainer.getChildren().clear();
        
        int capacity = model.getCapacity();
        int[] elements = model.getElements();
        int top = model.getTopIndex();

        // VBox renders from top to bottom child-wise.
        // Array index capacities: top of container is index (capacity - 1), bottom is index 0.
        for (int i = capacity - 1; i >= 0; i--) {
            boolean isActive = (i <= top);
            boolean isTop = (i == top);
            String val = isActive ? String.valueOf(elements[i]) : "";
            
            HBox row = createStackCell(val, i, isActive, isTop);
            stackContainer.getChildren().add(row);
        }
    }

    private HBox createStackCell(String valStr, int index, boolean isActive, boolean isTop) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER);

        // Optional Left space padding or pointer (TOP marker)
        Label topMarker = new Label(isTop ? "TOP →" : "     ");
        topMarker.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c; -fx-min-width: 45;");

        // Main Box
        VBox box = new VBox(0);
        box.setAlignment(Pos.CENTER);
        
        String bgStyle = isActive ? "-fx-background-color: #34495e;" : "-fx-background-color: #ecf0f1;";
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

        // Right side index marker cleanly integrated
        Label idxMarker = new Label("[" + index + "]");
        idxMarker.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13; -fx-min-width: 25;");
        
        row.getChildren().addAll(topMarker, box, idxMarker);
        return row;
    }

    private void logStep(String stepDescription) {
        Label l = new Label("• " + stepDescription);
        l.setStyle("-fx-text-fill: #34495e; -fx-font-size: 15;");
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

            Stage stage = (Stage) stackContainer.getScene().getWindow();
            double width = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
