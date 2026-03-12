package com.AlgoVista.graphs;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class TopologicalSortController {

    @FXML private Canvas graphCanvas;
    @FXML private VBox queueContainer;
    @FXML private VBox inDegreeContainer;
    @FXML private VBox codeContainer;
    @FXML private TextArea consoleArea;
    @FXML private Label queueSizeLabel;
    @FXML private Label cycleLabel;
    @FXML private Spinner<Integer> nodesSpinner;
    @FXML private Spinner<Integer> edgesSpinner;
    @FXML private Slider speedSlider;

    // Graph data (directed)
    private int numNodes;
    private List<List<Integer>> adjList;   // directed adjacency list
    private double[] nodeX, nodeY;

    // Node state
    private enum NodeState { UNPROCESSED, IN_QUEUE, PROCESSING, DONE }
    private NodeState[] nodeState;

    // Kahn's algorithm code (Python)
    private static final String[] CODE_LINES = {
        "from collections import deque",
        "",
        "def topological_sort(graph, n):",
        "    in_degree = [0] * n",
        "    for u in range(n):",
        "        for v in graph[u]:",
        "            in_degree[v] += 1",
        "",
        "    queue = deque()",
        "    for i in range(n):",
        "        if in_degree[i] == 0:",
        "            queue.append(i)",
        "",
        "    order = []",
        "    while queue:",
        "        node = queue.popleft()",
        "        order.append(node)",
        "        print(f'Processed: {node}')",
        "",
        "        for neighbor in graph[node]:",
        "            in_degree[neighbor] -= 1",
        "            if in_degree[neighbor] == 0:",
        "                queue.append(neighbor)",
        "",
        "    if len(order) != n:",
        "        print('Cycle detected! Not a DAG.')",
        "    return order"
    };

    // Simulation snapshots
    private List<Integer>     stepCodeLine;
    private List<Queue<Integer>> stepQueueState;
    private List<int[]>       stepInDegrees;
    private List<NodeState[]> stepNodeStates;
    private List<String>      stepOutput;
    private int currentStep;
    private boolean isRunning;
    private Timeline autoTimeline;

    @FXML
    public void initialize() {
        nodesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 12, 7));
        edgesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 20, 8));
        buildCodePanel();
        loadDefaultGraph();
    }

    // ─────────── DEFAULT / RANDOM GRAPHS ───────────
    @FXML
    private void loadDefaultGraph() {
        if (isRunning) stopAuto();
        numNodes = 7;
        adjList = buildAdjList(numNodes);
        nodeX = new double[numNodes];
        nodeY = new double[numNodes];

        // A clear layered layout for the default DAG
        // Layer 0: node 0          (x=290, y=60)
        // Layer 1: nodes 1,2       
        // Layer 2: nodes 3,4,5     
        // Layer 3: node 6          
        double[] xs = {290, 150, 420, 70,  255, 370, 510, 290};
        double[] ys = {55,  180, 180, 310, 310, 310, 310, 420};
        System.arraycopy(xs, 0, nodeX, 0, numNodes);
        System.arraycopy(ys, 0, nodeY, 0, numNodes);

        // Directed edges forming a DAG
        int[][] edges = {{0,1},{0,2},{1,3},{1,4},{2,4},{2,5},{2,6},{4,6},{3,6}};
        for (int[] e : edges) addDirectedEdge(e[0], e[1]);

        resetSort();
    }

    @FXML
    private void generateRandomGraph() {
        if (isRunning) stopAuto();
        numNodes = nodesSpinner.getValue();
        int numEdges = edgesSpinner.getValue();
        adjList = buildAdjList(numNodes);
        nodeX = new double[numNodes];
        nodeY = new double[numNodes];
        arrangeNodes();

        // Randomly add directed edges only from lower to higher index (guarantees DAG)
        Random rng = new Random();
        Set<String> used = new HashSet<>();
        int attempts = 0, added = 0;
        while (added < numEdges && attempts < 600) {
            int u = rng.nextInt(numNodes), v = rng.nextInt(numNodes);
            if (u < v && !used.contains(u + "-" + v)) {
                addDirectedEdge(u, v);
                used.add(u + "-" + v);
                added++;
            }
            attempts++;
        }
        resetSort();
    }

    private List<List<Integer>> buildAdjList(int n) {
        List<List<Integer>> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(new ArrayList<>());
        return list;
    }

    private void addDirectedEdge(int u, int v) {
        adjList.get(u).add(v);
    }

    private void arrangeNodes() {
        Canvas c = graphCanvas;
        double w = c.getWidth(), h = c.getHeight();
        double cx = w / 2, cy = h / 2;
        double radius = Math.min(w, h) * 0.38;
        for (int i = 0; i < numNodes; i++) {
            double angle = 2 * Math.PI * i / numNodes - Math.PI / 2;
            nodeX[i] = cx + radius * Math.cos(angle);
            nodeY[i] = cy + radius * Math.sin(angle);
        }
    }

    // ─────────── BUILD SIMULATION SNAPSHOTS ───────────
    private void buildKahnSteps() {
        stepCodeLine   = new ArrayList<>();
        stepQueueState = new ArrayList<>();
        stepInDegrees  = new ArrayList<>();
        stepNodeStates = new ArrayList<>();
        stepOutput     = new ArrayList<>();

        int[] inDegree = new int[numNodes];
        NodeState[] ns = new NodeState[numNodes];
        Arrays.fill(ns, NodeState.UNPROCESSED);
        List<String> outputSoFar = new ArrayList<>();

        // import / def lines
        snap(0, new LinkedList<>(), inDegree, ns, outputSoFar);
        snap(2, new LinkedList<>(), inDegree, ns, outputSoFar);

        // Compute in-degrees
        snap(3, new LinkedList<>(), inDegree, ns, outputSoFar);
        for (int u = 0; u < numNodes; u++) {
            snap(4, new LinkedList<>(), inDegree, ns, outputSoFar);
            for (int v : adjList.get(u)) {
                inDegree[v]++;
                snap(6, new LinkedList<>(), inDegree, ns, outputSoFar);
            }
        }

        // Initialize queue with 0 in-degree nodes
        Queue<Integer> queue = new LinkedList<>();
        snap(8, queue, inDegree, ns, outputSoFar);
        for (int i = 0; i < numNodes; i++) {
            snap(9, queue, inDegree, ns, outputSoFar);
            if (inDegree[i] == 0) {
                queue.add(i);
                ns[i] = NodeState.IN_QUEUE;
                snap(11, queue, inDegree, ns, outputSoFar);
            }
        }

        // Main BFS
        snap(13, queue, inDegree, ns, outputSoFar);
        snap(14, queue, inDegree, ns, outputSoFar);
        int processed = 0;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            ns[node] = NodeState.PROCESSING;
            snap(15, queue, inDegree, ns, outputSoFar);

            ns[node] = NodeState.DONE;
            outputSoFar.add("Processed: " + node);
            snap(16, queue, inDegree, ns, outputSoFar);
            snap(17, queue, inDegree, ns, outputSoFar);

            snap(19, queue, inDegree, ns, outputSoFar);
            for (int nb : adjList.get(node)) {
                inDegree[nb]--;
                snap(20, queue, inDegree, ns, outputSoFar);
                if (inDegree[nb] == 0) {
                    queue.add(nb);
                    ns[nb] = NodeState.IN_QUEUE;
                    snap(22, queue, inDegree, ns, outputSoFar);
                }
            }
            processed++;
            snap(14, queue, inDegree, ns, outputSoFar);
        }

        // Cycle check
        snap(24, queue, inDegree, ns, outputSoFar);
        if (processed != numNodes) {
            outputSoFar.add("⚠ Cycle detected! Not a valid DAG.");
            snap(25, queue, inDegree, ns, outputSoFar);
        }
        snap(26, queue, inDegree, ns, outputSoFar);

        currentStep = 0;
    }

    private void snap(int codeLine, Queue<Integer> queue, int[] inDeg, NodeState[] ns, List<String> out) {
        stepCodeLine.add(codeLine);
        stepQueueState.add(new LinkedList<>(queue));
        stepInDegrees.add(inDeg.clone());
        stepNodeStates.add(ns.clone());
        stepOutput.add(String.join("\n", out));
    }

    // ─────────── PLAYBACK ───────────
    @FXML
    private void runSort() {
        if (stepCodeLine == null) buildKahnSteps();
        if (!isRunning) {
            isRunning = true;
            autoTimeline = new Timeline(new KeyFrame(Duration.millis(speedSlider.getValue()), e -> {
                if (currentStep < stepCodeLine.size()) {
                    applyStep(currentStep++);
                } else {
                    stopAuto();
                }
            }));
            autoTimeline.setCycleCount(Timeline.INDEFINITE);
            autoTimeline.play();
        }
    }

    @FXML
    private void stepForward() {
        stopAuto();
        if (stepCodeLine == null) buildKahnSteps();
        if (currentStep < stepCodeLine.size()) applyStep(currentStep++);
    }

    @FXML
    private void resetSort() {
        stopAuto();
        stepCodeLine  = null;
        stepQueueState = null;
        currentStep   = 0;
        isRunning     = false;

        nodeState = new NodeState[numNodes];
        Arrays.fill(nodeState, NodeState.UNPROCESSED);
        cycleLabel.setText("");
        consoleArea.setText("");
        queueSizeLabel.setText("Size: 0");
        buildCodePanel();
        buildInDegreeDisplay(computeInDegrees());
        updateQueueVisual(new LinkedList<>());
        drawGraph();
    }

    private void stopAuto() {
        if (autoTimeline != null) autoTimeline.stop();
        isRunning = false;
    }

    private void applyStep(int idx) {
        nodeState = stepNodeStates.get(idx);
        drawGraphWithState();
        highlightCodeLine(stepCodeLine.get(idx));
        updateQueueVisual(stepQueueState.get(idx));
        buildInDegreeDisplay(stepInDegrees.get(idx));
        consoleArea.setText(stepOutput.get(idx));
        consoleArea.setScrollTop(Double.MAX_VALUE);
    }

    // ─────────── DRAWING ───────────
    private void drawGraph() {
        nodeState = new NodeState[numNodes];
        Arrays.fill(nodeState, NodeState.UNPROCESSED);
        drawGraphWithState();
    }

    private void drawGraphWithState() {
        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        double w = graphCanvas.getWidth(), h = graphCanvas.getHeight();
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.web("#0a0f1e"));
        gc.fillRect(0, 0, w, h);

        double nodeR = 22;

        // Draw directed edges with arrows
        for (int u = 0; u < numNodes; u++) {
            for (int v : adjList.get(u)) {
                drawArrowEdge(gc, nodeX[u], nodeY[u], nodeX[v], nodeY[v], nodeR);
            }
        }

        // Draw nodes
        for (int i = 0; i < numNodes; i++) {
            Color col = getNodeColor(nodeState != null ? nodeState[i] : NodeState.UNPROCESSED);
            boolean active = nodeState != null &&
                    (nodeState[i] == NodeState.PROCESSING || nodeState[i] == NodeState.IN_QUEUE);
            if (active) gc.setEffect(new javafx.scene.effect.Glow(0.55));

            gc.setFill(col);
            gc.fillOval(nodeX[i] - nodeR, nodeY[i] - nodeR, nodeR * 2, nodeR * 2);
            gc.setEffect(null);
            gc.setStroke(col.brighter());
            gc.setLineWidth(2.5);
            gc.strokeOval(nodeX[i] - nodeR, nodeY[i] - nodeR, nodeR * 2, nodeR * 2);

            // Node label
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("System", FontWeight.BOLD, 14));
            String lbl = String.valueOf(i);
            gc.fillText(lbl, nodeX[i] - lbl.length() * 4.5, nodeY[i] + 5);

            // In-degree badge
            if (nodeState != null && nodeState[i] == NodeState.UNPROCESSED) {
                int[] inDegs = computeInDegrees();
                int deg = inDegs[i];
                gc.setFill(deg == 0 ? Color.web("#f43f5e") : Color.web("#0ea5e9"));
                gc.fillOval(nodeX[i] + nodeR - 10, nodeY[i] - nodeR - 2, 18, 18);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("System", FontWeight.BOLD, 10));
                gc.fillText(String.valueOf(deg), nodeX[i] + nodeR - 5, nodeY[i] - nodeR + 11);
            }
        }
    }

    private void drawArrowEdge(GraphicsContext gc, double x1, double y1, double x2, double y2, double r) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double ex = x2 - r * Math.cos(angle);
        double ey = y2 - r * Math.sin(angle);
        double sx = x1 + r * Math.cos(angle);
        double sy = y1 + r * Math.sin(angle);

        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(2);
        gc.strokeLine(sx, sy, ex, ey);

        // Arrowhead
        double arrowLen = 12, arrowW = 7;
        double ax = ex - arrowLen * Math.cos(angle);
        double ay = ey - arrowLen * Math.sin(angle);
        gc.setFill(Color.web("#334155"));
        gc.fillPolygon(
            new double[]{ex, ax - arrowW * Math.sin(angle), ax + arrowW * Math.sin(angle)},
            new double[]{ey, ay + arrowW * Math.cos(angle), ay - arrowW * Math.cos(angle)},
            3
        );
    }

    private Color getNodeColor(NodeState s) {
        switch (s) {
            case IN_QUEUE:    return Color.web("#fbbf24");
            case PROCESSING:  return Color.web("#0ea5e9");
            case DONE:        return Color.web("#22c55e");
            default:          return Color.web("#475569");
        }
    }

    // ─────────── IN-DEGREE PANEL ───────────
    private int[] computeInDegrees() {
        int[] deg = new int[numNodes];
        for (int u = 0; u < numNodes; u++)
            for (int v : adjList.get(u)) deg[v]++;
        return deg;
    }

    private void buildInDegreeDisplay(int[] inDeg) {
        inDegreeContainer.getChildren().clear();
        // Header
        HBox hdr = new HBox();
        hdr.setStyle("-fx-background-color: #0f172a; -fx-padding: 3 6; -fx-background-radius: 4;");
        Label n = new Label("Node"); n.setTextFill(Color.web("#64748b")); n.setFont(Font.font("System", FontWeight.BOLD, 10)); n.setMinWidth(42);
        Label d = new Label("In-deg"); d.setTextFill(Color.web("#64748b")); d.setFont(Font.font("System", FontWeight.BOLD, 10));
        hdr.getChildren().addAll(n, d);
        inDegreeContainer.getChildren().add(hdr);

        for (int i = 0; i < numNodes; i++) {
            HBox row = new HBox(2);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(3, 6, 3, 6));
            String bg = inDeg[i] == 0 ? "rgba(244,63,94,0.15)" : "transparent";
            row.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 4;");

            Label nodeLbl = new Label(String.valueOf(i));
            nodeLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 11));
            nodeLbl.setTextFill(Color.web("#e2e8f0"));
            nodeLbl.setMinWidth(38);

            Label degLbl = new Label(String.valueOf(inDeg[i]));
            degLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 11));
            degLbl.setTextFill(inDeg[i] == 0 ? Color.web("#f43f5e") : Color.web("#0ea5e9"));

            row.getChildren().addAll(nodeLbl, degLbl);
            inDegreeContainer.getChildren().add(row);
        }
    }

    // ─────────── QUEUE VISUALIZER ───────────
    private void updateQueueVisual(Queue<Integer> queue) {
        queueContainer.getChildren().clear();
        List<Integer> items = new ArrayList<>(queue);
        queueSizeLabel.setText("Size: " + items.size());

        if (items.isEmpty()) {
            Label e = new Label("[ Empty ]");
            e.setTextFill(Color.web("#4b5563"));
            e.setFont(Font.font("Consolas", 12));
            queueContainer.getChildren().add(e);
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            int node = items.get(i);
            boolean isFront = (i == 0);
            HBox entry = new HBox(10);
            entry.setAlignment(Pos.CENTER);
            entry.setPadding(new Insets(5, 12, 5, 12));
            String bg = isFront ? "#fbbf24" : "#1e3a5f";
            String tc = isFront ? "#0f172a" : "#94a3b8";
            entry.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 6;");

            Label lbl = new Label("Node  " + node);
            lbl.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
            lbl.setTextFill(Color.web(tc));
            entry.getChildren().add(lbl);

            if (isFront) {
                Label badge = new Label("FRONT ▶");
                badge.setFont(Font.font("System", FontWeight.BOLD, 9));
                badge.setTextFill(Color.web("#0f172a"));
                entry.getChildren().add(badge);
            }
            queueContainer.getChildren().add(entry);
        }
    }

    // ─────────── CODE PANEL ───────────
    private void buildCodePanel() {
        codeContainer.getChildren().clear();
        codeContainer.setStyle("-fx-background-color: #0a0f1e; -fx-padding: 10; -fx-background-radius: 8;");
        for (int i = 0; i < CODE_LINES.length; i++) {
            HBox row = new HBox();
            row.setId("codeLine_" + i);
            row.setPadding(new Insets(2, 6, 2, 6));
            row.setStyle("-fx-background-color: transparent; -fx-background-radius: 4;");

            Label num = new Label(String.format("%2d", i + 1));
            num.setFont(Font.font("Consolas", 11));
            num.setTextFill(Color.web("#4b5563"));
            num.setMinWidth(24);

            Label code = new Label(CODE_LINES[i]);
            code.setFont(Font.font("Consolas", 11));
            code.setTextFill(Color.web("#e2e8f0"));

            row.getChildren().addAll(num, code);
            codeContainer.getChildren().add(row);
        }
    }

    private void highlightCodeLine(int idx) {
        int total = codeContainer.getChildren().size();
        for (int i = 0; i < total; i++) {
            HBox row = (HBox) codeContainer.getChildren().get(i);
            if (i == idx) {
                row.setStyle("-fx-background-color: rgba(14,165,233,0.2); -fx-background-radius: 4;");
                if (row.getChildren().size() >= 2) {
                    ((Label) row.getChildren().get(1)).setTextFill(Color.web("#fbbf24"));
                    ((Label) row.getChildren().get(1)).setFont(Font.font("Consolas", FontWeight.BOLD, 11));
                }
            } else {
                row.setStyle("-fx-background-color: transparent; -fx-background-radius: 4;");
                if (row.getChildren().size() >= 2) {
                    ((Label) row.getChildren().get(1)).setTextFill(Color.web("#e2e8f0"));
                    ((Label) row.getChildren().get(1)).setFont(Font.font("Consolas", 11));
                }
            }
        }
        // Auto-scroll to keep highlighted line visible
        if (total > 0 && idx >= 0) {
            double ratio = (double) idx / Math.max(1, total - 1);
            javafx.scene.Node parent = codeContainer.getParent();
            while (parent != null && !(parent instanceof ScrollPane)) {
                parent = parent.getParent();
            }
            if (parent instanceof ScrollPane) {
                final ScrollPane sp = (ScrollPane) parent;
                final double targetV = ratio;
                javafx.application.Platform.runLater(() -> sp.setVvalue(targetV));
            }
        }
    }

    // ─────────── NAVIGATION ───────────
    @FXML
    private void backToCategory() {
        stopAuto();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphAlgorithmsCategory.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) graphCanvas.getScene().getWindow();
            double w = stage.getWidth(), h = stage.getHeight(), x = stage.getX(), y = stage.getY();
            stage.setScene(new Scene(root, w, h));
            stage.setX(x); stage.setY(y);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
