package deque;

import java.util.Arrays;
import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>  {
    private T[] items;
    private int size;
    private int frontIndex;
    private int lastIndex;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        Arrays.fill(items, 0, items.length, null);
        size = 0;
        frontIndex = items.length - 1;
        lastIndex = 0;
    }
    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[frontIndex] = item;
        frontIndex = Math.floorMod(frontIndex - 1, items.length);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[lastIndex] = item;
        lastIndex = Math.floorMod(lastIndex + 1, items.length);
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (T i : this) {
            System.out.print(i );
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        frontIndex = Math.floorMod(frontIndex + 1, items.length);;
        T r = items[frontIndex];
        items[frontIndex] = null;
        size -= 1;
        desize(items.length / 2);
        return r;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        lastIndex = Math.floorMod(lastIndex - 1, items.length);
        T r = items[lastIndex];
        items[lastIndex] = null;
        size -= 1;
        desize(items.length / 2);
        return r;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return items[(frontIndex + 1 + index) % items.length];
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> ArrayDequeIteartor = new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public T next() {
                T returnItem = (T) get(currentIndex);
                currentIndex += 1;
                return returnItem;
            }
        };
        return ArrayDequeIteartor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Deque)) {
            return false;
        }
        Deque<T> o = (Deque<T>) obj;
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!this.get(i).equals(o.get(i))) {
                return false;
            }
        }
        return true;
    }

    private void resize(int cap) {
       T[] a = (T[]) new Object[cap];
       for (int i = 0; i < size; i++) {
           a[i] = get(i);
       }
       items = a;
       frontIndex = items.length - 1;
       lastIndex = size;
    }

    private void desize(int cap) {
        if (Math.abs(0.25 - 1.0 * size / items.length) <= 0.01 && items.length >= 16) {
            resize(cap);
        }
    }
}

