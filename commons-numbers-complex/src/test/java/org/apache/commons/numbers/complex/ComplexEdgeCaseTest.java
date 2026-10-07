
















package org.apache.commons.numbers.complex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;













class ComplexEdgeCaseTest {
    private static final double inf = Double.POSITIVE_INFINITY;
    private static final double nan = Double.NaN;













    private static void assertComplex(double a, double b,
            String name, UnaryOperator<Complex> operation,
            double x, double y) {
        assertComplex(a, b, name, operation, x, y, 1);
    }
















    private static void assertComplex(double a, double b,
            String name, UnaryOperator<Complex> operation,
            double x, double y, long maxUlps) {
        final Complex c = Complex.ofCartesian(a, b);
        final Complex e = Complex.ofCartesian(x, y);
        CReferenceTest.assertComplex(c, name, operation, e, maxUlps);
    }
















    private static void assertComplex(double a, double b, double c, double d,
            String name, BiFunction<Complex, Complex, Complex> operation,
            double x, double y) {
        assertComplex(a, b, c, d, name, operation, x, y, 1);
    }


















    private static void assertComplex(double a, double b, double c, double d,
            String name, BiFunction<Complex, Complex, Complex> operation,
            double x, double y, long maxUlps) {
        final Complex c1 = Complex.ofCartesian(a, b);
        final Complex c2 = Complex.ofCartesian(c, d);
        final Complex e = Complex.ofCartesian(x, y);
        CReferenceTest.assertComplex(c1, c2, name, operation, e, maxUlps);
    }

    @Test
    void testAcos() {

        final String name = "acos";
        final UnaryOperator<Complex> operation = Complex::acos;






        final double huge = Math.sqrt(Double.MAX_VALUE) * 2;
        final double big = Math.sqrt(Double.MAX_VALUE) / 8;
        final double medium = 100;
        final double small = Math.sqrt(Double.MIN_NORMAL) * 4;
        assertComplex(huge, big, name, operation, 0.06241880999595735, -356.27960012801969);
        assertComplex(huge, medium, name, operation, 3.7291703656001039e-153, -356.27765080781188);
        assertComplex(huge, small, name, operation, 2.2250738585072019e-308, -356.27765080781188);
        assertComplex(big, big, name, operation, 0.78539816339744828, -353.85163567585209);
        assertComplex(big, medium, name, operation, 5.9666725849601662e-152, -353.50506208557209);
        assertComplex(big, small, name, operation, 3.560118173611523e-307, -353.50506208557209);
        assertComplex(medium, big, name, operation, 1.5707963267948966, -353.50506208557209);
        assertComplex(medium, medium, name, operation, 0.78541066339744181, -5.6448909570623842);
        assertComplex(medium, small, name, operation, 5.9669709409662999e-156, -5.298292365610485);
        assertComplex(small, big, name, operation, 1.5707963267948966, -353.50506208557209);
        assertComplex(small, medium, name, operation, 1.5707963267948966, -5.2983423656105888);
        assertComplex(small, small, name, operation, 1.5707963267948966, -5.9666725849601654e-154);


        assertComplex(1, small, name, operation, 2.4426773395109241e-77, -2.4426773395109241e-77);

        assertComplex(1.00000002785941, 5.72464869028403e-200, name, operation, 2.4252018043912224e-196, -0.00023604834149293664);
    }



