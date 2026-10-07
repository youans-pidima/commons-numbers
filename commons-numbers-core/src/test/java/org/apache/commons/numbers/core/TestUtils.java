
















package org.apache.commons.numbers.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.DoubleConsumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;




public final class TestUtils {

    private static final long POSITIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(+0.0);

    private static final long NEGATIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(-0.0);

    private static boolean reportAllDeviations = false;

    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    private static final BigDecimal HALF = new BigDecimal(0.5);

    private static final MathContext MC_SCALING = new MathContext(2 * MathContext.DECIMAL128.getPrecision());




    private TestUtils() {
    }








    public static Object serializeAndRecover(Object o) {
        try {

            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream so = new ObjectOutputStream(bos);
            so.writeObject(o);


            final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            final ObjectInputStream si = new ObjectInputStream(bis);
            return si.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            return null;
        }
    }










    static void printf(String format, Object... args) {

        System.out.printf(format, args);

    }



























    static double assertEquals(BigDecimal expected, DD actual, double eps, Supplier<String> msg) {
        return assertEquals(expected, actual, eps, null, msg);
    }




























    static double assertEquals(BigDecimal expected, DD actual, double eps,
            DoubleConsumer error, Supplier<String> msg) {

        final BigDecimal delta = new BigDecimal(actual.hi())
            .add(new BigDecimal(actual.lo()))
            .subtract(expected);
        boolean equal;
        if (expected.compareTo(BigDecimal.ZERO) == 0) {


            equal = actual.doubleValue() == 0;


            if (eps < 0) {
                if (!equal || reportAllDeviations) {
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

        if (error != null) {
            error.accept(rel);
        }


        if (eps < 0) {
            if (!equal || reportAllDeviations) {
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















    static double assertScaledEquals(BigDecimal expected, DD actual, long exp, double eps, Supplier<String> msg) {
        Assertions.assertEquals(exp, (int) exp, () -> prefix(msg) + "Cannot scale the result");
        BigDecimal e;



        if (exp < 0) {
            e = expected.multiply(TWO.pow((int) -exp), MC_SCALING);
        } else {
            e = expected.multiply(HALF.pow((int) exp), MC_SCALING);
        }
        return TestUtils.assertEquals(e, actual, eps, () -> prefix(msg) + "scale=2^" + exp);
    }






















    static long assertEquals(double expected, double actual, long maxUlps, Supplier<String> msg) {
        return assertEquals(expected, actual, maxUlps, null, msg);
    }






















    private static long assertEquals(double expected, double actual, long maxUlps,
            LongConsumer error, Supplier<String> msg) {
        final long e = Double.doubleToLongBits(expected);
        final long a = Double.doubleToLongBits(actual);





        int sign;
        long delta;
        boolean equal;
        if (e == a) {

            equal = true;
            sign = 0;
            delta = 0;
        } else if ((a ^ e) < 0L) {


            long d1;
            long d2;
            if (a < e) {
                sign = -1;
                d1 = e - POSITIVE_ZERO_DOUBLE_BITS;
                d2 = a - NEGATIVE_ZERO_DOUBLE_BITS;
            } else {
                sign = 1;
                d1 = a - POSITIVE_ZERO_DOUBLE_BITS;
                d2 = e - NEGATIVE_ZERO_DOUBLE_BITS;
            }

            delta = d1 + d2;
            if (delta < 0) {

                equal = false;
            } else {

                equal = delta <= ((maxUlps < 0) ? (-maxUlps - 1) : maxUlps);
            }
        } else {
            if (a < e) {
                sign = -1;
                delta = e - a;
            } else {
                sign = 1;
                delta = a - e;
            }


            sign = a < 0 ? -sign : sign;


            equal = delta <= ((maxUlps < 0) ? (-maxUlps - 1) : maxUlps);
        }

        Assertions.assertEquals(sign, Double.compare(actual, expected));


        if (maxUlps < 0) {
            if (!equal || reportAllDeviations) {
                printf("%sexpected <%s> != actual <%s> (ulps=%c%s)%n",
                    prefix(msg), expected, actual, sign < 0 ? '-' : ' ', Long.toUnsignedString(delta));
            }
        } else if (!equal) {
            Assertions.fail(String.format("%sexpected <%s> != actual <%s> (ulps=%c%s)",
                prefix(msg), expected, actual, sign < 0 ? '-' : ' ', Long.toUnsignedString(delta)));
        }


        delta = delta < 0 ? Long.MAX_VALUE : delta;
        delta *= sign;
        if (error != null) {
            error.accept(delta);
        }
        return delta;
    }







    static String prefix(Supplier<String> msg) {
        return msg == null ? "" : msg.get() + ": ";
    }
}
