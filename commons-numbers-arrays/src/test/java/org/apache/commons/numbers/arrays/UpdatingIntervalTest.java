
















package org.apache.commons.numbers.arrays;

import java.util.Arrays;
import java.util.BitSet;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.ArraySampler;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;




class UpdatingIntervalTest {










    private static KeyUpdatingInterval createKeyUpdatingInterval(int[] indices, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("No indices to define the range");
        }


        final int[] k = Arrays.stream(indices).distinct().sorted().toArray();
        return new KeyUpdatingInterval(k, k.length);
    }










    private static BitIndexUpdatingInterval createBitIndexUpdatingInterval(int[] indices, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("No indices to define the range");
        }
        int min = indices[0];
        int max = min;
        for (int i = 1; i < n; i++) {
            min = Math.min(min, indices[i]);
            max = Math.max(max, indices[i]);
        }
        final BitIndexUpdatingInterval set = new BitIndexUpdatingInterval(min, max);
        for (int i = -1; ++i < n;) {
            set.set(indices[i]);
        }
        return set;
    }

    @ParameterizedTest
    @MethodSource(value = {"testIndices"})
    void testUpdateKeyInterval(int[] indices, int[] k) {
        assertUpdate(UpdatingIntervalTest::createKeyUpdatingInterval, indices, k);
    }

    @ParameterizedTest
    @MethodSource(value = {"testIndices"})
    void testUpdateBitIndexUpdatingInterval(int[] indices, int[] k) {

        Assumptions.assumeTrue(k[k.length - 1] < Integer.MAX_VALUE - 1);
        assertUpdate(UpdatingIntervalTest::createBitIndexUpdatingInterval, indices, k);
    }

    @ParameterizedTest
    @MethodSource(value = {"testIndices"})
    void testUpdateIndexSupport(int[] indices, int[] k) {
        assertUpdate(IndexSupport::createUpdatingInterval, indices, k);
    }

    @ParameterizedTest
    @MethodSource(value = {"testIndices"})
    void testSplitKeyInterval(int[] indices, int[] k) {
        assertSplit(UpdatingIntervalTest::createKeyUpdatingInterval, indices, k);
    }

    @ParameterizedTest
    @MethodSource(value = {"testIndices"})
    void testSplitBitIndexUpdatingInterval(int[] indices, int[] k) {

        Assumptions.assumeTrue(k[k.length - 1] < Integer.MAX_VALUE - 1);
        assertSplit(UpdatingIntervalTest::createBitIndexUpdatingInterval, indices, k);
    }

    @ParameterizedTest
    @MethodSource(value = {"testIndices"})
    void testSplitIndexSupport(int[] indices, int[] k) {
        assertSplit(IndexSupport::createUpdatingInterval, indices, k);
    }









    private static void assertUpdate(BiFunction<int[], Integer, UpdatingInterval> constructor,
            int[] indices, int[] k) {
        UpdatingInterval interval = constructor.apply(indices.clone(), indices.length);
        final int nm1 = k.length - 1;
        Assertions.assertEquals(k[0], interval.left());
        Assertions.assertEquals(k[nm1], interval.right());


        for (int i = 1; i < k.length; i++) {

            final int m = (k[i - 1] + k[i]) >>> 1;
            interval.updateLeft(m + 1);
            Assertions.assertEquals(k[i], interval.left());
        }
        Assertions.assertEquals(interval.left(), interval.right());


        interval = constructor.apply(indices.clone(), indices.length);
        for (int i = k.length; --i > 0;) {

            final int m = 1 + ((k[i - 1] + k[i]) >>> 1);
            interval.updateRight(m - 1);
            Assertions.assertEquals(k[i - 1], interval.right());
        }
        Assertions.assertEquals(interval.left(), interval.right());
    }









    private static void assertSplit(BiFunction<int[], Integer, UpdatingInterval> constructor, int[] indices, int[] k) {
        assertSplitMedian(constructor.apply(indices.clone(), indices.length),
            k, 0, k.length - 1);
        assertSplitMiddleIndices(constructor.apply(indices.clone(), indices.length),
            k, 0, k.length - 1);
    }









    private static void assertSplitMedian(UpdatingInterval interval, int[] indices, int i, int j) {
        if (indices[i] + 1 >= indices[j]) {

            return;
        }

        final int m = (indices[i] + indices[j]) >>> 1;

        int hi = Arrays.binarySearch(indices, i, j + 1, m + 1);
        if (hi < 0) {

            hi = ~hi;
        }

        int lo = hi;
        do {
            --lo;
        } while (indices[lo] >= m);

        final int left = interval.left();
        final int right = interval.right();

        final UpdatingInterval leftInterval = interval.splitLeft(m, m);
        Assertions.assertEquals(left, leftInterval.left());
        Assertions.assertEquals(indices[lo], leftInterval.right());
        Assertions.assertEquals(indices[hi], interval.left());
        Assertions.assertEquals(right, interval.right());


        assertSplitMedian(leftInterval, indices, i, lo);
        assertSplitMedian(interval, indices, hi, j);
    }









    private static void assertSplitMiddleIndices(UpdatingInterval interval, int[] indices, int i, int j) {
        if (i + 3 >= j) {

            return;
        }

        final int m1 = (i + j) >>> 1;
        final int m2 = m1 + 1;

        final int left = interval.left();
        final int right = interval.right();
        final UpdatingInterval leftInterval = interval.splitLeft(indices[m1], indices[m2]);
        Assertions.assertEquals(left, leftInterval.left());
        Assertions.assertEquals(indices[m1 - 1], leftInterval.right());
        Assertions.assertEquals(indices[m2 + 1], interval.left());
        Assertions.assertEquals(right, interval.right());


        assertSplitMiddleIndices(leftInterval, indices, i, m1 - 1);
        assertSplitMiddleIndices(interval, indices, m2 + 1, j);
    }

    static Stream<Arguments> testIndices() {
        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
        final Stream.Builder<int[]> builder = Stream.builder();

        builder.accept(new int[] {4});
        builder.accept(new int[] {4, 78});
        builder.accept(new int[] {4, 78, 999});
        builder.accept(new int[] {4, 78, 79, 999});
        builder.accept(new int[] {4, 5, 6, 7, 8});
        for (final int size : new int[] {25, 100, 400, 800}) {
            final BitSet set = new BitSet(size);
            for (final int n : new int[] {2, size / 8, size / 4, size / 2}) {
                set.clear();
                rng.ints(n, 0, size).forEach(set::set);
                final int[] a = set.stream().toArray();
                builder.accept(a.clone());

                a[0] = 0;
                a[a.length - 1] = Integer.MAX_VALUE - 1;
                builder.accept(a);
            }
        }



        Stream.Builder<Arguments> out = Stream.builder();
        builder.build().forEach(a -> {
            final int[] c = a.clone();
            out.accept(Arguments.of(a.clone(), c.clone()));

            final int[] b = Arrays.copyOf(a, a.length * 2);
            for (int i = a.length; i < b.length; i++) {
                b[i] = a[rng.nextInt(a.length)];
            }
            Arrays.sort(b);
            out.accept(Arguments.of(b.clone(), c.clone()));

            ArraySampler.shuffle(rng, a);
            ArraySampler.shuffle(rng, b);
            out.accept(Arguments.of(a, c.clone()));
            out.accept(Arguments.of(b, c.clone()));
        });
        return out.build();
    }

    @Test
    void testIndexIntervalCreate() {




        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(new int[] {1}, 1).getClass());


        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(new int[] {2, 1}, 2).getClass());
        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(new int[] {1, 2}, 2).getClass());


        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(new int[] {200, 1}, 2).getClass());


        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, 11).getClass());

        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(new int[] {1, 1, 2, 2, 3, 3, 4, 4, 5, 5}, 10).getClass());
        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(new int[] {100, 100, 200, 200, 300, 300, 400, 400, 500, 500}, 10).getClass());

        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(new int[] {11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1}, 11).getClass());

        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(
                new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, Integer.MAX_VALUE - 1}, 11).getClass());


        int[] k = IntStream.range(0, 30).map(i -> i * 64) .toArray();
        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(k.clone(), k.length).getClass());

        reverse(k, 0, k.length);
        Assertions.assertEquals(BitIndexUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(k.clone(), k.length).getClass());

        k[k.length - 1] = Integer.MAX_VALUE - 1;
        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(k, k.length).getClass());


        k = IntStream.range(0, 3000).map(i -> i * 64) .toArray();
        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(k.clone(), k.length).getClass());

        reverse(k, 0, k.length);
        Assertions.assertEquals(BitIndexUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(k.clone(), k.length).getClass());

        k[k.length - 1] = Integer.MAX_VALUE - 1;
        Assertions.assertEquals(KeyUpdatingInterval.class,
            IndexSupport.createUpdatingInterval(k.clone(), k.length).getClass());
    }








    private static void reverse(int[] a, int from, int to) {
        for (int i = from - 1, j = to; ++i < --j;) {
            final int v = a[i];
            a[i] = a[j];
            a[j] = v;
        }
    }
}
