package com.AlgoVista.heap;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Map;

public class heapVisualizer {
    private Canvas canvas;
    private GraphicsContext gc;
    private Map<Integer, NodePosition> nodePositions;

    private static final double NODE_RADIUS = 30;
    private static final double HORIZONTAL_SPACING = 75;
    private static final double VERTICAL_SPACING = 80;

    // Professional premium colors
    private Color normalColor = Color.web("#e0f2fe"); // sky-100
    private Color comparingColor = Color.web("#fef08a"); // yellow-200
    private Color swappingColor = Color.web("#fed7aa"); // orange-200
    private Color insertedColor = Color.web("#bbf7d0"); // green-200
    private Color extractedColor = Color.web("#fecaca"); // red-200

    private Map<Integer, Color> nodeColors;

    public String getNodeStateColorHex(int index) {
        Color c = nodeColors.getOrDefault(index, normalColor);
        if (c.equals(comparingColor)) return "#fef08a";
        if (c.equals(swappingColor)) return "#fed7aa";
        if (c.equals(insertedColor)) return "#bbf7d0";
        if (c.equals(extractedColor)) return "#fecaca";
        return "#e0f2fe";
    }

    public String getNodeStateStrokeHex(int index) {
        Color c = nodeColors.getOrDefault(index, normalColor);
        if (c.equals(comparingColor)) return "#eab308";
        if (c.equals(swappingColor)) return "#f97316";
        if (c.equals(insertedColor)) return "#22c55e";
        if (c.equals(extractedColor)) return "#ef4444";
        return "#bae6fd";
    }

    private Color getStrokeColor(Color fill) {
        if (fill.equals(normalColor)) return Color.web("#38bdf8"); // sky-400
        if (fill.equals(comparingColor)) return Color.web("#eab308"); // yellow-500
        if (fill.equals(swappingColor)) return Color.web("#f97316"); // orange-500
        if (fill.equals(insertedColor)) return Color.web("#22c55e"); // green-500
        if (fill.equals(extractedColor)) return Color.web("#ef4444"); // red-500
        return Color.BLACK;
    }

    public static class NodePosition {
        double x, y;
        public NodePosition(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public heapVisualizer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.nodePositions = new HashMap<>();
        this.nodeColors = new HashMap<>();
    }

    public void drawHeap(heapModel model) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (model.isEmpty()) {
            gc.setFill(Color.GRAY);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Heap is Empty - Generate or Insert to start",
                    canvas.getWidth() / 2, canvas.getHeight() / 2);
            return;
        }

        calculatePositions(model);
        drawEdges(model);
        drawNodes(model);
    }

    private void calculatePositions(heapModel model) {
        nodePositions.clear();
        int totalNodes = model.size();
        if (totalNodes == 0) return;

        int totalHeight = model.getHeight();
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        // Standardized padding and spacing
        double startY = 50;
        double verticalSpacing = Math.min(90, (canvasHeight - 120) / Math.max(1, totalHeight - 1));

        for (int i = 0; i < totalNodes; i++) {
            // Find level and index in level
            int level = (int) (Math.log(i + 1) / Math.log(2));
            int indexInLevel = i - ((int) Math.pow(2, level) - 1);
            int nodesInThisLevel = (int) Math.pow(2, level);

            // Partition the width of the canvas into 2^level equal slots
            double slotWidth = canvasWidth / nodesInThisLevel;
            double x = (indexInLevel * slotWidth) + (slotWidth / 2.0);
            double y = startY + (level * verticalSpacing);

            nodePositions.put(i, new NodePosition(x, y));
        }
    }

    private void drawEdges(heapModel model) {
        gc.setStroke(Color.web("#cbd5e1")); // slate-300
        gc.setLineWidth(2.5);

        // Dynamically get the radius used in drawNodes
        double currentRadius = model.getHeight() > 4 ? 22 : 30;

        for (int i = 0; i < model.size(); i++) {
            NodePosition parentPos = nodePositions.get(i);

            if (model.hasLeftChild(i)) {
                int leftChild = model.getLeftChildIndex(i);
                if (leftChild < model.size()) {
                    NodePosition leftPos = nodePositions.get(leftChild);
                    drawClippedLine(parentPos, leftPos, currentRadius);
                }
            }

            if (model.hasRightChild(i)) {
                int rightChild = model.getRightChildIndex(i);
                if (rightChild < model.size()) {
                    NodePosition rightPos = nodePositions.get(rightChild);
                    drawClippedLine(parentPos, rightPos, currentRadius);
                }
            }
        }
    }

    private void drawClippedLine(NodePosition p1, NodePosition p2, double radius) {
        double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        
        // Start from the edge of the parent node
        double startX = p1.x + Math.cos(angle) * radius;
        double startY = p1.y + Math.sin(angle) * radius;
        
        // End at the edge of the child node
        double endX = p2.x - Math.cos(angle) * radius;
        double endY = p2.y - Math.sin(angle) * radius;
        
        gc.strokeLine(startX, startY, endX, endY);
    }

    private void drawNodes(heapModel model) {
        // Dynamically scale node radius if heap is tall
        double currentRadius = model.getHeight() > 4 ? 22 : 30;
        double fontSize = currentRadius == 30 ? 20 : 15;

        for (int i = 0; i < model.size(); i++) {
            NodePosition pos = nodePositions.get(i);
            Color color = nodeColors.getOrDefault(i, normalColor);

            // Draw shadow
            gc.setFill(Color.rgb(0, 0, 0, 0.1));
            gc.fillOval(pos.x - currentRadius + 2, pos.y - currentRadius + 2,
                    currentRadius * 2, currentRadius * 2);

            // Draw circle
            gc.setFill(color);
            gc.fillOval(pos.x - currentRadius, pos.y - currentRadius,
                    currentRadius * 2, currentRadius * 2);

            gc.setStroke(getStrokeColor(color));
            gc.setLineWidth(currentRadius == 30 ? 3 : 2);
            gc.strokeOval(pos.x - currentRadius, pos.y - currentRadius,
                    currentRadius * 2, currentRadius * 2);

            // Draw value
            gc.setFill(Color.web("#0f172a")); // slate-900
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(model.get(i)), pos.x, pos.y + (currentRadius == 30 ? 7 : 5));

            // Draw index (Top-Right Badge)
            double badgeX = pos.x + currentRadius;
            double badgeY = pos.y - currentRadius - 5;
            
            gc.setFill(Color.web("#cbd5e1")); // slate-300 badge background
            gc.fillRoundRect(badgeX - 8, badgeY - 14, 16, 16, 6, 6);

            gc.setFill(Color.web("#1e293b")); // slate-800 text
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(i), badgeX, badgeY - 2);
        }
    }

    public void setNodeColor(int index, String state) {
        switch (state) {
            case "normal":
                nodeColors.put(index, normalColor);
                break;
            case "comparing":
                nodeColors.put(index, comparingColor);
                break;
            case "swapping":
                nodeColors.put(index, swappingColor);
                break;
            case "inserted":
                nodeColors.put(index, insertedColor);
                break;
            case "extracted":
                nodeColors.put(index, extractedColor);
                break;
        }
    }

    public void clearColors() {
        nodeColors.clear();
    }

    public void resetColors(heapModel model) {
        nodeColors.clear();
        for (int i = 0; i < model.size(); i++) {
            nodeColors.put(i, normalColor);
        }
    }
}