package bstmap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B {

    private static class BSTNode<K, V> {
        public K key;
        public V item;
        public BSTNode left;
        public BSTNode right;

        public BSTNode(K key, V item) {
            this.key = key;
            this.item = item;
            this.left = null;
            this.right = null;
        }
    }

    private int size;
    private BSTNode first;

    public BSTMap() {
        size = 0;
        first = null;
    }

    @Override
    public void clear() {
        first = null;
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        BSTNode p = first;
        while (p != null) {
            if (p.key.equals(key)) return true;
            if (((K)p.key).compareTo((K)key) > 0) p = p.left;
            if (((K)p.key).compareTo((K)key) < 0) p = p.right;
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        BSTNode p = first;
        while (p != null) {
            if (p.key.equals(key)) return p.item;
            if (((K)p.key).compareTo((K)key) > 0) p = p.left;
            else if (((K)p.key).compareTo((K)key) < 0) p = p.right;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(Object key, Object value) {
        if (first == null) {
            first = new BSTNode((K)key, (V)value);
            size++;
            return;
        }

        BSTNode p = first;
        while (p != null) {
            if (p.key.equals(key)) {
                p.item = value;
                return;
            }
            else if (((K)p.key).compareTo((K)key) > 0) {
                if (p.left == null) {
                    p.left = new BSTNode((K)key, (V)value);
                    size++;
                    return;
                }
                else p = p.left;
            }

            else if (((K)p.key).compareTo((K)key) < 0) {
                if (p.right == null) {
                    p.right = new BSTNode((K)key, (V)value);
                    size++;
                    return;
                }
                else p = p.right;
            }
        }
    }

    @Override
    public Set keySet() {
        return Set.of();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }

}

