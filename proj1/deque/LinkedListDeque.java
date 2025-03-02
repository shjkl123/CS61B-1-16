package deque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T>{

    private static class Node<T> {
        T item;
        Node<T> prev;
        Node<T> next;
        public Node(T first, Node<T> p, Node<T> q) {
            item = first;
            prev = p;
            next = q;
        }
    }
    private Node<T> front;
    private Node<T> last;
    private int Size;
    public LinkedListDeque() {
        front = new Node<T>(null, null, null);
        last = new Node<T>(null, front, front);
        front.next = last;
        front.prev = last;
        Size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node<T> p = new Node<T>(item, front, front.next);
        front.next.prev = p;
        front.next = p;
        Size += 1;
    }

    @Override
    public void addLast(T item) {
        Node<T> p = new Node<T>(item, last.prev, last);
        last.prev.next = p;
        last.prev = p;
        Size += 1;
    }


    @Override
    public int size() {
        return Size;
    }

    @Override
    public void printDeque() {
        for (T i : this) {
            System.out.print(i);
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T r = front.next.item;
        front.next.next.prev = front;
        front.next = front.next.next;
        Size -= 1;
        return r;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T r = last.prev.item;
        last.prev.prev.next = last;
        last.prev = last.prev.prev;
        Size -= 1;
        return r;
    }

    @Override
    public T get(int index) {
        if (index >= Size || index < 0) {
            return null;
        }
        Node <T> p = front.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index >= Size || index < 0) {
            return null;
        }
        return helpRecursive(index, front.next);
    }
    private T helpRecursive(int pos, Node<T> x) {
        if (pos == 0) {
            return x.item;
        }
        return helpRecursive(pos - 1, x.next);
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> LinkedListIterator = new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < Size;
            }

            @Override
            public T next() {
                T returnItem = (T) get(currentIndex);
                currentIndex += 1;
                return returnItem;
            }
        };
        return LinkedListIterator;
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
}


