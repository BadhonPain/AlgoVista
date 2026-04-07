package com.AlgoVista.array;

import java.util.Arrays;
import java.util.Random;

public class ArrayModel {
    private Integer[] elements;
    private int size;
    private int capacity;

    public ArrayModel(int initialCapacity) {
        this.capacity = initialCapacity;
        this.elements = new Integer[capacity];
        this.size = 0; // Initialize with size = 0
    }

    public Integer[] getElements() {
        return Arrays.copyOf(elements, capacity);
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return size >= capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void generateSample(int size, int min, int max) {
        clear();
        if (size > capacity) {
            size = capacity;
        }
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            elements[i] = random.nextInt(max - min + 1) + min;
        }
        this.size = size;
    }

    public boolean insertAtEnd(int value) {
        if (isFull()) {
            return false;
        }
        elements[size] = value;
        size++;
        return true;
    }

    public boolean insertAtIndex(int index, int value) {
        if (index < 0 || index > size) {
            return false;
        }
        if (isFull()) {
            return false;
        }
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
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        size--;
        elements[size] = null;
        
        capacity--; // Canvas physically shrinks
        Integer[] newElements = new Integer[capacity];
        System.arraycopy(elements, 0, newElements, 0, capacity);
        this.elements = newElements;
        
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
            if (elements[i] != null && elements[i] == value) {
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

    public void sort() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (elements[j] != null && elements[j + 1] != null && elements[j] > elements[j + 1]) {
                    swap(j, j + 1);
                }
            }
        }
    }

    public void swap(int i, int j) {
        if (i >= 0 && i < size && j >= 0 && j < size) {
            int temp = elements[i];
            elements[i] = elements[j];
            elements[j] = temp;
        }
    }

    public void clear() {
        Arrays.fill(elements, null);
        size = 0; 
    }
}
