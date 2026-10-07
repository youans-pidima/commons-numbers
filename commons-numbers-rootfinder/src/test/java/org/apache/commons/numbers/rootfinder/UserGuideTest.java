















package org.apache.commons.numbers.rootfinder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class UserGuideTest {

    @Test
    void testBrentSolver1() {
        double relAccuracy = 1e-6;
        double absAccuracy = 1e-14;
        double functionAccuracy = 1e-15;
        BrentSolver solver = new BrentSolver(relAccuracy,
                                             absAccuracy,
                                             functionAccuracy);
        double result1 = solver.findRoot(Math::sin, 3, 4);
        double result2 = solver.findRoot(Math::sin, 3, 3.14, 4);

        Assertions.assertEquals(Math.PI, result1, Math.PI * relAccuracy);
        Assertions.assertEquals(Math.PI, result2, Math.PI * relAccuracy);


        Assertions.assertThrows(IllegalArgumentException.class,
            () -> solver.findRoot(Math::sin, 2, 3));
    }
}
