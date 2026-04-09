package com.AlgoVista.stack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StackSnapshot {
    private final Integer[] elements;
    private final int capacity;
    private final int topIndex;
    private final Map<Integer, String> nodeColors;
    private final String message;
    private final String timeComplexity;

    public StackSnapshot(Integer[] elements, int capacity, int topIndex, Map<Integer, String> nodeColors, String message, String timeComplexity) {
        this.elements = Arrays.copyOf(elements, capacity);
        this.capacity = capacity;
        this.topIndex = topIndex;
        this.nodeColors = new HashMap<>(nodeColors != null ? nodeColors : new HashMap<>());
        this.message = message;
        this.timeComplexity = timeComplexity;
    }

    public Integer[] getElements() {
        return elements;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTopIndex() {
        return topIndex;
    }

    public Map<Integer, String> getNodeColors() {
        return nodeColors;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }
}
