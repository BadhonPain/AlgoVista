package com.AlgoVista.heap;

import java.util.ArrayList;
import java.util.List;

public class heapModel {
    private List<Integer> heap;
    private boolean isMaxHeap;
    private List<HeapOperation> operations;

    public class HeapOperation {
        public String type;
        public int index1;
        public int index2;
        public int value;
        public String description;
        public String complexity;
        public List<Integer> heapSnapshot;
        public List<Integer> sortedSnapshot;

        public HeapOperation(String type, int index1, int index2, int value, String description, String complexity) {
            this.type = type;
            this.index1 = index1;
            this.index2 = index2;
            this.value = value;
            this.description = description;
            this.complexity = complexity;
            this.heapSnapshot = new ArrayList<>(heap);
        }

        public HeapOperation withSnapshot(List<Integer> customHeap, List<Integer> customSorted) {
            this.heapSnapshot = new ArrayList<>(customHeap);
            if (customSorted != null) {
                this.sortedSnapshot = new ArrayList<>(customSorted);
            }
            return this;
        }
    }

    private List<Integer> transientHeap;
    private List<Integer> lastSortedArray;

    public heapModel(boolean isMaxHeap) {
        this.heap = new ArrayList<>();
        this.isMaxHeap = isMaxHeap;
        this.operations = new ArrayList<>();
    }

    public List<Integer> getLastSortedArray() {
        return lastSortedArray;
    }

    public void setTransientHeap(List<Integer> currentSnapshot) {
        this.transientHeap = currentSnapshot;
    }

    public void clearTransientHeap() {
        this.transientHeap = null;
    }

    public List<Integer> getHeap() {
        return transientHeap != null ? new ArrayList<>(transientHeap) : new ArrayList<>(heap);
    }

    public int size() {
        return transientHeap != null ? transientHeap.size() : heap.size();
    }

    public boolean isEmpty() {
        return transientHeap != null ? transientHeap.isEmpty() : heap.isEmpty();
    }

    public boolean isMaxHeap() {
        return isMaxHeap;
    }

    public void setHeapType(boolean isMaxHeap) {
        this.isMaxHeap = isMaxHeap;
    }

    public int getParentIndex(int i) {
        return (i - 1) / 2;
    }

    public int getLeftChildIndex(int i) {
        return 2 * i + 1;
    }

    public int getRightChildIndex(int i) {
        return 2 * i + 2;
    }

    public boolean hasParent(int i) {
        return i > 0;
    }

    public boolean hasLeftChild(int i) {
        return getLeftChildIndex(i) < heap.size();
    }

    public boolean hasRightChild(int i) {
        return getRightChildIndex(i) < heap.size();
    }

    public int get(int i) {
        return transientHeap != null ? transientHeap.get(i) : heap.get(i);
    }

    public Integer peek() {
        if (isEmpty()) return null;
        return transientHeap != null ? transientHeap.get(0) : heap.get(0);
    }

    // Insert operation
    public List<HeapOperation> insert(int value) {
        operations.clear();
        lastSortedArray = null;

        heap.add(value);
        int currentIndex = heap.size() - 1;

        operations.add(new HeapOperation("insert", currentIndex, -1, value,
                "Inserted " + value + " at index " + currentIndex, "O(log n)"));

        heapifyUp(currentIndex);

        operations.add(new HeapOperation("complete", -1, -1, -1,
                "Insert complete. Heap property restored.", ""));

        return new ArrayList<>(operations);
    }

    private void heapifyUp(int index) {
        while (hasParent(index)) {
            int parentIndex = getParentIndex(index);

            operations.add(new HeapOperation("compare", index, parentIndex, -1,
                    "Compare " + heap.get(index) + " (index " + index + ") with parent " +
                            heap.get(parentIndex) + " (index " + parentIndex + ")", ""));

            boolean shouldSwap = isMaxHeap ?
                    heap.get(index) > heap.get(parentIndex) :
                    heap.get(index) < heap.get(parentIndex);

            if (shouldSwap) {
                operations.add(new HeapOperation("swap", index, parentIndex, -1,
                        "Swap " + heap.get(index) + " ↔ " + heap.get(parentIndex), ""));

                swap(index, parentIndex);
                index = parentIndex;
            } else {
                operations.add(new HeapOperation("done", index, -1, -1,
                        "Heap property satisfied", ""));
                break;
            }
        }
    }

