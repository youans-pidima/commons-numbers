















package org.apache.commons.numbers.primes;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class UserGuideTest {

    @Test
    void testPrimes1() {
        int n = 51237173;
        boolean prime = Primes.isPrime(n);
        int     m     = Primes.nextPrime(n);
        Assertions.assertFalse(prime);
        Assertions.assertEquals(51237233, m);

        List<Integer> f1 = Primes.primeFactors(n);
        List<Integer> f2 = Primes.primeFactors(m);
        Assertions.assertEquals(Arrays.asList(13, 863, 4567), f1);
        Assertions.assertEquals(Arrays.asList(m), f2);

        Assertions.assertEquals(n, f1.stream().mapToInt(i -> i).reduce((i, j) -> i * j).getAsInt());
    }
}
