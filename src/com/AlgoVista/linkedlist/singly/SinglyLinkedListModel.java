package com.AlgoVista.linkedlist.singly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SinglyLinkedListModel {
    private SinglyNode head;
    private StringBuilder traverseResult;

    public SinglyLinkedListModel() {
        this.head = null;
        this.traverseResult = new StringBuilder();
    }

    public SinglyNode getHead() {
        return head;
    }

    public void prepend(int value) {
        SinglyNode newNode = new SinglyNode(value);
        newNode.setNext(head);
        head = newNode;
    }

    public void append(int value) {
        SinglyNode newNode = new SinglyNode(value);
        if (head == null) {
            head = newNode;
            return;
        }
        SinglyNode current = head;
        while (current.getNext() != null) {
            current = current.getNext();
        }
        current.setNext(newNode);
    }

    public boolean insertAt(int index, int value) {
        if (index < 0) return false;
        if (index == 0) {
            prepend(value);
            return true;
        }

        SinglyNode newNode = new SinglyNode(value);
        SinglyNode current = head;
        int currentIndex = 0;

        while (current != null && currentIndex < index - 1) {
            current = current.getNext();
            currentIndex++;
        }

        if (current == null) return false; // Index out of bounds

        newNode.setNext(current.getNext());
        current.setNext(newNode);
        return true;
    }

    public boolean deleteByValue(int value) {
        if (head == null) return false;

        if (head.getValue() == value) {
            head = head.getNext();
            return true;
        }

        SinglyNode current = head;
        while (current.getNext() != null && current.getNext().getValue() != value) {
            current = current.getNext();
        }

        if (current.getNext() != null) {
            current.setNext(current.getNext().getNext());
            return true;
        }

        return false;
    }

    public boolean deleteAt(int index) {
        if (head == null || index < 0) return false;

        if (index == 0) {
            head = head.getNext();
            return true;
        }

        SinglyNode current = head;
        int currentIndex = 0;

        while (current != null && currentIndex < index - 1) {
            current = current.getNext();
            currentIndex++;
        }

        if (current == null || current.getNext() == null) return false; // Index out of bounds

        current.setNext(current.getNext().getNext());
        return true;
    }

    public int search(int value) {
        SinglyNode current = head;
        int index = 0;
        while (current != null) {
            if (current.getValue() == value) {
                return index;
            }
            current = current.getNext();
            index++;
        }
        return -1;
    }

    public String traverse() {
        traverseResult.setLength(0);
        SinglyNode current = head;
        while (current != null) {
            traverseResult.append(current.getValue()).append(" -> ");
            current = current.getNext();
        }
        traverseResult.append("null");
        return traverseResult.toString();
    }

    public void reverse() {
        SinglyNode prev = null;
        SinglyNode current = head;
        SinglyNode next = null;

        while (current != null) {
            next = current.getNext();
            current.setNext(prev);
            prev = current;
            current = next;
        }
        head = prev;
    }

    public void clear() {
        head = null;
    }

    public void generateSample(int size, int min, int max) {
        clear();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            append(random.nextInt(max - min + 1) + min);
        }
    }

    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
        SinglyNode current = head;
        while (current != null) {
            list.add(current.getValue());
            current = current.getNext();
        }
        return list;
    }

    public boolean isEmpty() {
        return head == null;
    }
}
