package com.AlgoVista.queue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueueSnapshot {
    private final Integer[] elements;
    private final int capacity;
    private final int front;
    private final int rear;
    private final int size;
    private final Map<Integer, String> nodeColors;
    private final String message;
    private final String timeComplexity;

    public QueueSnapshot(Integer[] elements, int capacity, int front, int rear, int size, Map<Integer, String> nodeColors, String message, String timeComplexity) {
        this.elements = Arrays.copyOf(elements, capacity);
        this.capacity = capacity;
        this.front = front;
        this.rear = rear;
        this.size = size;
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

    public int getFront() {
        return front;
    }

    public int getRear() {
        return rear;
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
