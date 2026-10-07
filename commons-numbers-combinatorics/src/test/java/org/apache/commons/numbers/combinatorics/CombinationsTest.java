















package org.apache.commons.numbers.combinatorics;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class CombinationsTest {
    @Test
    void testGetN() {
        final int n = 5;
        final int k = 3;
        Assertions.assertEquals(n, Combinations.of(n, k).getN());
    }

    @Test
    void testGetK() {
        final int n = 5;
        final int k = 3;
        Assertions.assertEquals(k, Combinations.of(n, k).getK());
    }

    @Test
    void testLexicographicIterator() {
        checkLexicographicIterator(5, 3);
        checkLexicographicIterator(6, 4);
        checkLexicographicIterator(8, 2);
        checkLexicographicIterator(6, 1);
        checkLexicographicIterator(3, 3);
        checkLexicographicIterator(1, 1);
        checkLexicographicIterator(2, 0);
        checkLexicographicIterator(1, 0);
        checkLexicographicIterator(0, 0);
        checkLexicographicIterator(4, 2);
        checkLexicographicIterator(123, 2);
    }

    @Test
    void testLexicographicIteratorThrows() {
        checkLexicographicIteratorThrows(2, 1);

        checkLexicographicIteratorThrows(1, 1);
    }

    @Test
    void testLexicographicComparatorWrongIterate1() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = Combinations.of(n, k).comparator();
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> comp.compare(new int[] {1},
                               new int[] {0, 1, 2}));
    }

    @Test
    void testLexicographicComparatorWrongIterate2() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = Combinations.of(n, k).comparator();
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> comp.compare(new int[] {0, 1, 2},
                               new int[] {0, 1, 2, 3}));
    }

    @Test
    void testLexicographicComparatorWrongIterate3() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = Combinations.of(n, k).comparator();
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> comp.compare(new int[] {1, 2, 5},
                               new int[] {0, 1, 2}));
    }

    @Test
    void testLexicographicComparatorWrongIterate4() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = Combinations.of(n, k).comparator();
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> comp.compare(new int[] {1, 2, 4},
                               new int[] {-1, 1, 2}));
    }

    @Test
    void testLexicographicComparator() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = Combinations.of(n, k).comparator();
        Assertions.assertEquals(1, comp.compare(new int[] {1, 2, 4},
                                                new int[] {1, 2, 3}));
        Assertions.assertEquals(-1, comp.compare(new int[] {0, 1, 4},
                                                 new int[] {0, 2, 4}));
        Assertions.assertEquals(0, comp.compare(new int[] {1, 3, 4},
                                                new int[] {1, 3, 4}));
    }




    @Test
    void testLexicographicComparatorUnsorted() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = Combinations.of(n, k).comparator();
        Assertions.assertEquals(1, comp.compare(new int[] {1, 4, 2},
                                                new int[] {1, 3, 2}));
        Assertions.assertEquals(-1, comp.compare(new int[] {0, 4, 1},
                                                 new int[] {0, 4, 2}));
        Assertions.assertEquals(0, comp.compare(new int[] {1, 4, 3},
                                                new int[] {1, 3, 4}));
    }

    @Test
    void testEmptyCombination() {
        final Iterator<int[]> iter = Combinations.of(12345, 0).iterator();
        Assertions.assertTrue(iter.hasNext());
        final int[] c = iter.next();
        Assertions.assertEquals(0, c.length);
        Assertions.assertFalse(iter.hasNext());
    }

    @Test
    void testFullSetCombination() {
        final int n = 67;
        final Iterator<int[]> iter = Combinations.of(n, n).iterator();
        Assertions.assertTrue(iter.hasNext());
        final int[] c = iter.next();
        Assertions.assertEquals(n, c.length);

        for (int i = 0; i < n; i++) {
            Assertions.assertEquals(i, c[i]);
        }

        Assertions.assertFalse(iter.hasNext());
    }









    private static void checkLexicographicIterator(int n,
                                                   int k) {
        int[] lastIterate = null;

        long numIterates = 0;
        final Comparator<int[]> comp = Combinations.of(n, k).comparator();
        for (int[] iterate : Combinations.of(n, k)) {
            Assertions.assertEquals(k, iterate.length);


            if (lastIterate != null) {
                Assertions.assertEquals(1, comp.compare(iterate, lastIterate));
            }


            for (int i = 1; i < iterate.length; i++) {
                Assertions.assertTrue(iterate[i] > iterate[i - 1]);
            }

            lastIterate = iterate;
            ++numIterates;
        }


        Assertions.assertEquals(BinomialCoefficient.value(n, k), numIterates);
    }







    private static void checkLexicographicIteratorThrows(int n,
                                                         int k) {
        Iterator<int[]> iter = Combinations.of(n, k).iterator();


        iter.next();

        Assertions.assertThrows(UnsupportedOperationException.class, iter::remove);


        final long numIterates = BinomialCoefficient.value(n, k);
        for (long i = 1; i < numIterates; i++) {
            iter.next();
        }
        Assertions.assertThrows(NoSuchElementException.class, iter::next);
    }

    @Test
    void testBinomialCoefficientKLargerThanN() {
        Assertions.assertThrows(CombinatoricsException.class,
            () -> Combinations.of(4, 5)
        );
    }

    @Test
    void testBinomialCoefficientNegativeN() {
        Assertions.assertThrows(CombinatoricsException.class,
            () -> Combinations.of(-1, 1)
        );
    }

    @Test
    void testBinomialCoefficientNegativeK() {
        Assertions.assertThrows(CombinatoricsException.class,
            () -> Combinations.of(10, -1)
        );
    }

    @Test
    void testBinomialCoefficientKAboveN() {
        Assertions.assertThrows(CombinatoricsException.class,
            () -> Combinations.of(10, 20)
        );
    }
}
