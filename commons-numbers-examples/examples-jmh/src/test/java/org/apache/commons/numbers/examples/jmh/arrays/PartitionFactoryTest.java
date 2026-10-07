
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.EnumSet;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.AdaptMode;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.KeyStrategy;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.PairedKeyStrategy;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.SPStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class PartitionFactoryTest {
    @Test
    void testGetMinQuickSelectSize() {
        assertIntParameter(Partition.MIN_QUICKSELECT_SIZE, "QS", PartitionFactory::getMinQuickSelectSize);
    }

    @Test
    void testGetEdgeSelectConstant() {
        assertIntParameter(Partition.EDGESELECT_CONSTANT, "EC", PartitionFactory::getEdgeSelectConstant);
    }

    @Test
    void testGetLinearSortSelectConstant() {
        assertIntParameter(Partition.LINEAR_SORTSELECT_SIZE, "LC", PartitionFactory::getLinearSortSelectConstant);
    }

    @Test
    void testGetSubSamplingSize() {
        assertIntParameter(Partition.SUBSAMPLING_SIZE, "SU", PartitionFactory::getSubSamplingSize);
    }

    @Test
    void testGetRecursionMultiple() {
        assertDoubleParameter(Partition.RECURSION_MULTIPLE, "RM", PartitionFactory::getRecursionMultiple);
    }

    @Test
    void testGetRecursionConstant() {
        assertIntParameter(Partition.RECURSION_CONSTANT, "RC", PartitionFactory::getRecursionConstant);
    }

    @Test
    void testGetCompressionLevel() {
        assertIntParameter(Partition.COMPRESSION_LEVEL, "CL", PartitionFactory::getCompressionLevel);
    }

    @Test
    void testGetControlFlags() {
        assertIntParameter(Partition.CONTROL_FLAGS, "CF", PartitionFactory::getControlFlags);

        Assertions.assertEquals(-1, PartitionFactory.getControlFlags(new String[] {"CF-1"}));
        Assertions.assertEquals(-2, PartitionFactory.getControlFlags(new String[] {"CF-2"}));
        Assertions.assertEquals(-42, PartitionFactory.getControlFlags(new String[] {"none"}, -42));
    }

    @Test
    void testGetOptionFlags() {
        assertIntParameter(Partition.OPTION_FLAGS, "OF", PartitionFactory::getOptionFlags);

        Assertions.assertEquals(-1, PartitionFactory.getOptionFlags(new String[] {"OF-1"}));
        Assertions.assertEquals(-2, PartitionFactory.getOptionFlags(new String[] {"OF-2"}));
        Assertions.assertEquals(-42, PartitionFactory.getOptionFlags(new String[] {"none"}, -42));
    }

    @Test
    void testGetPivotingStrategy() {
        assertEnumParameter(Partition.PIVOTING_STRATEGY,
            s -> PartitionFactory.getEnumOrElse(s, PivotingStrategy.class, Partition.PIVOTING_STRATEGY));
    }

    @Test
    void testGetDualPivotingStrategy() {
        assertEnumParameter(Partition.DUAL_PIVOTING_STRATEGY,
            s -> PartitionFactory.getEnumOrElse(s, DualPivotingStrategy.class, Partition.DUAL_PIVOTING_STRATEGY));
    }

    @Test
    void testGetKeyStrategy() {
        assertEnumParameter(Partition.KEY_STRATEGY,
            s -> PartitionFactory.getEnumOrElse(s, KeyStrategy.class, Partition.KEY_STRATEGY));
    }

    @Test
    void testGetPairedKeyStrategy() {
        assertEnumParameter(Partition.PAIRED_KEY_STRATEGY,
            s -> PartitionFactory.getEnumOrElse(s, PairedKeyStrategy.class, Partition.PAIRED_KEY_STRATEGY));
    }

    @Test
    void testGetSPStrategy() {
        assertEnumParameter(Partition.SP_STRATEGY,
            s -> PartitionFactory.getEnumOrElse(s, SPStrategy.class, Partition.SP_STRATEGY));
    }

    @Test
    void testGetAdaptMode() {
        assertEnumParameter(Partition.ADAPT_MODE,
            s -> PartitionFactory.getEnumOrElse(s, AdaptMode.class, Partition.ADAPT_MODE));
    }

    @Test
    void testInvalidPrefix() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            PartitionFactory.createPartition("IDP", "ISP", 0, 0));
    }

    private static void assertIntParameter(int defaultValue, String pattern, ToIntFunction<String[]> fun) {
        final String[] s = {"nothing"};
        Assertions.assertEquals(defaultValue, fun.applyAsInt(s));
        Assertions.assertEquals("nothing", s[0]);

        if (defaultValue + 4 < 0) {
            defaultValue = 0;
        }
        s[0] = pattern + (defaultValue + 1);
        Assertions.assertEquals(defaultValue + 1, fun.applyAsInt(s));
        Assertions.assertEquals("", s[0]);
        s[0] = "before" + pattern + (defaultValue + 2);
        Assertions.assertEquals(defaultValue + 2, fun.applyAsInt(s));
        Assertions.assertEquals("before", s[0]);
        s[0] = pattern + (defaultValue + 3) + "after";
        Assertions.assertEquals(defaultValue + 3, fun.applyAsInt(s));
        Assertions.assertEquals("after", s[0]);
        s[0] = "before" + pattern + (defaultValue + 4) + "after";
        Assertions.assertEquals(defaultValue + 4, fun.applyAsInt(s));
        Assertions.assertEquals("beforeafter", s[0]);
    }

    private static void assertDoubleParameter(double defaultValue, String pattern, ToDoubleFunction<String[]> fun) {
        final String[] s = {"nothing"};
        Assertions.assertEquals(defaultValue, fun.applyAsDouble(s));
        Assertions.assertEquals("nothing", s[0]);
        s[0] = pattern + (defaultValue + 0.5);
        Assertions.assertEquals(defaultValue + 0.5, fun.applyAsDouble(s));
        Assertions.assertEquals("", s[0]);
        s[0] = "before" + pattern + (defaultValue + 1.5);
        Assertions.assertEquals(defaultValue + 1.5, fun.applyAsDouble(s));
        Assertions.assertEquals("before", s[0]);
        s[0] = pattern + (defaultValue + 2.5) + "after";
        Assertions.assertEquals(defaultValue + 2.5, fun.applyAsDouble(s));
        Assertions.assertEquals("after", s[0]);
        s[0] = "before" + pattern + (defaultValue + 3.5) + "after";
        Assertions.assertEquals(defaultValue + 3.5, fun.applyAsDouble(s));
        Assertions.assertEquals("beforeafter", s[0]);
    }

    private static <E extends Enum<E>> void assertEnumParameter(E defaultValue, Function<String[], E> fun) {
        final String[] s = {"nothing"};
        Assertions.assertEquals(defaultValue, fun.apply(s));
        Assertions.assertEquals("nothing", s[0]);
        EnumSet.allOf(defaultValue.getDeclaringClass()).forEach(e -> {
            s[0] = e.toString();
            Assertions.assertEquals(e, fun.apply(s));
            Assertions.assertEquals("", s[0]);
            s[0] = "before_" + e;
            Assertions.assertEquals(e, fun.apply(s));
            Assertions.assertEquals("before_", s[0]);
            s[0] = e + "after";
            Assertions.assertEquals(e, fun.apply(s));
            Assertions.assertEquals("after", s[0]);
            s[0] = "before_" + e + "after";
            Assertions.assertEquals(e, fun.apply(s));
            Assertions.assertEquals("before_after", s[0]);
        });
    }
}
