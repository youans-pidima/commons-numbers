
















package org.apache.commons.numbers.arrays;

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
        Assertions.assertThrows(IllegalArgumentException.class, () -> HashIndexSet.create(maxCapacity + 1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> HashIndexSet.create(Integer.MAX_VALUE));
    }

    @Test
    void testInvalidIndexThrows() {
        final HashIndexSet set = HashIndexSet.create(16);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> set.add(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> set.add(Integer.MIN_VALUE));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 32})
    void testCapacityExceededThrows(int capacity) {
        final HashIndexSet set = HashIndexSet.create(capacity);
        IntStream.range(0, capacity).forEach(set::add);



        Assertions.assertThrows(IllegalStateException.class, () -> {
            for (int i = capacity; i < capacity * 2; i++) {
                set.add(i);
            }
        });
    }

    @ParameterizedTest
    @MethodSource(value = {"testIndices"})
    void testAdd(int[] indices, int capacity) {
        final HashIndexSet set = HashIndexSet.create(capacity);
        final BitSet ref = new BitSet(capacity);
        for (final int i : indices) {
            final boolean observed = ref.get(i);

            Assertions.assertEquals(!observed, set.add(i), () -> String.valueOf(i));
            ref.set(i);
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
