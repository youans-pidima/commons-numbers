
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Supplier;
import org.apache.commons.numbers.core.DD;
import org.junit.jupiter.api.Assertions;




final class TestUtils {

    private TestUtils() {}





























    static double assertEquals(BigDecimal expected, DD actual, double eps, String msg) {
        return assertEquals(expected, actual, eps, () -> msg);
    }



























    static double assertEquals(BigDecimal expected, DD actual, double eps, Supplier<String> msg) {

        final BigDecimal delta = new BigDecimal(actual.hi())
            .add(new BigDecimal(actual.lo()))
            .subtract(expected);
        boolean equal;
        if (expected.compareTo(BigDecimal.ZERO) == 0) {


            equal = actual.doubleValue() == 0;


            if (eps < 0) {
                if (!equal) {
                    printf("%sexpected 0 != actual <%s + %s> (abs.error=%s)%n",
                        prefix(msg), actual.hi(), actual.lo(), delta.doubleValue());
                }
            } else if (!equal) {
                Assertions.fail(String.format("%sexpected 0 != actual <%s + %s> (abs.error=%s)",
                    prefix(msg), actual.hi(), actual.lo(), delta.doubleValue()));
            }

            return 0;
        }

        final double rel = delta.divide(expected, MathContext.DECIMAL128).doubleValue();

        equal = Math.abs(rel) <= Math.abs(eps);


        if (eps < 0) {
            if (!equal) {
                printf("%sexpected <%s> != actual <%s + %s> (rel.error=%s (%.3f x tol))%n",
                    prefix(msg), expected.round(MathContext.DECIMAL128), actual.hi(), actual.lo(),
                    rel, Math.abs(rel) / eps);
            }
        } else if (!equal) {
            Assertions.fail(String.format("%sexpected <%s> != actual <%s + %s> (rel.error=%s (%.3f x tol))",
                prefix(msg), expected.round(MathContext.DECIMAL128), actual.hi(), actual.lo(),
                rel, Math.abs(rel) / eps));
        }

        return rel;
    }









    static void printf(String format, Object... args) {

        System.out.printf(format, args);

    }







    static String prefix(Supplier<String> msg) {
        return msg == null ? "" : msg.get() + ": ";
    }
}
