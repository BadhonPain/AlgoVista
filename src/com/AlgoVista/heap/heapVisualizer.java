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

    // Professional colors
    private Color normalColor = Color.rgb(135, 206, 250);
    private Color comparingColor = Color.rgb(255, 255, 102);
    private Color swappingColor = Color.rgb(255, 165, 0);
    private Color insertedColor = Color.rgb(144, 238, 144);
    private Color extractedColor = Color.rgb(255, 160, 160);

    private Map<Integer, Color> nodeColors;

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
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

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

        int height = model.getHeight();
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        // Better spacing calculation
        int maxNodesInLastLevel = (int) Math.pow(2, height - 1);
        double minSpacing = 65;

        double startY = 45;
        double verticalSpacing = Math.min(80, (canvasHeight - 120) / Math.max(1, height - 1));

        int nodeIndex = 0;
        for (int level = 0; level < height && nodeIndex < model.size(); level++) {
            int nodesInLevel = (int) Math.pow(2, level);
            int actualNodesInLevel = Math.min(nodesInLevel, model.size() - nodeIndex);

            // Dynamic spacing based on level
            double levelSpacing = minSpacing * Math.pow(2, height - level - 1);
            double levelWidth = (actualNodesInLevel - 1) * levelSpacing;
            double startX = (canvasWidth - levelWidth) / 2;
            double y = startY + level * verticalSpacing;

            for (int i = 0; i < actualNodesInLevel && nodeIndex < model.size(); i++) {
                double x = startX + i * levelSpacing;
                nodePositions.put(nodeIndex, new NodePosition(x, y));
                nodeIndex++;
            }
        }
    }

    private void drawEdges(heapModel model) {
        gc.setStroke(Color.rgb(100, 100, 100));
        gc.setLineWidth(2.5);

        for (int i = 0; i < model.size(); i++) {
            NodePosition parentPos = nodePositions.get(i);

            if (model.hasLeftChild(i)) {
                int leftChild = model.getLeftChildIndex(i);
                NodePosition leftPos = nodePositions.get(leftChild);
                gc.strokeLine(parentPos.x, parentPos.y + NODE_RADIUS,
                        leftPos.x, leftPos.y - NODE_RADIUS);
            }

            if (model.hasRightChild(i)) {
                int rightChild = model.getRightChildIndex(i);
                NodePosition rightPos = nodePositions.get(rightChild);
                gc.strokeLine(parentPos.x, parentPos.y + NODE_RADIUS,
                        rightPos.x, rightPos.y - NODE_RADIUS);
            }
        }
    }

    private void drawNodes(heapModel model) {
        for (int i = 0; i < model.size(); i++) {
            NodePosition pos = nodePositions.get(i);
            Color color = nodeColors.getOrDefault(i, normalColor);

            // Draw shadow
            gc.setFill(Color.rgb(0, 0, 0, 0.2));
            gc.fillOval(pos.x - NODE_RADIUS + 3, pos.y - NODE_RADIUS + 3,
                    NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Draw circle
            gc.setFill(color);
            gc.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                    NODE_RADIUS * 2, NODE_RADIUS * 2);

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(3);
            gc.strokeOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                    NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Draw value (black, bold)
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(model.get(i)), pos.x, pos.y + 7);

            // Draw index BELOW node (RED, bold - with more spacing)
            gc.setFill(Color.RED);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            gc.fillText(String.valueOf(i), pos.x, pos.y + NODE_RADIUS + 25);
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