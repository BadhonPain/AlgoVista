package com.AlgoVista.graphs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GraphStructureController {
    @FXML private Canvas graphCanvas;
    @FXML private RadioButton rbUU, rbUW, rbDU, rbDW;
    @FXML private Spinner<Integer> spinnerNodes, spinnerEdges;
    @FXML private TableView<ObservableList<String>> matrixTable;
    @FXML private TableView<AdjListRow> adjListTable;
    @FXML private TableView<EdgeRow> edgeListTable;
    @FXML private Button finishCustomButton;
    private boolean customMode = false;
    private Integer selectedNode = null;
    private int customNodeCount = 0;

    private GraphModel graphModel;
    private GraphVisualizer visualizer;
    private ToggleGroup graphTypeGroup;

    public static class AdjListRow {
        private String node;
        private String neighbors;

        public AdjListRow(String node, String neighbors) {
            this.node = node;
            this.neighbors = neighbors;
        }

        public String getNode() { return node; }
        public String getNeighbors() { return neighbors; }
    }

    public static class EdgeRow {
        private String edge;
        private String from;
        private String to;

        public EdgeRow(String edge, String from, String to) {
            this.edge = edge;
            this.from = from;
            this.to = to;
        }

        public String getEdge() { return edge; }
        public String getFrom() { return from; }
        public String getTo() { return to; }
    }

    @FXML
    public void initialize() {
        // Initialize graph type toggle group
        graphTypeGroup = new ToggleGroup();
        rbUU.setToggleGroup(graphTypeGroup);
        rbUW.setToggleGroup(graphTypeGroup);
        rbDU.setToggleGroup(graphTypeGroup);
        rbDW.setToggleGroup(graphTypeGroup);
        rbUU.setSelected(true);

        // Initialize spinners
        spinnerNodes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 15, 7));
        spinnerEdges.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 8));

        // Initialize visualizer
        visualizer = new GraphVisualizer(graphCanvas);

        // Generate initial random graph
        generateRandomGraph();
    }

    @FXML
    private void generateRandomGraph() {
        int numNodes = spinnerNodes.getValue();
        int numEdges = spinnerEdges.getValue();

        boolean isDirected = rbDU.isSelected() || rbDW.isSelected();
        boolean isWeighted = rbUW.isSelected() || rbDW.isSelected();

        // Create new graph model
        graphModel = new GraphModel(numNodes, isDirected, isWeighted);

        // Generate random node positions in a circle
        double centerX = graphCanvas.getWidth() / 2;
        double centerY = graphCanvas.getHeight() / 2;
        double radius = Math.min(centerX, centerY) - 50;

        for (int i = 0; i < numNodes; i++) {
            double angle = 2 * Math.PI * i / numNodes;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            graphModel.setNodePosition(i, new Point2D(x, y));
        }

        // Generate random edges
        Random random = new Random();
        int edgesAdded = 0;
        int maxAttempts = numEdges * 10;
        int attempts = 0;

        while (edgesAdded < numEdges && attempts < maxAttempts) {
            int from = random.nextInt(numNodes);
            int to = random.nextInt(numNodes);

            if (from != to && graphModel.getAdjMatrix()[from][to] == 0) {
                int weight = isWeighted ? random.nextInt(9) + 1 : 1;
                graphModel.addEdge(from, to, weight);
                edgesAdded++;
            }
            attempts++;
        }

        // Update visualization
        updateVisualization();
    }

    private void updateVisualization() {
        // Draw graph on canvas
        visualizer.drawGraph(graphModel);

        // Update tables
        updateMatrixTable();
        updateAdjListTable();
        updateEdgeListTable();
    }

    private void updateMatrixTable() {
        matrixTable.getColumns().clear();
        matrixTable.getItems().clear();

        int[][] matrix = graphModel.getAdjMatrix();
        int n = graphModel.getNumNodes();

        // Add column for row headers
        TableColumn<ObservableList<String>, String> headerCol = new TableColumn<>("");
        headerCol.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(param.getValue().get(0)));
        headerCol.setPrefWidth(50);
        matrixTable.getColumns().add(headerCol);

        // Add columns for each node
        for (int i = 0; i < n; i++) {
            final int colIndex = i + 1;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(String.valueOf(i));
            col.setCellValueFactory(param ->
                    new javafx.beans.property.SimpleStringProperty(param.getValue().get(colIndex)));
            col.setPrefWidth(50);
            matrixTable.getColumns().add(col);
        }

        // Add rows
        for (int i = 0; i < n; i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(String.valueOf(i)); // Row header
            for (int j = 0; j < n; j++) {
                row.add(matrix[i][j] == 0 ? "" : String.valueOf(matrix[i][j]));
            }
            matrixTable.getItems().add(row);
        }
    }

    private void updateAdjListTable() {
        adjListTable.getColumns().clear();
        adjListTable.getItems().clear();

        // Create columns
        TableColumn<AdjListRow, String> nodeCol = new TableColumn<>("Node");
        nodeCol.setCellValueFactory(new PropertyValueFactory<>("node"));
        nodeCol.setPrefWidth(80);

        TableColumn<AdjListRow, String> neighborsCol = new TableColumn<>("Neighbors");
        neighborsCol.setCellValueFactory(new PropertyValueFactory<>("neighbors"));
        neighborsCol.setPrefWidth(300);

        adjListTable.getColumns().addAll(nodeCol, neighborsCol);

        // Add data
        Map<Integer, List<int[]>> adjList = graphModel.getAdjList();
        for (int i = 0; i < graphModel.getNumNodes(); i++) {
            StringBuilder neighbors = new StringBuilder();
            List<int[]> nodeNeighbors = adjList.get(i);

            for (int j = 0; j < nodeNeighbors.size(); j++) {
                int[] neighbor = nodeNeighbors.get(j);
                neighbors.append(neighbor[0]);
                if (graphModel.isWeighted() && neighbor[1] > 1) {
                    neighbors.append("(").append(neighbor[1]).append(")");
                }
                if (j < nodeNeighbors.size() - 1) {
                    neighbors.append(", ");
                }
            }

            adjListTable.getItems().add(new AdjListRow(String.valueOf(i), neighbors.toString()));
        }
    }

    private void updateEdgeListTable() {
        edgeListTable.getColumns().clear();
        edgeListTable.getItems().clear();

        // Create columns
        TableColumn<EdgeRow, String> edgeCol = new TableColumn<>("Edge #");
        edgeCol.setCellValueFactory(new PropertyValueFactory<>("edge"));
        edgeCol.setPrefWidth(80);

        TableColumn<EdgeRow, String> fromCol = new TableColumn<>("From");
        fromCol.setCellValueFactory(new PropertyValueFactory<>("from"));
        fromCol.setPrefWidth(80);

        TableColumn<EdgeRow, String> toCol = new TableColumn<>("To");
        toCol.setCellValueFactory(new PropertyValueFactory<>("to"));
        toCol.setPrefWidth(80);

        edgeListTable.getColumns().addAll(edgeCol, fromCol, toCol);

        // Add data (avoid duplicates for undirected)
        List<GraphModel.Edge> edges = graphModel.getEdgeList();
        int edgeNum = 0;
        for (int i = 0; i < edges.size(); i++) {
            GraphModel.Edge edge = edges.get(i);

            // Skip reverse edges for undirected graphs
            if (!graphModel.isDirected() && edge.from > edge.to) continue;

            edgeListTable.getItems().add(new EdgeRow(
                    String.valueOf(edgeNum++),
                    String.valueOf(edge.from),
                    String.valueOf(edge.to)
            ));
        }
    }

    @FXML
    private void clearGraph() {
        if (graphModel != null) {
            graphModel.clear();
            updateVisualization();
        }
    }

    @FXML
    private void backToCategory() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/GraphCategory.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) graphCanvas.getScene().getWindow();
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
        }
    }

    @FXML
    private void startCustomMode() {
        // Ask user for number of nodes
        TextInputDialog dialog = new TextInputDialog("5");
        dialog.setTitle("Custom Graph");
        dialog.setHeaderText("Create Custom Graph");
        dialog.setContentText("Enter number of nodes:");

        dialog.showAndWait().ifPresent(result -> {
            try {
                customNodeCount = Integer.parseInt(result);
                if (customNodeCount < 2 || customNodeCount > 15) {
                    showAlert("Invalid Input", "Please enter between 2 and 15 nodes.");
                    return;
                }

                // Start custom mode
                customMode = true;
                finishCustomButton.setVisible(true);

                // Get graph type
                boolean isDirected = rbDU.isSelected() || rbDW.isSelected();
                boolean isWeighted = rbUW.isSelected() || rbDW.isSelected();

                // Create new empty graph
                graphModel = new GraphModel(customNodeCount, isDirected, isWeighted);

                // Place nodes in a circle
                double centerX = graphCanvas.getWidth() / 2;
                double centerY = graphCanvas.getHeight() / 2;
                double radius = Math.min(centerX, centerY) - 50;

                for (int i = 0; i < customNodeCount; i++) {
                    double angle = 2 * Math.PI * i / customNodeCount;
                    double x = centerX + radius * Math.cos(angle);
                    double y = centerY + radius * Math.sin(angle);
                    graphModel.setNodePosition(i, new Point2D(x, y));
                }

                // Enable canvas click handler
                setupCanvasClickHandler();

                // Draw empty graph
                visualizer.drawGraph(graphModel);

                showAlert("Custom Mode", "Click on a node, then click on another node to create an edge.\nClick 'Finish Custom Graph' when done.");

            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number.");
            }
        });
    }

    private void setupCanvasClickHandler() {
        graphCanvas.setOnMouseClicked(event -> {
            if (!customMode) return;

            double x = event.getX();
            double y = event.getY();

            // Find clicked node
            Integer clickedNode = findNodeAtPosition(x, y);

            if (clickedNode != null) {
                if (selectedNode == null) {
                    // First node selected
                    selectedNode = clickedNode;
                    System.out.println("Selected node: " + selectedNode);
                } else {
                    // Second node selected - create edge
                    if (clickedNode.equals(selectedNode)) {
                        // Same node clicked - deselect
                        selectedNode = null;
                        return;
                    }

                    int weight = 1;
                    if (graphModel.isWeighted()) {
                        // Ask for weight
                        TextInputDialog weightDialog = new TextInputDialog("1");
                        weightDialog.setTitle("Edge Weight");
                        weightDialog.setHeaderText("Enter edge weight:");
                        weightDialog.setContentText("Weight:");

                        weightDialog.showAndWait().ifPresent(result -> {
                            try {
                                int w = Integer.parseInt(result);
                                graphModel.addEdge(selectedNode, clickedNode, w);
                                visualizer.drawGraph(graphModel);
                            } catch (NumberFormatException e) {
                                showAlert("Invalid Input", "Please enter a valid number.");
                            }
                        });
                    } else {
                        // Unweighted - just add edge
                        graphModel.addEdge(selectedNode, clickedNode, weight);
                        visualizer.drawGraph(graphModel);
                    }

                    selectedNode = null;
                }
            }
        });
    }

    private Integer findNodeAtPosition(double x, double y) {
        double nodeRadius = 25;
        for (int i = 0; i < graphModel.getNumNodes(); i++) {
            Point2D pos = graphModel.getNodePosition(i);
            if (pos != null) {
                double distance = Math.sqrt(Math.pow(x - pos.getX(), 2) + Math.pow(y - pos.getY(), 2));
                if (distance <= nodeRadius) {
                    return i;
                }
            }
        }
        return null;
    }

    @FXML
    private void finishCustomMode() {
        customMode = false;
        finishCustomButton.setVisible(false);
        selectedNode = null;

        // Remove click handler
        graphCanvas.setOnMouseClicked(null);

        // Update all tables
        updateVisualization();

        showAlert("Custom Graph Complete", "Your custom graph has been created!");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}