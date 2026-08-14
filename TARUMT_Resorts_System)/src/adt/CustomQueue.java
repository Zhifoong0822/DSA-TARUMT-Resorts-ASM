/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import entity.Booking;

/**
 *
 * @author Yu He
 */
public class CustomQueue<T> implements QueueInterface<T> {

    private Object[] queue;
    private int front;
    private int rear;
    private int size;

    private static final int DEFAULT_CAPACITY = 10;

    public CustomQueue() {

        queue = new Object[DEFAULT_CAPACITY];

        front = 0;
        rear = -1;
        size = 0;
    }

    @Override
    public void enqueue(T item) {

        if (size == queue.length) {
            resize();
        }

        rear = (rear + 1) % queue.length;

        queue[rear] = item;

        size++;
    }
    
    @Override
public void enqueueByPriority(T item) {

    if (size == queue.length) {

        System.out.println("Queue is full.");
        return;
    }

    Booking newBooking =
            (Booking) item;

    int priority =
            getPriority(newBooking);

    int insertPosition = size;

    // Find where the new booking should be inserted
    for (int i = 0; i < size; i++) {

        Booking current =
                (Booking) queue[
                        (front + i)
                                % queue.length
                ];

        int currentPriority =
                getPriority(current);

        if (priority < currentPriority) {

            insertPosition = i;

            break;
        }
    }

    // Shift elements backward
    for (int i = size; i > insertPosition; i--) {

        queue[
                (front + i)
                        % queue.length
        ] =
                queue[
                        (front + i - 1)
                                % queue.length
                ];
    }

    queue[
            (front + insertPosition)
                    % queue.length
    ] = item;

    size++;
}
    @Override
    @SuppressWarnings("unchecked")
    public T dequeue() {

        if (isEmpty()) {
            return null;
        }

        T item = (T) queue[front];

        queue[front] = null;

        front = (front + 1) % queue.length;

        size--;

        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {

        if (isEmpty()) {
            return null;
        }

        return (T) queue[front];
    }

    @Override
    public boolean isEmpty() {

        return size == 0;
    }

    @Override
    public int size() {

        return size;
    }

    /**
     * Removes the item at the supplied queue position without changing the
     * order of the remaining items.
     */
    @SuppressWarnings("unchecked")
    public T remove(int index) {

        if (index < 0 || index >= size) {
            return null;
        }

        int removedPosition = (front + index) % queue.length;
        T item = (T) queue[removedPosition];

        for (int i = index; i < size - 1; i++) {
            int current = (front + i) % queue.length;
            int next = (front + i + 1) % queue.length;
            queue[current] = queue[next];
        }

        queue[rear] = null;
        rear = (rear - 1 + queue.length) % queue.length;
        size--;

        if (size == 0) {
            front = 0;
            rear = -1;
        }

        return item;
    }

    private void resize() {

        Object[] newQueue = new Object[queue.length * 2];

        for (int i = 0; i < size; i++) {

            newQueue[i] = queue[(front + i) % queue.length];
        }

        queue = newQueue;

        front = 0;
        rear = size - 1;
    }

    @Override
    public void display() {

    if (isEmpty()) {

        System.out.println("Queue is empty.");
        return;
    }

    System.out.println("\n==============================================================");

    System.out.printf(
            "%-12s %-15s %-12s %-12s%n",
            "Waiting ID",
            "Name",
            "Member",
            "Room Type"
    );

    System.out.println(
            "---------------------------------------------------------------"
    );

    for (int i = 0; i < size; i++) {

        Booking booking =
                (Booking) queue[(front + i) % queue.length];

        System.out.printf(
                "%-12s %-15s %-12s %-12s%n",
                booking.getWaitingNumber(),
                booking.getGuestDisplayName(),
                booking.getMembershipType(),
                booking.getRoomType()
        );
    }

    System.out.println(
            "==============================================================="
    );
}
    
    private int getPriority(
        Booking booking) {

    if (booking.getMembershipType()
            .equalsIgnoreCase("VIP")) {

        return 1;
    }

    if (booking.getMembershipType()
            .equalsIgnoreCase("NORMAL")) {
        
            return 2;
        }

        return 3;
    }
    
    @Override
    public T get(int index) {

    if (index < 0 || index >= size) {
        return null;
    }

    @SuppressWarnings("unchecked")
    T item = (T) queue[(front + index) % queue.length];

    return item;
    }
    
    
}
