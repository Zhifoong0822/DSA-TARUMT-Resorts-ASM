// Author: [Chew Zhi Foong]
package adt;

public interface MapInterface<K, V> {
    void put(K key, V value);
    V get(K key);
    boolean containsKey(K key);
    int size();
    boolean isEmpty();
    void clear();
    V[] values(V[] array);
}
