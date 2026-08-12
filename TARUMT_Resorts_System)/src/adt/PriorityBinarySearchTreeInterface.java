// Author: Yong Shen
package adt;

public interface PriorityBinarySearchTreeInterface<T extends Comparable<T>> {
    T getRootData();
    void add(T newEntry);
    T removeTop();
    T getTop();
    boolean contains(T entry);
    int getNumberOfEntries();
    boolean isEmpty();
    void clear();
    T[] toArray(T[] array);
}
