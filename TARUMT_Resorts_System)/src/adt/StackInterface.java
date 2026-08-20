/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

//@author Daniel Kok Wei Zen

public interface StackInterface<T> {

    public void push(T newEntry);

    public T pop();

    public T peek();

    public boolean isEmpty();

    public int getSize();

    public void clear();
}