    @Test
    void testAsin() {

        final String name = "asin";
        final UnaryOperator<Complex> operation = Complex::asin;



        final double huge = Math.sqrt(Double.MAX_VALUE) * 2;
        final double big = Math.sqrt(Double.MAX_VALUE) / 8;
        final double medium = 100;
        final double small = Math.sqrt(Double.MIN_NORMAL) * 4;
        assertComplex(huge, big, name, operation, 1.5083775167989393, 356.27960012801969);
        assertComplex(huge, medium, name, operation, 1.5707963267948966, 356.27765080781188);
        assertComplex(huge, small, name, operation, 1.5707963267948966, 356.27765080781188);
        assertComplex(big, big, name, operation, 0.78539816339744828, 353.85163567585209);
        assertComplex(big, medium, name, operation, 1.5707963267948966, 353.50506208557209);
        assertComplex(big, small, name, operation, 1.5707963267948966, 353.50506208557209);
        assertComplex(medium, big, name, operation, 5.9666725849601662e-152, 353.50506208557209);
        assertComplex(medium, medium, name, operation, 0.78538566339745486, 5.6448909570623842);
        assertComplex(medium, small, name, operation, 1.5707963267948966, 5.298292365610485);
        assertComplex(small, big, name, operation, 3.560118173611523e-307, 353.50506208557209);
        assertComplex(small, medium, name, operation, 5.9663742737040751e-156, 5.2983423656105888);
        assertComplex(small, small, name, operation, 5.9666725849601654e-154, 5.9666725849601654e-154);


        assertComplex(1, small, name, operation, 1.5707963267948966, 2.4426773395109241e-77);

        assertComplex(1.00000002785941, 5.72464869028403e-200, name, operation, 1.5707963267948966, 0.00023604834149293664);
    }



    @Test
    void testAtanh() {


        final String name = "atanh";
        final UnaryOperator<Complex> operation = Complex::atanh;






        final double big = Math.sqrt(Double.MAX_VALUE) / 2;
        final double medium = 100;
        final double small = Math.sqrt(Double.MIN_NORMAL) * 2;
        assertComplex(big, big, name, operation, 7.4583407312002067e-155, 1.5707963267948966);
        assertComplex(big, medium, name, operation, 1.4916681462400417e-154, 1.5707963267948966);
        assertComplex(big, small, name, operation, 1.4916681462400417e-154, 1.5707963267948966);
        assertComplex(medium, big, name, operation, 2.225073858507202e-306, 1.5707963267948966);
        assertComplex(medium, medium, name, operation, 0.0049999166641667555, 1.5657962434640633);
        assertComplex(medium, small, name, operation, 0.010000333353334761, 1.5707963267948966);
        assertComplex(small, big, name, operation, 0, 1.5707963267948966);
        assertComplex(small, medium, name, operation, 2.9830379886812147e-158, 1.5607966601082315);
        assertComplex(small, small, name, operation, 2.9833362924800827e-154, 2.9833362924800827e-154);

        assertComplex(inf, big, name, operation, 0, 1.5707963267948966);
        assertComplex(big, inf, name, operation, 0, 1.5707963267948966);
    }

    @Test
    void testCosh() {


        final String name = "cosh";
        final UnaryOperator<Complex> operation = Complex::cosh;



        final double big = Double.MAX_VALUE;
        final double medium = 2;
        final double small = Double.MIN_NORMAL;
        assertComplex(big, big, name, operation, -inf, inf);
        assertComplex(big, medium, name, operation, -inf, inf);
        assertComplex(big, small, name, operation, inf, inf);
        assertComplex(medium, big, name, operation, -3.7621493762972804, 0.017996317370418576);
        assertComplex(medium, medium, name, operation, -1.5656258353157435, 3.297894836311237);
        assertComplex(medium, small, name, operation, 3.7621956910836314, 8.0700322819551687e-308);
        assertComplex(small, big, name, operation, -0.99998768942655991, 1.1040715888508271e-310);
        assertComplex(small, medium, name, operation, -0.41614683654714241, 2.0232539340376892e-308);
        assertComplex(small, small, name, operation, 1, 0);










        final double tiny = Double.MIN_VALUE;
        final double x = 709.783;
        Assertions.assertEquals(inf, Math.exp(x));

        assertComplex(x, 0, name, operation, 8.9910466927705402e+307, 0.0);
        assertComplex(-x, 0, name, operation, 8.9910466927705402e+307, -0.0);



        assertComplex(x, small, name, operation, 8.9910466927705402e+307, 2.0005742956701358);
        assertComplex(-x, small, name, operation, 8.9910466927705402e+307, -2.0005742956701358);
        assertComplex(x, tiny, name, operation, 8.9910466927705402e+307, 4.4421672910524807e-16);
        assertComplex(-x, tiny, name, operation, 8.9910466927705402e+307, -4.4421672910524807e-16);

        assertComplex(2 * x, tiny, name, operation, inf, 7.9879467061901743e+292);
        assertComplex(-2 * x, tiny, name, operation, inf, -7.9879467061901743e+292);


        assertComplex(3 * x, tiny, name, operation, inf, inf);
        assertComplex(-3 * x, tiny, name, operation, inf, -inf);



        final double pi2 = Math.PI / 2;
        assertComplex(x, pi2, name, operation, 5.5054282766429199e+291, 8.9910466927705402e+307);
        assertComplex(-x, pi2, name, operation, 5.5054282766429199e+291, -8.9910466927705402e+307);
        assertComplex(2 * x, pi2, name, operation, inf, inf);
        assertComplex(-2 * x, pi2, name, operation, inf, -inf);


        assertComplex(3 * x, pi2, name, operation, inf, inf);
        assertComplex(-3 * x, pi2, name, operation, inf, -inf);
    }

