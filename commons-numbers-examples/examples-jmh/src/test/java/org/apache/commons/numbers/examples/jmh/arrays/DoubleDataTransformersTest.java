
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;




class DoubleDataTransformersTest {
    @ParameterizedTest
    @MethodSource(value = {"nanData"})
    void testNaNErrorWithNaN(double[] a) {
        final DoubleDataTransformer t1 = DoubleDataTransformers.createFactory(NaNPolicy.ERROR, false).get();
        Assertions.assertThrows(IllegalArgumentException.class, () -> t1.preProcess(a));
        final DoubleDataTransformer t2 = DoubleDataTransformers.createFactory(NaNPolicy.ERROR, true).get();
        Assertions.assertThrows(IllegalArgumentException.class, () -> t2.preProcess(a));
    }

    @ParameterizedTest
    @MethodSource(value = {"nonNanData"})
    void testNaNError(double[] a) {
        assertSortTransformer(a, DoubleDataTransformers.createFactory(NaNPolicy.ERROR, false).get(), true, false);
        assertSortTransformer(a, DoubleDataTransformers.createFactory(NaNPolicy.ERROR, true).get(), true, true);
    }

    @ParameterizedTest
    @MethodSource(value = {"nanData", "nonNanData"})
    void testNaNInclude(double[] a) {
        assertSortTransformer(a, DoubleDataTransformers.createFactory(NaNPolicy.INCLUDE, false).get(), true, false);
        assertSortTransformer(a, DoubleDataTransformers.createFactory(NaNPolicy.INCLUDE, true).get(), true, true);
    }

    @ParameterizedTest
    @MethodSource(value = {"nanData", "nonNanData"})
    void testNaNExclude(double[] a) {
        assertSortTransformer(a, DoubleDataTransformers.createFactory(NaNPolicy.EXCLUDE, false).get(), false, false);
        assertSortTransformer(a, DoubleDataTransformers.createFactory(NaNPolicy.EXCLUDE, true).get(), false, true);
    }











    private static void assertSortTransformer(double[] a, DoubleDataTransformer t,
            boolean includeNaN, boolean copy) {
        final double[] original = a.clone();
        final double[] b = t.preProcess(a);
        if (copy) {
            Assertions.assertNotSame(a, b);
        } else {
            Assertions.assertSame(a, b);
        }

        final int nanCount = (int) Arrays.stream(a).filter(Double::isNaN).count();
        Assertions.assertEquals(a.length - nanCount, t.length(), "Length to process");
        Assertions.assertEquals(a.length - (includeNaN ? 0 : nanCount), t.size(), "Size of data");

        Arrays.sort(b, 0, t.length());

        Arrays.sort(original);

        if (b.length > 0) {

            t.postProcess(b, new int[] {b.length - 1}, 1);
        }
        Assertions.assertArrayEquals(original, b);
    }

    static Stream<double[]> nanData() {
        final Stream.Builder<double[]> builder = Stream.builder();
        final double nan = Double.NaN;
        builder.add(new double[] {nan});
        builder.add(new double[] {1, 2, nan});
        builder.add(new double[] {nan, 2, 3});
        builder.add(new double[] {1, nan, 3});
        builder.add(new double[] {nan, nan});
        builder.add(new double[] {nan, 2, nan});
        builder.add(new double[] {nan, nan, nan});
        builder.add(new double[] {1, 0.0, 0.0, nan, -1});
        builder.add(new double[] {1, 0.0, -0.0, nan, -1});
        builder.add(new double[] {1, -0.0, 0.0, nan, -1});
        builder.add(new double[] {1, -0.0, -0.0, nan, -1});
        builder.add(new double[] {1, 0.0, 0.0, nan, -1, 0.0, 0.0});
        builder.add(new double[] {1, 0.0, -0.0, nan, -1, 0.0, 0.0});
        builder.add(new double[] {1, 0.0, -0.0, nan, -1, 0.0, -0.0});
        builder.add(new double[] {nan, -0.0, 0.0, nan, -1, -0.0, -0.0});
        builder.add(new double[] {nan, -0.0, -0.0, nan, -1, -0.0, -0.0});
        return builder.build();
    }

    static Stream<double[]> nonNanData() {
        final Stream.Builder<double[]> builder = Stream.builder();
        builder.add(new double[] {});
        builder.add(new double[] {3});
        builder.add(new double[] {3, 2, 1});
        builder.add(new double[] {1, 0.0, 0.0, 3, -1});
        builder.add(new double[] {1, 0.0, -0.0, 3, -1});
        builder.add(new double[] {1, -0.0, 0.0, 3, -1});
        builder.add(new double[] {1, -0.0, -0.0, 3, -1});
        builder.add(new double[] {1, 0.0, 0.0, 3, -1, 0.0, 0.0});
        builder.add(new double[] {1, 0.0, -0.0, 3, -1, 0.0, 0.0});
        builder.add(new double[] {1, 0.0, -0.0, 3, -1, 0.0, -0.0});
        builder.add(new double[] {1, -0.0, 0.0, 3, -1, -0.0, -0.0});
        builder.add(new double[] {1, -0.0, -0.0, 3, -1, -0.0, -0.0});
        return builder.build();
    }
}