    // Extract operation
    public List<HeapOperation> extract() {
        operations.clear();
        lastSortedArray = null;

        if (heap.isEmpty()) {
            return operations;
        }

        int root = heap.get(0);
        String heapType = isMaxHeap ? "Max" : "Min";

        operations.add(new HeapOperation("extract", 0, -1, root,
                "Extract" + heapType + "(): Removing root " + root, "O(log n)"));

        if (heap.size() == 1) {
            heap.remove(0);
            operations.add(new HeapOperation("complete", -1, -1, -1,
                    "Heap is now empty", ""));
            return new ArrayList<>(operations);
        }

        int lastValue = heap.get(heap.size() - 1);
        heap.set(0, lastValue);
        heap.remove(heap.size() - 1);

        operations.add(new HeapOperation("move", 0, heap.size(), lastValue,
                "Move last element " + lastValue + " to root position", ""));

        heapifyDown(0);

        operations.add(new HeapOperation("complete", -1, -1, root,
                "Extract complete. Returned: " + root, ""));

        return new ArrayList<>(operations);
    }

    private void heapifyDown(int index) {
        while (hasLeftChild(index)) {
            int targetChildIndex = getLeftChildIndex(index);

            if (hasRightChild(index)) {
                int rightChildIndex = getRightChildIndex(index);
                operations.add(new HeapOperation("compare", targetChildIndex, rightChildIndex, -1,
                        "Compare children: " + heap.get(targetChildIndex) + " vs " + heap.get(rightChildIndex), ""));

                boolean rightIsPreferred = isMaxHeap ?
                        heap.get(rightChildIndex) > heap.get(targetChildIndex) :
                        heap.get(rightChildIndex) < heap.get(targetChildIndex);

                if (rightIsPreferred) {
                    targetChildIndex = rightChildIndex;
                }
            }

            operations.add(new HeapOperation("compare", index, targetChildIndex, -1,
                    "Compare " + heap.get(index) + " with child " + heap.get(targetChildIndex), ""));

            boolean shouldSwap = isMaxHeap ?
                    heap.get(index) < heap.get(targetChildIndex) :
                    heap.get(index) > heap.get(targetChildIndex);

            if (shouldSwap) {
                operations.add(new HeapOperation("swap", index, targetChildIndex, -1,
                        "Swap " + heap.get(index) + " ↔ " + heap.get(targetChildIndex), ""));

                swap(index, targetChildIndex);
                index = targetChildIndex;
            } else {
                operations.add(new HeapOperation("done", index, -1, -1,
                        "Heap property satisfied", ""));
                break;
            }
        }
    }

    // Build heap O(n)
    public List<HeapOperation> buildHeapOptimal(List<Integer> values) {
        operations.clear();
        heap.clear();
        lastSortedArray = null;
        heap.addAll(values);

        operations.add(new HeapOperation("build", -1, -1, -1,
                "Building heap in O(n) using bottom-up approach", "O(n)"));

        for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
            operations.add(new HeapOperation("heapify", i, -1, -1,
                    "Heapify subtree rooted at index " + i, ""));
            heapifyDown(i);
        }

        operations.add(new HeapOperation("complete", -1, -1, -1,
                "Build complete. Heap created in O(n) time", ""));