    @Test
    void testSinh() {


        final String name = "sinh";
        final UnaryOperator<Complex> operation = Complex::sinh;



        final double big = Double.MAX_VALUE;
        final double medium = 2;
        final double small = Double.MIN_NORMAL;
        assertComplex(big, big, name, operation, -inf, inf);
        assertComplex(big, medium, name, operation, -inf, inf);
        assertComplex(big, small, name, operation, inf, inf);
        assertComplex(medium, big, name, operation, -3.6268157591156114, 0.018667844927220067);
        assertComplex(medium, medium, name, operation, -1.5093064853236158, 3.4209548611170133);
        assertComplex(medium, small, name, operation, 3.626860407847019, 8.3711632828186228e-308);
        assertComplex(small, big, name, operation, -2.2250464665720564e-308, 0.004961954789184062);
        assertComplex(small, medium, name, operation, -9.2595744730151568e-309, 0.90929742682568171);
        assertComplex(small, small, name, operation, 2.2250738585072014e-308, 2.2250738585072014e-308);








        final double tiny = Double.MIN_VALUE;
        final double x = 709.783;
        Assertions.assertEquals(inf, Math.exp(x));

        assertComplex(x, 0, name, operation, 8.9910466927705402e+307, 0.0);
        assertComplex(-x, 0, name, operation, -8.9910466927705402e+307, 0.0);



        assertComplex(x, small, name, operation, 8.9910466927705402e+307, 2.0005742956701358);
        assertComplex(-x, small, name, operation, -8.9910466927705402e+307, 2.0005742956701358);
        assertComplex(x, tiny, name, operation, 8.9910466927705402e+307, 4.4421672910524807e-16);
        assertComplex(-x, tiny, name, operation, -8.9910466927705402e+307, 4.4421672910524807e-16);

        assertComplex(2 * x, tiny, name, operation, inf, 7.9879467061901743e+292);
        assertComplex(-2 * x, tiny, name, operation, -inf, 7.9879467061901743e+292);


        assertComplex(3 * x, tiny, name, operation, inf, inf);
        assertComplex(-3 * x, tiny, name, operation, -inf, inf);



        final double pi2 = Math.PI / 2;
        assertComplex(x, pi2, name, operation, 5.5054282766429199e+291, 8.9910466927705402e+307);
        assertComplex(-x, pi2, name, operation, -5.5054282766429199e+291, 8.9910466927705402e+307);
        assertComplex(2 * x, pi2, name, operation, inf, inf);
        assertComplex(-2 * x, pi2, name, operation, -inf, inf);


        assertComplex(3 * x, pi2, name, operation, inf, inf);
        assertComplex(-3 * x, pi2, name, operation, -inf, inf);
    }

