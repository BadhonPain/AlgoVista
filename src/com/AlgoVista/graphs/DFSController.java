package com.AlgoVista.graphs;

import javafx.animation.*;
import javafx.application.Platform;
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

public class DFSController {

    @FXML private Canvas graphCanvas;
    @FXML private VBox stackContainer;
    @FXML private VBox codeContainer;
    @FXML private TextArea consoleArea;
    @FXML private Label stackSizeLabel;
    @FXML private Spinner<Integer> startNodeSpinner;
    @FXML private Spinner<Integer> nodesSpinner;
    @FXML private Spinner<Integer> edgesSpinner;
    @FXML private Slider speedSlider;
    @FXML private Label speedLabel;

    // Graph data
    private int numNodes = 7;
    private List<List<Integer>> adjacencyList;
    private double[] nodeX, nodeY;

    // DFS state
    private enum NodeState { UNVISITED, IN_STACK, VISITING, VISITED }
    private NodeState[] nodeState;
    private List<int[]> steps; // each step: [type, nodeId] type: 0=push, 1=pop, 2=visit, 3=backtrack
    private int currentStep;
    private boolean isRunning;
    private Timeline autoTimeline;

    // Code lines and their highlight row indices
    private static final String[] CODE_LINES = {
        "def dfs(graph, start):",
        "    visited = set()",
        "    stack = [start]",
        "    result = []",
        "    ",
        "    while stack:",
        "        node = stack.pop()",
        "        ",
        "        if node not in visited:",
        "            visited.add(node)",
        "            result.append(node)",
        "            print(f'Visiting: {node}')",
        "            ",
        "            for neighbor in reversed(graph[node]):",
        "                if neighbor not in visited:",
        "                    stack.append(neighbor)",
        "    ",
        "    return result"
    };

    // Which code line is highlighted at each step
    private List<Integer> stepCodeLine;
    private List<Deque<Integer>> stepStackState;
    private List<String> stepConsoleOutput;
    private List<NodeState[]> stepNodeStates;

