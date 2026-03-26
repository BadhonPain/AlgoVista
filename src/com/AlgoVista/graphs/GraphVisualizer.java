package com.AlgoVista.graphs;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class GraphVisualizer {
    private Canvas canvas;
    private GraphicsContext gc;
    private static final double NODE_RADIUS = 25;

    public GraphVisualizer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void drawGraph(GraphModel model) {
        drawGraph(model, null);
    }

    public void drawGraph(GraphModel model, Integer selectedNodeIndex) {
        // Clear canvas with transparent or white background
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw edges first (so they're behind nodes)
        drawEdges(model);

        // Draw nodes
        drawNodes(model, selectedNodeIndex);
    }

    private void drawEdges(GraphModel model) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        for (GraphModel.Edge edge : model.getEdgeList()) {
            if (!model.isDirected() && edge.from > edge.to) continue;

            Point2D fromPos = model.getNodePosition(edge.from);
            Point2D toPos = model.getNodePosition(edge.to);

            if (fromPos != null && toPos != null) {
                gc.strokeLine(fromPos.getX(), fromPos.getY(), toPos.getX(), toPos.getY());

                if (model.isWeighted() && edge.weight > 1) {
                    double midX = (fromPos.getX() + toPos.getX()) / 2;
                    double midY = (fromPos.getY() + toPos.getY()) / 2;

                    gc.setFill(Color.BLUE);
                    gc.setFont(Font.font("System", 14));
                    gc.fillText(String.valueOf(edge.weight), midX, midY);
                }

                if (model.isDirected()) {
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

    private void drawNodes(GraphModel model, Integer selectedNodeIndex) {
        for (int i = 0; i < model.getNumNodes(); i++) {
            Point2D pos = model.getNodePosition(i);
            if (pos != null) {
                // Determine node color (Yellow if selected)
                if (selectedNodeIndex != null && selectedNodeIndex == i) {
                    gc.setFill(Color.YELLOW);
                } else {
                    gc.setFill(Color.LIGHTBLUE);
                }

                gc.fillOval(pos.getX() - NODE_RADIUS, pos.getY() - NODE_RADIUS,
                        NODE_RADIUS * 2, NODE_RADIUS * 2);

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(2);
                gc.strokeOval(pos.getX() - NODE_RADIUS, pos.getY() - NODE_RADIUS,
                        NODE_RADIUS * 2, NODE_RADIUS * 2);

                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("System", FontWeight.BOLD, 16));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.valueOf(i), pos.getX(), pos.getY() + 5);
            }
        }
    }
}