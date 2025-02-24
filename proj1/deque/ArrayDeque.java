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
        frontIndex -= 1;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[lastIndex] = item;
        lastIndex += 1;
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
        int index;
        if (frontIndex == items.length - 1) {
            index = 0;
        } else {
            index = frontIndex + 1;
            frontIndex += 1;
        }
        T r = items[index];
        items[index] = null;
        size -= 1;
        desize(size / 2);
        return r;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int index;
        if (lastIndex == 0) {
            index = items.length - 1;
        } else {
            index = lastIndex - 1;
            lastIndex -= 1;
        }
        T r = items[index];
        items[index] = null;
        size -= 1;
        desize(size / 2);
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
        if (obj instanceof ArrayDeque) {
            if (((ArrayDeque) obj).size() != size ) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (this.get(i) != ((ArrayDeque) obj).get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void resize(int cap) {
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

