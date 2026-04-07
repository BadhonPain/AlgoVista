package com.AlgoVista.array;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArraySnapshot {
    private final Integer[] elements;
    private final int size;
    private final Map<Integer, String> nodeColors;
    private final String message;
    private final String timeComplexity;

    public ArraySnapshot(Integer[] elements, int size, Map<Integer, String> nodeColors, String message, String timeComplexity) {
        this.elements = Arrays.copyOf(elements, size);
        this.size = size;
        this.nodeColors = new HashMap<>(nodeColors != null ? nodeColors : new HashMap<>());
        this.message = message;
        this.timeComplexity = timeComplexity;
    }

    public Integer[] getElements() {
        return elements;
    }

    public int getSize() {
        return size;
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
