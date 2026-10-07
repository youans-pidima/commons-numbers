















package org.apache.commons.numbers.examples.jmh.gamma;

import java.util.function.DoubleBinaryOperator;
import org.apache.commons.numbers.examples.jmh.gamma.GammaContinuedFractionPerformance.BaseData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;




class GammaContinuedFractionTest {

    @ParameterizedTest
    @ValueSource(strings = {
        GammaContinuedFractionPerformance.IMP_NUMBERS_1_0,
        GammaContinuedFractionPerformance.IMP_NUMBERS_EXT_A,
        GammaContinuedFractionPerformance.IMP_NUMBERS_EXT_A1,
        GammaContinuedFractionPerformance.IMP_ITERATOR,
        GammaContinuedFractionPerformance.IMP_NUMBERS_1_1_INC,
    })
    void testContinuedFraction(String implementation) {


        final DoubleBinaryOperator f1 = BaseData.createFunction(GammaContinuedFractionPerformance.IMP_NUMBERS_1_1);
        final DoubleBinaryOperator f2 = BaseData.createFunction(implementation);
        for (final double[] pair : GammaContinuedFractionPerformance.getData()) {
            final double a = pair[0];
            final double z = pair[1];
            final double expected = f1.applyAsDouble(a, z);
            final double actual = f2.applyAsDouble(a, z);
            Assertions.assertEquals(expected, actual, Math.abs(1e-14 * expected),
                () -> String.format("(%s,%s)", a, z));
        }
    }
}
