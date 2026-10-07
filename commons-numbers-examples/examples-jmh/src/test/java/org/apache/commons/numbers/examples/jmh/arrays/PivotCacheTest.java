
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;




class PivotCacheTest {

    @ParameterizedTest
    @MethodSource(value = {"testSingleRange", "testSinglePoint"})
    void testSingleRangeUsingFilledIndexSetAsScanningPivotCache(int left, int right, int[][] indices) {

        int[] allIndices = Arrays.stream(indices).flatMapToInt(Arrays::stream).toArray();

        final int n = allIndices.length;
        allIndices = Arrays.copyOfRange(allIndices, 0, n + 2);
        allIndices[n] = left;
        allIndices[n + 1] = right;
        final IndexSet set = IndexSet.of(allIndices);
        assertSingleRange(left, right, indices, set.asScanningPivotCache(left, right));
    }

    @ParameterizedTest
    @MethodSource(value = {"testSingleRange", "testSinglePoint"})
    void testSingleRangeUsingScanningPivotCache(int left, int right, int[][] indices) {
        assertSingleRange(left, right, indices, IndexSet.createScanningPivotCache(left, right));
    }

    @ParameterizedTest
    @MethodSource(value = {"testSingleRange", "testSinglePoint"})
    void testSingleRangeUsingIndexSet(int left, int right, int[][] indices) {
        assertSingleRange(left, right, indices, IndexSet.ofRange(left, right));
    }

    @ParameterizedTest
    @MethodSource
    void testSinglePoint(int left, int right, int[][] indices) {
        Assumptions.assumeTrue(left == right);
        assertSingleRange(left, right, indices, PivotCaches.ofIndex(left));
    }

    @ParameterizedTest
    @MethodSource
    void testSingleRange(int left, int right, int[][] indices) {
        assertSingleRange(left, right, indices, PivotCaches.ofRange(left, right));
    }

    @ParameterizedTest
    @MethodSource(value = {"testSingleRange", "testSinglePoint"})
    void testPairedIndex(int left, int right, int[][] indices) {
        if (left == right) {
            assertSingleRange(left, right, indices, PivotCaches.ofPairedIndex(left));
        } else if (left + 1 == right) {
            assertSingleRange(left, right, indices,
                PivotCaches.ofPairedIndex(left | Integer.MIN_VALUE));
        } else {
            Assumptions.abort("Not a paired index");
        }
    }













    private static void assertSingleRange(int left, int right, int[][] indices, PivotCache cache) {
        final BitSet ref = new BitSet();
        Assertions.assertEquals(left, cache.left());
        Assertions.assertEquals(right, cache.right());
        Assertions.assertEquals(ref.previousSetBit(left), cache.previousPivot(left));
        Assertions.assertEquals(ref.nextSetBit(left), cache.nextPivot(left));
        Assertions.assertEquals(Integer.MAX_VALUE, cache.nextPivotOrElse(right, Integer.MAX_VALUE));


        for (final int[] batch : indices) {
            for (final int i : batch) {
                ref.set(i);
                cache.add(i);
                if (i >= left && i <= right && !cache.sparse()) {
                    Assertions.assertTrue(cache.contains(i));
                }
            }
            Assertions.assertEquals(left, cache.left());
            Assertions.assertEquals(right, cache.right());


            final int lower = ref.previousSetBit(left);
            final int upper = ref.nextSetBit(right);
            Assertions.assertEquals(lower, cache.previousPivot(left));
            Assertions.assertEquals(upper, cache.nextPivot(right));
            if (upper < 0) {
                Assertions.assertEquals(Integer.MAX_VALUE, cache.nextPivotOrElse(right, Integer.MAX_VALUE));
            }




            if (cache.sparse()) {
                continue;
            }


            Assertions.assertEquals(ref.get(left), cache.contains(left));
            Assertions.assertEquals(ref.get(right), cache.contains(right));

            if (!(cache instanceof ScanningPivotCache)) {
                continue;
            }
            final ScanningPivotCache scanningCache = (ScanningPivotCache) cache;





            int p = ref.nextSetBit(left);
            Assertions.assertEquals(p, cache.nextPivot(left));

            if (p == upper) {

                continue;
            }

            int p2 = ref.previousSetBit(right);
            Assertions.assertEquals(p2, cache.previousPivot(right));






            BitSet copy = (BitSet) ref.clone();

            copy.set(left, p);

            int s = ref.nextClearBit(p);
            int e = ref.nextSetBit(s);

            while (left < e && e < upper) {

                copy.set(s, e);
                Assertions.assertEquals(s, scanningCache.nextNonPivot(p));
                Assertions.assertEquals(e, cache.nextPivot(s));
                p = s;
                s = ref.nextClearBit(p);
                e = ref.nextSetBit(s);
            }

            copy.set(p + 1, right + 1);
            final int unsorted = copy.nextClearBit(left);
            Assertions.assertTrue(right < unsorted, () -> "Bad left-to-right traversal: " + unsorted);



            copy = (BitSet) ref.clone();

            copy.set(p2 + 1, right + 1);

            e = ref.previousClearBit(p2);
            s = ref.previousSetBit(e);
            while (lower < s) {

                copy.set(s + 1, e + 1);
                Assertions.assertEquals(e, scanningCache.previousNonPivot(p2));
                Assertions.assertEquals(s, cache.previousPivot(e));
                p2 = s;
                e = ref.previousClearBit(p2);
                s = ref.previousSetBit(e);
            }


            copy.set(left, p2);
            final int unsorted2 = copy.previousClearBit(right);
            Assertions.assertTrue(unsorted2 < left, () -> "Bad right-to-left traversal: " + unsorted2);
        }
    }

