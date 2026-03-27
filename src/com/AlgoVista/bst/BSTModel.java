package com.AlgoVista.bst;

import java.util.ArrayList;
import java.util.List;

public class BSTModel {

    public static class BSTOperation {
        public String type; 
        public BSTNode targetNode;
        public String description;
        public String complexity;
        public List<Integer> queueState;

        public BSTOperation(String type, BSTNode targetNode, String description, String complexity) {
            this(type, targetNode, description, complexity, null);
        }

        public BSTOperation(String type, BSTNode targetNode, String description, String complexity, List<Integer> queueState) {
            this.type = type;
            this.targetNode = targetNode;
            this.description = description;
            this.complexity = complexity;
            this.queueState = queueState;
        }
    }

    private BSTNode root;
    private BSTNode transientRoot; // Snapshot root used by canvas during animation

    public BSTNode getRoot() { return root; }
    public BSTNode getEffectiveRoot() { return transientRoot != null ? transientRoot : root; }
    public boolean isEmpty() { return root == null; }
    public void clear() { root = null; transientRoot = null; }

    /** Returns a deep copy of the current tree for use as an animation snapshot. */
    public BSTNode deepCopyRoot() {
        return root != null ? root.deepCopy() : null;
    }

    public void setTransientRoot(BSTNode snapshot) { this.transientRoot = snapshot; }
    public void clearTransientRoot() { this.transientRoot = null; }

    // --- INSERTION ---
    public List<BSTOperation> insert(int value) {
        List<BSTOperation> ops = new ArrayList<>();
        if (root == null) {
            root = new BSTNode(value);
            ops.add(new BSTOperation("inserted", root, "Inserted root node " + value, "O(1) — empty tree"));
            return ops;
        }
        ops.add(new BSTOperation("comparing", root, "Insert(" + value + "): Starting at root. Complexity: O(log n) avg, O(n) worst", "O(log n) avg"));
        insertRec(root, value, ops);
        return ops;
    }

    private BSTNode insertRec(BSTNode current, int value, List<BSTOperation> ops) {
        ops.add(new BSTOperation("comparing", current, "Compare " + value + " with " + current.value + " (current node)", "O(1) per step"));

        if (value < current.value) {
            if (current.left == null) {
                current.left = new BSTNode(value);
                ops.add(new BSTOperation("inserted", current.left, "✓ Inserted " + value + " as left child of " + current.value, "O(log n) avg"));
            } else {
                ops.add(new BSTOperation("moving_left", current, value + " < " + current.value + " → Go left", "O(1) per step"));
                current.left = insertRec(current.left, value, ops);
            }
        } else if (value > current.value) {
            if (current.right == null) {
                current.right = new BSTNode(value);
                ops.add(new BSTOperation("inserted", current.right, "✓ Inserted " + value + " as right child of " + current.value, "O(log n) avg"));
            } else {
                ops.add(new BSTOperation("moving_right", current, value + " > " + current.value + " → Go right", "O(1) per step"));
                current.right = insertRec(current.right, value, ops);
            }
        } else {
            ops.add(new BSTOperation("duplicate", current, value + " already exists in BST — skipped.", "O(log n) avg"));
        }
        return current;
    }

    // --- FIND ---
    public List<BSTOperation> find(int value) {
        List<BSTOperation> ops = new ArrayList<>();
        ops.add(new BSTOperation("comparing", root, "Find(" + value + "): Starting search. Complexity: O(log n) avg, O(n) worst", "O(log n) avg"));
        findRec(root, value, ops);
        return ops;
    }

    private void findRec(BSTNode current, int value, List<BSTOperation> ops) {
        if (current == null) {
            ops.add(new BSTOperation("not_found", null, "✗ Value " + value + " not found in BST.", "O(log n) avg"));
            return;
        }
        ops.add(new BSTOperation("comparing", current, "Compare " + value + " with " + current.value, "O(1) per step"));
        if (value == current.value) {
            ops.add(new BSTOperation("found", current, "✓ Found value " + value + "!", "O(log n) avg"));
        } else if (value < current.value) {
            ops.add(new BSTOperation("moving_left", current, value + " < " + current.value + " → Go left", "O(1) per step"));
            findRec(current.left, value, ops);
        } else {
            ops.add(new BSTOperation("moving_right", current, value + " > " + current.value + " → Go right", "O(1) per step"));
            findRec(current.right, value, ops);
        }
    }

