package com.AlgoVista.linkedlist.doubly;

import java.util.Random;

public class DoublyLinkedListModel {
    private DoublyNode head;
    private DoublyNode tail;
    private StringBuilder traverseResult;

    public DoublyLinkedListModel() {
        this.head = null;
        this.tail = null;
        this.traverseResult = new StringBuilder();
    }

    public DoublyNode getHead() {
        return head;
    }

    public void prepend(int value) {
        DoublyNode newNode = new DoublyNode(value);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
        }
    }

    public void append(int value) {
        DoublyNode newNode = new DoublyNode(value);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
    }

    public boolean insertAt(int index, int value) {
        if (index < 0) return false;
        if (index == 0) {
            prepend(value);
            return true;
        }

        DoublyNode current = head;
        int currentIndex = 0;

        while (current != null && currentIndex < index) {
            current = current.getNext();
            currentIndex++;
        }

        if (current == null && currentIndex == index) {
            append(value);
            return true;
        } else if (current == null) {
            return false; // Index out of bounds
        }

        DoublyNode newNode = new DoublyNode(value);
        DoublyNode prevNode = current.getPrev();

        prevNode.setNext(newNode);
        newNode.setPrev(prevNode);
        newNode.setNext(current);
        current.setPrev(newNode);

        return true;
    }

    public boolean deleteByValue(int value) {
        DoublyNode current = head;

        while (current != null) {
            if (current.getValue() == value) {
                if (current == head && current == tail) {
                    head = tail = null;
                } else if (current == head) {
                    head = current.getNext();
                    head.setPrev(null);
                } else if (current == tail) {
                    tail = current.getPrev();
                    tail.setNext(null);
                } else {
                    current.getPrev().setNext(current.getNext());
                    current.getNext().setPrev(current.getPrev());
                }
                return true;
            }
            current = current.getNext();
        }

        return false;
    }

    public boolean deleteAt(int index) {
        if (head == null || index < 0) return false;

        DoublyNode current = head;
        int currentIndex = 0;

        while (current != null && currentIndex < index) {
            current = current.getNext();
            currentIndex++;
        }

        if (current == null) return false; // Index out of bounds

        if (current == head && current == tail) {
            head = tail = null;
        } else if (current == head) {
            head = current.getNext();
            if (head != null) head.setPrev(null);
        } else if (current == tail) {
            tail = current.getPrev();
            if (tail != null) tail.setNext(null);
        } else {
            current.getPrev().setNext(current.getNext());
            current.getNext().setPrev(current.getPrev());
        }

        return true;
    }

    public int search(int value) {
        DoublyNode current = head;
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

    public String traverseForward() {
        traverseResult.setLength(0);
        DoublyNode current = head;
        traverseResult.append("null <-> ");
        while (current != null) {
            traverseResult.append(current.getValue()).append(" <-> ");
            current = current.getNext();
        }
        traverseResult.append("null");
        return traverseResult.toString();
    }

    public String traverseBackward() {
        traverseResult.setLength(0);
        DoublyNode current = tail;
        traverseResult.append("null <-> ");
        while (current != null) {
            traverseResult.append(current.getValue()).append(" <-> ");
            current = current.getPrev();
        }
        traverseResult.append("null");
        return traverseResult.toString();
    }

    public void reverse() {
        DoublyNode current = head;
        DoublyNode temp = null;

        while (current != null) {
            temp = current.getPrev();
            current.setPrev(current.getNext());
            current.setNext(temp);
            current = current.getPrev(); // Move to the next node (which is actually in the 'prev' pointer now)
        }

        if (temp != null) {
            tail = head;
            head = temp.getPrev();
        }
    }

    public void clear() {
        head = null;
        tail = null;
    }

    public void generateSample(int size, int min, int max) {
        clear();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            append(random.nextInt(max - min + 1) + min);
        }
    }

    public boolean isEmpty() {
        return head == null;
    }
}
