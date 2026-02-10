package com.AlgoVista.graphs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class GraphTraversalController {
    @FXML private Canvas graphCanvas;
    @FXML private RadioButton rbUU, rbUW, rbDU, rbDW;
    @FXML private ComboBox<String> algorithmCombo;
    @FXML private Spinner<Integer> startNodeSpinner, nodesSpinner, edgesSpinner;
    @FXML private Slider speedSlider;
    @FXML private Button playButton, pauseButton, resetButton;
    @FXML private Label traversalOrderLabel, timeComplexityLabel, spaceComplexityLabel;
    @FXML private TextArea distanceTableArea;
    @FXML private Label algorithmInfoLabel;
    @FXML private Button finishCustomButton;
    private boolean customMode = false;
    private Integer selectedNode = null;
    private int customNodeCount = 0;

    private GraphModel graphModel;
    private GraphicsContext gc;
    private ToggleGroup graphTypeGroup;

    private Timeline animation;
    private List<Integer> traversalOrder;
    private int currentStep;
    private Map<Integer, Color> nodeColors;
    private Map<Integer, Integer> distances;
    private Map<Integer, Integer> parent;

    private static final double NODE_RADIUS = 25;
    private static final Color UNVISITED_COLOR = Color.LIGHTGRAY;
    private static final Color VISITING_COLOR = Color.YELLOW;
    private static final Color VISITED_COLOR = Color.LIGHTGREEN;
    private static final Color PATH_COLOR = Color.ORANGE;

    @FXML
    public void initialize() {
        gc = graphCanvas.getGraphicsContext2D();

        // Initialize graph type toggle group
        graphTypeGroup = new ToggleGroup();
        rbUU.setToggleGroup(graphTypeGroup);
        rbUW.setToggleGroup(graphTypeGroup);
        rbDU.setToggleGroup(graphTypeGroup);
        rbDW.setToggleGroup(graphTypeGroup);
        rbUU.setSelected(true);

        // Initialize algorithm combo
        algorithmCombo.getItems().addAll("BFS", "DFS", "Dijkstra", "Bellman-Ford");
        algorithmCombo.setValue("BFS");

        // Initialize spinners
        nodesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 15, 7));
        edgesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 8));
        startNodeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 6, 0));

        // Initialize node colors
        nodeColors = new HashMap<>();

        // Generate initial graph
        generateRandomGraph();

        // Update complexity on algorithm change
