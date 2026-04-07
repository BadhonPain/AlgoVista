package com.AlgoVista.queue;

public class QueueModel {
    private Integer[] elements;
    private int capacity;
    private int front;
    private int rear;
    private int size;

    public QueueModel(int capacity) {
        this.capacity = capacity;
        this.elements = new Integer[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSize() {
        return size;
    }

    public int getFront() {
        return front;
    }

    public int getRear() {
        return rear;
    }

    public Integer[] getElements() {
        return elements.clone();
    }

    public boolean enqueue(int value) {
        if (isFull()) return false;
        rear = (rear + 1) % capacity;
        elements[rear] = value;
        size++;
        return true;
    }

    public Integer dequeue() {
        if (isEmpty()) return null;
        Integer value = elements[front];
        elements[front] = null; // Clear visually
        front = (front + 1) % capacity;
        size--;
        return value;
    }

    public Integer peek() {
        if (isEmpty()) return null;
        return elements[front];
    }

    public void clear() {
        this.elements = new Integer[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public void generateSample(int count, int min, int max) {
        clear();
        int additions = Math.min(count, capacity);
        for (int i = 0; i < additions; i++) {
            int randomVal = (int) (Math.random() * (max - min + 1)) + min;
            enqueue(randomVal);
        }
    }

    public String traverse() {
        if (isEmpty()) return "Queue is empty.";
        StringBuilder sb = new StringBuilder();
        int count = 0;
        int i = front;
        while (count < size) {
            sb.append(elements[i]).append(count == size - 1 ? "" : " -> ");
            i = (i + 1) % capacity;
            count++;
        }
        return sb.toString();
    }
}