    // --- DELETION ---
    public List<BSTOperation> delete(int value) {
        List<BSTOperation> ops = new ArrayList<>();
        ops.add(new BSTOperation("comparing", root, "Delete(" + value + "): Starting search. Complexity: O(log n) avg, O(n) worst", "O(log n) avg"));
        root = deleteRec(root, value, ops);
        return ops;
    }

    private BSTNode deleteRec(BSTNode current, int value, List<BSTOperation> ops) {
        if (current == null) {
            ops.add(new BSTOperation("not_found", null, "✗ Value " + value + " not found for deletion.", "O(log n) avg"));
            return current;
        }

        ops.add(new BSTOperation("comparing", current, "Compare " + value + " with " + current.value, "O(1) per step"));
        if (value < current.value) {
            ops.add(new BSTOperation("moving_left", current, value + " < " + current.value + " → Go left", "O(1) per step"));
            current.left = deleteRec(current.left, value, ops);
        } else if (value > current.value) {
            ops.add(new BSTOperation("moving_right", current, value + " > " + current.value + " → Go right", "O(1) per step"));
            current.right = deleteRec(current.right, value, ops);
        } else {
            ops.add(new BSTOperation("found_delete", current, "✓ Found " + value + " — preparing to delete.", "O(log n) avg"));

            if (current.left == null && current.right == null) {
                ops.add(new BSTOperation("deleted", current, "Case 1: Leaf node — simply removed.", "O(log n) avg"));
                return null;
            }
            if (current.left == null) {
                ops.add(new BSTOperation("deleted", current, "Case 2: No left child — replace with right child.", "O(log n) avg"));
                return current.right;
            } else if (current.right == null) {
                ops.add(new BSTOperation("deleted", current, "Case 2: No right child — replace with left child.", "O(log n) avg"));
                return current.left;
            }

            ops.add(new BSTOperation("finding_successor", current, "Case 3: Two children — finding in-order successor...", "O(log n) avg"));
            BSTNode successor = minValueNode(current.right, ops);
            current.value = successor.value;
            ops.add(new BSTOperation("replace_value", current, "✓ Replaced with successor value " + successor.value, "O(log n) avg"));
            current.right = deleteRec(current.right, successor.value, ops);
        }
        return current;
    }

    private BSTNode minValueNode(BSTNode node, List<BSTOperation> ops) {
        BSTNode current = node;
        while (current.left != null) {
            ops.add(new BSTOperation("moving_left", current, "Searching left for minimum value...", "O(1) per step"));
            current = current.left;
        }
        ops.add(new BSTOperation("found_successor", current, "✓ In-order successor found: " + current.value, "O(log n) avg"));
        return current;
    }

    // --- UPDATE ---
    public List<BSTOperation> update(int oldValue, int newValue) {
        List<BSTOperation> ops = new ArrayList<>();
        ops.add(new BSTOperation("traversing", root, "Starting update: " + oldValue + " -> " + newValue, "O(1)"));
        
        // First check if oldValue exists
        if (getNode(root, oldValue) == null) {
            ops.add(new BSTOperation("not_found", null, "Update failed: Old value " + oldValue + " not found.", "O(h)"));
            return ops;
        }

        // Check if new value already exists to prevent duplicate (if tree doesn't support them)
        if (getNode(root, newValue) != null) {
            ops.add(new BSTOperation("not_found", null, "Update failed: New value " + newValue + " already exists.", "O(h)"));
            return ops;
        }

        ops.add(new BSTOperation("traversing", root, "Deleting old value " + oldValue + "...", "O(h)"));
        ops.addAll(delete(oldValue));

        ops.add(new BSTOperation("traversing", root, "Inserting new value " + newValue + "...", "O(h)"));
        ops.addAll(insert(newValue));
        
        ops.add(new BSTOperation("inserted", getNode(root, newValue), "Successfully updated to " + newValue, "O(h)"));
        return ops;
    }

    private BSTNode getNode(BSTNode current, int value) {
        if (current == null) return null;
        if (value == current.value) return current;
        return value < current.value ? getNode(current.left, value) : getNode(current.right, value);
    }

    // --- TRAVERSALS ---
    private List<Integer> getQueueState(java.util.Queue<BSTNode> queue) {
        List<Integer> state = new ArrayList<>();
        for (BSTNode node : queue) {
            state.add(node.value);
        }
        return state;
    }

