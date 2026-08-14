/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package adt;

/**
 *
 * @author Yu He
 */
public interface ListInterface<T> {

    boolean add(T data);

    void add(int index, T data);

    T get(int index);

    T set(int index, T data);

    T remove(int index);

    boolean remove(T data);

    boolean contains(T data);

    int indexOf(T data);

    boolean isEmpty();

    int size();

    void clear();

}