    static Stream<Arguments> testSinglePoint() {
        final Stream.Builder<Arguments> builder = Stream.builder();

        builder.accept(Arguments.of(5, 5, new int[][] {{5}}));
        builder.accept(Arguments.of(5, 5, new int[][] {{2, 44}, {4, 6}, {5}}));

        return builder.build();
    }

    static Stream<Arguments> testSingleRange() {
        final Stream.Builder<Arguments> builder = Stream.builder();

        builder.accept(Arguments.of(5, 5, new int[][] {{5}}));
        builder.accept(Arguments.of(5, 5, new int[][] {{2, 44}, {4, 6}, {5}}));

        builder.accept(Arguments.of(5, 6, new int[][] {{5, 6}}));
        builder.accept(Arguments.of(5, 6, new int[][] {{2, 44}, {4, 6}, {5}}));
        builder.accept(Arguments.of(5, 6, new int[][] {{2, 44}, {5, 7}, {6}}));

        builder.accept(Arguments.of(5, 80, new int[][] {{5, 80}}));
        builder.accept(Arguments.of(5, 80, new int[][] {{2, 100}, {3, 90}, {4, 80}}));
        builder.accept(Arguments.of(5, 80, new int[][] {{2, 100}, {5, 90}}));


        builder.accept(Arguments.of(5, 80, new int[][] {{63}}));
        builder.accept(Arguments.of(5, 80, new int[][] {{63, 64, 65, 66}}));
        builder.accept(Arguments.of(5, 80, new int[][] {{2, 63, 101}}));


        builder.accept(Arguments.of(5, 80, new int[][] {{31, 63}}));
        builder.accept(Arguments.of(5, 80, new int[][] {{31, 44, 63}}));
        builder.accept(Arguments.of(5, 80, new int[][] {{31, 32, 33, 44, 63}}));

        return builder.build();
    }

    @ParameterizedTest
    @MethodSource(value = {"testSetRange", "testSetRangeSinglePoint"})
    void testSetRangeUsingFilledIndexSetAsScanningPivotCache(int left, int right, int[][] indices) {

        int[] allIndices = Arrays.stream(indices).flatMapToInt(Arrays::stream)
            .filter(i -> i >= left && i <= right).toArray();

        final int n = allIndices.length;
        allIndices = Arrays.copyOfRange(allIndices, 0, n + 2);
        allIndices[n] = left;
        allIndices[n + 1] = right;
        final IndexSet set = IndexSet.of(allIndices);
        assertSetRange(left, right, indices, set.asScanningPivotCache(left, right));
    }

    @ParameterizedTest
    @MethodSource(value = {"testSetRange", "testSetRangeSinglePoint"})
    void testSetRangeUsingScanningPivotCache(int left, int right, int[][] indices) {
        assertSetRange(left, right, indices, IndexSet.createScanningPivotCache(left, right));
    }

    @ParameterizedTest
    @MethodSource(value = {"testSetRange", "testSetRangeSinglePoint"})
    void testSetRangeUsingIndexSet(int left, int right, int[][] indices) {
        assertSetRange(left, right, indices, IndexSet.ofRange(left, right));
    }

    @ParameterizedTest
    @MethodSource
    void testSetRangeSinglePoint(int left, int right, int[][] indices) {
        Assumptions.assumeTrue(left == right);
        assertSetRange(left, right, indices, PivotCaches.ofIndex(left));
    }

