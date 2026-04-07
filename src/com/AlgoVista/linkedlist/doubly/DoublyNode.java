package com.AlgoVista.linkedlist.doubly;

public class DoublyNode {
    private int value;
    private DoublyNode prev;
    private DoublyNode next;

    public DoublyNode(int value) {
        this.value = value;
        this.prev = null;
        this.next = null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public DoublyNode getPrev() {
        return prev;
    }

    public void setPrev(DoublyNode prev) {
        this.prev = prev;
    }

    public DoublyNode getNext() {
        return next;
    }

    public void setNext(DoublyNode next) {
        this.next = next;
    }
}
