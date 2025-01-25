package DebugExercise;

import org.junit.Test;
import static org.junit.Assert.*;

public class DebugExercise1Test {
    @Test
    public void round() {
        assertEquals(3, DebugExercise1.divideThenRound(10, 3));
        assertEquals(2, DebugExercise1.divideThenRound(10, 5));
        assertEquals(2, DebugExercise1.divideThenRound(8, 5));
    }

}
