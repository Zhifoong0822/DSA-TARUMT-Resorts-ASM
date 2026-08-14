/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author Yu He
 */
public class CustomList<T> implements ListInterface<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private T[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public CustomList() {
        data = (T[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {

        if (size >= data.length) {

            T[] newArray = (T[]) new Object[data.length * 2];

            for (int i = 0; i < data.length; i++) {
                newArray[i] = data[i];
            }

            data = newArray;
        }
    }

    @Override
    public boolean add(T element) {

        ensureCapacity();

        data[size] = element;
        size++;

        return true;
    }

    @Override
    public void add(int index, T element) {

        checkPosition(index);

        ensureCapacity();

        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }

        data[index] = element;
        size++;
    }

    @Override
    public T get(int index) {

        checkElement(index);

        return data[index];
    }

    @Override
    public T set(int index, T element) {

        checkElement(index);

        T old = data[index];
        data[index] = element;

        return old;
    }

    @Override
    public T remove(int index) {

        checkElement(index);

        T removed = data[index];

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }

        data[size - 1] = null;
        size--;

        return removed;
    }

    @Override
    public boolean remove(T element) {

        int index = indexOf(element);

        if (index == -1)
            return false;

        remove(index);

        return true;
    }

    @Override
    public boolean contains(T element) {

        return indexOf(element) != -1;
    }

    @Override
    public int indexOf(T element) {

        for (int i = 0; i < size; i++) {

            if (element == null) {

                if (data[i] == null)
                    return i;

            } else if (element.equals(data[i])) {

                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean isEmpty() {

        return size == 0;
    }

    @Override
    public int size() {

        return size;
    }

    @Override
    public void clear() {

        for (int i = 0; i < size; i++) {
            data[i] = null;
        }

        size = 0;
    }

    private void checkElement(int index) {

        if (index < 0 || index >= size) {

            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + size);
        }
    }

    private void checkPosition(int index) {

        if (index < 0 || index > size) {

            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + size);
        }
    }

}