    @Test
    void testTanh() {


        final String name = "tanh";
        final UnaryOperator<Complex> operation = Complex::tanh;




        assertComplex(1, Double.MAX_VALUE, name, operation, 0.76160203106265523, -0.0020838895895863505);




        assertComplex(1, Double.MIN_NORMAL, name, operation, 0.76159415595576485, 9.344739287691424e-309);
        assertComplex(1, Double.MIN_VALUE, name, operation, 0.76159415595576485, 0);






        assertComplex(Double.MAX_VALUE, 1, name, operation, 1, 0.0);
        assertComplex(Double.MAX_VALUE, -1, name, operation, 1, -0.0);
        assertComplex(-Double.MAX_VALUE, 1, name, operation, -1, 0.0);
        assertComplex(-Double.MAX_VALUE, -1, name, operation, -1, -0.0);




        assertComplex(Double.MIN_NORMAL, 1, name, operation, 7.6220323800193346e-308, 1.5574077246549021);
        assertComplex(Double.MIN_VALUE, 1, name, operation, 1.4821969375237396e-323, 1.5574077246549021);






        double x = 709.783 / 2;
        double y = Math.PI / 4;
        Assertions.assertEquals(1.0, Math.sin(2 * y), 1e-16);
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Math.exp(2 * x));

