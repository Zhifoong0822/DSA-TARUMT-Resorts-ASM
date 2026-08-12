/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author Yu He
 */
public interface QueueInterface<T> {

    void enqueue(T item);

    void enqueueFront(T item);

    void enqueueByPriority(T item);

    T dequeue();

    T peek();

    boolean isEmpty();

    int size();

    void display();
}