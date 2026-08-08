// Author: [Chew Zhi Foong]

package adt;

public class CustomHashMap<K, V> implements MapInterface<K, V> {
    
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Node<K, V>[] table;
    private int capacity;
    private int size;
    private static final int DEFAULT_CAPACITY = 101; 

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.table = new Node[capacity];
        this.size = 0;
    }

    private int getHashIndex(K key) {
        if (key == null) return 0;
        return Math.abs(key.hashCode()) % capacity;
    }

    @Override
    public void put(K key, V value) {
        int index = getHashIndex(key);
        Node<K, V> head = table[index];

        // Check if the key already exists, if so, update its value
        Node<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        // Insert new node at the beginning
        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = table[index];
        table[index] = newNode;
        size++;
    }

    @Override
    public V get(K key) {
        int index = getHashIndex(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null; 
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        table = new Node[capacity];
        size = 0;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public V[] values(V[] array) {
        if (array.length < size) {
            array = (V[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), size);
        }
        int index = 0;
        for (int i = 0; i < capacity; i++) {
            Node<K, V> current = table[i];
            while (current != null) {
                array[index++] = current.value;
                current = current.next;
            }
        }
    return array;
}
}
