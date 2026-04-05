package com.AlgoVista.stack;

import java.util.Arrays;
import java.util.Random;

public class StackModel {
    private int[] elements;
    private int top;
    private int capacity;

    public StackModel(int capacity) {
        this.capacity = capacity;
        this.elements = new int[capacity];
        this.top = -1;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTopIndex() {
        return top;
    }

    public int[] getElements() {
        return elements.clone();
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == capacity - 1;
    }

    public int size() {
        return top + 1;
    }

    public boolean push(int value) {
        if (isFull()) {
            return false;
        }
        top++;
        elements[top] = value;
        return true;
    }

    public Integer pop() {
        if (isEmpty()) {
            return null;
        }
        int poppedValue = elements[top];
        elements[top] = 0; // Clear it purely for visualization cleanliness
        top--;
        return poppedValue;
    }

    public Integer peek() {
        if (isEmpty()) {
            return null;
        }
        return elements[top];
    }

    public int search(int value) {
        // Standard stack search starts from top
        // Returns the 1-based distance from top, or -1 if not found.
        // For visualizer matching array, we might want the absolute index or the distance.
        // Let's return the absolute array index to make highlighting easier for the Controller.
        for (int i = top; i >= 0; i--) {
            if (elements[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        Arrays.fill(elements, 0);
        top = -1;
    }

    public void generateSample(int sizeToFill, int min, int max) {
        clear();
        int actualSize = Math.min(sizeToFill, capacity);
        Random random = new Random();
        for (int i = 0; i < actualSize; i++) {
            push(random.nextInt(max - min + 1) + min);
        }
    }

    public String traverse() {
        if (isEmpty()) {
            return "Stack is empty.";
        }
        StringBuilder sb = new StringBuilder("TOP -> ");
        for (int i = top; i >= 0; i--) {
            sb.append(elements[i]);
            if (i > 0) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
