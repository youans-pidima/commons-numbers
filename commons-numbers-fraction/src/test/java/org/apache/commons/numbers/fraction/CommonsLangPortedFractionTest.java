















package org.apache.commons.numbers.fraction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;







class CommonsLangPortedFractionTest {

    private static final int SKIP = 500;


    @Test
    void testConstants() {
        assertEquals(0, Fraction.ZERO.getNumerator());
        assertEquals(1, Fraction.ZERO.getDenominator());

        assertEquals(1, Fraction.ONE.getNumerator());
        assertEquals(1, Fraction.ONE.getDenominator());




































    }

    @Test
    void testFactory_int_int() {
        Fraction f = null;


        f = Fraction.of(0, 1);
        assertEquals(0, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f = Fraction.of(0, 2);
        assertEquals(0, f.getNumerator());
        assertEquals(1, f.getDenominator());


        f = Fraction.of(1, 1);
        assertEquals(1, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f = Fraction.of(2, 1);
        assertEquals(2, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f = Fraction.of(23, 345);
        assertEquals(1, f.getNumerator());
        assertEquals(15, f.getDenominator());


        f = Fraction.of(22, 7);
        assertEquals(22, f.getNumerator());
        assertEquals(7, f.getDenominator());


        f = Fraction.of(-6, 10);
        assertEquals(-3, f.getNumerator());
        assertEquals(5, f.getDenominator());

        f = Fraction.of(6, -10);
        assertEquals(3, f.getNumerator());
        assertEquals(-5, f.getDenominator());

        f = Fraction.of(-6, -10);
        assertEquals(-3, f.getNumerator());
        assertEquals(-5, f.getDenominator());


        assertThrows(ArithmeticException.class, () -> Fraction.of(1, 0));
        assertThrows(ArithmeticException.class, () -> Fraction.of(2, 0));
        assertThrows(ArithmeticException.class, () -> Fraction.of(-3, 0));





        f = Fraction.of(4, Integer.MIN_VALUE);
        assertEquals(-1, f.signum());
        assertEquals(1, f.getNumerator());
        assertEquals(Integer.MIN_VALUE / 4, f.getDenominator());

        f = Fraction.of(1, Integer.MIN_VALUE);
        assertEquals(-1, f.signum());
        assertEquals(1, f.getNumerator());
        assertEquals(Integer.MIN_VALUE, f.getDenominator());
    }



























































    @Test
    void testReducedFactory_int_int() {
        Fraction f = null;


        f = Fraction.of(0, 1);
        assertEquals(0, f.getNumerator());
        assertEquals(1, f.getDenominator());


        f = Fraction.of(1, 1);
        assertEquals(1, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f = Fraction.of(2, 1);
        assertEquals(2, f.getNumerator());
        assertEquals(1, f.getDenominator());


        f = Fraction.of(22, 7);
        assertEquals(22, f.getNumerator());
        assertEquals(7, f.getDenominator());


        f = Fraction.of(-6, 10);
        assertEquals(-3, f.getNumerator());
        assertEquals(5, f.getDenominator());

        f = Fraction.of(6, -10);
        assertEquals(3, f.getNumerator());
        assertEquals(-5, f.getDenominator());

        f = Fraction.of(-6, -10);
        assertEquals(-3, f.getNumerator());
        assertEquals(-5, f.getDenominator());


        assertThrows(ArithmeticException.class, () -> Fraction.of(1, 0));
        assertThrows(ArithmeticException.class, () -> Fraction.of(2, 0));
        assertThrows(ArithmeticException.class, () -> Fraction.of(-3, 0));


        f = Fraction.of(0, 2);
        assertEquals(0, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f = Fraction.of(2, 2);
        assertEquals(1, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f = Fraction.of(2, 4);
        assertEquals(1, f.getNumerator());
        assertEquals(2, f.getDenominator());

        f = Fraction.of(15, 10);
        assertEquals(3, f.getNumerator());
        assertEquals(2, f.getDenominator());

        f = Fraction.of(121, 22);
        assertEquals(11, f.getNumerator());
        assertEquals(2, f.getDenominator());



        f = Fraction.of(-2, Integer.MIN_VALUE);
        assertEquals(-1, f.getNumerator());
        assertEquals(Integer.MIN_VALUE / 2, f.getDenominator());




        f = Fraction.of(-7, Integer.MIN_VALUE);
        assertEquals(1, f.signum());
        assertEquals(-7, f.getNumerator());
        assertEquals(Integer.MIN_VALUE, f.getDenominator());


        f = Fraction.of(Integer.MIN_VALUE, 2);
        assertEquals(Integer.MIN_VALUE / 2, f.getNumerator());
        assertEquals(1, f.getDenominator());
    }

    @Test
    void testFactory_double() {
        assertThrows(IllegalArgumentException.class, () -> Fraction.from(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> Fraction.from(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> Fraction.from(Double.NEGATIVE_INFINITY));
        Fraction.from((double) Integer.MAX_VALUE + 1);


        Fraction f = Fraction.from(0.0d);
        assertEquals(0, f.getNumerator());
        assertEquals(1, f.getDenominator());


        f = Fraction.from(1.0d);
        assertEquals(1, f.getNumerator());
        assertEquals(1, f.getDenominator());


        f = Fraction.from(0.5d);
        assertEquals(1, f.getNumerator());
        assertEquals(2, f.getDenominator());


        f = Fraction.from(-0.875d);
        assertEquals(-7, f.getNumerator());
        assertEquals(8, f.getDenominator());


        f = Fraction.from(1.25d);
        assertEquals(5, f.getNumerator());
        assertEquals(4, f.getDenominator());


        f = Fraction.from(0.66666d);
        assertEquals(2, f.getNumerator());
        assertEquals(3, f.getDenominator());


        f = Fraction.from(1.0d / 10001d);
        assertEquals(1, f.getNumerator());
        assertEquals(10001, f.getDenominator());


        Fraction f2 = null;
        for (int i = 1; i <= 100; i++) {
            for (int j = 1; j <= i; j++) {
                f = Fraction.from((double) j / (double) i);

                f2 = Fraction.of(j, i);
                assertEquals(f2.getNumerator(), f.getNumerator());
                assertEquals(f2.getDenominator(), f.getDenominator());
            }
        }

        for (int i = 1001; i <= 10000; i += SKIP) {
            for (int j = 1; j <= i; j++) {
                f = Fraction.from((double) j / (double) i, 1e-8, 100);
                f2 = Fraction.of(j, i);
                assertEquals(f2.getNumerator(), f.getNumerator());
                assertEquals(f2.getDenominator(), f.getDenominator());
            }
        }
    }

























































































































































































































    @Test
    void testNegate() {
        Fraction f = null;

        f = Fraction.of(50, 75);
        f = f.negate();
        assertEquals(-2, f.getNumerator());
        assertEquals(3, f.getDenominator());

        f = Fraction.of(-50, 75);
        f = f.negate();
        assertEquals(2, f.getNumerator());
        assertEquals(3, f.getDenominator());


        f = Fraction.of(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
        f = f.negate();
        assertEquals(Integer.MIN_VALUE + 2, f.getNumerator());
        assertEquals(Integer.MAX_VALUE, f.getDenominator());




        f = Fraction.of(Integer.MIN_VALUE, 1).negate();
        assertEquals(1, f.signum());
        assertEquals(Integer.MIN_VALUE, f.getNumerator());
        assertEquals(-1, f.getDenominator());
    }

    @Test
    void testAbs() {
        Fraction f = null;

        f = Fraction.of(50, 75);
        f = f.abs();
        assertEquals(2, f.getNumerator());
        assertEquals(3, f.getDenominator());

        f = Fraction.of(-50, 75);
        f = f.abs();
        assertEquals(2, f.getNumerator());
        assertEquals(3, f.getDenominator());

        f = Fraction.of(Integer.MAX_VALUE, 1);
        f = f.abs();
        assertEquals(Integer.MAX_VALUE, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f = Fraction.of(Integer.MAX_VALUE, -1);
        f = f.abs();
        assertEquals(-Integer.MAX_VALUE, f.getNumerator());
        assertEquals(-1, f.getDenominator());




        f = Fraction.of(Integer.MIN_VALUE, 1).abs();
        assertEquals(1, f.signum());
        assertEquals(Integer.MIN_VALUE, f.getNumerator());
        assertEquals(-1, f.getDenominator());
    }

    @Test
    void testPow() {
        Fraction f = null;

        f = Fraction.of(3, 5);
        assertEquals(Fraction.ONE, f.pow(0));

        f = Fraction.of(3, 5);
        assertEquals(f, f.pow(1));
        assertEquals(f, f.pow(1));

        f = Fraction.of(3, 5);
        f = f.pow(2);
        assertEquals(9, f.getNumerator());
        assertEquals(25, f.getDenominator());

        f = Fraction.of(3, 5);
        f = f.pow(3);
        assertEquals(27, f.getNumerator());
        assertEquals(125, f.getDenominator());

        f = Fraction.of(3, 5);
        f = f.pow(-1);
        assertEquals(5, f.getNumerator());
        assertEquals(3, f.getDenominator());

        f = Fraction.of(3, 5);
        f = f.pow(-2);
        assertEquals(25, f.getNumerator());
        assertEquals(9, f.getDenominator());


        f = Fraction.of(6, 10);
        assertEquals(Fraction.ONE, f.pow(0));

        f = Fraction.of(6, 10);
        assertEquals(f, f.pow(1));
        assertEquals(f.pow(1), Fraction.of(3, 5));

        f = Fraction.of(6, 10);
        f = f.pow(2);
        assertEquals(9, f.getNumerator());
        assertEquals(25, f.getDenominator());

        f = Fraction.of(6, 10);
        f = f.pow(3);
        assertEquals(27, f.getNumerator());
        assertEquals(125, f.getDenominator());

        f = Fraction.of(6, 10);
        f = f.pow(-1);
        assertEquals(5, f.getNumerator());
        assertEquals(3, f.getDenominator());

        f = Fraction.of(6, 10);
        f = f.pow(-2);
        assertEquals(25, f.getNumerator());
        assertEquals(9, f.getDenominator());


        f = Fraction.of(0, 1231);
        f = f.pow(1);
        assertEquals(0, f.compareTo(Fraction.ZERO));
        assertEquals(0, f.getNumerator());
        assertEquals(1, f.getDenominator());
        f = f.pow(2);
        assertEquals(0, f.compareTo(Fraction.ZERO));
        assertEquals(0, f.getNumerator());
        assertEquals(1, f.getDenominator());


        final Fraction fr = f;
        assertThrows(ArithmeticException.class, () -> fr.pow(-1));
        assertThrows(ArithmeticException.class, () -> fr.pow(Integer.MIN_VALUE));


        f = Fraction.of(1, 1);
        f = f.pow(0);
        assertEquals(Fraction.ONE, f);
        f = f.pow(1);
        assertEquals(Fraction.ONE, f);
        f = f.pow(-1);
        assertEquals(Fraction.ONE, f);
        f = f.pow(Integer.MAX_VALUE);
        assertEquals(Fraction.ONE, f);
        f = f.pow(Integer.MIN_VALUE);
        assertEquals(Fraction.ONE, f);

        assertOperationThrows(ArithmeticException.class, Fraction.of(Integer.MAX_VALUE, 1), a -> a.pow(2));


        assertOperationThrows(ArithmeticException.class, Fraction.of(Integer.MIN_VALUE, 1), a -> a.pow(3));

        assertOperationThrows(ArithmeticException.class, Fraction.of(65536, 1), a -> a.pow(2));
    }

    @Test
    void testAdd() {
        Fraction f = null;
        Fraction f1 = null;
        Fraction f2 = null;

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(1, 5);
        f = f1.add(f2);
        assertEquals(4, f.getNumerator());
        assertEquals(5, f.getDenominator());

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(2, 5);
        f = f1.add(f2);
        assertEquals(1, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(3, 5);
        f = f1.add(f2);
        assertEquals(6, f.getNumerator());
        assertEquals(5, f.getDenominator());

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(-4, 5);
        f = f1.add(f2);
        assertEquals(-1, f.getNumerator());
        assertEquals(5, f.getDenominator());

        f1 = Fraction.of(Integer.MAX_VALUE - 1, 1);
        f2 = Fraction.ONE;
        f = f1.add(f2);
        assertEquals(Integer.MAX_VALUE, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(1, 2);
        f = f1.add(f2);
        assertEquals(11, f.getNumerator());
        assertEquals(10, f.getDenominator());

        f1 = Fraction.of(3, 8);
        f2 = Fraction.of(1, 6);
        f = f1.add(f2);
        assertEquals(13, f.getNumerator());
        assertEquals(24, f.getDenominator());

        f1 = Fraction.of(0, 5);
        f2 = Fraction.of(1, 5);
        f = f1.add(f2);
        assertSame(f2, f);
        f = f2.add(f1);
        assertSame(f2, f);

        f1 = Fraction.of(-1, 13 * 13 * 2 * 2);
        f2 = Fraction.of(-2, 13 * 17 * 2);
        final Fraction fr = f1.add(f2);
        assertEquals(13 * 13 * 17 * 2 * 2, fr.getDenominator());
        assertEquals(-17 - 2 * 13 * 2, fr.getNumerator());

        assertThrows(NullPointerException.class, () -> fr.add(null));



        f1 = Fraction.of(1, 32768 * 3);
        f2 = Fraction.of(1, 59049);
        f = f1.add(f2);
        assertEquals(52451, f.getNumerator());
        assertEquals(1934917632, f.getDenominator());

        f1 = Fraction.of(Integer.MIN_VALUE, 3);
        f2 = Fraction.of(1, 3);
        f = f1.add(f2);
        assertEquals(Integer.MIN_VALUE + 1, f.getNumerator());
        assertEquals(3, f.getDenominator());

        f1 = Fraction.of(Integer.MAX_VALUE - 1, 1);
        f2 = Fraction.ONE;
        f = f1.add(f2);
        assertEquals(Integer.MAX_VALUE, f.getNumerator());
        assertEquals(1, f.getDenominator());

        final Fraction overflower = f;
        assertThrows(ArithmeticException.class, () -> overflower.add(Fraction.ONE));


        assertOperationThrows(ArithmeticException.class, Fraction.of(Integer.MIN_VALUE, 5), Fraction.of(-1, 5), Fraction::add);

        final Fraction maxValue = Fraction.of(-Integer.MAX_VALUE, 1);
        assertThrows(ArithmeticException.class, () -> maxValue.add(maxValue));

        final Fraction negativeMaxValue = Fraction.of(-Integer.MAX_VALUE, 1);
        assertThrows(ArithmeticException.class, () -> negativeMaxValue.add(negativeMaxValue));

        final Fraction f3 = Fraction.of(3, 327680);
        final Fraction f4 = Fraction.of(2, 59049);
        assertThrows(ArithmeticException.class, () -> f3.add(f4));
    }

    @Test
    void testSubtract() {
        Fraction f = null;
        Fraction f1 = null;
        Fraction f2 = null;

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(1, 5);
        f = f1.subtract(f2);
        assertEquals(2, f.getNumerator());
        assertEquals(5, f.getDenominator());

        f1 = Fraction.of(7, 5);
        f2 = Fraction.of(2, 5);
        f = f1.subtract(f2);
        assertEquals(1, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(3, 5);
        f = f1.subtract(f2);
        assertEquals(0, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(-4, 5);
        f = f1.subtract(f2);
        assertEquals(7, f.getNumerator());
        assertEquals(5, f.getDenominator());

        f1 = Fraction.of(0, 5);
        f2 = Fraction.of(4, 5);
        f = f1.subtract(f2);
        assertEquals(-4, f.getNumerator());
        assertEquals(5, f.getDenominator());

        f1 = Fraction.of(0, 5);
        f2 = Fraction.of(-4, 5);
        f = f1.subtract(f2);
        assertEquals(4, f.getNumerator());
        assertEquals(5, f.getDenominator());

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(1, 2);
        f = f1.subtract(f2);
        assertEquals(1, f.getNumerator());
        assertEquals(10, f.getDenominator());

        f1 = Fraction.of(0, 5);
        f2 = Fraction.of(1, 5);
        f = f2.subtract(f1);
        assertSame(f2, f);

        final Fraction fr = f;
        assertThrows(NullPointerException.class, () -> fr.subtract(null));



        f1 = Fraction.of(1, 32768 * 3);
        f2 = Fraction.of(1, 59049);
        f = f1.subtract(f2);
        assertEquals(-13085, f.getNumerator());
        assertEquals(1934917632, f.getDenominator());

        f1 = Fraction.of(Integer.MIN_VALUE, 3);
        f2 = Fraction.of(1, 3).negate();
        f = f1.subtract(f2);
        assertEquals(Integer.MIN_VALUE + 1, f.getNumerator());
        assertEquals(3, f.getDenominator());

        f1 = Fraction.of(Integer.MAX_VALUE, 1);
        f2 = Fraction.ONE;
        f = f1.subtract(f2);
        assertEquals(Integer.MAX_VALUE - 1, f.getNumerator());
        assertEquals(1, f.getDenominator());


        assertOperationThrows(ArithmeticException.class, Fraction.of(1, Integer.MAX_VALUE), Fraction.of(1, Integer.MAX_VALUE - 1), Fraction::subtract);


        assertOperationThrows(ArithmeticException.class, Fraction.of(Integer.MIN_VALUE, 5), Fraction.of(1, 5), Fraction::subtract);

        assertOperationThrows(ArithmeticException.class, Fraction.of(Integer.MIN_VALUE, 1), Fraction.ONE, Fraction::subtract);

        assertOperationThrows(ArithmeticException.class, Fraction.of(Integer.MAX_VALUE, 1), Fraction.ONE.negate(), Fraction::subtract);


        assertOperationThrows(ArithmeticException.class, Fraction.of(3, 327680), Fraction.of(2, 59049), Fraction::subtract);
    }

    @Test
    void testMultiply() {
        Fraction f = null;
        Fraction f1 = null;
        Fraction f2 = null;

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(2, 5);
        f = f1.multiply(f2);
        assertEquals(6, f.getNumerator());
        assertEquals(25, f.getDenominator());

        f1 = Fraction.of(6, 10);
        f2 = Fraction.of(6, 10);
        f = f1.multiply(f2);
        assertEquals(9, f.getNumerator());
        assertEquals(25, f.getDenominator());
        f = f.multiply(f2);
        assertEquals(27, f.getNumerator());
        assertEquals(125, f.getDenominator());

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(-2, 5);
        f = f1.multiply(f2);
        assertEquals(-6, f.getNumerator());
        assertEquals(25, f.getDenominator());

        f1 = Fraction.of(-3, 5);
        f2 = Fraction.of(-2, 5);
        f = f1.multiply(f2);
        assertEquals(6, f.getNumerator());
        assertEquals(25, f.getDenominator());

        f1 = Fraction.of(0, 5);
        f2 = Fraction.of(2, 7);
        f = f1.multiply(f2);
        assertSame(Fraction.ZERO, f);

        f1 = Fraction.of(2, 7);
        f2 = Fraction.ONE;
        f = f1.multiply(f2);
        assertEquals(2, f.getNumerator());
        assertEquals(7, f.getDenominator());

        f1 = Fraction.of(Integer.MAX_VALUE, 1);
        f2 = Fraction.of(Integer.MIN_VALUE, Integer.MAX_VALUE);
        f = f1.multiply(f2);
        assertEquals(Integer.MIN_VALUE, f.getNumerator());
        assertEquals(1, f.getDenominator());

        final Fraction fr = f;
        assertThrows(NullPointerException.class, () -> fr.multiply(null));

        final Fraction fr1 = Fraction.of(1, Integer.MAX_VALUE);
        assertThrows(ArithmeticException.class, () -> fr1.multiply(fr1));

        final Fraction fr2 = Fraction.of(1, -Integer.MAX_VALUE);
        assertThrows(ArithmeticException.class, () -> fr2.multiply(fr2));
    }

    @Test
    void testDivide() {
        Fraction f = null;
        Fraction f1 = null;
        Fraction f2 = null;

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(2, 5);
        f = f1.divide(f2);
        assertEquals(3, f.getNumerator());
        assertEquals(2, f.getDenominator());

        assertOperationThrows(ArithmeticException.class, Fraction.of(3, 5), Fraction.ZERO, Fraction::divide);

        f1 = Fraction.of(0, 5);
        f2 = Fraction.of(2, 7);
        f = f1.divide(f2);
        assertSame(Fraction.ZERO, f);

        f1 = Fraction.of(2, 7);
        f2 = Fraction.ONE;
        f = f1.divide(f2);
        assertEquals(2, f.getNumerator());
        assertEquals(7, f.getDenominator());

        f1 = Fraction.of(1, Integer.MAX_VALUE);
        f = f1.divide(f1);
        assertEquals(1, f.getNumerator());
        assertEquals(1, f.getDenominator());

        f1 = Fraction.of(Integer.MIN_VALUE, Integer.MAX_VALUE);
        f2 = Fraction.of(1, Integer.MAX_VALUE);
        final Fraction fr = f1.divide(f2);
        assertEquals(Integer.MIN_VALUE, fr.getNumerator());
        assertEquals(1, fr.getDenominator());

        assertThrows(NullPointerException.class, () -> fr.divide(null));

        final Fraction smallest = Fraction.of(1, Integer.MAX_VALUE);
        final Fraction smallestReciprocal = smallest.reciprocal();
        assertThrows(ArithmeticException.class, () -> smallest.divide(smallestReciprocal));

        final Fraction negative = Fraction.of(1, -Integer.MAX_VALUE);
        final Fraction negativeReciprocal = negative.reciprocal();
        assertThrows(ArithmeticException.class, () -> negative.divide(negativeReciprocal));
    }

    @Test
    void testEquals() {
        Fraction f1 = null;
        Fraction f2 = null;

        f1 = Fraction.of(3, 5);
        assertNotEquals(f1, null);
        assertNotEquals(f1, new Object());
        assertNotEquals(f1, Integer.valueOf(6));

        f1 = Fraction.of(3, 5);
        f2 = Fraction.of(2, 5);
        assertNotEquals(f1, f2);
        assertEquals(f1, f1);
        assertEquals(f2, f2);

        f2 = Fraction.of(3, 5);
        assertEquals(f1, f2);

        f2 = Fraction.of(6, 10);
        assertEquals(f1, f2);
    }

    @Test
    void testHashCode() {
        final Fraction f1 = Fraction.of(3, 5);
        Fraction f2 = Fraction.of(3, 5);

        assertEquals(f1.hashCode(), f2.hashCode());

        f2 = Fraction.of(2, 5);
        assertNotEquals(f1.hashCode(), f2.hashCode());

        f2 = Fraction.of(6, 10);
        assertEquals(f1.hashCode(), f2.hashCode());
    }

    @Test
    void testCompareTo() {
        Fraction f1 = null;
        Fraction f2 = null;

        f1 = Fraction.of(3, 5);
        assertEquals(0, f1.compareTo(f1));

        final Fraction fr = f1;
        assertThrows(NullPointerException.class, () -> fr.compareTo(null));

        f2 = Fraction.of(2, 5);
        assertTrue(f1.compareTo(f2) > 0);
        assertEquals(0, f2.compareTo(f2));

        f2 = Fraction.of(4, 5);
        assertTrue(f1.compareTo(f2) < 0);
        assertEquals(0, f2.compareTo(f2));

        f2 = Fraction.of(3, 5);
        assertEquals(0, f1.compareTo(f2));
        assertEquals(0, f2.compareTo(f2));

        f2 = Fraction.of(6, 10);
        assertEquals(0, f1.compareTo(f2));
        assertEquals(0, f2.compareTo(f2));








    }

    @Test
    void testToString() {
        Fraction f = null;

        f = Fraction.of(3, 5);
        final String str = f.toString();
        assertEquals("3 / 5", str);
        assertEquals(str, f.toString());

        f = Fraction.of(7, 5);
        assertEquals("7 / 5", f.toString());

        f = Fraction.of(4, 2);
        assertEquals("2", f.toString());

        f = Fraction.of(0, 2);
        assertEquals("0", f.toString());

        f = Fraction.of(2, 2);
        assertEquals("1", f.toString());

        f = Fraction.of(Integer.MIN_VALUE);
        assertEquals("-2147483648", f.toString());

        f = Fraction.of(-1).add(Fraction.of(-1, Integer.MAX_VALUE));
        assertEquals("-2147483648 / 2147483647", f.toString());
    }




















































    private static <T extends Throwable> T assertOperationThrows(Class<T> expectedType,
            Fraction f, UnaryOperator<Fraction> operation) {
        return assertThrows(expectedType, () -> operation.apply(f));
    }













    private static <T extends Throwable> T assertOperationThrows(Class<T> expectedType,
            Fraction f1, Fraction f2, BiFunction<Fraction, Fraction, Fraction> operation) {
        return assertThrows(expectedType, () -> operation.apply(f1, f2));
    }
}
