















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;








class BetaTest {
    @ParameterizedTest
    @CsvFileSource(resources = "beta_med_data.csv")
    void testBeta(double a, double b, double beta) {
        TestUtils.assertEquals(beta, Beta.value(a, b), 200);
    }
}
