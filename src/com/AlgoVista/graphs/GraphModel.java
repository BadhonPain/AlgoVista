package com.AlgoVista.graphs;

import javafx.geometry.Point2D;
import java.util.*;

public class GraphModel {
    private int numNodes;
    private int[][] adjMatrix;
    private Map<Integer, List<int[]>> adjList; // int[]{neighbor, weight}
    private List<Edge> edgeList;
    private Map<Integer, Point2D> nodePositions;
    private boolean isDirected;
    private boolean isWeighted;

    public static class Edge {
        public int from;
        public int to;
        public int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return from + " -> " + to + (weight > 1 ? " (" + weight + ")" : "");
        }
    }

    public GraphModel(int numNodes, boolean isDirected, boolean isWeighted) {
        this.numNodes = numNodes;
        this.isDirected = isDirected;
        this.isWeighted = isWeighted;
        this.adjMatrix = new int[numNodes][numNodes];
        this.adjList = new HashMap<>();
        this.edgeList = new ArrayList<>();
        this.nodePositions = new HashMap<>();

        // Initialize adjacency list
        for (int i = 0; i < numNodes; i++) {
            adjList.put(i, new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        if (from >= numNodes || to >= numNodes || from < 0 || to < 0) return;

        // Add to matrix
        adjMatrix[from][to] = weight;
        if (!isDirected) {
            adjMatrix[to][from] = weight;
        }

        // Add to adjacency list
        adjList.get(from).add(new int[]{to, weight});
        if (!isDirected) {
            adjList.get(to).add(new int[]{from, weight});
        }

        // Add to edge list
        edgeList.add(new Edge(from, to, weight));
        if (!isDirected) {
            edgeList.add(new Edge(to, from, weight));
        }
    }

    public void setNodePosition(int nodeId, Point2D position) {
        nodePositions.put(nodeId, position);
    }

    public Point2D getNodePosition(int nodeId) {
        return nodePositions.get(nodeId);
    }

    public int getNumNodes() {
        return numNodes;
    }

    public int[][] getAdjMatrix() {
        return adjMatrix;
    }

    public Map<Integer, List<int[]>> getAdjList() {
        return adjList;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    public boolean isDirected() {
        return isDirected;
    }

    public boolean isWeighted() {
        return isWeighted;
    }

    public void clear() {
        adjMatrix = new int[numNodes][numNodes];
        adjList.clear();
        edgeList.clear();
        for (int i = 0; i < numNodes; i++) {
            adjList.put(i, new ArrayList<>());
        }
    }
}