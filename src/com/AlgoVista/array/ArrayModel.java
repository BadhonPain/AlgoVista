package com.AlgoVista.array;

import java.util.Arrays;
import java.util.Random;

public class ArrayModel {
    private int[] elements;
    private int size;
    private int capacity;

    public ArrayModel(int initialCapacity) {
        this.capacity = initialCapacity;
        this.elements = new int[capacity];
        this.size = 0;
    }

    public int[] getElements() {
        return Arrays.copyOf(elements, size);
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void generateSample(int size, int min, int max) {
        clear();
        if (size > capacity) {
            resize(size);
        }
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            elements[i] = random.nextInt(max - min + 1) + min;
        }
        this.size = size;
    }

    public boolean insertAtEnd(int value) {
        if (isFull()) {
            resize(capacity * 2); 
            // Return false if strictly fixed-size, but let's allow dynamic resizing for better UX
            // Or return true since we resized
        }
        elements[size] = value;
        size++;
        return true;
    }

    public boolean insertAtIndex(int index, int value) {
        if (index < 0 || index > size) {
            return false; // Out of bounds
        }
        if (isFull()) {
            resize(capacity * 2);
        }
        // Shift elements to the right
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = value;
        size++;
        return true;
    }

    public boolean updateValue(int index, int value) {
        if (index < 0 || index >= size) {
            return false;
        }
        elements[index] = value;
        return true;
    }

    public boolean deleteAtIndex(int index) {
        if (index < 0 || index >= size) {
            return false;
        }
        // Shift elements to the left
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        size--;
        elements[size] = 0; // Clear the last element (optional but good practice)
        return true;
    }

    public boolean deleteByValue(int value) {
        int index = search(value);
        if (index != -1) {
            return deleteAtIndex(index);
        }
        return false;
    }

    public int search(int value) {
        for (int i = 0; i < size; i++) {
            if (elements[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public String traverse() {
        if (isEmpty()) return "Array is empty";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public void reverse() {
        int left = 0;
        int right = size - 1;
        while (left < right) {
            int temp = elements[left];
            elements[left] = elements[right];
            elements[right] = temp;
            left++;
            right--;
        }
    }

    // Using Bubble Sort for educational purposes with O(n^2) complexity mentioned in UI
    public void sort() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (elements[j] > elements[j + 1]) {
                    int temp = elements[j];
                    elements[j] = elements[j + 1];
                    elements[j + 1] = temp;
                }
            }
        }
    }

    public void clear() {
        Arrays.fill(elements, 0);
        size = 0;
        capacity = 10; // reset capacity
        elements = new int[capacity];
    }

    private void resize(int newCapacity) {
        this.capacity = newCapacity;
        int[] newArray = new int[capacity];
        System.arraycopy(elements, 0, newArray, 0, size);
        this.elements = newArray;
    }
}
