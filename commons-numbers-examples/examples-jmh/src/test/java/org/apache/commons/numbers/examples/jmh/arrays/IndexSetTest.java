
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;




class IndexSetTest {

    @Test
    void testInvalidRangeThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> IndexSet.ofRange(-1, 3));
        Assertions.assertThrows(IllegalArgumentException.class, () -> IndexSet.ofRange(0, -1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> IndexSet.ofRange(456, 123));
        Assertions.assertThrows(IllegalArgumentException.class, () -> IndexSet.of(new int[0]));
    }

    @Test
    void testInvalidPivotCacheRangeThrows() {
        final int left = 10;
        final int right = 156;
        final IndexSet set = IndexSet.ofRange(left, right);
        Assertions.assertThrows(IllegalArgumentException.class, () -> set.asScanningPivotCache(left - 1, right));
        Assertions.assertThrows(IllegalArgumentException.class, () -> set.asScanningPivotCache(right, left));
        Assertions.assertDoesNotThrow(() -> set.asScanningPivotCache(left, right));
        Assertions.assertDoesNotThrow(() -> set.asScanningPivotCache(left + 1, right - 1));

        final int noOfLongs = 1 + (right - left) / Long.SIZE;
        final int highBit = left + noOfLongs * Long.SIZE - 1;

        set.set(highBit);
        final int capacity = set.nextClearBit(highBit);
        Assertions.assertEquals(highBit + 1, capacity);
        Assertions.assertDoesNotThrow(() -> set.asScanningPivotCache(left, highBit));
        Assertions.assertThrows(IllegalArgumentException.class, () -> set.asScanningPivotCache(left, capacity));
    }

    @Test
    void testMemoryFootprint() {


        final long longBytes = Long.BYTES;
        Assertions.assertEquals(longBytes * 1, IndexSet.memoryFootprint(0, 0));
        Assertions.assertEquals(longBytes * 1, IndexSet.memoryFootprint(0, 63));
        Assertions.assertEquals(longBytes * 2, IndexSet.memoryFootprint(0, 64));
        Assertions.assertEquals(longBytes * 2, IndexSet.memoryFootprint(0, 127));
        Assertions.assertEquals(longBytes * 3, IndexSet.memoryFootprint(0, 128));

        Assertions.assertEquals(longBytes * Math.ceil(128 / 64.0), IndexSet.memoryFootprint(0, 127));
        Assertions.assertEquals(longBytes * Math.ceil(129 / 64.0), IndexSet.memoryFootprint(0, 128));

        Assertions.assertEquals(longBytes * (1 << 25), IndexSet.memoryFootprint(0, Integer.MAX_VALUE));

        final int left = 12563;
        Assertions.assertEquals(longBytes * 1, IndexSet.memoryFootprint(left, left + 0));
        Assertions.assertEquals(longBytes * 1, IndexSet.memoryFootprint(left, left + 63));
        Assertions.assertEquals(longBytes * 2, IndexSet.memoryFootprint(left, left + 64));
        Assertions.assertEquals(longBytes * 2, IndexSet.memoryFootprint(left, left + 127));
        Assertions.assertEquals(longBytes * 3, IndexSet.memoryFootprint(left, left + 128));
    }

    @ParameterizedTest
    @MethodSource
    void testGetSet(int[] indices, int n) {
        final IndexSet set = createIndexSet(indices);
        final BitSet ref = new BitSet(n);
        for (final int i : indices) {
            Assertions.assertEquals(ref.get(i), set.get(i), () -> String.valueOf(i));
            set.set(i);
            ref.set(i);
            Assertions.assertTrue(set.get(i));
        }
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet"})
    void testSetRange1(int[] indices, int n) {
        final IndexSet set = createIndexSet(indices);
        final BitSet ref = new BitSet(n);
        for (final int i : indices) {
            Assertions.assertEquals(ref.get(i), set.get(i));

            set.set(i, i);
            ref.set(i);
            Assertions.assertTrue(set.get(i));
        }
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet"})
    void testSetRange(int[] indices, int n) {
        final IndexSet set = createIndexSet(indices);
        Arrays.sort(indices);
        for (int i = 1; i < indices.length; i++) {
            final int from = indices[i - 1];
            final int to = indices[i];

            if (from == to) {
                continue;
            }
            for (int j = from; j < to; j++) {
                Assertions.assertFalse(set.get(j));
            }
            set.set(from, to - 1);
            for (int j = from; j < to; j++) {
                Assertions.assertTrue(set.get(j));
            }
        }
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet"})
    void testPreviousNextSetBit(int[] indices, int n) {
        final IndexSet set = createIndexSet(indices);
        final BitSet ref = new BitSet(n);
        Arrays.sort(indices);
        Assertions.assertEquals(-1, set.previousSetBit(0));
        Assertions.assertEquals(-1, set.nextSetBit(0));
        final int highBit = indices[indices.length - 1];
        Assertions.assertEquals(-1, set.previousSetBit(highBit));
        Assertions.assertEquals(-1, set.nextSetBit(highBit));
        for (int i = 1; i < indices.length; i++) {
            final int from = indices[i - 1];
            final int to = indices[i];
            final int middle = (from + to) >>> 1;
            Assertions.assertEquals(ref.previousSetBit(middle), set.previousSetBit(middle));
            Assertions.assertEquals(ref.nextSetBit(middle), set.nextSetBit(middle));
            set.set(from);
            set.set(to);
            ref.set(from);
            ref.set(to);
            Assertions.assertEquals(ref.previousSetBit(middle), set.previousSetBit(middle));
            Assertions.assertEquals(ref.nextSetBit(middle), set.nextSetBit(middle));
        }
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet"})
    void testPreviousNextClearBit(int[] indices, int n) {
        final IndexSet set = createIndexSet(indices);
        final BitSet ref = new BitSet(n);
        Arrays.sort(indices);


        final int highBit = indices[indices.length - 1];


        Assertions.assertEquals(0, set.nextClearBit(0));
        Assertions.assertEquals(indices[0] - 1, set.nextClearBit(indices[0] - 1));
        Assertions.assertEquals(indices[0], set.nextClearBit(indices[0]));
        Assertions.assertEquals(highBit, set.nextClearBit(highBit));
        Assertions.assertEquals(0, set.previousClearBit(0));
        Assertions.assertEquals(indices[0] - 1, set.previousClearBit(indices[0] - 1));
        Assertions.assertEquals(indices[0], set.previousClearBit(indices[0]));
        Assertions.assertEquals(highBit, set.previousClearBit(highBit));

        for (int i = 1; i < indices.length; i++) {
            final int from = indices[i - 1];
            final int to = indices[i];
            final int middle = (from + to) >>> 1;
            Assertions.assertEquals(ref.nextClearBit(middle), set.nextClearBit(middle));
            Assertions.assertEquals(ref.previousClearBit(middle), set.previousClearBit(middle));

            set.set(from, to);
            ref.set(from, to + 1);
            Assertions.assertEquals(ref.nextClearBit(middle), set.nextClearBit(middle));
            Assertions.assertEquals(ref.previousClearBit(middle), set.previousClearBit(middle));
        }
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet", "testForEachToArray"})
    void testForEachToArray(int[] indices, int n) {
        final IndexSet set = createIndexSet(indices);
        final BitSet ref = new BitSet(n);
        Arrays.stream(indices).forEach(i -> {
            set.set(i);
            ref.set(i);
        });
        final int[] e = ref.stream().toArray();

        final int[] a = new int[e.length];
        final int[] c = {0};
        set.forEach(i -> a[c[0]++] = i);
        Assertions.assertArrayEquals(e, a);



        int[] original = indices.clone();

        int len = set.toArray(indices);
        int[] x = Arrays.copyOf(indices, len);
        Assertions.assertArrayEquals(e, x);

        if (len < indices.length) {
            final int[] y = Arrays.copyOfRange(indices, len, indices.length);
            final int[] z = Arrays.copyOfRange(original, len, original.length);
            Assertions.assertArrayEquals(z, y);
        }



        len = set.toArray2(indices);
        x = Arrays.copyOf(indices, len);
        Assertions.assertArrayEquals(e, x);

        if (len < indices.length) {
            final int[] y = Arrays.copyOfRange(indices, len, indices.length);
            final int[] z = Arrays.copyOfRange(original, len, original.length);
            Assertions.assertArrayEquals(z, y);
        }
    }

    static Stream<Arguments> testForEachToArray() {
        final Stream.Builder<Arguments> builder = Stream.builder();

        builder.accept(Arguments.of(new int[] {1, 1, 1, 2, 3, 4, 5, 6, 7}, 10));
        builder.accept(Arguments.of(new int[] {5, 6, 2, 2, 3, 8, 1, 1, 4, 3}, 10));
        builder.accept(Arguments.of(new int[] {2, 2, 2, 2, 2}, 10));
        builder.accept(Arguments.of(new int[] {2000, 2001, 2000, 2001}, 2010));
        return builder.build();
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet"})
    void testOfIndices(int[] indices) {
        assertOfIndices(indices, -1);
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet"})
    void testOfIndicesTruncated(int[] indices) {
        Assumptions.assumeTrue(indices.length > 1);
        final int n = ThreadLocalRandom.current().nextInt(1, indices.length);
        assertOfIndices(indices, n);
    }

    private static void assertOfIndices(int[] indices, int n) {
        final IndexSet set = n < 0 ? IndexSet.of(indices) : IndexSet.of(indices, n);
        final BitSet ref = new BitSet();
        final int upper = n < 0 ? indices.length : n;
        for (int i = 0; i < upper; i++) {
            ref.set(indices[i]);
            Assertions.assertTrue(set.get(indices[i]));
        }
        final int[] e = ref.stream().toArray();
        final int[] a = new int[e.length];
        final int[] c = {0};
        set.forEach(i -> a[c[0]++] = i);
        Assertions.assertArrayEquals(e, a);
    }

    static Stream<Arguments> testGetSet() {
        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
        final Stream.Builder<Arguments> builder = Stream.builder();
        for (final int size : new int[] {5, 500}) {
            final int[] a = rng.ints(10, 0, size).toArray();
            builder.accept(Arguments.of(a.clone(), size));

            a[0] = 0;
            builder.accept(Arguments.of(a, size));
        }

        final int n = 513;

        builder.accept(Arguments.of(new int[] {n - 13, n - 1}, n));
        builder.accept(Arguments.of(new int[] {n - 78, n - 1}, n));
        builder.accept(Arguments.of(new int[] {n - 137, n - 1}, n));


        builder.accept(Arguments.of(new int[] {1, n - 1}, n));
        return builder.build();
    }







    private static IndexSet createIndexSet(int[] indices) {
        final int min = Arrays.stream(indices).min().getAsInt();
        final int max = Arrays.stream(indices).max().getAsInt();
        return IndexSet.ofRange(min, max);
    }

    @Test
    void testCardinalityEmpty() {
        final IndexSet set = IndexSet.ofRange(34, 219);
        Assertions.assertEquals(0, set.cardinality());
        Assertions.assertEquals(0, set.cardinality2());
        Assertions.assertEquals(0, set.cardinality4());
        Assertions.assertEquals(0, set.cardinality8());
        Assertions.assertEquals(0, set.cardinality16());
        Assertions.assertEquals(0, set.cardinality32());
        Assertions.assertEquals(0, set.cardinality64());
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet", "testCardinality"})
    void testCardinality(int[] indices) {

        Assertions.assertEquals(compressedCardinality(indices, 0), IndexSet.of(indices).cardinality());
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet", "testCardinality"})
    void testCardinality2(int[] indices) {
        Assertions.assertEquals(compressedCardinality(indices, 1), IndexSet.of(indices).cardinality2());
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet", "testCardinality"})
    void testCardinality4(int[] indices) {
        Assertions.assertEquals(compressedCardinality(indices, 2), IndexSet.of(indices).cardinality4());
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet", "testCardinality"})
    void testCardinality8(int[] indices) {
        Assertions.assertEquals(compressedCardinality(indices, 3), IndexSet.of(indices).cardinality8());
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet", "testCardinality"})
    void testCardinality16(int[] indices) {
        Assertions.assertEquals(compressedCardinality(indices, 4), IndexSet.of(indices).cardinality16());
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet", "testCardinality"})
    void testCardinality32(int[] indices) {
        Assertions.assertEquals(compressedCardinality(indices, 5), IndexSet.of(indices).cardinality32());
    }

    @ParameterizedTest
    @MethodSource(value = {"testGetSet", "testCardinality"})
    void testCardinality64(int[] indices) {
        Assertions.assertEquals(compressedCardinality(indices, 6), IndexSet.of(indices).cardinality64());
    }

    private static int compressedCardinality(int[] indices, int compression) {
        final int min = Arrays.stream(indices).min().orElse(0);
        final int max = Arrays.stream(indices).max().orElse(64);
        final BitSet ref = new BitSet((max - min) >>> compression);
        for (final int i : indices) {
            ref.set((i - min) >>> compression);
        }
        return ref.cardinality() << compression;
    }

    static Stream<int[]> testCardinality() {
        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
        final Stream.Builder<int[]> builder = Stream.builder();
        for (int i = 0; i < 64; i++) {
            builder.accept(new int[] {i});
            for (final int j = 0; i < 64; i++) {
                builder.accept(new int[] {i, j});
            }
        }
        builder.accept(IntStream.range(0, 64).toArray());
        for (int i = 0; i < 50; i++) {
            builder.accept(rng.ints(30, 0, 500).toArray());
            builder.accept(rng.ints(10, 499, 879).toArray());
            builder.accept(rng.ints(2, 0, 64).toArray());
            builder.accept(rng.ints(4, 0, 64).toArray());
            builder.accept(rng.ints(8, 0, 64).toArray());
            builder.accept(rng.ints(16, 0, 64).toArray());
            builder.accept(rng.ints(32, 0, 64).toArray());
            builder.accept(rng.ints(64, 0, 64).toArray());
        }
        return builder.build();
    }
}
