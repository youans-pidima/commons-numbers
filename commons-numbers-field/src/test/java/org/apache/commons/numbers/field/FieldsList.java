















package org.apache.commons.numbers.field;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import java.util.ArrayList;

import org.apache.commons.numbers.fraction.Fraction;
import org.apache.commons.numbers.core.DD;
import org.apache.commons.numbers.core.Precision;
import org.apache.commons.numbers.fraction.BigFraction;

import org.junit.jupiter.api.Assertions;




final class FieldsList {

    private static final List<FieldTestData<?>> LIST =
            new ArrayList<>();

    static {
        try {

            add(FractionField.get(),
                Fraction.of(13, 4),
                Fraction.of(5, 29),
                Fraction.of(-279, 11));
            add(BigFractionField.get(),
                BigFraction.of(13256093L, 43951044L),
                BigFraction.of(543016315L, 29L),
                BigFraction.of(-27930919051L, 11L));
            add(FP64Field.get(),
                FP64.of(23.45678901),
                FP64.of(-543.2109876),
                FP64.of(-234.5678901),

                (x, y) -> Precision.equals(x.doubleValue(), y.doubleValue(), 1));
            add(DDField.get(),
                createDD(23.45678901, 4.5671892973),
                createDD(-543.2109876, 5.237848286),
                createDD(-234.5678901, -4.561268179),

                FieldsList::areEqual);

            add(DDField.get(),
                createDD(2.45678901, 45.671892973),
                createDD(-54.32109876, 52.37848286),
                createDD(-0.0, -0.0),

                FieldsList::areEqual);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    private FieldsList() {}







    private static <T> void add(Field<T> field,
                                T a,
                                T b,
                                T c) {
        LIST.add(new FieldTestData<>(field, a, b, c));
    }








    private static <T> void add(Field<T> field,
                                T a,
                                T b,
                                T c,
                                BiPredicate<T, T> equals) {
        LIST.add(new FieldTestData<>(field, a, b, c, equals));
    }









    private static DD createDD(double a, double b) {
        final int ea = Math.getExponent(a);
        final int eb = Math.getExponent(b);

        double bb = Math.scalb(b, ea - eb - 53);

        if (a != a + bb) {
            bb *= 0.5;
        }
        Assertions.assertEquals(a, a + bb);
        return DD.ofSum(a, bb);
    }








    private static boolean areEqual(DD x, DD y) {

        if (x.equals(y)) {
            return true;
        }

        if (x.hi() == y.hi() && Precision.equals(x.lo(), y.lo(), 1)) {
            return true;
        }







        return Math.abs(x.subtract(y).doubleValue()) /
            Math.max(Math.abs(x.doubleValue()), Math.abs(y.doubleValue())) <= Math.pow(2, -105);
    }







    static Stream<FieldTestData<?>> stream() {
        return LIST.stream();
    }
}
