package deque;


import jh61b.junit.In;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;

public class ArrayDequeTest {

    @Test
    public void Test1() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        a.addLast(2);
        assertEquals(1, (int)a.get(0));
        assertEquals(1, (int)a.removeFirst());
        assertEquals(2, (int) a.get(0));
    }

    @Test
    public void Test2() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < 10; i++) {
            a.addFirst(i);
            assertEquals(i + 1, (int)a.size());
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(9 - i, (int)a.removeFirst());
        }
        assertTrue(a.isEmpty());
        for (int i = 0; i < 10; i++) {
            a.addLast(i);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(i, (int)a.removeFirst());
        }
        System.out.println(a.size());
    }

    @Test
    public void Test3() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        assertTrue(a.isEmpty());
        for (int i = 0; i < 100; i++) {
            a.addFirst(i);
            a.addLast(i);
        }
        a.printDeque();
    }

    @Test
    public void Test4() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        LinkedListDeque<Integer> b = new LinkedListDeque<>();
        for (int i = 0; i < 1000; i ++) {
            int operator =  StdRandom.uniform(0, 5);
            if (operator == 0) {
                int number = StdRandom.uniform(0, 50);
                a.addFirst(number);
                b.addFirst(number);
            } else if (operator == 1) {
                int number = StdRandom.uniform(0, 50);
                a.addLast(number);
                b.addLast(number);
            } else if (operator == 2) {
                a.removeFirst();
                b.removeFirst();
            } else if (operator == 3) {
                a.removeLast();
                b.removeLast();
            } else if (operator == 4) {
                for (int j = 0; j < 10; j++) {
                    if (a.isEmpty()) {
                        continue;
                    }
                    int index = StdRandom.uniform(0, a.size());
                    assertEquals((int)b.get(index), (int)a.get(index));
                }
            }
        }
        a.printDeque();
        b.printDeque();
    }

}
