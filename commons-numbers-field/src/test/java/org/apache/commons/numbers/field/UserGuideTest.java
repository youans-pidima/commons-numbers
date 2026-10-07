















package org.apache.commons.numbers.field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class UserGuideTest {

    @Test
    void testField1() {
        Field<FP64> field = FP64Field.get();
        FP64 result = field.one().multiply(3).divide(field.one().multiply(4));
        double value = result.doubleValue();
        Assertions.assertEquals(0.75, value);
    }
}
