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
    @FXML private VBox stepLogContainer;

    private DoublyLinkedListModel model;
    private boolean isAnimating = false;

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
        if (isAnimating) return;
        Integer val = getInputValue();
        if (val == null) return;
        
        isAnimating = true;
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        logStep("PREPEND OP initiating...");

        new Thread(() -> {
            try {
                logStep("Creating new node with value [" + val + "]...");
                Thread.sleep(800);
                
                logStep("Setting new node's next pointer to current Head, and Head's prev to new node...");
                Thread.sleep(800);
                
                model.prepend(val);
                
                logStep("Updating Head pointer to the new node...");
                javafx.application.Platform.runLater(() -> updateVisualization());
                Thread.sleep(800);
                
                javafx.application.Platform.runLater(() -> highlightNode(0, "#2ecc71")); // green highlight
                Thread.sleep(600);
                
                logStep("Prepend complete.");
                logMessage("Prepended " + val + " to the list.");
            } catch (Exception e) {} 
            finally { 
                javafx.application.Platform.runLater(() -> valueInput.clear());
                isAnimating = false; 
            }
        }).start();
    }

    @FXML
    private void append() {
        if (isAnimating) return;
        Integer val = getInputValue();
        if (val == null) return;
        
        isAnimating = true;
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        logStep("APPEND OP initiating...");

        new Thread(() -> {
            try {
                logStep("Creating new node with value [" + val + "]...");
                Thread.sleep(800);
                
                logStep("Jumping to Tail pointer, or traversing if Tail not cached...");
                int size = 0;
                DoublyNode curr = model.getHead();
                while (curr != null) {
                    final int currIdx = size;
                    logStep("Passing index " + currIdx + "...");
                    javafx.application.Platform.runLater(() -> highlightNode(currIdx, "#3498db")); // blue
                    Thread.sleep(600);
                    curr = curr.getNext();
                    size++;
                }
                
                if (size > 0) logStep("Found Tail. Setting Tail's next to new node, and new node's prev to Tail...");
                else logStep("List is empty. Setting Head & Tail to new node...");
                Thread.sleep(800);
                
                model.append(val);
                
                javafx.application.Platform.runLater(() -> updateVisualization());
                Thread.sleep(500);
                
                int finalIdx = size; // new node is placed at size
                javafx.application.Platform.runLater(() -> highlightNode(finalIdx, "#2ecc71")); // green
                Thread.sleep(600);
                
                logStep("Append complete.");
                logMessage("Appended " + val + " to the list.");
            } catch (Exception e) {} 
            finally { 
                javafx.application.Platform.runLater(() -> valueInput.clear());
                isAnimating = false; 
            }
        }).start();
    }

    @FXML
    private void insertAt() {
        if (isAnimating) return;
        Integer val = getInputValue();
        Integer idx = getInputIndex();
        if (val == null || idx == null) return;
        
        isAnimating = true;
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        logStep("INSERT_AT OP initiating...");

        new Thread(() -> {
            try {
                logStep("Creating new node with value [" + val + "]...");
                Thread.sleep(800);
                
                logStep("Traversing list to target index " + idx + "...");
                int currIdx = 0;
                DoublyNode curr = model.getHead();
                while (curr != null && currIdx < idx - 1) {
                    final int cIdx = currIdx;
                    javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#3498db")); // blue
                    Thread.sleep(600);
                    curr = curr.getNext();
                    currIdx++;
                }
                
                if (idx > 0 && curr == null) {
                    logStep("Index out of bounds mapping.");
                    javafx.application.Platform.runLater(() -> showAlert("Invalid Index", "Cannot insert at index " + idx + "."));
                } else {
                    logStep("Found insertion point. Rewiring prev and next pointers...");
                    Thread.sleep(800);
                    
                    boolean success = model.insertAt(idx, val);
                    if (success) {
                        javafx.application.Platform.runLater(() -> updateVisualization());
                        Thread.sleep(500);
                        javafx.application.Platform.runLater(() -> highlightNode(idx, "#2ecc71"));
                        logStep("Insert complete.");
                        logMessage("Inserted " + val + " at index " + idx + ".");
                    }
                }
            } catch (Exception e) {} 
            finally { 
                javafx.application.Platform.runLater(() -> {
                    valueInput.clear();
                    indexInput.clear();
                });
                isAnimating = false; 
            }
        }).start();
    }

    @FXML
    private void deleteByValue() {
        if (isAnimating) return;
        Integer val = getInputValue();
        if (val == null) return;
        
        isAnimating = true;
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        logStep("DELETE_BY_VALUE OP initiating...");

        new Thread(() -> {
            try {
                logStep("Searching for value [" + val + "]...");
                int currIdx = 0;
                DoublyNode curr = model.getHead();
                boolean found = false;
                
                while (curr != null) {
                    final int cIdx = currIdx;
                    logStep("Checking index " + currIdx + "...");
                    javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#3498db")); // blue
                    Thread.sleep(800);
                    
                    if (curr.getValue() == val) {
                        found = true;
                        logStep("Found value at index " + currIdx + "! Marking for deletion...");
                        javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#e74c3c")); // red
                        Thread.sleep(1000);
                        break;
                    }
                    curr = curr.getNext();
                    currIdx++;
                }
                
                if (found) {
                    logStep("Bypassing the node's prev and next pointers...");
                    model.deleteByValue(val);
                    Thread.sleep(800);
                    javafx.application.Platform.runLater(() -> updateVisualization());
                    logStep("Deletion complete.");
                    logMessage("Deleted first occurrence of value " + val + ".");
                } else {
                    logStep("Value not found in the list.");
                    javafx.application.Platform.runLater(() -> showAlert("Not Found", "Value " + val + " not found."));
                }
            } catch (Exception e) {} 
            finally { 
                javafx.application.Platform.runLater(() -> valueInput.clear());
                isAnimating = false; 
            }
        }).start();
    }

    @FXML
    private void deleteAt() {
        if (isAnimating) return;
        Integer idx = getInputIndex();
        if (idx == null) return;
        
        isAnimating = true;
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        logStep("DELETE_AT OP initiating...");

        new Thread(() -> {
            try {
                logStep("Traversing to index " + idx + "...");
                int currIdx = 0;
                DoublyNode curr = model.getHead();
                boolean found = false;
                
                while (curr != null && currIdx <= idx) {
                    final int cIdx = currIdx;
                    if (currIdx == idx) {
                        found = true;
                        logStep("Reached node at index " + idx + ". Marking for deletion.");
                        javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#e74c3c")); // red
                        Thread.sleep(1000);
                        break;
                    } else {
                        javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#3498db")); // blue
                        Thread.sleep(600);
                    }
                    curr = curr.getNext();
                    currIdx++;
                }

                if (found) {
                    logStep("Bypassing the node's prev and next pointers...");
                    model.deleteAt(idx);
                    Thread.sleep(800);
                    javafx.application.Platform.runLater(() -> updateVisualization());
                    logStep("Deletion complete.");
                    logMessage("Deleted node at index " + idx + ".");
                } else {
                    logStep("Index out of bounds.");
                    javafx.application.Platform.runLater(() -> showAlert("Invalid Index", "Cannot delete at index " + idx));
                }
            } catch (Exception e) {} 
            finally { 
                javafx.application.Platform.runLater(() -> indexInput.clear());
                isAnimating = false; 
            }
        }).start();
    }

    @FXML
    private void search() {
        if (isAnimating) return;
        Integer val = getInputValue();
        if (val == null) return;
        
        isAnimating = true;
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        logStep("SEARCH OP initiating...");

        new Thread(() -> {
            try {
                logStep("Searching for value [" + val + "]...");
                int currIdx = 0;
                DoublyNode curr = model.getHead();
                boolean found = false;
                
                while (curr != null) {
                    final int cIdx = currIdx;
                    logStep("Checking index " + currIdx + "...");
                    javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#3498db")); // blue
                    Thread.sleep(800);
                    
                    if (curr.getValue() == val) {
                        found = true;
                        logStep("Match found at index " + currIdx + "!!");
                        javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#2ecc71")); // green
                        Thread.sleep(1000);
                        break;
                    }
                    curr = curr.getNext();
                    currIdx++;
                }

                if (!found) {
                    logStep("Reached end of list. Value not found.");
                    javafx.application.Platform.runLater(() -> showAlert("Not Found", "Value " + val + " is not present."));
                }
            } catch (Exception e) {} 
            finally { 
                isAnimating = false; 
                javafx.application.Platform.runLater(() -> valueInput.clear());
            }
        }).start();
    }

    @FXML
    private void traverseForward() {
        if (isAnimating) return;
        if (model.isEmpty()) {
            logMessage("List is empty.");
            return;
        }

        isAnimating = true;
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        logStep("TRAVERSE FORWARD OP initiating...");

        new Thread(() -> {
            try {
                int currIdx = 0;
                DoublyNode curr = model.getHead();
                
                while (curr != null) {
                    final int cIdx = currIdx;
                    final int cVal = curr.getValue();
                    logStep("Visiting Node " + cIdx + " [Value: " + cVal + "]");
                    javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#9b59b6")); // purple
                    Thread.sleep(800);
                    
                    curr = curr.getNext();
                    currIdx++;
                }
                
                logStep("Reached end of list (null).");
                logMessage("Forward Traversal:\n" + model.traverseForward());
            } catch (Exception e) {} 
            finally { isAnimating = false; }
        }).start();
    }

    @FXML
    private void traverseBackward() {
        if (isAnimating) return;
        if (model.isEmpty()) {
            logMessage("List is empty.");
            return;
        }

        isAnimating = true;
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        logStep("TRAVERSE BACKWARD OP initiating...");

        new Thread(() -> {
            try {
                logStep("Jumping to Tail pointer...");
                DoublyNode curr = model.getHead();
                int size = 0;
                while(curr != null && curr.getNext() != null) { curr = curr.getNext(); size++; }
                
                int currIdx = size;
                
                while (curr != null) {
                    final int cIdx = currIdx;
                    final int cVal = curr.getValue();
                    logStep("Visiting Node " + cIdx + " [Value: " + cVal + "]");
                    javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#8e44ad")); // dark purple
                    Thread.sleep(800);
                    
                    curr = curr.getPrev();
                    currIdx--;
                }
                
                logStep("Reached head of list (null).");
                logMessage("Backward Traversal:\n" + model.traverseBackward());
            } catch (Exception e) {} 
            finally { isAnimating = false; }
        }).start();
    }

    @FXML
    private void reverse() {
        if (isAnimating) return;
        if (model.isEmpty()) {
            showAlert("Empty List", "Cannot reverse an empty list.");
            return;
        }
        
        isAnimating = true;
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        logStep("REVERSE OP initiating...");

        new Thread(() -> {
            try {
                logStep("Iterating through the list to swap prev and next pointers on every node...");
                Thread.sleep(1000);
                
                int size = 0;
                DoublyNode temp = model.getHead();
                while (temp != null) { size++; temp = temp.getNext(); }
                
                for(int i=0; i<size; i++) {
                    final int cIdx = i;
                    logStep("Swapping pointers for Node " + cIdx);
                    javafx.application.Platform.runLater(() -> highlightNode(cIdx, "#e67e22")); // orange
                    Thread.sleep(600);
                }
                
                logStep("Updating Head indicator to the previous Tail node...");
                model.reverse();
                Thread.sleep(800);
                
                javafx.application.Platform.runLater(() -> updateVisualization());
                logStep("Reversal complete. List is now backwards.");
                logMessage("Reversed the doubly linked list.");
            } catch (Exception e) {} 
            finally { isAnimating = false; }
        }).start();
    }

    @FXML
    private void generateList() {
        if (isAnimating) return;
        int size = sizeSpinner.getValue();
        model.generateSample(size, 1, 99);
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        updateVisualization();
        logMessage("Generated a random list of size " + size + ".");
    }

    @FXML
    private void clear() {
        if (isAnimating) return;
        model.clear();
        if (stepLogContainer != null) stepLogContainer.getChildren().clear();
        updateVisualization();
        logMessage("Cleared the list.");
    }

    private void updateVisualization() {
        listContainer.getChildren().clear();

        DoublyNode current = model.getHead();
        int index = 0;

        if (current == null) {
            Label emptyLbl = new Label("List is empty");
            emptyLbl.setStyle("-fx-font-size: 20; -fx-text-fill: #95a5a6;");
            listContainer.getChildren().add(emptyLbl);
            return;
        }

        // Add 'null' at the beginning
        Label nullStartLabel = new Label("null");
        nullStartLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        nullStartLabel.setTranslateY(20);

        Label arrowFromNull = new Label("↔");
        arrowFromNull.setStyle("-fx-font-size: 28; -fx-text-fill: #34495e; -fx-font-weight: bold;");
        arrowFromNull.setTranslateY(15);

        listContainer.getChildren().addAll(nullStartLabel, arrowFromNull);

        while (current != null) {
            // Create Node Box
            VBox nodeBox = createNodeBox(current.getValue(), index);
            listContainer.getChildren().add(nodeBox);

            // Create Arrow Double-Sided
            if (current.getNext() != null) {
                Label arrow = new Label("↔");
                arrow.setStyle("-fx-font-size: 28; -fx-text-fill: #34495e; -fx-font-weight: bold;");
                arrow.setTranslateY(15);
                listContainer.getChildren().add(arrow);
            }

            current = current.getNext();
            index++;
        }

        // Add 'null' at the end
        Label arrowToNull = new Label("↔");
        arrowToNull.setStyle("-fx-font-size: 28; -fx-text-fill: #34495e; -fx-font-weight: bold;");
        arrowToNull.setTranslateY(15);
        
        Label nullEndLabel = new Label("null");
        nullEndLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
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
                "-fx-font-size: 20;" +
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
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #c0392b;"
        );

        container.getChildren().addAll(valueLabel, indexLabel);
        return container;
    }

    private void highlightNode(int index, String hexColor) {
        int childIndex = 2 + (index * 2);
        if (childIndex < listContainer.getChildren().size()) {
            VBox nodeContainer = (VBox) listContainer.getChildren().get(childIndex);
            Label valueLabel = (Label) nodeContainer.getChildren().get(0);

            ScaleTransition st = new ScaleTransition(Duration.millis(300), nodeContainer);
            st.setFromX(1.0); st.setToX(1.3);
            st.setFromY(1.0); st.setToY(1.3);
            st.setCycleCount(2);
            st.setAutoReverse(true);
            st.play();

            String oldStyle = valueLabel.getStyle();
            valueLabel.setStyle(oldStyle.replaceAll("-fx-background-color: #[0-9a-fA-F]{6};", "-fx-background-color: " + hexColor + ";"));
            
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

    private void logStep(String stepDescription) {
        javafx.application.Platform.runLater(() -> {
            Label l = new Label("• " + stepDescription);
            l.setStyle("-fx-text-fill: #34495e; -fx-font-size: 17;");
            l.setWrapText(true);
            if (stepLogContainer != null) {
                stepLogContainer.getChildren().add(l);
            }
        });
    }

    private void logMessage(String message) {
        javafx.application.Platform.runLater(() -> outputLabel.setText(message));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LinkedListMenu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) listContainer.getScene().getWindow();
            double width = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();
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