    public List<BSTOperation> levelOrderTraversal() {
        List<BSTOperation> ops = new ArrayList<>();
        if (root == null) {
            ops.add(new BSTOperation("not_found", null, "Tree is empty.", "O(1)"));
            return ops;
        }
        
        java.util.Queue<BSTNode> queue = new java.util.LinkedList<>();
        queue.add(root);
        ops.add(new BSTOperation("enqueue", root, "Enqueue root " + root.value, "O(1)", getQueueState(queue)));
        
        while (!queue.isEmpty()) {
            BSTNode current = queue.poll();
            ops.add(new BSTOperation("dequeue", current, "Dequeue " + current.value, "O(1)", getQueueState(queue)));
            ops.add(new BSTOperation("traversing", current, "Visiting " + current.value, "O(N)", getQueueState(queue)));
            
            if (current.left != null) {
                queue.add(current.left);
                ops.add(new BSTOperation("enqueue", current.left, "Enqueue left child " + current.left.value, "O(1)", getQueueState(queue)));
            }
            if (current.right != null) {
                queue.add(current.right);
                ops.add(new BSTOperation("enqueue", current.right, "Enqueue right child " + current.right.value, "O(1)", getQueueState(queue)));
            }
        }
        return ops;
    }

    public List<BSTOperation> inorderTraversal() {
        List<BSTOperation> ops = new ArrayList<>();
        inorderRec(root, ops);
        if (ops.isEmpty()) ops.add(new BSTOperation("not_found", null, "Tree is empty.", "O(1)"));
        return ops;
    }
    private void inorderRec(BSTNode current, List<BSTOperation> ops) {
        if (current != null) {
            inorderRec(current.left, ops);
            ops.add(new BSTOperation("traversing", current, "Visiting " + current.value, "O(N)"));
            inorderRec(current.right, ops);
        }
    }

    public List<BSTOperation> preorderTraversal() {
        List<BSTOperation> ops = new ArrayList<>();
        preorderRec(root, ops);
        if (ops.isEmpty()) ops.add(new BSTOperation("not_found", null, "Tree is empty.", "O(1)"));
        return ops;
    }
    private void preorderRec(BSTNode current, List<BSTOperation> ops) {
        if (current != null) {
            ops.add(new BSTOperation("traversing", current, "Visiting " + current.value, "O(N)"));
            preorderRec(current.left, ops);
            preorderRec(current.right, ops);
        }
    }

    public List<BSTOperation> postorderTraversal() {
        List<BSTOperation> ops = new ArrayList<>();
        postorderRec(root, ops);
        if (ops.isEmpty()) ops.add(new BSTOperation("not_found", null, "Tree is empty.", "O(1)"));
        return ops;
    }
    private void postorderRec(BSTNode current, List<BSTOperation> ops) {
        if (current != null) {
            postorderRec(current.left, ops);
            postorderRec(current.right, ops);
            ops.add(new BSTOperation("traversing", current, "Visiting " + current.value, "O(N)"));
        }
    }
    
    // --- POSITIONING ---
    public void updatePositions(double canvasWidth, double canvasHeight) {
        BSTNode effectiveRoot = getEffectiveRoot();
        if (effectiveRoot != null) {
            int maxDepth = getMaxDepthOf(effectiveRoot);
            double initialXOffset = canvasWidth / 4;
            positionRec(effectiveRoot, canvasWidth / 2, 60, initialXOffset, 70);
        }
    }
    
    // ...

    private void positionRec(BSTNode node, double x, double y, double xOffset, double yStep) {
        if (node != null) {
            node.x = x;
            node.y = y;
            // Prevent overlapping by ensuring min xOffset
            double nextXOffset = Math.max(xOffset / 1.8, 30);
            positionRec(node.left, x - xOffset, y + yStep, nextXOffset, yStep);
            positionRec(node.right, x + xOffset, y + yStep, nextXOffset, yStep);
        }
    }

    public int getMaxDepth() {
        return getMaxDepthOf(getEffectiveRoot());
    }

    private int getMaxDepthOf(BSTNode node) {
        if (node == null) return 0;
        return 1 + Math.max(getMaxDepthOf(node.left), getMaxDepthOf(node.right));
    }
}