    @ParameterizedTest
    @MethodSource(value = {"testSetRange"})
    void testSetRangeUsingRange(int left, int right, int[][] indices) {
        Assumptions.assumeTrue(left < right);
        assertSetRange(left, right, indices, PivotCaches.ofRange(left, right));
    }

    @ParameterizedTest
    @MethodSource(value = {"testSingleRange", "testSinglePoint"})
    void testSetRangeUsingPairedIndex(int left, int right, int[][] indices) {
        if (left == right) {
            assertSingleRange(left, right, indices, PivotCaches.ofPairedIndex(left));
        } else if (left + 1 == right) {
            assertSingleRange(left, right, indices,
                PivotCaches.ofPairedIndex(left | Integer.MIN_VALUE));
        } else {
            Assumptions.abort("Not a paired index");
        }
    }













    private static void assertSetRange(int left, int right, int[][] indices, PivotCache cache) {
        final BitSet ref = new BitSet();
        Assertions.assertEquals(left, cache.left());
        Assertions.assertEquals(right, cache.right());
        Assertions.assertEquals(ref.previousSetBit(left), cache.previousPivot(left));
        Assertions.assertEquals(ref.nextSetBit(left), cache.nextPivot(left));
        Assertions.assertEquals(Integer.MAX_VALUE, cache.nextPivotOrElse(right, Integer.MAX_VALUE));


        for (final int[] batch : indices) {

            ref.set(batch[0], batch[1] + 1);
            cache.add(batch[0], batch[1]);
            Assertions.assertEquals(left, cache.left());
            Assertions.assertEquals(right, cache.right());


            final int lower = ref.previousSetBit(left);
            final int upper = ref.nextSetBit(right);
            Assertions.assertEquals(lower, cache.previousPivot(left), "left flanking pivot");
            Assertions.assertEquals(upper, cache.nextPivot(right), "right flanking pivot");
            if (upper < 0) {
                Assertions.assertEquals(Integer.MAX_VALUE, cache.nextPivotOrElse(right, Integer.MAX_VALUE));
            }
            if (cache.sparse()) {
                continue;
            }

            for (int i = left; i <= right; i++) {
                Assertions.assertEquals(ref.get(i), cache.contains(i));
            }
        }
    }

    static Stream<Arguments> testSetRangeSinglePoint() {
        final Stream.Builder<Arguments> builder = Stream.builder();



        final int max = Integer.MAX_VALUE - 1;

        builder.accept(Arguments.of(5, 5, new int[][] {{5, 5}}));
        builder.accept(Arguments.of(5, 5, new int[][] {{2, 44}, {4, 6}, {5, 5}}));
        builder.accept(Arguments.of(5, 5, new int[][] {{2, 44}, {5, 6}}));
        builder.accept(Arguments.of(5, 5, new int[][] {{2, 44}, {4, 5}}));
        builder.accept(Arguments.of(5, 5, new int[][] {{2, 44}, {6, 7}, {5, 6}}));
        builder.accept(Arguments.of(5, 5, new int[][] {{2, 44}, {3, 4}, {4, 5}}));
        builder.accept(Arguments.of(5, 5, new int[][] {{1, 2}, {7, 7}}));
        builder.accept(Arguments.of(5, 5, new int[][] {{0, 0}, {max, max}}));

        return builder.build();
    }

    static Stream<Arguments> testSetRange() {
        final Stream.Builder<Arguments> builder = Stream.builder();



        final int max = Integer.MAX_VALUE - 1;

        builder.accept(Arguments.of(5, 6, new int[][] {{5, 6}}));
        builder.accept(Arguments.of(5, 6, new int[][] {{2, 3}, {44, 49}, {7, 8}, {5, 5}}));
        builder.accept(Arguments.of(5, 6, new int[][] {{0, 1}, {max - 1, max}}));


        builder.accept(Arguments.of(5, 80, new int[][] {{42, 42}, {1, 1}, {2, 2}, {90, 95}, {67, 68}}));
        builder.accept(Arguments.of(5, 80, new int[][] {{0, 1}, {max - 1, max}}));


        builder.accept(Arguments.of(5, 140, new int[][] {{42, 42}, {1, 1}, {2, 2}, {90, 95}, {187, 190}, {67, 68}}));
        builder.accept(Arguments.of(5, 140, new int[][] {{0, 0}, {max, max}}));

        return builder.build();
    }
}
