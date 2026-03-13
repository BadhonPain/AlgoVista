package com.AlgoVista.linkedlist.doubly;

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

public class DoublyLinkedListController {

    @FXML private Spinner<Integer> sizeSpinner;
    @FXML private TextField valueInput;
    @FXML private TextField indexInput;
    @FXML private FlowPane listContainer;
    @FXML private Label outputLabel;
    @FXML private VBox complexitiesContainer;

    private DoublyLinkedListModel model;

    @FXML
    public void initialize() {
        model = new DoublyLinkedListModel();
        
        // Setup initial spinner configuration
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 5));
        
        setupComplexities();
        updateVisualization();
        logMessage("Ready. Use the left panel to manipulate the Doubly Linked List.");
    }

    private void setupComplexities() {
        addComplexityRow("Insert at Head", "Add new node at beginning", "O(1)");
        addComplexityRow("Insert at Tail", "Add new node at end", "O(1)"); // Tail pointer maintained
        addComplexityRow("Insert at Position", "Traverse to a position and insert node", "O(n)");
        addComplexityRow("Delete at Head", "Remove first node", "O(1)");
        addComplexityRow("Delete at Tail", "Remove last node", "O(1)"); // Tail pointer maintained
        addComplexityRow("Delete by Value", "Search node by value and remove it", "O(n)");
        addComplexityRow("Delete at Position", "Traverse to a position and remove node", "O(n)");
        addComplexityRow("Search", "Find node by value", "O(n)");
        addComplexityRow("Access by Index", "Move to a position", "O(n)");
        addComplexityRow("Traverse Forward", "Visit nodes from head to tail", "O(n)");
        addComplexityRow("Traverse Backward", "Visit nodes from tail to head", "O(n)");
        addComplexityRow("Reverse List", "Reverse prev/next links", "O(n)");
        addComplexityRow("Space Complexity", "Extra auxiliary space", "O(1)"); // In-place operations
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
    private void prepend() {
        Integer val = getInputValue();
        if (val == null) return;
        
        model.prepend(val);
        updateVisualization();
        logMessage("Prepended " + val + " to the list.");
        valueInput.clear();
    }

    @FXML
    private void append() {
        Integer val = getInputValue();
        if (val == null) return;
        
        model.append(val);
        updateVisualization();
        logMessage("Appended " + val + " to the list.");
        valueInput.clear();
    }

    @FXML
    private void insertAt() {
        Integer val = getInputValue();
        Integer idx = getInputIndex();
        if (val == null || idx == null) return;
        
        boolean success = model.insertAt(idx, val);
        if (success) {
            updateVisualization();
            logMessage("Inserted " + val + " at index " + idx + ".");
            valueInput.clear();
            indexInput.clear();
        } else {
            showAlert("Invalid Index", "Cannot insert at index " + idx + ". Index out of bounds.");
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
            showAlert("Value Not Found", "The value " + val + " was not found in the list.");
        }
    }

    @FXML
    private void deleteAt() {
        Integer idx = getInputIndex();
        if (idx == null) return;
        
        boolean success = model.deleteAt(idx);
        if (success) {
            updateVisualization();
            logMessage("Deleted node at index " + idx + ".");
            indexInput.clear();
        } else {
            showAlert("Invalid Index", "Cannot delete at index " + idx + ". Index out of bounds.");
        }
    }

    @FXML
    private void search() {
        Integer val = getInputValue();
        if (val == null) return;
        
        int idx = model.search(val);
        if (idx != -1) {
            logMessage("Value " + val + " found at index " + idx + ".");
            highlightNode(idx);
        } else {
            logMessage("Value " + val + " not found in the list.");
            showAlert("Not Found", "Value " + val + " is not present in the list.");
        }
    }

    @FXML
    private void traverseForward() {
        if (model.isEmpty()) {
            logMessage("List is empty.");
        } else {
            String result = model.traverseForward();
            logMessage("Forward Traversal:\n" + result);
        }
    }

    @FXML
    private void traverseBackward() {
        if (model.isEmpty()) {
            logMessage("List is empty.");
        } else {
            String result = model.traverseBackward();
            logMessage("Backward Traversal:\n" + result);
        }
    }

    @FXML
    private void reverse() {
        if (model.isEmpty()) {
            showAlert("Empty List", "Cannot reverse an empty list.");
            return;
        }
        model.reverse();
        updateVisualization();
        logMessage("Reversed the doubly linked list.");
    }

    @FXML
    private void generateList() {
        int size = sizeSpinner.getValue();
        model.generateSample(size, 1, 99);
        updateVisualization();
        logMessage("Generated a random list of size " + size + ".");
    }

    @FXML
    private void clear() {
        model.clear();
        updateVisualization();
        logMessage("Cleared the list.");
    }

    private void updateVisualization() {
        listContainer.getChildren().clear();

        DoublyNode current = model.getHead();
        int index = 0;

        if (current == null) {
            Label emptyLbl = new Label("List is empty");
            emptyLbl.setStyle("-fx-font-size: 18; -fx-text-fill: #95a5a6;");
            listContainer.getChildren().add(emptyLbl);
            return;
        }

        // Add 'null' at the beginning
        Label nullStartLabel = new Label("null");
        nullStartLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        nullStartLabel.setTranslateY(20);

        Label arrowFromNull = new Label("↔");
        arrowFromNull.setStyle("-fx-font-size: 24; -fx-text-fill: #34495e; -fx-font-weight: bold;");
        arrowFromNull.setTranslateY(15);

        listContainer.getChildren().addAll(nullStartLabel, arrowFromNull);

        while (current != null) {
            // Create Node Box
            VBox nodeBox = createNodeBox(current.getValue(), index);
            listContainer.getChildren().add(nodeBox);

            // Create Arrow Double-Sided
            if (current.getNext() != null) {
                Label arrow = new Label("↔");
                arrow.setStyle("-fx-font-size: 24; -fx-text-fill: #34495e; -fx-font-weight: bold;");
                arrow.setTranslateY(15);
                listContainer.getChildren().add(arrow);
            }

            current = current.getNext();
            index++;
        }

        // Add 'null' at the end
        Label arrowToNull = new Label("↔");
        arrowToNull.setStyle("-fx-font-size: 24; -fx-text-fill: #34495e; -fx-font-weight: bold;");
        arrowToNull.setTranslateY(15);
        
        Label nullEndLabel = new Label("null");
        nullEndLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        nullEndLabel.setTranslateY(20);

        listContainer.getChildren().addAll(arrowToNull, nullEndLabel);
    }

    private VBox createNodeBox(int value, int index) {
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);
        
        // Data Box (Top)
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.setStyle(
                "-fx-background-color: #f39c12;" +
                "-fx-border-color: #2c3e50;" +
                "-fx-border-width: 2 2 0 2;" +
                "-fx-min-width: 50;" +
                "-fx-max-width: 50;" +
                "-fx-min-height: 40;" +
                "-fx-max-height: 40;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 16;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: black;"
        );

        // Index Box (Bottom)
        Label indexLabel = new Label(String.valueOf(index));
        indexLabel.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #2c3e50;" +
                "-fx-border-width: 2;" +
                "-fx-min-width: 50;" +
                "-fx-max-width: 50;" +
                "-fx-min-height: 20;" +
                "-fx-max-height: 20;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 11;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #c0392b;"
        );

        container.getChildren().addAll(valueLabel, indexLabel);
        return container;
    }

    private void highlightNode(int index) {
        // Compute actual child index (null + arrow + node0 + arrow + node1...)
        // node 0 -> child 2
        // node 1 -> child 4
        int childIndex = 2 + (index * 2);
        if (childIndex < listContainer.getChildren().size()) {
            VBox nodeContainer = (VBox) listContainer.getChildren().get(childIndex);
            Label valueLabel = (Label) nodeContainer.getChildren().get(0);

            // Pop animation and color change
            ScaleTransition st = new ScaleTransition(Duration.millis(300), nodeContainer);
            st.setFromX(1.0); st.setToX(1.3);
            st.setFromY(1.0); st.setToY(1.3);
            st.setCycleCount(2);
            st.setAutoReverse(true);
            st.play();

            String oldStyle = valueLabel.getStyle();
            valueLabel.setStyle(oldStyle.replace("-fx-background-color: #f39c12;", "-fx-background-color: #2ecc71;"));
            
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LinkedListMenu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) listContainer.getScene().getWindow();
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
            System.err.println("Could not load fxml from /fxml/LinkedListMenu.fxml");
        }
    }
}
