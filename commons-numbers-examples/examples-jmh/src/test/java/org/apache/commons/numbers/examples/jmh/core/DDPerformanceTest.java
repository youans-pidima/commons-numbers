















package org.apache.commons.numbers.examples.jmh.core;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.numbers.examples.jmh.core.DDPerformance.DoubleInt;
import org.apache.commons.numbers.examples.jmh.core.DDPerformance.DoubleIntFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;




class DDPerformanceTest {
    @ParameterizedTest
    @CsvSource({
        "300, 300, 0, 1",
        "1000, 500, 0.001, 0.5",


    })
    void testKSFunction(int samples, int values, double lx, double ux) {
        final List<DoubleIntFunction> funs = DDPerformance.KSMethod.getImplementations()
            .map(DDPerformance.KSMethod::createFunction)
            .collect(Collectors.toList());
        final DoubleInt[] data = DDPerformance.KSData.createData(samples, values, lx, ux);
        final DoubleIntFunction ref = funs.get(0);
        for (final DoubleInt d : data) {
            final double x = d.getX();
            final int n = d.getN();
            final double expected = ref.apply(x, n);
            for (int i = 1; i < funs.size(); i++) {
                final double actual = funs.get(i).apply(x, n);
                Assertions.assertEquals(expected, actual, () -> String.format("(%s,%s)", x, n));
            }
        }
    }
}
