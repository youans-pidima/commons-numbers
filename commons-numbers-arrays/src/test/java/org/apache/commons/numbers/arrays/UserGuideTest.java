















package org.apache.commons.numbers.arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class UserGuideTest {

    @Test
    void testSortInPlace1() {
        double[] x = {3, 1, 2};
        double[] y = {1, 2, 3};
        double[] z = {0, 5, 7};
        SortInPlace.ASCENDING.apply(x, y, z);
        Assertions.assertArrayEquals(new double[] {1, 2, 3}, x);
        Assertions.assertArrayEquals(new double[] {2, 3, 1}, y);
        Assertions.assertArrayEquals(new double[] {5, 7, 0}, z);
    }

    @Test
    void testMultidimensionalCounter1() {
        MultidimensionalCounter c = MultidimensionalCounter.of(100, 50);
        int size = c.getSize();
        Assertions.assertEquals(5000, size);

        int index = 233;
        int[] indices1 = c.toMulti(index);
        int[] indices2 = c.toMulti(index + 1);
        Assertions.assertArrayEquals(new int[] {4, 33}, indices1);
        Assertions.assertArrayEquals(new int[] {4, 34}, indices2);

        int index1 = c.toUni(4, 33);
        int index2 = c.toUni(indices1);
        Assertions.assertEquals(index, index1);
        Assertions.assertEquals(index, index2);

        Assertions.assertArrayEquals(new int[] {0, 49}, c.toMulti(49));
        Assertions.assertArrayEquals(new int[] {1, 0}, c.toMulti(50));
        Assertions.assertArrayEquals(new int[] {99, 49}, c.toMulti(4999));
    }

    @Test
    void testSelection1() {
        int[] data = {0, 1, 2, 1, 2, 5, 2, 3, 3, 6, 7, 7, 7, 7};

        Selection.select(data, 4);
        Assertions.assertEquals(2, data[4]);

        Selection.select(data, new int[] {4, 8});
        Assertions.assertEquals(2, data[4]);
        Assertions.assertEquals(5, data[8]);
    }
}
