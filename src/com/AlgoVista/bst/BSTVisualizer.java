package com.AlgoVista.bst;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Map;

public class BSTVisualizer {
    private Canvas canvas;
    private GraphicsContext gc;
    private static final double NODE_RADIUS = 25;

    private Map<BSTNode, String> nodeColors = new HashMap<>();

    public BSTVisualizer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void clearColors() {
        nodeColors.clear();
    }

    public void setNodeColor(BSTNode node, String state) {
        if (node != null) {
            nodeColors.put(node, state);
        }
    }

    public void drawTree(BSTModel model) {
        int depth = model.getMaxDepth();
        
        // Calculate needed dimensions based on perfect binary tree leaves
        // At depth D, max leaves = 2^(D-1). Say 60px min distance per leaf.
        double requiredWidth = Math.max(930, Math.pow(2, depth - 1) * 60);
        double requiredHeight = Math.max(500, depth * 70 + 100);
        
        canvas.setWidth(requiredWidth);
        canvas.setHeight(requiredHeight);

        // Clear canvas with a nice dark theme match or slightly distinct color
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#1E1E1E")); // Dark theme to match AlgoVista
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        model.updatePositions(canvas.getWidth(), canvas.getHeight());
        
        // Draw edges first
        drawEdges(model.getRoot());

        // Then draw nodes
        drawNodes(model.getRoot());
    }

    private void drawEdges(BSTNode node) {
        if (node == null) return;
        
        gc.setStroke(Color.web("#808080"));
        gc.setLineWidth(3);

        if (node.left != null) {
            gc.strokeLine(node.x, node.y, node.left.x, node.left.y);
            drawEdges(node.left);
        }
        if (node.right != null) {
            gc.strokeLine(node.x, node.y, node.right.x, node.right.y);
            drawEdges(node.right);
        }
    }

    private void drawNodes(BSTNode node) {
        if (node == null) return;

        String state = nodeColors.getOrDefault(node, "default");
        
        Color startColor = Color.web("#38BDF8"); // Default Sky Blue
        Color endColor = Color.web("#0284C7");
        Color strokeColor = Color.web("#FFFFFF");

        switch (state) {
            case "comparing":
                startColor = Color.web("#FCD34D"); // Yellow Warning
                endColor = Color.web("#D97706");
                break;
            case "moving_left":
            case "moving_right":
                startColor = Color.web("#A78BFA"); // Purple
                endColor = Color.web("#7C3AED");
                break;
            case "inserted":
            case "found":
            case "traversing":
                startColor = Color.web("#4ADE80"); // Green Success
                endColor = Color.web("#16A34A");
                break;
            case "deleted":
            case "found_delete":
            case "replace_value":
                startColor = Color.web("#F87171"); // Red Danger
                endColor = Color.web("#DC2626");
                break;
            case "finding_successor":
            case "found_successor":
                startColor = Color.web("#FB923C"); // Orange
                endColor = Color.web("#EA580C");
                break;
        }

        // Draw shadow
        gc.setFill(Color.color(0, 0, 0, 0.4));
        gc.fillOval(node.x - NODE_RADIUS + 3, node.y - NODE_RADIUS + 3, NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Define gradient
        LinearGradient gradient = new LinearGradient(
                node.x - NODE_RADIUS, node.y - NODE_RADIUS, node.x + NODE_RADIUS, node.y + NODE_RADIUS,
                false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new Stop(0, startColor), new Stop(1, endColor)
        );

        // Draw Node Background
        gc.setFill(gradient);
        gc.fillOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Draw Border
        gc.setStroke(strokeColor);
        gc.setLineWidth(2);
        gc.strokeOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Draw Value
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(node.value), node.x, node.y + 6);

        // Recursively draw children
        drawNodes(node.left);
        drawNodes(node.right);
    }
}
