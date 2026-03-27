package com.AlgoVista.trees;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.AlgoVista.utils.ShortcutManager;

import java.io.IOException;
import java.util.*;

public class TreeTraversalController {

    // ─── FXML fields ───────────────────────────────────────────
    @FXML private Canvas      treeCanvas;
    @FXML private VBox        codeContainer;
    @FXML private VBox        callStackContainer;
    @FXML private HBox        sequenceBox;
    @FXML private TextArea    outputArea;
    @FXML private Label       sequenceTitle;
    @FXML private Label       algoLabel;
    @FXML private Label       stepCountLabel;
    @FXML private Label       callStackSizeLabel;
    @FXML private Slider      speedSlider;
    @FXML private Label       speedLabel;
    @FXML private TextField   treeInputField;
    @FXML private Button      preOrderBtn, inOrderBtn, postOrderBtn;

    // ─── Tree node ─────────────────────────────────────────────
    private static class TNode {
        int val;
        TNode left, right;
        double x, y;           // canvas position
        TNode(int v) { val = v; }
    }

    // ─── Traversal mode ────────────────────────────────────────
    private enum Mode { PRE, IN, POST }
    private Mode currentMode = Mode.PRE;

    // ─── Node visual state ─────────────────────────────────────
    private enum NodeState { UNVISITED, VISITING, VISITED }
    private Map<TNode, NodeState> nodeStateMap = new LinkedHashMap<>();
    private TNode root;

    // ─── Simulation snapshots ──────────────────────────────────
    private record Snapshot(
        int codeLine,
        Map<TNode, NodeState> states,
        List<String> callStack,    // top at index 0
        String outputSoFar,
        TNode current,
        List<TNode> visitedOrder
    ) {}
    private List<Snapshot> snapshots;
    private int currentStep;

    private boolean isRunning;
    private Timeline autoTimeline;

    // ─── Code definitions ──────────────────────────────────────
    private static final String[] PRE_CODE = {
        "def pre_order(node):",
        "    if node is None:",
        "        return",
        "    print(node.val)     # Visit",
        "    pre_order(node.left)",
        "    pre_order(node.right)"
    };
    private static final String[] IN_CODE = {
        "def in_order(node):",
        "    if node is None:",
        "        return",
        "    in_order(node.left)",
        "    print(node.val)     # Visit",
        "    in_order(node.right)"
    };
    private static final String[] POST_CODE = {
        "def post_order(node):",
        "    if node is None:",
        "        return",
        "    post_order(node.left)",
        "    post_order(node.right)",
        "    print(node.val)     # Visit"
    };

    // ─── Accent colors per mode ────────────────────────────────
    private String accentHex() {
        switch (currentMode) {
            case IN:   return "#10b981";
            case POST: return "#ec4899";
            default:   return "#8b5cf6";
        }
    }