    @FXML
    public void initialize() {
        // Setup spinners
        startNodeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
        nodesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 12, 7));
        edgesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 30, 9));

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (speedLabel != null) {
                speedLabel.setText(String.format("%.1fx", newVal.doubleValue()));
            }
            if (autoTimeline != null) {
                autoTimeline.setRate(newVal.doubleValue());
            }
        });

        buildCodePanel();
        generateDefaultGraph();
        drawGraph();
    }

    private void buildCodePanel() {
        codeContainer.getChildren().clear();
        codeContainer.setStyle("-fx-background-color: #0a0f1e; -fx-padding: 10; -fx-background-radius: 8;");
        for (int i = 0; i < CODE_LINES.length; i++) {
            HBox lineRow = new HBox();
            lineRow.setId("codeLine_" + i);
            lineRow.setPadding(new Insets(2, 6, 2, 6));
            lineRow.setStyle("-fx-background-color: transparent; -fx-background-radius: 4;");

            // Line number
            Label lineNum = new Label(String.format("%2d", i + 1));
            lineNum.setFont(Font.font("Consolas", 12));
            lineNum.setTextFill(Color.web("#4b5563"));
            lineNum.setMinWidth(25);

            // Code text
            Label codeLbl = new Label(CODE_LINES[i]);
            codeLbl.setFont(Font.font("Consolas", 12));
            codeLbl.setTextFill(Color.web("#e2e8f0"));

            lineRow.getChildren().addAll(lineNum, codeLbl);
            codeContainer.getChildren().add(lineRow);
        }
    }

    private void generateDefaultGraph() {
        numNodes = nodesSpinner.getValue();
        adjacencyList = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) adjacencyList.add(new ArrayList<>());

        nodeX = new double[numNodes];
        nodeY = new double[numNodes];

        // Arrange nodes in a circle-ish layout
        arrangeNodes();

        // Default edges that form a meaningful graph
        int[][] defaultEdges = {
            {0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {2, 5}, {3, 6}, {4, 6}, {5, 6}
        };
        for (int[] e : defaultEdges) {
            if (e[0] < numNodes && e[1] < numNodes) {
                addEdge(e[0], e[1]);
            }
        }
    }

    private void addEdge(int u, int v) {
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    private void arrangeNodes() {
        Canvas c = graphCanvas;
        double w = c.getWidth(), h = c.getHeight();
        double cx = w / 2, cy = h / 2;

        if (numNodes == 7) {
            // Manual nice layout for default 7-node graph
            nodeX = new double[]{cx, cx - 150, cx + 150, cx - 220, cx - 80, cx + 80, cx};
            nodeY = new double[]{cy - 170, cy - 60, cy - 60, cy + 80, cy + 80, cy + 80, cy + 200};
        } else {
            double radius = Math.min(w, h) * 0.38;
            for (int i = 0; i < numNodes; i++) {
                double angle = 2 * Math.PI * i / numNodes - Math.PI / 2;
                nodeX[i] = cx + radius * Math.cos(angle);
                nodeY[i] = cy + radius * Math.sin(angle);
            }
        }
    }

    @FXML
    private void generateRandomGraph() {
        if (isRunning) stopAuto();
        numNodes = nodesSpinner.getValue();
        int numEdges = edgesSpinner.getValue();

        adjacencyList = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) adjacencyList.add(new ArrayList<>());
        nodeX = new double[numNodes];
        nodeY = new double[numNodes];

        arrangeNodes();

        // Ensure connectivity: random spanning tree first
        List<Integer> shuffled = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) shuffled.add(i);
        Collections.shuffle(shuffled);
        for (int i = 1; i < numNodes; i++) {
            addEdge(shuffled.get(i - 1), shuffled.get(i));
        }

        // Add extra random edges
        Random rng = new Random();
        int extra = numEdges - (numNodes - 1);
        int tries = 0;
        while (extra > 0 && tries < 500) {
            int u = rng.nextInt(numNodes), v = rng.nextInt(numNodes);
            if (u != v && !adjacencyList.get(u).contains(v)) {
                addEdge(u, v);
                extra--;
            }
            tries++;
        }

        // Sort neighbors for consistent DFS order
        for (List<Integer> l : adjacencyList) Collections.sort(l);

        resetDFS();
        drawGraph();
    }

    // ────────────── DFS STEP BUILDER ──────────────
    @FXML
    private void runDFS() {
        if (steps == null || steps.isEmpty()) buildDFSSteps();
        if (!isRunning) {
            isRunning = true;
            startAutoPlay();
        }
    }

    private void buildDFSSteps() {
        int start = Math.min(startNodeSpinner.getValue(), numNodes - 1);
        steps = new ArrayList<>();
        stepCodeLine = new ArrayList<>();
        stepStackState = new ArrayList<>();
        stepConsoleOutput = new ArrayList<>();
        stepNodeStates = new ArrayList<>();

        boolean[] visited = new boolean[numNodes];
        Deque<Integer> stack = new ArrayDeque<>();
        List<String> outputSoFar = new ArrayList<>();

        NodeState[] initState = new NodeState[numNodes];
        Arrays.fill(initState, NodeState.UNVISITED);
        NodeState[] curState = initState.clone();

        // Step 0: initialize
        addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                new int[]{-1, -1}, 1, stack, outputSoFar, curState); // visited = set()

        stack.push(start);
        curState[start] = NodeState.IN_STACK;
        addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                new int[]{0, start}, 2, stack, outputSoFar, curState); // stack = [start]

        // Step: while stack
        addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                new int[]{-1, -1}, 5, stack, outputSoFar, curState);

        while (!stack.isEmpty()) {
            int node = stack.pop();
            curState[node] = NodeState.VISITING;
            addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                    new int[]{1, node}, 6, stack, outputSoFar, curState); // node = stack.pop()

            addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                    new int[]{-1, node}, 8, stack, outputSoFar, curState); // if node not in visited

            if (!visited[node]) {
                visited[node] = true;
                curState[node] = NodeState.VISITED;
                outputSoFar.add("Visiting: " + node);
                addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                        new int[]{2, node}, 9, stack, outputSoFar, curState); // visited.add(node)

                addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                        new int[]{2, node}, 11, stack, outputSoFar, curState); // print visiting

                // Iterate neighbors in reversed order (to process smaller index first)
                List<Integer> neighbors = adjacencyList.get(node);
                List<Integer> reversed = new ArrayList<>(neighbors);
                Collections.reverse(reversed);

                addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                        new int[]{-1, node}, 13, stack, outputSoFar, curState); // for neighbor in reversed

                for (int neighbor : reversed) {
                    addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                            new int[]{-1, neighbor}, 14, stack, outputSoFar, curState); // if not visited

                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                        curState[neighbor] = NodeState.IN_STACK;
                        addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                                new int[]{0, neighbor}, 15, stack, outputSoFar, curState); // stack.append(neighbor)
                    }
                }
            }
            // while check again
            addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                    new int[]{-1, -1}, 5, stack, outputSoFar, curState);
        }

        // return result
        addStepSnapshot(steps, stepCodeLine, stepStackState, stepConsoleOutput, stepNodeStates,
                new int[]{-1, -1}, 17, stack, outputSoFar, curState);

        currentStep = 0;
    }

    private void addStepSnapshot(List<int[]> stepsL, List<Integer> codes, List<Deque<Integer>> stacks,
                                  List<String> output, List<NodeState[]> states,
                                  int[] stepData, int codeLine, Deque<Integer> stack,
                                  List<String> outputSoFar, NodeState[] curState) {
        stepsL.add(stepData);
        codes.add(codeLine);
        // Deep copy stack
        Deque<Integer> stackCopy = new ArrayDeque<>(stack);
        stacks.add(stackCopy);
        output.add(String.join("\n", outputSoFar));
        states.add(curState.clone());
    }

    private void startAutoPlay() {
        autoTimeline = new Timeline();
        autoTimeline.setCycleCount(Timeline.INDEFINITE);
        autoTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(800), e -> {
            if (currentStep < steps.size()) {
                applyStep(currentStep);
                currentStep++;
            } else {
                stopAuto();
                isRunning = false;
            }
        }));
        autoTimeline.setRate(speedSlider.getValue());
        autoTimeline.play();
    }

    private void stopAuto() {
        if (autoTimeline != null) autoTimeline.stop();
        isRunning = false;
    }

    @FXML
    private void stepForward() {
        stopAuto();
        if (steps == null || steps.isEmpty()) buildDFSSteps();
        if (currentStep < steps.size()) {
            applyStep(currentStep);
            currentStep++;
        }
    }

    private void applyStep(int idx) {
        if (idx >= steps.size()) return;
        NodeState[] ns = stepNodeStates.get(idx);
        nodeState = ns;

        // Draw graph
        drawGraphWithState();

        // Update code highlight
        highlightCodeLine(stepCodeLine.get(idx));

        // Update stack
        updateStackVisual(stepStackState.get(idx));

        // Update console
        consoleArea.setText(stepConsoleOutput.get(idx));
        consoleArea.setScrollTop(Double.MAX_VALUE);
    }

    // ────────────── DRAWING ──────────────
    private void drawGraph() {
        if (nodeState == null) {
            nodeState = new NodeState[numNodes];
            Arrays.fill(nodeState, NodeState.UNVISITED);
        }
        drawGraphWithState();
    }

    private void drawGraphWithState() {
        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        double w = graphCanvas.getWidth(), h = graphCanvas.getHeight();
        gc.clearRect(0, 0, w, h);

        // Background — match the panel container color
        gc.setFill(Color.web("#0a0f1e"));
        gc.fillRect(0, 0, w, h);

        double nodeR = 22;

        // Draw edges
        gc.setLineWidth(2);
        for (int u = 0; u < numNodes; u++) {
            for (int v : adjacencyList.get(u)) {
                if (v > u) { // draw each edge once
                    gc.setStroke(Color.web("#334155"));
                    gc.strokeLine(nodeX[u], nodeY[u], nodeX[v], nodeY[v]);
                }
            }
        }

        // Draw nodes
        for (int i = 0; i < numNodes; i++) {
            Color nodeColor = getNodeColor(nodeState != null ? nodeState[i] : NodeState.UNVISITED);
            Color borderColor = nodeColor.brighter();

            // Glow if active
            if (nodeState != null && (nodeState[i] == NodeState.VISITING || nodeState[i] == NodeState.IN_STACK)) {
                gc.setEffect(new javafx.scene.effect.Glow(0.5));
            }

            gc.setFill(nodeColor);
            gc.fillOval(nodeX[i] - nodeR, nodeY[i] - nodeR, nodeR * 2, nodeR * 2);

            gc.setEffect(null);
            gc.setStroke(borderColor);
            gc.setLineWidth(2.5);
            gc.strokeOval(nodeX[i] - nodeR, nodeY[i] - nodeR, nodeR * 2, nodeR * 2);

            // Node label
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("System", FontWeight.BOLD, 14));
            String label = String.valueOf(i);
            double textW = label.length() * 8.5;
            gc.fillText(label, nodeX[i] - textW / 2, nodeY[i] + 5);
        }
    }

    private Color getNodeColor(NodeState state) {
        switch (state) {
            case IN_STACK:  return Color.web("#fbbf24");
            case VISITING:  return Color.web("#f43f5e");
            case VISITED:   return Color.web("#22c55e");
            default:        return Color.web("#475569");
        }
    }

    // ────────────── STACK VISUALIZER ──────────────
    private void updateStackVisual(Deque<Integer> stack) {
        stackContainer.getChildren().clear();
        List<Integer> items = new ArrayList<>(stack);
        // items[0] is the top of the stack (push order)
        stackSizeLabel.setText("Size: " + items.size());

        if (items.isEmpty()) {
            Label emptyLbl = new Label("[ Empty ]");
            emptyLbl.setTextFill(Color.web("#4b5563"));
            emptyLbl.setFont(Font.font("Consolas", 12));
            stackContainer.getChildren().add(emptyLbl);
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            int node = items.get(i);
            HBox entry = new HBox();
            entry.setAlignment(Pos.CENTER);
            entry.setPadding(new Insets(5, 12, 5, 12));
            entry.setSpacing(10);

            boolean isTop = (i == 0);
            String bg = isTop ? "#fbbf24" : "#1e3a5f";
            String textColor = isTop ? "#0f172a" : "#94a3b8";

            entry.setStyle("-fx-background-color: " + bg + ";" +
                    "-fx-background-radius: 6;" +
                    "-fx-border-color: #334155;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 6;");

            Label nodeLbl = new Label("Node " + node);
            nodeLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
            nodeLbl.setTextFill(Color.web(textColor));

            if (isTop) {
                Label topBadge = new Label("TOP ▲");
                topBadge.setFont(Font.font("System", FontWeight.BOLD, 10));
                topBadge.setTextFill(Color.web("#0f172a"));
                entry.getChildren().addAll(nodeLbl, topBadge);
            } else {
                entry.getChildren().add(nodeLbl);
            }

            stackContainer.getChildren().add(entry);
        }
    }

    // ────────────── CODE HIGHLIGHT ──────────────
    private void highlightCodeLine(int lineIdx) {
        int total = codeContainer.getChildren().size();
        for (int i = 0; i < total; i++) {
            HBox row = (HBox) codeContainer.getChildren().get(i);
            if (i == lineIdx) {
                row.setStyle("-fx-background-color: rgba(244, 63, 94, 0.25); -fx-background-radius: 4;");
                if (row.getChildren().size() >= 2) {
                    Label codeLbl = (Label) row.getChildren().get(1);
                    codeLbl.setTextFill(Color.web("#fbbf24"));
                    codeLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
                }
            } else {
                row.setStyle("-fx-background-color: transparent; -fx-background-radius: 4;");
                if (row.getChildren().size() >= 2) {
                    Label codeLbl = (Label) row.getChildren().get(1);
                    codeLbl.setTextFill(Color.web("#e2e8f0"));
                    codeLbl.setFont(Font.font("Consolas", 12));
                }
            }
        }
        // Auto-scroll the ScrollPane so the highlighted line stays visible
        if (total > 0 && lineIdx >= 0) {
            double ratio = (double) lineIdx / Math.max(1, total - 1);
            // Find parent ScrollPane and set its vValue
            javafx.scene.Node parent = codeContainer.getParent();
            while (parent != null && !(parent instanceof ScrollPane)) {
                parent = parent.getParent();
            }
            if (parent instanceof ScrollPane) {
                final ScrollPane sp = (ScrollPane) parent;
                final double targetV = ratio;
                Platform.runLater(() -> sp.setVvalue(targetV));
            }
        }
    }

    // ────────────── RESET ──────────────
    @FXML
    private void resetDFS() {
        stopAuto();
        isRunning = false;
        steps = null;
        stepCodeLine = null;
        stepStackState = null;
        stepConsoleOutput = null;
        stepNodeStates = null;
        currentStep = 0;

        nodeState = new NodeState[numNodes];
        Arrays.fill(nodeState, NodeState.UNVISITED);

        buildCodePanel();
        updateStackVisual(new ArrayDeque<>());
        consoleArea.setText("");
        stackSizeLabel.setText("Size: 0");
        drawGraph();
    }

    // ────────────── NAVIGATION ──────────────
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
