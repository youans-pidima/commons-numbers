















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class LanczosApproximationTest {
    @Test
    void testG() {
        Assertions.assertEquals(607d / 128d, LanczosApproximation.g());
    }

    @Test
    void testSomeValues() {


        Assertions.assertEquals(29.020294557631818d, LanczosApproximation.value(0.1d));
        Assertions.assertEquals(13.14778027539684d, LanczosApproximation.value(1.0d));
        Assertions.assertEquals(7.897828855157814d, LanczosApproximation.value(2.0d));
    }
}
