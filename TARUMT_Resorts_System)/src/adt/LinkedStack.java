/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author Gigabyte
 */
public class LinkedStack<T> implements StackInterface<T> {

    private Node<T> topNode;
    private int size;

    public LinkedStack() {
        topNode = null;
        size = 0;
    }

    @Override
    public void push(T newEntry) {

        Node<T> newNode = new Node<>(newEntry);

        newNode.next = topNode;

        topNode = newNode;

        size++;
    }

    @Override
    public T pop() {

        if (isEmpty()) {
            return null;
        }

        T topData = topNode.data;

        topNode = topNode.next;

        size--;

        return topData;
    }

    @Override
    public T peek() {

        if (isEmpty()) {
            return null;
        }

        return topNode.data;
    }

    @Override
    public boolean isEmpty() {
        return topNode == null;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void clear() {

        topNode = null;
        size = 0;
    }

    private static class Node<T> {

        private T data;
        private Node<T> next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
}