        assertComplex(x, y, name, operation, 1, 1.1122175583895849e-308);
    }

    @Test
    void testExp() {
        final String name = "exp";
        final UnaryOperator<Complex> operation = Complex::exp;




        assertComplex(1000, 0, name, operation, inf, 0.0);
        assertComplex(1000, 1, name, operation, inf, inf);
        assertComplex(1000, 2, name, operation, -inf, inf);
        assertComplex(1000, 3, name, operation, -inf, inf);
        assertComplex(1000, 4, name, operation, -inf, -inf);


        assertComplex(-1000, 0, name, operation, 0.0, 0.0);
        assertComplex(-1000, 1, name, operation, 0.0, 0.0);
        assertComplex(-1000, 2, name, operation, -0.0, 0.0);
        assertComplex(-1000, 3, name, operation, -0.0, 0.0);
        assertComplex(-1000, 4, name, operation, -0.0, -0.0);
    }

    @Test
    void testLog() {
        final String name = "log";
        final UnaryOperator<Complex> operation = Complex::log;







        assertComplex(-Double.MAX_VALUE, Double.MAX_VALUE, name, operation, 7.101292864836639e2, Math.PI * 3 / 4);
        assertComplex(Double.MAX_VALUE, Double.MAX_VALUE, name, operation, 7.101292864836639e2, Math.PI / 4);
        assertComplex(-Double.MAX_VALUE, Double.MAX_VALUE / 4, name, operation, 7.098130252042921e2, 2.896613990462929);
        assertComplex(Double.MAX_VALUE, Double.MAX_VALUE / 4, name, operation, 7.098130252042921e2, 2.449786631268641e-1, 2);


        assertComplex(-Double.MIN_NORMAL, Double.MIN_NORMAL, name, operation, -708.04984494198413, 2.3561944901923448);
        assertComplex(Double.MIN_NORMAL, Double.MIN_NORMAL, name, operation, -708.04984494198413, 0.78539816339744828);




        double expected = Math.log(Math.sqrt(2)) - Math.log(2) * 1074;
        assertComplex(-Double.MIN_VALUE, Double.MIN_VALUE, name, operation, expected, Math.atan2(1, -1));
        expected = Math.log(Math.sqrt(5)) - Math.log(2) * 1074;
        assertComplex(-Double.MIN_VALUE, 2 * Double.MIN_VALUE, name, operation, expected, Math.atan2(2, -1));










        assertLog(1.0001, Math.sqrt(1.2 - 1.0001 * 1.0001), 1);
        assertLog(1.0001, Math.sqrt(1.1 - 1.0001 * 1.0001), 1);
        assertLog(1.0001, Math.sqrt(1.02 - 1.0001 * 1.0001), 0);
        assertLog(1.0001, Math.sqrt(1.01 - 1.0001 * 1.0001), 0);





        assertLog(0.99, 0.00001, 0);
        assertLog(0.95, 0.00001, 0);
        assertLog(0.9, 0.00001, 0);
        assertLog(0.85, 0.00001, 0);
        assertLog(0.8, 0.00001, 0);
        assertLog(0.75, 0.00001, 0);

        assertLog(0.7, 0.00001, 2);




        assertLog(0.97, Math.sqrt(0.99 - 0.97 * 0.97), 0);

        assertLog(0.97, Math.sqrt(1.01 - 0.97 * 0.97), 0);



        assertLog(0.97, Math.sqrt(0.9999 - 0.97 * 0.97), 0);


        final int steps = 20;
        final double[] magnitude = {0.999, 1.0, 1.001};
        final int[] ulps = {0, 0, 1};
        for (int j = 0; j < magnitude.length; j++) {
            for (int i = 1; i <= steps; i++) {
                final double theta = i * Math.PI / (4 * steps);
                assertLog(magnitude[j] * Math.sin(theta), magnitude[j] * Math.cos(theta), ulps[j]);
            }
        }


        double theta = Math.PI / (4 * steps);
        while (theta > 0) {
            theta /= 2;
            assertLog(Math.sin(theta), Math.cos(theta), 0);
        }


        final double up1 = Math.nextUp(1.0);
        final double down1 = Math.nextDown(1.0);
        assertLog(down1, Double.MIN_NORMAL, 0);
        assertLog(down1, Double.MIN_VALUE, 0);

        assertLog(up1, Double.MIN_NORMAL, 2);
        assertLog(up1, Double.MIN_VALUE, 2);








        assertLog(0.007640392270319105, 0.9999708117770016, 0);
        assertLog(0.40158433204881533, 0.9158220483548684, 0);
        assertLog(0.13258789214774552, 0.9911712520325727, 0);
        assertLog(0.2552206803398717, 0.9668828286441191, 0);

        assertLog(0.4650816500945186, 0.8852677892848919, 0);
        assertLog(0.06548693057069123, 0.9978534270745526, 0);
        assertLog(0.08223027214657339, 0.9966133564942327, 0);
        assertLog(0.06548693057069123, 0.9978534270745526, 0);
        assertLog(0.04590800199633988, 0.9989456718724518, 0);
        assertLog(0.3019636508581243, 0.9533194394118022, 0);
    }












    private static void assertLog(double x, double y, long maxUlps) {

        final BigDecimal bx = new BigDecimal(x);
        final BigDecimal by = new BigDecimal(y);
        final BigDecimal exact = bx.multiply(bx).add(by.multiply(by)).subtract(BigDecimal.ONE);
        final double real = 0.5 * Math.log1p(exact.doubleValue());
        final double imag = Math.atan2(y, x);
        assertComplex(x, y, "log", Complex::log, real, imag, maxUlps);
    }

    @Test
    void testSqrt() {
        final String name = "sqrt";
        final UnaryOperator<Complex> operation = Complex::sqrt;












        Assertions.assertEquals(0, Math.atan2(0, 1));
        Assertions.assertEquals(Math.PI, Math.atan2(0, -1));


        final double cosArgRe = 1.0;
        final double sinArgRe = 0.0;
        Assertions.assertNotEquals(0.0, Math.cos(Math.PI / 2), "Expected cos(pi/2) to be non-zero");
        Assertions.assertEquals(0.0, Math.cos(Math.PI / 2), 6.123233995736766e-17);

        Assertions.assertEquals(Math.PI / 2, Math.atan2(1, 0));
        Assertions.assertEquals(-Math.PI / 2, Math.atan2(-1, 0));


        final double cosArgIm = Math.cos(Math.PI / 4);
        final double sinArgIm = Math.sin(Math.PI / 4);
        final double root2over2 = Math.sqrt(2) / 2;
        final double ulp = Math.ulp(cosArgIm);
        Assertions.assertNotEquals(cosArgIm, sinArgIm, "Expected cos(pi/4) to not exactly equal sin(pi/4)");
        Assertions.assertEquals(root2over2, cosArgIm, 0, "Expected cos(pi/4) to be sqrt(2) / 2");
        Assertions.assertEquals(root2over2, sinArgIm, ulp, "Expected sin(pi/4) to be 1 ulp from sqrt(2) / 2");
        for (final double a : new double[] {0.5, 1.0, 1.2322, 345345.234523}) {
            final double rootA = Math.sqrt(a);
            assertComplex(a, 0, name, operation, rootA * cosArgRe, rootA * sinArgRe, 0);


            assertComplex(-a, 0, name, operation, rootA * sinArgRe, rootA * cosArgRe, 0);


            assertComplex(0, a, name, operation, rootA * root2over2, rootA * root2over2, 0);
            assertComplex(0, -a, name, operation, rootA * root2over2, -rootA * root2over2, 0);
        }


        double a = Double.MAX_VALUE;
        final double b = a / 4;
        Assertions.assertEquals(inf, Complex.ofCartesian(a, b).abs(), "Expected overflow");




        final double newAbs = 1.3612566508088272E154;
        assertComplex(a, b, name, operation, newAbs * Math.cos(0.5 * Math.atan2(b, a)),
                                             newAbs * Math.sin(0.5 * Math.atan2(b, a)), 3);
        assertComplex(b, a, name, operation, newAbs * Math.cos(0.5 * Math.atan2(a, b)),
                                             newAbs * Math.sin(0.5 * Math.atan2(a, b)), 2);


        a = Double.MAX_VALUE / 2;
        assertComplex(-a, a, name, operation, 4.3145940638864765e+153, 1.0416351505169177e+154, 2);
        assertComplex(a, a, name, operation, 1.0416351505169177e+154, 4.3145940638864758e+153);
        assertComplex(-a, -a, name, operation, 4.3145940638864765e+153,  -1.0416351505169177e+154, 2);
        assertComplex(a, -a, name, operation, 1.0416351505169177e+154, -4.3145940638864758e+153);




        a = Double.MIN_NORMAL;
        assertComplex(-a, a, name, operation, 6.7884304867749663e-155, 1.6388720948399111e-154);
        assertComplex(a, a, name, operation, 1.6388720948399111e-154, 6.7884304867749655e-155);
        assertComplex(-a, -a, name, operation, 6.7884304867749663e-155, -1.6388720948399111e-154);
        assertComplex(a, -a, name, operation, 1.6388720948399111e-154, -6.7884304867749655e-155);
    }




    @Test
    void testDivide() {
        final String name = "divide";
        final BiFunction<Complex, Complex, Complex> operation = Complex::divide;











        double x = Math.scalb(Double.MIN_VALUE, 1023 + 51);
        Assertions.assertEquals(1.0, x);



        assertComplex(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, name, operation, 1.0, 0.0);
        assertComplex(Double.MAX_VALUE, 0.0, Double.MAX_VALUE, 0.0, name, operation, 1.0, 0.0);

        assertComplex(1.0, 1.0, 1.0, 1.0, name, operation, 1.0, 0.0);
        assertComplex(1.0, 0.0, 1.0, 0.0, name, operation, 1.0, 0.0);

        x = Double.MIN_NORMAL;
        while (x != 0) {
            assertComplex(x, x, x, x, name, operation, 1.0, 0.0);
            assertComplex(x, 0, x, 0, name, operation, 1.0, 0.0);
            x /= 2;
        }


        assertComplex(1, 1, Double.MIN_VALUE, Double.MIN_VALUE, name, operation, inf, 0);

        assertComplex(Double.MIN_NORMAL, Double.MIN_NORMAL, Double.MIN_VALUE, Double.MIN_VALUE, name, operation, 4503599627370496L, 0);
        assertComplex(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_NORMAL, Double.MIN_NORMAL, name, operation, 2.2204460492503131e-16, 0);
    }

    @Test
    void testPow() {
        final String name = "pow";
        final BiFunction<Complex, Complex, Complex> operation = Complex::pow;





        assertComplex(1, 1, nan, nan, name, operation, nan, nan);
        assertComplex(nan, nan, 1, 1, name, operation, nan, nan);
        assertComplex(nan, 1, 1, 1, name, operation, nan, nan);
        assertComplex(1, nan, 1, 1, name, operation, nan, nan);
        assertComplex(1, 1, nan, 1, name, operation, nan, nan);
        assertComplex(1, 1, 1, nan, name, operation, nan, nan);


        assertComplex(Double.MAX_VALUE, 1, 2, 2, name, operation, inf, -inf);
        assertComplex(1, Double.MAX_VALUE, 2, 2, name, operation, -inf, inf);
    }
}
