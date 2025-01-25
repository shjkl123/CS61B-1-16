package DebugExercise;

import org.junit.Test;
import static org.junit.Assert.*;
public class DebugExercise2Test {
    @Test
    public void ElementWise(){
        int [] a1 = {1, 2, 3, 4};
        int [] a2 = {1, 2, 3, 4};
        int [] a3 = {1, 3, 2, 4};
        int [] a4 = {3, 3, 3, 3};
        int [] a5 = {0, 0, 0, 0};
        int [] a6 = {0, -2, -6, 0};
        int [] a7 = {0, -1, -2, -3};
        assertEquals(10, DebugExercise2.sumOfElementwiseMaxes(a1, a2));
        assertEquals(13, DebugExercise2.sumOfElementwiseMaxes(a3, a4));
        assertEquals(0, DebugExercise2.sumOfElementwiseMaxes(a5, a6));
        assertEquals(-3, DebugExercise2.sumOfElementwiseMaxes(a6, a7));
    }
}

