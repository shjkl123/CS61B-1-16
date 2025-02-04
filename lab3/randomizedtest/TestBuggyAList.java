package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public  void testThreeAddThreeMove() {
        BuggyAList<Integer> s = new BuggyAList<>();
        AList<Integer> r = new AList<>();
        for(int i = 0; i < 3; i++) {
            s.addLast(i + 4);
            r.addLast(i + 4);
        }
        for(int i = 0; i < 3; i++) {
            assertEquals(r.removeLast(), s.removeLast());
            assertEquals(r.size(), s.size());
        }
    }

    @Test
    public void randomizedTest() {
        BuggyAList<Integer> L = new BuggyAList<>();
        AListNoResizing<Integer> R = new AListNoResizing<>();
        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                R.addLast(randVal);
                assertEquals(L.size(), R.size());
            } else if (operationNumber == 1) {
                // size
                int size1 = L.size();
                int size2 = R.size();
                assertEquals(size1, size2);
            } else if (operationNumber == 2) {
                if (L.size() == 0 || R.size() == 0) {
                    continue;
                }
                int last1 = L.getLast();
                int last2 = R.getLast();
                assertEquals(last1, last2);
            } else if (operationNumber == 3) {
                if (L.size() == 0 || R.size() == 0) {
                    continue;
                }
                int remove1 = L.removeLast();
                int remove2 = R.removeLast();
                assertEquals(remove1, remove2);
            }
        }
    }
}
