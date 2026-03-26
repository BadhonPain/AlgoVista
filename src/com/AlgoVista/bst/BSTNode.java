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

    /** Returns a structurally identical deep copy of this subtree. */
    public BSTNode deepCopy() {
        BSTNode copy = new BSTNode(this.value);
        copy.x = this.x;
        copy.y = this.y;
        if (this.left != null)  copy.left  = this.left.deepCopy();
        if (this.right != null) copy.right = this.right.deepCopy();
        return copy;
    }
}
