package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>  {
    private T[] items;
    private int size;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        if (items.length == size) {
            resize(size * 2);
        }
        for (int i = size - 1; i >= 0; i--) {
            items[i + 1] = items[i];
        }
        items[0] = item;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (items.length == size) {
            resize(size * 2);
        }
        items[size] = item;
        size += 1;
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
    public void printDeque() {
        //
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T r = items[0];
        for (int i = 0; i < size - 1; i++) {
            items[i] = items[i + 1];
        }
        size -= 1;
        return r;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T r = items[size - 1];
        size -= 1;
        return r;
    }

    @Override
    public T get(int index) {
        if (size == 0) {
            return null;
        }
        return items[index];
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void resize(int cap) {
        T[] a = (T[]) new Object[cap];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }
}
