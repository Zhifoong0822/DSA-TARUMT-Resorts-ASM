// Author: TanYong Shen
package adt;

public class PriorityBinarySearchTree<T extends Comparable<T>> implements PriorityBinarySearchTreeInterface<T> {

    private class Node {
        private T data;
        private Node left;
        private Node right;

        private Node(T data) {
            this.data = data;
            left = null;
            right = null;
        }
    }

    private Node root;
    private int numberOfEntries;

    public PriorityBinarySearchTree() {
        root = null;
        numberOfEntries = 0;
    }

    @Override
    public T getRootData() {
        if (root == null) {
            return null;
        }
        return root.data;
    }

    @Override
    public void add(T newEntry) {
        if (newEntry == null) {
            return;
        }

        if (root == null) {
            root = new Node(newEntry);
        } else {
            addEntry(root, newEntry);
        }
        numberOfEntries++;
    }

    private void addEntry(Node rootNode, T newEntry) {
        int comparison = newEntry.compareTo(rootNode.data);

        if (comparison < 0) {
            if (rootNode.left == null) {
                rootNode.left = new Node(newEntry);
            } else {
                addEntry(rootNode.left, newEntry);
            }
        } else {
            if (rootNode.right == null) {
                rootNode.right = new Node(newEntry);
            } else {
                addEntry(rootNode.right, newEntry);
            }
        }
    }

    @Override
    public T removeTop() {
        if (root == null) {
            return null;
        }

        T result;
        if (root.right == null) {
            result = root.data;
            root = root.left;
        } else {
            Node parent = root;
            Node current = root.right;

            while (current.right != null) {
                parent = current;
                current = current.right;
            }

            result = current.data;
            parent.right = current.left;
        }

        numberOfEntries--;
        return result;
    }

    @Override
    public T getTop() {
        if (root == null) {
            return null;
        }

        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.data;
    }

    @Override
    public boolean contains(T entry) {
        return containsEntry(root, entry);
    }

    private boolean containsEntry(Node rootNode, T entry) {
        if (rootNode == null || entry == null) {
            return false;
        }
        if (rootNode.data.equals(entry)) {
            return true;
        }
        return containsEntry(rootNode.left, entry) || containsEntry(rootNode.right, entry);
    }

    @Override
    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public void clear() {
        root = null;
        numberOfEntries = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        if (array.length < numberOfEntries) {
            array = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), numberOfEntries);
        }

        int[] index = new int[1];
        fillDescending(root, array, index);
        return array;
    }

    private void fillDescending(Node rootNode, T[] array, int[] index) {
        if (rootNode != null) {
            fillDescending(rootNode.right, array, index);
            array[index[0]] = rootNode.data;
            index[0]++;
            fillDescending(rootNode.left, array, index);
        }
    }
}
