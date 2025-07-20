package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    @Override
    public Iterator<K> iterator() {
        return null;
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int size = 0;
    private int bucketSize = 16;
    private double loadFactor = 0.75;
    /** Constructors */
    public MyHashMap() {
        buckets = createTable(bucketSize);
    }

    public MyHashMap(int initialSize) {
        bucketSize = initialSize;
        buckets = createTable(bucketSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.bucketSize = initialSize;
        this.buckets = createTable(bucketSize);
        this.loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection[] s = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            s[i] = createBucket();
        }
        return s;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public int size() {
        return size;
    }
    
    private int mod(int x, int y) {
        assert y > 0 : "y need greater than 0";
        int res = x % y;
        return res >= 0 ? res : res + y;
    }

    private double abs(double x) {
        return x >= 0 ? x : -x;
    }

    @Override
    public void put(K key, V value) {
        if (abs(size * 1.0 / bucketSize - loadFactor) <= 0.01) {
            Set<Node> s = NodeSet();
            clear();
            bucketSize *= 2;
            buckets = createTable(bucketSize);
            for (Node nd : s) {
                put(nd.key, nd.value);
            }
        }
        if (containsKey(key)) remove(key);
        Node p = createNode(key, value);
        buckets[mod(key.hashCode(), bucketSize)].add(p);
        size++;
    }

    @Override
    public Set<K> keySet() {
        Set<K> s = new HashSet<K>();
        for (Collection<Node> p : buckets) {
            for (Node nd: p) {
                s.add(nd.key);
            }
        }
        return s;
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) return null;
        Collection<Node> p = buckets[mod(key.hashCode(), bucketSize)];
        for (Node nd : p) {
            if (nd.key.equals(key)) {
                V value = nd.value;
                p.remove(nd);
                size--;
                return value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (!containsKey(key)) return null;
        Collection<Node> p = buckets[mod(key.hashCode(), bucketSize)];
        for (Node nd : p) {
            if (nd.key.equals(key) && nd.value.equals(value)) {
                p.remove(nd);
                size--;
                return value;
            }
        }
        return null;
    }

    @Override
    public V get(K key) {
        Collection<Node> p = buckets[mod(key.hashCode(), bucketSize)];
        for (Node nd : p) {
            if (nd.key.equals(key)) return nd.value;
        }
        return null;
    }

    @Override
    public void clear() {
        for (Collection<Node> p : buckets) {
            p.clear();
        }
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
       return get(key) != null;
    }

    Set<Node> NodeSet() {
        Set<Node> s = new HashSet<>();
        for (Collection<Node> p : buckets) {
            s.addAll(p);
        }
        return s;
    }
}
