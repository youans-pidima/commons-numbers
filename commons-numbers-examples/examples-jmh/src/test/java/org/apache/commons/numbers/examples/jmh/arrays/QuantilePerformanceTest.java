
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.EnumSet;
import org.apache.commons.numbers.examples.jmh.arrays.SelectionPerformance.AbstractDataSource;
import org.apache.commons.numbers.examples.jmh.arrays.SelectionPerformance.AbstractDataSource.Distribution;
import org.apache.commons.numbers.examples.jmh.arrays.SelectionPerformance.AbstractDataSource.Modification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class SelectionPerformanceTest {
    @Test
    void testGetDistribution() {
        assertGetEnumFromParam(Distribution.class);
    }

    @Test
    void testGetModification() {
        assertGetEnumFromParam(Modification.class);
    }

    static <E extends Enum<E>> void assertGetEnumFromParam(Class<E> cls) {
        Assertions.assertEquals(EnumSet.allOf(cls),
            AbstractDataSource.getEnumFromParam(cls, "all"));
        Assertions.assertThrows(IllegalStateException.class,
            () -> AbstractDataSource.getEnumFromParam(cls, "nothing"));
        for (final E e1 : cls.getEnumConstants()) {
            final String s = e1.name().toLowerCase();
            Assertions.assertEquals(EnumSet.of(e1),
                AbstractDataSource.getEnumFromParam(cls, e1.name()));
            Assertions.assertEquals(EnumSet.of(e1),
                AbstractDataSource.getEnumFromParam(cls, s));
            for (final E e2 : cls.getEnumConstants()) {
                Assertions.assertEquals(EnumSet.of(e1, e2),
                    AbstractDataSource.getEnumFromParam(cls, s + ":" + e2.name()));
                Assertions.assertEquals(EnumSet.of(e1, e2),
                    AbstractDataSource.getEnumFromParam(cls, e2.name() + ":" + e1.name()));
            }
        }
    }
}
