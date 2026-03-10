package com.AlgoVista.bst;

public class BSTNode {
    int value;
    BSTNode left;
    BSTNode right;
    
    // Coordinates for drawing on the Canvas
    double x;
    double y;

    public BSTNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}
