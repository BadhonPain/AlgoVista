package com.AlgoVista.array;

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

public class ArrayController {

    @FXML private Spinner<Integer> sizeSpinner;
    @FXML private TextField valueInput;
    @FXML private TextField indexInput;
    @FXML private FlowPane arrayContainer;
    @FXML private Label outputLabel;
    @FXML private VBox complexitiesContainer;

    private ArrayModel model;

    @FXML
    public void initialize() {
        // Initialize with default capacity 10
        model = new ArrayModel(10);
        
        // Setup initial spinner configuration (1 to 50, default 5)
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 5));
        
        setupComplexities();
        updateVisualization();
        logMessage("Array Visualizer initialized. Use the left panel to manipulate the array.");
    }

    private void setupComplexities() {
        addComplexityRow("Access by Index", "Get element directly by index", "O(1)");
        addComplexityRow("Update by Index", "Replace value at a specific index", "O(1)");
        addComplexityRow("Search", "Find an element by value using linear search", "O(n)");
        addComplexityRow("Traverse", "Visit each element in order", "O(n)");
        addComplexityRow("Insert at End", "Add element at the end (O(n) if resize needed)", "O(1)");
        addComplexityRow("Insert at Index", "Insert element and shift remaining elements right", "O(n)");
        addComplexityRow("Delete at Index", "Remove element and shift remaining elements left", "O(n)");
        addComplexityRow("Delete by Value", "Search for the value, then remove it", "O(n)");
        addComplexityRow("Reverse", "Reverse the array order", "O(n)");
        addComplexityRow("Sort (Bubble)", "Sort array elements using Bubble Sort", "O(n²)");
        addComplexityRow("Space Complexity", "Extra auxiliary space", "O(1)");
    }

    private void addComplexityRow(String operation, String description, String complexity) {
        HBox row = new HBox(10);
        row.getStyleClass().add("complexity-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox textContainer = new VBox(2);
        javafx.scene.layout.HBox.setHgrow(textContainer, javafx.scene.layout.Priority.ALWAYS);
        
        Label nameLbl = new Label(operation);
        nameLbl.getStyleClass().add("complexity-op-name");
        
        Label descLbl = new Label(description);
        descLbl.getStyleClass().add("complexity-op-desc");
        descLbl.setWrapText(true);

        textContainer.getChildren().addAll(nameLbl, descLbl);

        Label badge = new Label(complexity);
        if (complexity.equals("O(1)") || complexity.startsWith("O(1)")) {
            badge.getStyleClass().add("complexity-badge-o1");
        } else if (complexity.contains("n²")) {
            // Reusing amber badge class but with custom inline color for O(n^2) red
            badge.getStyleClass().add("complexity-badge-on");
            badge.setStyle("-fx-background-color: rgba(231, 76, 60, 0.2); -fx-text-fill: #e74c3c; -fx-border-color: #c0392b;");
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
        int size = sizeSpinner.getValue();
        model = new ArrayModel(size); // Reinitialize with new capacity
        updateVisualization();
        logMessage("Created empty array with capacity " + size + ".");
    }

    @FXML
    private void generateRandom() {
        int size = sizeSpinner.getValue();
        model.generateSample(size, 1, 99);
        updateVisualization();
        logMessage("Generated a random array of size " + size + ".");
    }

    @FXML
    private void insertAtEnd() {
        Integer val = getInputValue();
        if (val == null) return;
        
        boolean success = model.insertAtEnd(val);
        if (success) {
            updateVisualization();
            logMessage("Inserted " + val + " at the end (index " + (model.getSize() - 1) + ").");
            highlightNode(model.getSize() - 1, "#2ecc71"); // Green for insertion
            valueInput.clear();
        } else {
            showAlert("Capacity Reached", "Cannot insert, array is full.");
        }
    }

    @FXML
    private void insertAtIndex() {
        Integer val = getInputValue();
        Integer idx = getInputIndex();
        if (val == null || idx == null) return;
        
        boolean success = model.insertAtIndex(idx, val);
        if (success) {
            updateVisualization();
            logMessage("Inserted " + val + " at index " + idx + ".");
            highlightNode(idx, "#2ecc71");
            valueInput.clear();
            indexInput.clear();
        } else {
            showAlert("Invalid Index", "Cannot insert at index " + idx + ". Valid range: 0 to " + model.getSize());
        }
    }

    @FXML
    private void updateValue() {
        Integer val = getInputValue();
        Integer idx = getInputIndex();
        if (val == null || idx == null) return;
        
        boolean success = model.updateValue(idx, val);
        if (success) {
            updateVisualization();
            logMessage("Updated index " + idx + " to new value " + val + ".");
            highlightNode(idx, "#3498db"); // Blue for update
            valueInput.clear();
            indexInput.clear();
        } else {
            showAlert("Invalid Index", "Cannot update at index " + idx + ". Out of bounds.");
        }
    }

    @FXML
    private void deleteAtIndex() {
        Integer idx = getInputIndex();
        if (idx == null) return;

        // Store value for logging
        int[] elements = model.getElements();
        String removedVal = "";
        if (idx >= 0 && idx < model.getSize()) {
            removedVal = String.valueOf(elements[idx]);
        }
        
        boolean success = model.deleteAtIndex(idx);
        if (success) {
            updateVisualization();
            logMessage("Deleted value " + removedVal + " at index " + idx + ".");
            indexInput.clear();
        } else {
            showAlert("Invalid Index", "Cannot delete at index " + idx + ". Out of bounds.");
        }
    }

    @FXML
    private void deleteByValue() {
        Integer val = getInputValue();
        if (val == null) return;
        
        boolean success = model.deleteByValue(val);
        if (success) {
            updateVisualization();
            logMessage("Deleted first occurrence of value " + val + ".");
            valueInput.clear();
        } else {
            showAlert("Value Not Found", "The value " + val + " was not found in the array. Deletion failed.");
        }
    }

    @FXML
    private void search() {
        Integer val = getInputValue();
        if (val == null) return;
        
        int idx = model.search(val);
        if (idx != -1) {
            logMessage("Value " + val + " found at index " + idx + ".");
            highlightNode(idx, "#f1c40f"); // Yellow for search
        } else {
            logMessage("Value " + val + " not found in the array.");
            showAlert("Not Found", "Value " + val + " is not present in the array.");
        }
    }

    @FXML
    private void traverse() {
        if (model.isEmpty()) {
            logMessage("Array is empty.");
        } else {
            String result = model.traverse();
            logMessage("Traversal Result:\n" + result);
        }
    }

    @FXML
    private void reverse() {
        if (model.isEmpty()) {
            showAlert("Empty Array", "Cannot reverse an empty array.");
            return;
        }
        model.reverse();
        updateVisualization();
        logMessage("Reversed the array elements.");
    }

    @FXML
    private void sort() {
        if (model.isEmpty()) {
            showAlert("Empty Array", "Cannot sort an empty array.");
            return;
        }
        model.sort();
        updateVisualization();
        logMessage("Sorted the array using Bubble Sort.");
    }

    @FXML
    private void clear() {
        model.clear();
        updateVisualization();
        logMessage("Cleared the array.");
    }

    private void updateVisualization() {
        arrayContainer.getChildren().clear();

        if (model.isEmpty() && model.getCapacity() == 0) {
            Label emptyLbl = new Label("Array is empty/uninitialized");
            emptyLbl.setStyle("-fx-font-size: 18; -fx-text-fill: #95a5a6;");
            arrayContainer.getChildren().add(emptyLbl);
            return;
        }

        int[] currentElements = model.getElements();
        
        // Show current elements
        for (int i = 0; i < currentElements.length; i++) {
            VBox nodeBox = createArrayBox(String.valueOf(currentElements[i]), i, false);
            arrayContainer.getChildren().add(nodeBox);
        }

        // Show empty slots up to capacity (optional but educational)
        for (int i = currentElements.length; i < model.getCapacity(); i++) {
            VBox emptyBox = createArrayBox("", i, true);
            arrayContainer.getChildren().add(emptyBox);
        }
    }

    private VBox createArrayBox(String value, int index, boolean isEmptySlot) {
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);
        
        // Data Box (Top)
        Label valueLabel = new Label(value);
        String bgStyle = isEmptySlot ? "-fx-background-color: #ecf0f1;" : "-fx-background-color: #9b59b6;";
        String textFill = isEmptySlot ? "transparent" : "white";
        
        valueLabel.setStyle(
                bgStyle +
                "-fx-border-color: #2c3e50;" +
                "-fx-border-width: 2 2 0 2;" +
                "-fx-min-width: 55;" +
                "-fx-max-width: 55;" +
                "-fx-min-height: 55;" +
                "-fx-max-height: 55;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 18;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + textFill + ";"
        );

        // Index Box (Bottom)
        Label indexLabel = new Label(String.valueOf(index));
        indexLabel.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #2c3e50;" +
                "-fx-border-width: 2;" +
                "-fx-min-width: 55;" +
                "-fx-max-width: 55;" +
                "-fx-min-height: 20;" +
                "-fx-max-height: 20;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 11;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #e74c3c;"
        );

        container.getChildren().addAll(valueLabel, indexLabel);
        return container;
    }

    private void highlightNode(int index, String hexColor) {
        if (index < arrayContainer.getChildren().size()) {
            VBox nodeContainer = (VBox) arrayContainer.getChildren().get(index);
            Label valueLabel = (Label) nodeContainer.getChildren().get(0);

            // Pop animation and color change
            ScaleTransition st = new ScaleTransition(Duration.millis(300), nodeContainer);
            st.setFromX(1.0); st.setToX(1.15);
            st.setFromY(1.0); st.setToY(1.15);
            st.setCycleCount(2);
            st.setAutoReverse(true);
            st.play();

            String oldStyle = valueLabel.getStyle();
            valueLabel.setStyle(oldStyle.replace("-fx-background-color: #9b59b6;", "-fx-background-color: " + hexColor + ";"));
            
            // Restore after short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> valueLabel.setStyle(oldStyle));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void logMessage(String message) {
        outputLabel.setText(message);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) arrayContainer.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setX(x);
            stage.setY(y);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load fxml from /fxml/dashboard.fxml");
        }
    }
}
