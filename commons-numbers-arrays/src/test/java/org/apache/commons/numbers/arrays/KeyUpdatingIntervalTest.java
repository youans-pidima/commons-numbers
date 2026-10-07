
















package org.apache.commons.numbers.arrays;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;




class KeyUpdatingIntervalTest {

    @ParameterizedTest
    @MethodSource
    void testSearch(int[] keys, int left, int right) {

        final int l = left < 0 ? 0 : left;
        final int r = right < 0 ? keys.length - 1 : right;
        for (int i = l; i <= r; i++) {
            final int k = keys[i];

            Assertions.assertEquals(k, keys[KeyUpdatingInterval.searchLessOrEqual(keys, l, r, k)], "leq");
        }

        Assertions.assertEquals(l - 1, KeyUpdatingInterval.searchLessOrEqual(keys, l, r, keys[l] - 44), "leq below");
        Assertions.assertEquals(r, KeyUpdatingInterval.searchLessOrEqual(keys, l, r, keys[r] + 44), "leq above");

        for (int i = l + 1; i <= r; i++) {

            final int k1 = keys[i - 1];
            final int k2 = keys[i];
            for (int k = k1 + 1; k < k2; k++) {
                Assertions.assertEquals(i - 1, KeyUpdatingInterval.searchLessOrEqual(keys, l, r, k), "leq between");
            }
        }
    }

    static Stream<Arguments> testSearch() {
        final Stream.Builder<Arguments> builder = Stream.builder();
        final int allIndices = -1;
        builder.add(Arguments.of(new int[] {1}, allIndices, allIndices));
        builder.add(Arguments.of(new int[] {1, 2}, allIndices, allIndices));
        builder.add(Arguments.of(new int[] {1, 10}, allIndices, allIndices));
        builder.add(Arguments.of(new int[] {1, 2, 3}, allIndices, allIndices));
        builder.add(Arguments.of(new int[] {1, 4, 7}, allIndices, allIndices));
        builder.add(Arguments.of(new int[] {1, 4, 5, 7}, allIndices, allIndices));

        builder.add(Arguments.of(new int[] {1, 1, 1, 1, 1, 1}, allIndices, allIndices));
        builder.add(Arguments.of(new int[] {1, 1, 1, 1, 3, 3, 3, 3, 3, 5, 5, 5, 5}, allIndices, allIndices));

        builder.add(Arguments.of(new int[] {1, 4, 5, 7, 13, 15}, 2, 4));
        builder.add(Arguments.of(new int[] {1, 4, 5, 7, 13, 15}, 0, 3));
        builder.add(Arguments.of(new int[] {1, 4, 5, 7, 13, 15}, 3, 5));
        return builder.build();
    }
}