        return new ArrayList<>(operations);
    }

    // Build heap O(n log n)
    public List<HeapOperation> buildHeapNaive(List<Integer> values) {
        operations.clear();
        heap.clear();
        lastSortedArray = null;

        operations.add(new HeapOperation("build", -1, -1, -1,
                "Building heap in O(n log n) using successive insertions", "O(n log n)"));

        for (int value : values) {
            heap.add(value);
            int index = heap.size() - 1;
            operations.add(new HeapOperation("insert", index, -1, value,
                    "Insert " + value + " at index " + index, ""));
            heapifyUp(index);
        }

        operations.add(new HeapOperation("complete", -1, -1, -1,
                "Build complete using O(n log n) insertions", ""));

        return new ArrayList<>(operations);
    }

    // Update key
    public List<HeapOperation> updateKey(int index, int newValue) {
        operations.clear();
        lastSortedArray = null;

        if (index < 0 || index >= heap.size()) {
            operations.add(new HeapOperation("error", -1, -1, -1,
                    "Invalid index: " + index, ""));
            return operations;
        }

        int oldValue = heap.get(index);
        operations.add(new HeapOperation("update", index, -1, newValue,
                "UpdateKey(" + index + ", " + newValue + "): Old value = " + oldValue, "O(log n)"));

        heap.set(index, newValue);

        boolean increased = newValue > oldValue;
        boolean decreased = newValue < oldValue;

        if ((isMaxHeap && increased) || (!isMaxHeap && decreased)) {
            operations.add(new HeapOperation("heapify", index, -1, -1,
                    "Heapify up from index " + index, ""));
            heapifyUp(index);
        } else if ((isMaxHeap && decreased) || (!isMaxHeap && increased)) {
            operations.add(new HeapOperation("heapify", index, -1, -1,
                    "Heapify down from index " + index, ""));
            heapifyDown(index);
        } else {
            operations.add(new HeapOperation("done", index, -1, -1,
                    "No change needed, value is the same", ""));
        }

        operations.add(new HeapOperation("complete", -1, -1, -1,
                "UpdateKey complete", ""));

        return new ArrayList<>(operations);
    }

    // Delete by index
    public List<HeapOperation> deleteByIndex(int index) {
        operations.clear();
        lastSortedArray = null;

        if (index < 0 || index >= heap.size()) {
            operations.add(new HeapOperation("error", -1, -1, -1,
                    "Invalid index: " + index, ""));
            return operations;
        }

        int value = heap.get(index);
        operations.add(new HeapOperation("delete", index, -1, value,
                "Delete(" + index + "): Removing value " + value, "O(log n)"));

        if (index == heap.size() - 1) {
            heap.remove(index);
            operations.add(new HeapOperation("complete", -1, -1, -1,
                    "Deleted last element", ""));
            return operations;
        }

        int lastValue = heap.get(heap.size() - 1);
        heap.set(index, lastValue);
        heap.remove(heap.size() - 1);

        operations.add(new HeapOperation("move", index, heap.size(), lastValue,
                "Move last element " + lastValue + " to index " + index, ""));

        // Heapify in the appropriate direction
        if (hasParent(index)) {
            int parentIndex = getParentIndex(index);
            boolean shouldGoUp = isMaxHeap ?
                    heap.get(index) > heap.get(parentIndex) :
                    heap.get(index) < heap.get(parentIndex);

            if (shouldGoUp) {
                heapifyUp(index);
            } else {
                heapifyDown(index);
            }
        } else {
            heapifyDown(index);
        }

        operations.add(new HeapOperation("complete", -1, -1, value,
                "Delete complete. Removed: " + value, ""));

        return new ArrayList<>(operations);
    }
    // Heap Sort
    public List<HeapOperation> heapSort() {
        operations.clear();

        if (heap.isEmpty()) {
            return operations;
        }

        // Snapshot the original heap FIRST – used for all canvas snapshots
        List<Integer> originalHeap = new ArrayList<>(heap);

        // Create a WORKING COPY to sort (does not affect canvas)
        List<Integer> tempHeap = new ArrayList<>(heap);
        List<Integer> sorted = new ArrayList<>();

        operations.add(new HeapOperation("sort", -1, -1, -1,
                "Starting HeapSort: Sorting heap elements without destroying it", "O(n log n)")
                .withSnapshot(originalHeap, sorted));

        // Perform heap sort on the COPY; canvas always shows originalHeap
        while (!tempHeap.isEmpty()) {
            int root = tempHeap.get(0);
            sorted.add(root);

            operations.add(new HeapOperation("extract", 0, -1, root,
                    "Extract " + root + " to sorted array (position " + sorted.size() + ")", "")
                    .withSnapshot(originalHeap, new ArrayList<>(sorted)));

            if (tempHeap.size() > 1) {
                int lastValue = tempHeap.get(tempHeap.size() - 1);
                tempHeap.set(0, lastValue);
                tempHeap.remove(tempHeap.size() - 1);
                heapifyDownOnList(tempHeap, 0, originalHeap, new ArrayList<>(sorted));
            } else {
                tempHeap.remove(0);
            }
        }

        // Show final sorted array
        StringBuilder sortedStr = new StringBuilder();
        String heapType = isMaxHeap ? "descending" : "ascending";
        sortedStr.append("Sorted (").append(heapType).append("): [");
        for (int i = 0; i < sorted.size(); i++) {
            sortedStr.append(sorted.get(i));
            if (i < sorted.size() - 1) sortedStr.append(", ");
        }
        sortedStr.append("]");

        operations.add(new HeapOperation("complete", -1, -1, -1,
                "HeapSort complete! " + sortedStr.toString(), "")
                .withSnapshot(originalHeap, sorted));

        // Restore original heap so canvas remains unchanged
        heap = originalHeap;
        this.lastSortedArray = new ArrayList<>(sorted);

        return new ArrayList<>(operations);
    }

    // Helper method to heapify on a list (used for HeapSort)
    private void heapifyDownOnList(List<Integer> list, int index, List<Integer> originalHeapSnapshot, List<Integer> sortedSnapshot) {
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            if (leftChild >= list.size()) break;

            int targetChildIndex = leftChild;

            if (rightChild < list.size()) {
                operations.add(new HeapOperation("compare", leftChild, rightChild, -1,
                        "Compare children: " + list.get(leftChild) + " vs " + list.get(rightChild), "")
                        .withSnapshot(originalHeapSnapshot, sortedSnapshot));

                boolean rightIsPreferred = isMaxHeap ?
                        list.get(rightChild) > list.get(targetChildIndex) :
                        list.get(rightChild) < list.get(targetChildIndex);

                if (rightIsPreferred) {
                    targetChildIndex = rightChild;
                }
            }

            operations.add(new HeapOperation("compare", index, targetChildIndex, -1,
                    "Compare " + list.get(index) + " with child " + list.get(targetChildIndex), "")
                    .withSnapshot(originalHeapSnapshot, sortedSnapshot));

            boolean shouldSwap = isMaxHeap ?
                    list.get(index) < list.get(targetChildIndex) :
                    list.get(index) > list.get(targetChildIndex);

            if (shouldSwap) {
                operations.add(new HeapOperation("swap", index, targetChildIndex, -1,
                        "Swap " + list.get(index) + " \u2194 " + list.get(targetChildIndex), "")
                        .withSnapshot(originalHeapSnapshot, sortedSnapshot));

                // Swap in list
                int temp = list.get(index);
                list.set(index, list.get(targetChildIndex));
                list.set(targetChildIndex, temp);

                index = targetChildIndex;
            } else {
                operations.add(new HeapOperation("done", index, -1, -1,
                        "Heap property satisfied", "")
                        .withSnapshot(originalHeapSnapshot, sortedSnapshot));
                break;
            }
        }
    }
    private void swap(int i, int j) {
        int temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public void clear() {
        heap.clear();
        operations.clear();
        lastSortedArray = null;
    }

    public int getHeight() {
        int s = size();
        if (s == 0) return 0;
        return (int) Math.ceil(Math.log(s + 1) / Math.log(2));
    }
}