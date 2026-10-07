















package org.apache.commons.numbers.primes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SmallPrimesTest {


    private static final int[] LARGE_PRIME = {3673, 3677};

    @Test
    void smallTrialDivision_smallComposite() {
        final List<Integer> factors = new ArrayList<>();
        final int result = SmallPrimes.smallTrialDivision(3 * 7 * 23, factors);
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(Arrays.asList(3, 7, 23), factors);
    }

    @Test
    void smallTrialDivision_repeatedFactors() {
        final List<Integer> factors = new ArrayList<>();
        final int result = SmallPrimes.smallTrialDivision(2 * 2 * 3 * 3 * 3, factors);
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(Arrays.asList(2, 2, 3, 3, 3), factors);
    }

    @Test
    void smallTrialDivision_oneFactor() {
        final List<Integer> factors = new ArrayList<>();
        final int result = SmallPrimes.smallTrialDivision(59, factors);
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(Collections.singletonList(59), factors);
    }

    @Test
    void smallTrialDivision_BoundaryPrimes() {
        final List<Integer> factors = new ArrayList<>();
        final int penultimatePrime = SmallPrimes.PRIMES[SmallPrimes.PRIMES.length - 2];
        final int result = SmallPrimes.smallTrialDivision(penultimatePrime * SmallPrimes.PRIMES_LAST, factors);
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(Arrays.asList(penultimatePrime, SmallPrimes.PRIMES_LAST), factors);
    }

    @Test
    void smallTrialDivision_largeComposite() {
        final List<Integer> factors = new ArrayList<>();
        final int result = SmallPrimes.smallTrialDivision(2 * 5 * LARGE_PRIME[0], factors);
        Assertions.assertEquals(LARGE_PRIME[0], result);
        Assertions.assertEquals(Arrays.asList(2, 5), factors);
    }

    @Test
    void smallTrialDivision_noSmallPrimeFactors() {
        final List<Integer> factors = new ArrayList<>();
        final int result = SmallPrimes.smallTrialDivision(LARGE_PRIME[0] * LARGE_PRIME[1], factors);
        Assertions.assertEquals(LARGE_PRIME[0] * LARGE_PRIME[1], result);
        Assertions.assertEquals(Collections.<Integer>emptyList(), factors);
    }

    @Test
    void boundedTrialDivision_twoDifferentFactors() {
        final List<Integer> factors = new ArrayList<>();
        SmallPrimes.boundedTrialDivision(LARGE_PRIME[0] * LARGE_PRIME[1], Integer.MAX_VALUE, factors);
        Assertions.assertEquals(Arrays.asList(LARGE_PRIME[0], LARGE_PRIME[1]), factors);
    }

    @Test
    void boundedTrialDivision_square() {
        final List<Integer> factors = new ArrayList<>();
        SmallPrimes.boundedTrialDivision(LARGE_PRIME[0] * LARGE_PRIME[0], Integer.MAX_VALUE, factors);
        Assertions.assertEquals(Arrays.asList(LARGE_PRIME[0], LARGE_PRIME[0]), factors);
    }

    @Test
    void trialDivision_smallComposite() {
        final List<Integer> factors = SmallPrimes.trialDivision(5 * 11 * 29 * 103);
        Assertions.assertEquals(Arrays.asList(5, 11, 29, 103), factors);
    }

    @Test
    void trialDivision_repeatedFactors() {
        final List<Integer> factors = SmallPrimes.trialDivision(2 * 2 * 2 * 2 * 5 * 5);
        Assertions.assertEquals(Arrays.asList(2, 2, 2, 2, 5, 5), factors);
    }

    @Test
    void trialDivision_oneSmallFactor() {
        final List<Integer> factors = SmallPrimes.trialDivision(101);
        Assertions.assertEquals(Collections.singletonList(101), factors);
    }

    @Test
    void trialDivision_largeComposite() {
        final List<Integer> factors = SmallPrimes.trialDivision(2 * 3 * LARGE_PRIME[0]);
        Assertions.assertEquals(Arrays.asList(2, 3, LARGE_PRIME[0]), factors);
    }

    @Test
    void trialDivision_veryLargeComposite() {
        final List<Integer> factors = SmallPrimes.trialDivision(2 * LARGE_PRIME[0] * LARGE_PRIME[1]);
        Assertions.assertEquals(Arrays.asList(2, LARGE_PRIME[0], LARGE_PRIME[1]), factors);
    }

    @Test
    void millerRabinPrimeTest_primes() {
        for (final int n : PrimesTest.PRIMES) {
            if (n % 2 == 1) {
                Assertions.assertTrue(SmallPrimes.millerRabinPrimeTest(n));
            }
        }
    }

    @Test
    void millerRabinPrimeTest_composites() {
        for (final int n : PrimesTest.NOT_PRIMES) {
            if (n % 2 == 1) {
                Assertions.assertFalse(SmallPrimes.millerRabinPrimeTest(n));
            }
        }
    }
}