    @FXML
    public void initialize() {
        buildDefaultTree();
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (speedLabel != null) {
                speedLabel.setText(String.format("%.1fx", newVal.doubleValue() / 1000.0));
            }
        });

        treeCanvas.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ShortcutManager.register(newVal,
                    this::runTraversal,
                    this::stepForward,
                    this::resetTraversal,
                    this::backToCategory
                );
            }
        });
    }

    // ━━━━━━━━━━ MODE SWITCHING ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @FXML public void setPreOrder()  { currentMode = Mode.PRE;  applyModeSwitch(); }
    @FXML public void setInOrder()   { currentMode = Mode.IN;   applyModeSwitch(); }
    @FXML public void setPostOrder() { currentMode = Mode.POST; applyModeSwitch(); }

    private void applyModeSwitch() {
        updateTabStyles();
        resetTraversal();
    }

    private void updateTabStyles() {
        String acc = accentHex();
        String active   = "-fx-background-color: " + acc + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12; -fx-padding: 6 18; -fx-background-radius: 7; -fx-cursor: hand;";
        String inactive = "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-font-size: 12; -fx-padding: 6 18; -fx-background-radius: 7; -fx-cursor: hand;";

        preOrderBtn.setStyle(currentMode == Mode.PRE  ? active : inactive);
        inOrderBtn.setStyle(currentMode == Mode.IN    ? active : inactive);
        postOrderBtn.setStyle(currentMode == Mode.POST ? active : inactive);

        String title = switch (currentMode) { case IN -> "In-Order Sequence"; case POST -> "Post-Order Sequence"; default -> "Pre-Order Sequence"; };
        String algo  = switch (currentMode) { case IN -> "In-Order Traversal"; case POST -> "Post-Order Traversal"; default -> "Pre-Order Traversal"; };
        sequenceTitle.setText(title);
        sequenceTitle.setTextFill(Color.web(accentHex()));
        algoLabel.setText(algo);
    }

    // ━━━━━━━━━━ TREE BUILDING ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @FXML
    private void buildDefaultTree() {
        root = insertLevelOrder(new int[]{1, 2, 3, 4, 5, 6, 7});
        finalizeBuild();
    }

    @FXML
    private void buildCustomTree() {
        String text = treeInputField.getText().trim();
        if (text.isEmpty()) { buildDefaultTree(); return; }
        try {
            String[] parts = text.split("[,\\s]+");
            int[] vals = new int[parts.length];
            for (int i = 0; i < parts.length; i++) vals[i] = Integer.parseInt(parts[i].trim());
            root = insertLevelOrder(vals);
            finalizeBuild();
        } catch (NumberFormatException ex) {
            outputArea.setText("⚠ Invalid input. Use comma-separated integers.");
        }
    }

    private void finalizeBuild() {
        computeNodePositions(root, 0, treeCanvas.getWidth(), 0, 55);
        resetTraversal();
    }

    /** Build a complete binary tree in level-order from an array. */
    private TNode insertLevelOrder(int[] vals) {
        if (vals.length == 0) return null;
        TNode[] nodes = new TNode[vals.length];
        for (int i = 0; i < vals.length; i++) nodes[i] = new TNode(vals[i]);
        for (int i = 0; i < vals.length; i++) {
            int l = 2 * i + 1, r = 2 * i + 2;
            if (l < vals.length) nodes[i].left  = nodes[l];
            if (r < vals.length) nodes[i].right = nodes[r];
        }
        return nodes[0];
    }

    /** Position nodes for a nice tree layout. */
    private void computeNodePositions(TNode node, double xMin, double xMax, int depth, double yStep) {
        if (node == null) return;
        node.x = (xMin + xMax) / 2.0;
        node.y = 50 + depth * yStep;
        computeNodePositions(node.left,  xMin, node.x, depth + 1, yStep);
        computeNodePositions(node.right, node.x, xMax, depth + 1, yStep);
    }

    // ━━━━━━━━━━ BUILD SNAPSHOTS ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private void buildSnapshots() {
        snapshots = new ArrayList<>();
        Map<TNode, NodeState> states = new LinkedHashMap<>();
        collectNodes(root, states);   // all UNVISITED

        List<String> callStack  = new ArrayList<>();
        List<String> outputSoFar = new ArrayList<>() {
            final StringBuilder sb = new StringBuilder();
            @Override public boolean add(String s) { sb.append(s).append("\n"); return true; }
        };
        List<TNode> visitedOrder = new ArrayList<>();
        // Use a wrapper so we can modify from lambda
        final StringBuilder output = new StringBuilder();

        switch (currentMode) {
            case PRE  -> snapshotPre (root, states, callStack, output, visitedOrder);
            case IN   -> snapshotIn  (root, states, callStack, output, visitedOrder);
            case POST -> snapshotPost(root, states, callStack, output, visitedOrder);
        }

        currentStep = 0;
    }

    private void collectNodes(TNode n, Map<TNode, NodeState> m) {
        if (n == null) return;
        m.put(n, NodeState.UNVISITED);
        collectNodes(n.left,  m);
        collectNodes(n.right, m);
    }

    private void snap(int line, Map<TNode, NodeState> states, List<String> stack,
                      StringBuilder output, TNode current, List<TNode> visited) {
        Map<TNode, NodeState> copy = new LinkedHashMap<>(states);
        List<String> stackCopy = new ArrayList<>(stack);
        List<TNode>  visitCopy = new ArrayList<>(visited);
        snapshots.add(new Snapshot(line, copy, stackCopy, output.toString(), current, visitCopy));
    }

    private void snapshotPre(TNode n, Map<TNode, NodeState> st, List<String> cs,
                              StringBuilder out, List<TNode> vo) {
        snap(0, st, cs, out, n, vo);          // def pre_order
        snap(1, st, cs, out, n, vo);          // if node is None
        if (n == null) { snap(2, st, cs, out, n, vo); return; }

        cs.add(0, "pre_order(" + n.val + ")");
        st.put(n, NodeState.VISITING);
        snap(3, st, cs, out, n, vo);          // print

        st.put(n, NodeState.VISITED);
        out.append("Visited: ").append(n.val).append("\n");
        vo.add(n);
        snap(3, st, cs, out, n, vo);

        snap(4, st, cs, out, n, vo);          // recurse left
        snapshotPre(n.left, st, cs, out, vo);
        snap(5, st, cs, out, n, vo);          // recurse right
        snapshotPre(n.right, st, cs, out, vo);

        cs.remove(0);
    }

    private void snapshotIn(TNode n, Map<TNode, NodeState> st, List<String> cs,
                             StringBuilder out, List<TNode> vo) {
        snap(0, st, cs, out, n, vo);
        snap(1, st, cs, out, n, vo);
        if (n == null) { snap(2, st, cs, out, n, vo); return; }

        cs.add(0, "in_order(" + n.val + ")");
        st.put(n, NodeState.VISITING);

        snap(3, st, cs, out, n, vo);          // recurse left
        snapshotIn(n.left, st, cs, out, vo);

        snap(4, st, cs, out, n, vo);          // print
        st.put(n, NodeState.VISITED);
        out.append("Visited: ").append(n.val).append("\n");
        vo.add(n);
        snap(4, st, cs, out, n, vo);

        snap(5, st, cs, out, n, vo);          // recurse right
        snapshotIn(n.right, st, cs, out, vo);

        cs.remove(0);
    }

    private void snapshotPost(TNode n, Map<TNode, NodeState> st, List<String> cs,
                               StringBuilder out, List<TNode> vo) {
        snap(0, st, cs, out, n, vo);
        snap(1, st, cs, out, n, vo);
        if (n == null) { snap(2, st, cs, out, n, vo); return; }

        cs.add(0, "post_order(" + n.val + ")");
        st.put(n, NodeState.VISITING);

        snap(3, st, cs, out, n, vo);           // recurse left
        snapshotPost(n.left, st, cs, out, vo);
        snap(4, st, cs, out, n, vo);           // recurse right
        snapshotPost(n.right, st, cs, out, vo);

        snap(5, st, cs, out, n, vo);           // print
        st.put(n, NodeState.VISITED);
        out.append("Visited: ").append(n.val).append("\n");
        vo.add(n);
        snap(5, st, cs, out, n, vo);

        cs.remove(0);
    }

    // ━━━━━━━━━━ PLAYBACK ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @FXML private void runTraversal() {
        if (snapshots == null) buildSnapshots();
        if (!isRunning) {
            isRunning = true;
            double invertedDelay = speedSlider.getMax() + speedSlider.getMin() - speedSlider.getValue();
            autoTimeline = new Timeline(new KeyFrame(Duration.millis(invertedDelay), e -> {
                if (currentStep < snapshots.size()) applyStep(currentStep++);
                else stopAuto();
            }));
            autoTimeline.setCycleCount(Timeline.INDEFINITE);
            autoTimeline.play();
        }
    }

    @FXML private void stepForward() {
        stopAuto();
        if (snapshots == null) buildSnapshots();
        if (currentStep < snapshots.size()) applyStep(currentStep++);
    }

    @FXML private void resetTraversal() {
        stopAuto();
        snapshots = null;
        currentStep = 0;
        isRunning = false;
        nodeStateMap.clear();
        collectNodes(root, nodeStateMap);
        buildCodePanel();
        updateTabStyles();
        sequenceBox.getChildren().clear();
        callStackContainer.getChildren().clear();
        outputArea.setText("");
        stepCountLabel.setText("Step 0 / 0");
        callStackSizeLabel.setText("Depth: 0");
        drawTree(null);
    }

    private void stopAuto() {
        if (autoTimeline != null) autoTimeline.stop();
        isRunning = false;
    }

    private void applyStep(int idx) {
        if (idx >= snapshots.size()) return;
        Snapshot s = snapshots.get(idx);
        nodeStateMap = new LinkedHashMap<>(s.states());
        drawTree(s.current());
        highlightCodeLine(s.codeLine());
        updateCallStack(s.callStack());
        updateSequence(s.visitedOrder());
        outputArea.setText(s.outputSoFar());
        outputArea.setScrollTop(Double.MAX_VALUE);
        stepCountLabel.setText("Step " + (idx + 1) + " / " + snapshots.size());
    }

    // ━━━━━━━━━━ DRAWING ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private void drawTree(TNode current) {
        GraphicsContext gc = treeCanvas.getGraphicsContext2D();
        double w = treeCanvas.getWidth(), h = treeCanvas.getHeight();
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.web("#0a0f1e"));
        gc.fillRect(0, 0, w, h);

        drawEdges(gc, root);
        drawNodes(gc, root, current);
    }

    private void drawEdges(GraphicsContext gc, TNode n) {
        if (n == null) return;
        gc.setStroke(Color.web("#1e3a5f"));
        gc.setLineWidth(2);
        if (n.left  != null) gc.strokeLine(n.x, n.y, n.left.x,  n.left.y);
        if (n.right != null) gc.strokeLine(n.x, n.y, n.right.x, n.right.y);
        drawEdges(gc, n.left);
        drawEdges(gc, n.right);
    }

    private void drawNodes(GraphicsContext gc, TNode n, TNode current) {
        if (n == null) return;
        double r = 22;
        NodeState st = nodeStateMap.getOrDefault(n, NodeState.UNVISITED);
        boolean isCurrent = (n == current);

        Color fill = switch (st) {
            case VISITING -> Color.web("#a78bfa");
            case VISITED  -> Color.web("#22c55e");
            default       -> Color.web("#334155");
        };
        if (isCurrent) fill = Color.web("#f59e0b");

        if (isCurrent || st == NodeState.VISITING) {
            gc.setEffect(new Glow(0.6));
        }

        gc.setFill(fill);
        gc.fillOval(n.x - r, n.y - r, r * 2, r * 2);
        gc.setEffect(null);

        // Border
        gc.setStroke(fill.brighter());
        gc.setLineWidth(2.5);
        gc.strokeOval(n.x - r, n.y - r, r * 2, r * 2);

        // Label
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        String lbl = String.valueOf(n.val);
        gc.fillText(lbl, n.x - lbl.length() * 4.5, n.y + 5);

        drawNodes(gc, n.left, current);
        drawNodes(gc, n.right, current);
    }

    // ━━━━━━━━━━ SEQUENCE CHIP ROW ━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private void updateSequence(List<TNode> visited) {
        sequenceBox.getChildren().clear();
        String acc = accentHex();
        for (int i = 0; i < visited.size(); i++) {
            TNode n = visited.get(i);
            boolean isLast = (i == visited.size() - 1);

            Label chip = new Label(String.valueOf(n.val));
            chip.setFont(Font.font("System", FontWeight.BOLD, 13));
            chip.setPadding(new Insets(5, 12, 5, 12));
            chip.setTextFill(isLast ? Color.web("#0f172a") : Color.web(acc));
            chip.setStyle("-fx-background-color: " + (isLast ? acc : "rgba(139,92,246,0.12)") + ";"
                    + "-fx-background-radius: 20;"
                    + "-fx-border-color: " + acc + ";"
                    + "-fx-border-width: 1.5;"
                    + "-fx-border-radius: 20;");
            sequenceBox.getChildren().add(chip);

            if (i < visited.size() - 1) {
                Label arrow = new Label("→");
                arrow.setTextFill(Color.web("#334155"));
                arrow.setFont(Font.font("System", 12));
                sequenceBox.getChildren().add(arrow);
            }
        }
    }

    // ━━━━━━━━━━ CALL STACK VISUALS ━━━━━━━━━━━━━━━━━━━━━━━━━━
    private void updateCallStack(List<String> stack) {
        callStackContainer.getChildren().clear();
        callStackSizeLabel.setText("Depth: " + stack.size());

        if (stack.isEmpty()) {
            Label empty = new Label("[ Empty ]");
            empty.setTextFill(Color.web("#4b5563"));
            empty.setFont(Font.font("Consolas", 12));
            callStackContainer.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < stack.size(); i++) {
            boolean isTop = (i == 0);
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER);
            row.setPadding(new Insets(5, 12, 5, 12));
            String bg = isTop ? accentHex() : "#1e3a5f";
            String tc = isTop ? "#0f172a" : "#94a3b8";
            row.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 6;");

            Label lbl = new Label(stack.get(i));
            lbl.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
            lbl.setTextFill(Color.web(tc));
            row.getChildren().add(lbl);
            if (isTop) {
                Label badge = new Label("TOP ▲");
                badge.setFont(Font.font("System", FontWeight.BOLD, 9));
                badge.setTextFill(Color.web("#0f172a"));
                row.getChildren().add(badge);
            }
            callStackContainer.getChildren().add(row);
        }
    }

    // ━━━━━━━━━━ CODE PANEL ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private String[] currentCode() {
        return switch (currentMode) { case IN -> IN_CODE; case POST -> POST_CODE; default -> PRE_CODE; };
    }

    private void buildCodePanel() {
        codeContainer.getChildren().clear();
        codeContainer.setStyle("-fx-background-color: #0a0f1e; -fx-padding: 10; -fx-background-radius: 8;");
        String[] lines = currentCode();
        for (int i = 0; i < lines.length; i++) {
            HBox row = new HBox(8);
            row.setPadding(new Insets(2, 6, 2, 6));
            row.setStyle("-fx-background-color: transparent; -fx-background-radius: 4;");

            Label num = new Label(String.format("%2d", i + 1));
            num.setFont(Font.font("Consolas", 11));
            num.setTextFill(Color.web("#4b5563"));
            num.setMinWidth(22);

            Label code = new Label(lines[i]);
            code.setFont(Font.font("Consolas", 11));
            code.setTextFill(Color.web("#e2e8f0"));

            row.getChildren().addAll(num, code);
            codeContainer.getChildren().add(row);
        }
    }

    private void highlightCodeLine(int idx) {
        String acc = "rgba(" + hexToRgb(accentHex()) + ", 0.2)";
        int total = codeContainer.getChildren().size();
        for (int i = 0; i < total; i++) {
            HBox row = (HBox) codeContainer.getChildren().get(i);
            boolean active = (i == idx);
            row.setStyle("-fx-background-color: " + (active ? acc : "transparent") + "; -fx-background-radius: 4;");
            if (row.getChildren().size() >= 2) {
                Label lbl = (Label) row.getChildren().get(1);
                lbl.setTextFill(active ? Color.web("#fbbf24") : Color.web("#e2e8f0"));
                lbl.setFont(Font.font("Consolas", active ? FontWeight.BOLD : FontWeight.NORMAL, 11));
            }
        }
        // Auto-scroll
        if (total > 0) {
            double ratio = (double) idx / Math.max(1, total - 1);
            javafx.scene.Node p = codeContainer.getParent();
            while (p != null && !(p instanceof ScrollPane)) p = p.getParent();
            if (p instanceof ScrollPane sp) Platform.runLater(() -> sp.setVvalue(ratio));
        }
    }

    /** Convert "#rrggbb" to "r, g, b" for rgba CSS use. */
    private String hexToRgb(String hex) {
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0,2), 16);
        int g = Integer.parseInt(hex.substring(2,4), 16);
        int b = Integer.parseInt(hex.substring(4,6), 16);
        return r + ", " + g + ", " + b;
    }

    // ━━━━━━━━━━ NAVIGATION ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    @FXML private void backToCategory() {
        stopAuto();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecursionCategory.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) treeCanvas.getScene().getWindow();
            double w = stage.getWidth(), h = stage.getHeight(), x = stage.getX(), y = stage.getY();
            stage.setScene(new Scene(root, w, h));
            stage.setX(x); stage.setY(y);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