//        algorithmCombo.setOnAction(e -> updateComplexity());
        algorithmCombo.setOnAction(e -> {
            updateComplexity();
            updateAlgorithmInfo();
        });

        // At the end of initialize() method
        updateAlgorithmInfo();
    }

    @FXML
    private void generateRandomGraph() {
        int numNodes = nodesSpinner.getValue();
        int numEdges = edgesSpinner.getValue();

        boolean isDirected = rbDU.isSelected() || rbDW.isSelected();
        boolean isWeighted = rbUW.isSelected() || rbDW.isSelected();

        // Create new graph model
        graphModel = new GraphModel(numNodes, isDirected, isWeighted);

        // Update start node spinner max value
        startNodeSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, numNodes - 1, 0)
        );

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

        // Reset visualization
        resetVisualization();
        updateComplexity();
    }

    private void resetVisualization() {
        nodeColors.clear();
        for (int i = 0; i < graphModel.getNumNodes(); i++) {
            nodeColors.put(i, UNVISITED_COLOR);
        }
        drawGraph();
        traversalOrderLabel.setText("Traversal Order: ");
        distanceTableArea.clear();
    }

    @FXML
    private void playTraversal() {
        if (animation != null && animation.getStatus() == Timeline.Status.PAUSED) {
            animation.play();
            return;
        }

        String algorithm = algorithmCombo.getValue();
        int startNode = startNodeSpinner.getValue();

        // Reset
        currentStep = 0;
        resetVisualization();

        // Run algorithm
        switch (algorithm) {
            case "BFS":
                traversalOrder = bfs(startNode);
                break;
            case "DFS":
                traversalOrder = dfs(startNode);
                break;
            case "Dijkstra":
                traversalOrder = dijkstra(startNode);
                displayDistanceTable();
                break;
            case "Bellman-Ford":
                traversalOrder = bellmanFord(startNode);
                displayDistanceTable();
                break;
        }

        // Animate
        animateTraversal();
    }

    private void animateTraversal() {
        double speed = speedSlider.getValue();
        Duration duration = Duration.millis(1000 / speed);

        animation = new Timeline(new KeyFrame(duration, e -> {
            if (currentStep < traversalOrder.size()) {
                int node = traversalOrder.get(currentStep);

                // Update color
                if (currentStep == 0) {
                    nodeColors.put(node, VISITING_COLOR);
                } else {
                    int prevNode = traversalOrder.get(currentStep - 1);
                    nodeColors.put(prevNode, VISITED_COLOR);
                    nodeColors.put(node, VISITING_COLOR);
                }

                // Update traversal order display
                updateTraversalOrderDisplay();

                drawGraph();
                currentStep++;
            } else {
                // Mark last node as visited
                if (!traversalOrder.isEmpty()) {
                    int lastNode = traversalOrder.get(traversalOrder.size() - 1);
                    nodeColors.put(lastNode, VISITED_COLOR);
                    drawGraph();
                }
                animation.stop();
            }
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void updateTraversalOrderDisplay() {
        StringBuilder sb = new StringBuilder("Traversal Order: ");
        for (int i = 0; i <= currentStep && i < traversalOrder.size(); i++) {
            sb.append(traversalOrder.get(i));
            if (i < currentStep && i < traversalOrder.size() - 1) {
                sb.append(" → ");
            }
        }
        traversalOrderLabel.setText(sb.toString());
    }

    @FXML
    private void pauseTraversal() {
        if (animation != null) {
            animation.pause();
        }
    }

    @FXML
    private void resetTraversal() {
        if (animation != null) {
            animation.stop();
        }
        currentStep = 0;
        resetVisualization();
    }

    // BFS Algorithm
    private List<Integer> bfs(int start) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[graphModel.getNumNodes()];
        Queue<Integer> queue = new LinkedList<>();

        queue.add(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            order.add(node);

            for (int[] neighbor : graphModel.getAdjList().get(node)) {
                int next = neighbor[0];
                if (!visited[next]) {
                    visited[next] = true;
                    queue.add(next);
                }
            }
        }

        return order;
    }

    // DFS Algorithm
    private List<Integer> dfs(int start) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[graphModel.getNumNodes()];
        dfsHelper(start, visited, order);
        return order;
    }

    private void dfsHelper(int node, boolean[] visited, List<Integer> order) {
        visited[node] = true;
        order.add(node);

        for (int[] neighbor : graphModel.getAdjList().get(node)) {
            int next = neighbor[0];
            if (!visited[next]) {
                dfsHelper(next, visited, order);
            }
        }
    }

    // Dijkstra's Algorithm
    private List<Integer> dijkstra(int start) {
        int n = graphModel.getNumNodes();
        distances = new HashMap<>();
        parent = new HashMap<>();
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>();

        // Initialize distances
        for (int i = 0; i < n; i++) {
            distances.put(i, Integer.MAX_VALUE);
            parent.put(i, -1);
        }
        distances.put(start, 0);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int node = current[0];

            if (visited[node]) continue;

            visited[node] = true;
            order.add(node);

            for (int[] neighbor : graphModel.getAdjList().get(node)) {
                int next = neighbor[0];
                int weight = neighbor[1];
                int newDist = distances.get(node) + weight;

                if (newDist < distances.get(next)) {
                    distances.put(next, newDist);
                    parent.put(next, node);
                    pq.add(new int[]{next, newDist});
                }
            }
        }

        return order;
    }

    // Bellman-Ford Algorithm
    private List<Integer> bellmanFord(int start) {
        int n = graphModel.getNumNodes();
        distances = new HashMap<>();
        parent = new HashMap<>();
        List<Integer> order = new ArrayList<>();

        // Initialize distances
        for (int i = 0; i < n; i++) {
            distances.put(i, Integer.MAX_VALUE);
            parent.put(i, -1);
        }
        distances.put(start, 0);
        order.add(start);

        // Relax edges V-1 times
        for (int i = 0; i < n - 1; i++) {
            for (GraphModel.Edge edge : graphModel.getEdgeList()) {
                if (distances.get(edge.from) != Integer.MAX_VALUE) {
                    int newDist = distances.get(edge.from) + edge.weight;
                    if (newDist < distances.get(edge.to)) {
                        distances.put(edge.to, newDist);
                        parent.put(edge.to, edge.from);
                        if (!order.contains(edge.to)) {
                            order.add(edge.to);
                        }
                    }
                }
            }
        }

        return order;
    }

    private void displayDistanceTable() {
        if (distances == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Node\tDistance\tParent\n");
        sb.append("─────────────────────────\n");

        for (int i = 0; i < graphModel.getNumNodes(); i++) {
            int dist = distances.get(i);
            String distStr = (dist == Integer.MAX_VALUE) ? "∞" : String.valueOf(dist);
            String parentStr = (parent.get(i) == -1) ? "-" : String.valueOf(parent.get(i));
            sb.append(String.format("%d\t%s\t\t%s\n", i, distStr, parentStr));
        }

        distanceTableArea.setText(sb.toString());
    }

    private void updateComplexity() {
        String algorithm = algorithmCombo.getValue();
        int V = graphModel != null ? graphModel.getNumNodes() : 7;
        int E = graphModel != null ? graphModel.getEdgeList().size() /
                (graphModel.isDirected() ? 1 : 2) : 8;

        String timeComplexity = "";
        String spaceComplexity = "";

        switch (algorithm) {
            case "BFS":
                timeComplexity = "Time: O(V + E) = O(" + V + " + " + E + ")";
                spaceComplexity = "Space: O(V) = O(" + V + ")";
                break;
            case "DFS":
                timeComplexity = "Time: O(V + E) = O(" + V + " + " + E + ")";
                spaceComplexity = "Space: O(V) = O(" + V + ")";
                break;
            case "Dijkstra":
                timeComplexity = "Time: O((V + E) log V) = O((" + V + " + " + E + ") log " + V + ")";
                spaceComplexity = "Space: O(V) = O(" + V + ")";
                break;
            case "Bellman-Ford":
                timeComplexity = "Time: O(V × E) = O(" + V + " × " + E + ")";
                spaceComplexity = "Space: O(V) = O(" + V + ")";
                break;
        }

        timeComplexityLabel.setText(timeComplexity);
        spaceComplexityLabel.setText(spaceComplexity);
    }

    private void drawGraph() {
        // Clear canvas
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        // Draw edges
        drawEdges();

        // Draw nodes
        drawNodes();
    }

    private void drawEdges() {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        for (GraphModel.Edge edge : graphModel.getEdgeList()) {
            if (!graphModel.isDirected() && edge.from > edge.to) continue;

            Point2D fromPos = graphModel.getNodePosition(edge.from);
            Point2D toPos = graphModel.getNodePosition(edge.to);

            if (fromPos != null && toPos != null) {
                gc.strokeLine(fromPos.getX(), fromPos.getY(), toPos.getX(), toPos.getY());

                // Draw weight if weighted
                if (graphModel.isWeighted() && edge.weight > 1) {
                    double midX = (fromPos.getX() + toPos.getX()) / 2;
                    double midY = (fromPos.getY() + toPos.getY()) / 2;

                    gc.setFill(Color.RED);
                    gc.setFont(Font.font(14));
                    gc.fillText(String.valueOf(edge.weight), midX, midY);
                }

                // Draw arrow for directed graphs
                if (graphModel.isDirected()) {
                    drawArrow(fromPos, toPos);
                }
            }
        }
    }

    private void drawArrow(Point2D from, Point2D to) {
        double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());

        double arrowX = to.getX() - NODE_RADIUS * Math.cos(angle);
        double arrowY = to.getY() - NODE_RADIUS * Math.sin(angle);

        double arrowLength = 10;
        double arrowAngle = Math.PI / 6;

        double x1 = arrowX - arrowLength * Math.cos(angle - arrowAngle);
        double y1 = arrowY - arrowLength * Math.sin(angle - arrowAngle);
        double x2 = arrowX - arrowLength * Math.cos(angle + arrowAngle);
        double y2 = arrowY - arrowLength * Math.sin(angle + arrowAngle);

        gc.strokeLine(arrowX, arrowY, x1, y1);
        gc.strokeLine(arrowX, arrowY, x2, y2);
    }

    private void drawNodes() {
        for (int i = 0; i < graphModel.getNumNodes(); i++) {
            Point2D pos = graphModel.getNodePosition(i);
            if (pos != null) {
                Color color = nodeColors.getOrDefault(i, UNVISITED_COLOR);

                // Draw circle
                gc.setFill(color);
                gc.fillOval(pos.getX() - NODE_RADIUS, pos.getY() - NODE_RADIUS,
                        NODE_RADIUS * 2, NODE_RADIUS * 2);

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(2);
                gc.strokeOval(pos.getX() - NODE_RADIUS, pos.getY() - NODE_RADIUS,
                        NODE_RADIUS * 2, NODE_RADIUS * 2);

                // Draw node label
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font(16));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.valueOf(i), pos.getX(), pos.getY() + 5);
            }
        }
    }

    @FXML
    private void backToCategory() {
        try {
            if (animation != null) {
                animation.stop();
            }

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
    private void updateAlgorithmInfo() {
        String algorithm = algorithmCombo.getValue();
        String info = "";

        switch (algorithm) {
            case "BFS":
                info = "Breadth-First Search explores graph level by level. " +
                        "Uses a queue. Good for finding shortest path in unweighted graphs.";
                break;
            case "DFS":
                info = "Depth-First Search explores as deep as possible before backtracking. " +
                        "Uses recursion/stack. Good for topological sorting and cycle detection.";
                break;
            case "Dijkstra":
                info = "Finds shortest path from source to all nodes in weighted graphs. " +
                        "Uses priority queue. Does not work with negative weights.";
                break;
            case "Bellman-Ford":
                info = "Finds shortest path and detects negative cycles. " +
                        "Works with negative weights. Slower than Dijkstra.";
                break;
        }

        algorithmInfoLabel.setText(info);
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
                playButton.setDisable(true);
                pauseButton.setDisable(true);
                resetButton.setDisable(true);

                // Get graph type
                boolean isDirected = rbDU.isSelected() || rbDW.isSelected();
                boolean isWeighted = rbUW.isSelected() || rbDW.isSelected();

                // Create new empty graph
                graphModel = new GraphModel(customNodeCount, isDirected, isWeighted);

                // Update start node spinner
                startNodeSpinner.setValueFactory(
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, customNodeCount - 1, 0)
                );

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

                // Reset colors
                nodeColors.clear();
                for (int i = 0; i < customNodeCount; i++) {
                    nodeColors.put(i, UNVISITED_COLOR);
                }

                // Enable canvas click handler
                setupCanvasClickHandler();

                // Draw empty graph
                drawGraph();

                // Update info
                traversalOrderLabel.setText("Custom Mode Active");
                algorithmInfoLabel.setText("Click on two nodes to create an edge. Click 'Finish' when done.");

                showAlert("Custom Mode",
                        "✓ Click first node\n" +
                                "✓ Click second node to create edge\n" +
                                "✓ Click 'Finish' when done");

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
                    // First node selected - highlight it
                    selectedNode = clickedNode;
                    nodeColors.put(selectedNode, VISITING_COLOR);
                    drawGraph();
                    algorithmInfoLabel.setText("Node " + selectedNode + " selected. Now click another node to create an edge.");
                } else {
                    // Second node selected - create edge
                    if (clickedNode.equals(selectedNode)) {
                        // Same node clicked - deselect
                        nodeColors.put(selectedNode, UNVISITED_COLOR);
                        selectedNode = null;
                        drawGraph();
                        algorithmInfoLabel.setText("Selection cancelled. Click on two nodes to create an edge.");
                        return;
                    }

                    int weight = 1;
                    if (graphModel.isWeighted()) {
                        // Ask for weight
                        TextInputDialog weightDialog = new TextInputDialog("1");
                        weightDialog.setTitle("Edge Weight");
                        weightDialog.setHeaderText("Creating edge: " + selectedNode + " → " + clickedNode);
                        weightDialog.setContentText("Enter weight:");

                        Optional<String> weightResult = weightDialog.showAndWait();
                        if (weightResult.isPresent()) {
                            try {
                                weight = Integer.parseInt(weightResult.get());
                                if (weight <= 0) {
                                    showAlert("Invalid Weight", "Weight must be positive.");
                                    nodeColors.put(selectedNode, UNVISITED_COLOR);
                                    selectedNode = null;
                                    drawGraph();
                                    return;
                                }
                            } catch (NumberFormatException e) {
                                showAlert("Invalid Input", "Please enter a valid number.");
                                nodeColors.put(selectedNode, UNVISITED_COLOR);
                                selectedNode = null;
                                drawGraph();
                                return;
                            }
                        } else {
                            // User canceled weight input
                            nodeColors.put(selectedNode, UNVISITED_COLOR);
                            selectedNode = null;
                            drawGraph();
                            return;
                        }
                    }

                    // Add the edge
                    graphModel.addEdge(selectedNode, clickedNode, weight);

                    // Reset selection
                    nodeColors.put(selectedNode, UNVISITED_COLOR);
                    selectedNode = null;

                    // Redraw
                    drawGraph();

                    algorithmInfoLabel.setText("Edge created! Click on two nodes to add more edges, or click 'Finish'.");
                }
            } else {
                // Clicked empty space - deselect
                if (selectedNode != null) {
                    nodeColors.put(selectedNode, UNVISITED_COLOR);
                    selectedNode = null;
                    drawGraph();
                    algorithmInfoLabel.setText("Selection cancelled. Click on two nodes to create an edge.");
                }
            }
        });
    }

    private Integer findNodeAtPosition(double x, double y) {
        for (int i = 0; i < graphModel.getNumNodes(); i++) {
            Point2D pos = graphModel.getNodePosition(i);
            if (pos != null) {
                double distance = Math.sqrt(Math.pow(x - pos.getX(), 2) + Math.pow(y - pos.getY(), 2));
                if (distance <= NODE_RADIUS) {
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

        // Re-enable buttons
        playButton.setDisable(false);
        pauseButton.setDisable(false);
        resetButton.setDisable(false);

        // Remove click handler
        graphCanvas.setOnMouseClicked(null);

        // Reset colors
        for (int i = 0; i < graphModel.getNumNodes(); i++) {
            nodeColors.put(i, UNVISITED_COLOR);
        }

        // Redraw
        drawGraph();

        // Update info
        traversalOrderLabel.setText("Custom graph created. Select algorithm and click Play.");
        updateAlgorithmInfo();
        updateComplexity();

        showAlert("Custom Graph Complete", "Your custom graph is ready! Now you can run traversal algorithms on it.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}