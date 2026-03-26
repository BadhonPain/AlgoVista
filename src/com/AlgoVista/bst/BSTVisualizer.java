package com.AlgoVista.bst;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Map;

public class BSTVisualizer {
    private Canvas canvas;
    private GraphicsContext gc;
    private static final double NODE_RADIUS = 28;

    private Map<Integer, String> nodeColors = new HashMap<>();

    // Color palette per state
    private static final Color DEFAULT_START  = Color.web("#38bdf8");
    private static final Color DEFAULT_END    = Color.web("#0369a1");
    private static final Color DEFAULT_STROKE = Color.web("#7dd3fc");

    private static final Color COMPARE_START  = Color.web("#fde68a");
    private static final Color COMPARE_END    = Color.web("#d97706");
    private static final Color COMPARE_STROKE = Color.web("#fbbf24");

    private static final Color MOVE_START  = Color.web("#c4b5fd");
    private static final Color MOVE_END    = Color.web("#7c3aed");
    private static final Color MOVE_STROKE = Color.web("#a78bfa");

    private static final Color SUCCESS_START  = Color.web("#6ee7b7");
    private static final Color SUCCESS_END    = Color.web("#059669");
    private static final Color SUCCESS_STROKE = Color.web("#34d399");

    private static final Color DELETE_START  = Color.web("#fca5a5");
    private static final Color DELETE_END    = Color.web("#b91c1c");
    private static final Color DELETE_STROKE = Color.web("#f87171");

    private static final Color ORANGE_START  = Color.web("#fdba74");
    private static final Color ORANGE_END    = Color.web("#c2410c");
    private static final Color ORANGE_STROKE = Color.web("#fb923c");

    public BSTVisualizer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void clearColors() {
        nodeColors.clear();
    }

    public void setNodeColor(BSTNode node, String state) {
        if (node != null) {
            nodeColors.put(node.value, state);
        }
    }

    public void drawTree(BSTModel model) {
        int depth = model.getMaxDepth();

        double requiredWidth  = Math.max(900, Math.pow(2, Math.max(depth - 1, 1)) * 70);
        double requiredHeight = Math.max(520, depth * 85 + 80);

        canvas.setWidth(requiredWidth);
        canvas.setHeight(requiredHeight);

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#1e293b"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGrid();

        model.updatePositions(canvas.getWidth(), canvas.getHeight());

        BSTNode effectiveRoot = model.getEffectiveRoot();
        if (effectiveRoot == null) {
            gc.setFill(Color.web("#334155"));
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("BST is Empty — Insert or Generate to begin",
                    canvas.getWidth() / 2, canvas.getHeight() / 2);
            return;
        }

        drawEdges(effectiveRoot);
        drawNodes(effectiveRoot);
    }

    private void drawGrid() {
        gc.setStroke(Color.color(1, 1, 1, 0.02));
        gc.setLineWidth(1);
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        for (double x = 0; x < w; x += 35) gc.strokeLine(x, 0, x, h);
        for (double y = 0; y < h; y += 35) gc.strokeLine(0, y, w, y);
    }

    private void drawEdges(BSTNode node) {
        if (node == null) return;

        if (node.left != null) {
            drawEdge(node, node.left, false);
            drawEdges(node.left);
        }
        if (node.right != null) {
            drawEdge(node, node.right, true);
            drawEdges(node.right);
        }
    }

    private void drawEdge(BSTNode from, BSTNode to, boolean isRight) {
        // Glow shadow
        gc.setStroke(Color.color(0.22, 0.62, 0.98, 0.12));
        gc.setLineWidth(6);
        gc.strokeLine(from.x, from.y, to.x, to.y);

        // Main edge
        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(2.2);
        gc.strokeLine(from.x, from.y, to.x, to.y);

        // Directional arrow tip
        drawArrow(from.x, from.y, to.x, to.y);
    }

    private void drawArrow(double x1, double y1, double x2, double y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double tipX = x2 - NODE_RADIUS * Math.cos(angle);
        double tipY = y2 - NODE_RADIUS * Math.sin(angle);
        double arrowLen = 10;
        double arrowAngle = Math.toRadians(22);

        double ax1 = tipX - arrowLen * Math.cos(angle - arrowAngle);
        double ay1 = tipY - arrowLen * Math.sin(angle - arrowAngle);
        double ax2 = tipX - arrowLen * Math.cos(angle + arrowAngle);
        double ay2 = tipY - arrowLen * Math.sin(angle + arrowAngle);

        gc.setStroke(Color.web("#475569"));
        gc.setLineWidth(1.8);
        gc.strokeLine(tipX, tipY, ax1, ay1);
        gc.strokeLine(tipX, tipY, ax2, ay2);
    }

    private void drawNodes(BSTNode node) {
        if (node == null) return;

        // Look up state by value (works even with snapshot copies which are different objects)
        String state = nodeColors.getOrDefault(node.value, "default");

        Color startColor, endColor, strokeColor;

        switch (state) {
            case "comparing":
                startColor = COMPARE_START; endColor = COMPARE_END; strokeColor = COMPARE_STROKE;
                break;
            case "moving_left":
            case "moving_right":
                startColor = MOVE_START; endColor = MOVE_END; strokeColor = MOVE_STROKE;
                break;
            case "inserted":
            case "found":
            case "traversing":
                startColor = SUCCESS_START; endColor = SUCCESS_END; strokeColor = SUCCESS_STROKE;
                break;
            case "deleted":
            case "found_delete":
            case "replace_value":
                startColor = DELETE_START; endColor = DELETE_END; strokeColor = DELETE_STROKE;
                break;
            case "finding_successor":
            case "found_successor":
                startColor = ORANGE_START; endColor = ORANGE_END; strokeColor = ORANGE_STROKE;
                break;
            default:
                startColor = DEFAULT_START; endColor = DEFAULT_END; strokeColor = DEFAULT_STROKE;
        }

        double cx = node.x;
        double cy = node.y;
        double r = NODE_RADIUS;

        // Outer glow ring (for highlighted nodes)
        if (!state.equals("default")) {
            gc.setFill(Color.color(
                    strokeColor.getRed(), strokeColor.getGreen(), strokeColor.getBlue(), 0.18));
            gc.fillOval(cx - r - 8, cy - r - 8, (r + 8) * 2, (r + 8) * 2);
        }

        // Drop shadow
        gc.setFill(Color.color(0, 0, 0, 0.5));
        gc.fillOval(cx - r + 3, cy - r + 5, r * 2, r * 2);

        // Radial gradient fill
        RadialGradient fill = new RadialGradient(
                -45, 0.3, cx - r * 0.3, cy - r * 0.3, r * 1.4,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, startColor.brighter()),
                new Stop(0.6, startColor),
                new Stop(1, endColor)
        );
        gc.setFill(fill);
        gc.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Gloss highlight (inner top-left sheen)
        RadialGradient gloss = new RadialGradient(
                0, 0, cx - r * 0.35, cy - r * 0.35, r * 0.7,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.color(1, 1, 1, 0.28)),
                new Stop(1, Color.color(1, 1, 1, 0.0))
        );
        gc.setFill(gloss);
        gc.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Border stroke
        gc.setStroke(strokeColor);
        gc.setLineWidth(state.equals("default") ? 1.8 : 2.5);
        gc.strokeOval(cx - r, cy - r, r * 2, r * 2);

        // Value text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(node.value), cx, cy + 5.5);

        // Recurse
        drawNodes(node.left);
        drawNodes(node.right);
    }
}
