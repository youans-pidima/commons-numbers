
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;




class HashIndexSetTest {

    @Test
    void testInvalidCapacityThrows() {
        final int maxCapacity = 1 << 29;
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HashIndexSet(maxCapacity + 1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HashIndexSet(Integer.MAX_VALUE));
    }

    @Test
    void testInvalidIndexThrows() {
        final HashIndexSet set = new HashIndexSet(16);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> set.add(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> set.add(Integer.MIN_VALUE));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> set.contains(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> set.contains(Integer.MIN_VALUE));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 32})
    void testCapacityExceededThrows(int capacity) {
        final HashIndexSet set = new HashIndexSet(capacity);
        IntStream.range(0, capacity).forEach(set::add);
        Assertions.assertEquals(capacity, set.size());



        Assertions.assertThrows(IllegalStateException.class, () -> {
            for (int i = capacity, upperLimit = 2 * capacity; i < upperLimit; i++) {
                set.add(i);
            }
        });
    }

    @Test
    void testMemoryFootprint() {

        final long intBytes = Integer.BYTES;
        Assertions.assertEquals(intBytes * 16, HashIndexSet.memoryFootprint(-1));
        Assertions.assertEquals(intBytes * 16, HashIndexSet.memoryFootprint(8));

        Assertions.assertEquals(intBytes * 32, HashIndexSet.memoryFootprint(16));
        Assertions.assertEquals(intBytes * 64, HashIndexSet.memoryFootprint(17));
        Assertions.assertEquals(intBytes * 64, HashIndexSet.memoryFootprint(31));
        Assertions.assertEquals(intBytes * 64, HashIndexSet.memoryFootprint(32));
        Assertions.assertEquals(intBytes * 128, HashIndexSet.memoryFootprint(33));

        Assertions.assertEquals(intBytes * (1 << 30), HashIndexSet.memoryFootprint(1 << 29));

        Assertions.assertEquals(intBytes * (1L << 31), HashIndexSet.memoryFootprint((1 << 29) + 1));
        Assertions.assertEquals(intBytes * (1L << 31), HashIndexSet.memoryFootprint(1 << 30));
        Assertions.assertEquals(intBytes * (1L << 32), HashIndexSet.memoryFootprint(Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @MethodSource(value = {"testIndices"})
    void testAddContains(int[] indices, int capacity) {
        final HashIndexSet set = new HashIndexSet(capacity);
        final BitSet ref = new BitSet(capacity);
        for (final int i : indices) {

            Assertions.assertEquals(!ref.get(i), set.add(i), () -> String.valueOf(i));
            ref.set(i);
            Assertions.assertTrue(set.contains(i), () -> String.valueOf(i));
        }
        Assertions.assertEquals(ref.cardinality(), set.size(), "Size");
    }

    @ParameterizedTest
    @MethodSource(value = {"testIndices"})
    void testToArray(int[] indices, int capacity) {
        final HashIndexSet set = new HashIndexSet(capacity);
        final BitSet ref = new BitSet(capacity);
        Arrays.stream(indices).forEach(i -> {
            set.add(i);
            ref.set(i);
        });
        final int[] e = ref.stream().toArray();
        Assertions.assertEquals(e.length, set.size(), "Size");
        final int[] a = new int[e.length];
        set.toArray(a);
        Arrays.sort(a);
        Assertions.assertArrayEquals(e, a);


        int[] original = indices.clone();
        final int len = set.toArray(indices);
        int[] x = Arrays.copyOf(indices, len);
        Arrays.sort(x);
        Assertions.assertArrayEquals(e, x);

        if (len < indices.length) {
            x = Arrays.copyOfRange(indices, len, indices.length);
            original = Arrays.copyOfRange(original, len, original.length);
            Assertions.assertArrayEquals(original, x);
        }
    }

    static Stream<Arguments> testIndices() {
        final Stream.Builder<Arguments> builder = Stream.builder();

        builder.accept(Arguments.of(new int[] {1, 2}, 10));
        builder.accept(Arguments.of(new int[] {1, 2, 3, 4, 5}, 10));


        builder.accept(Arguments.of(new int[] {1, 1, 1, 2, 3, 4, 5, 6, 7}, 10));
        builder.accept(Arguments.of(new int[] {5, 6, 2, 2, 3, 8, 1, 1, 4, 3}, 10));
        builder.accept(Arguments.of(new int[] {2, 2, 2, 2, 2}, 10));
        builder.accept(Arguments.of(new int[] {2000, 2001, 2000, 2001}, 2010));

        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
        for (final int size : new int[] {5, 500}) {

            builder.accept(Arguments.of(rng.ints(10, 0, size).toArray(), size));

            builder.accept(Arguments.of(rng.ints(size, 0, size).toArray(), size));
            builder.accept(Arguments.of(rng.ints(size, 0, size >> 1).toArray(), size));
            builder.accept(Arguments.of(rng.ints(size, 0, size >> 2).toArray(), size));
        }

        return builder.build();
    }
}
