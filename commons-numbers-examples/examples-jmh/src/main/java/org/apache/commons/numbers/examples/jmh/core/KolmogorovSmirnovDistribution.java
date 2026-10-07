















package org.apache.commons.numbers.examples.jmh.core;

import org.apache.commons.numbers.core.DD;
import org.apache.commons.numbers.core.DDMath;









final class KolmogorovSmirnovDistribution {

    private KolmogorovSmirnovDistribution() {}







































    static final class One {



        private static final int VERY_LARGE_N = 1000000;

        private static final int SD_MAX_TERMS = 3;

        private static final int SD_MIN_N = 8;











        private static final int SUM_PRECISION_BITS = 53;



        private static final int SD_SUM_PRECISION_BITS = 107;




        private interface StaticScaledPower {


















            long pow(double x, double xx, int n, SDD f);
        }




        private interface ScaledPower {
















            DD pow(DD x, int n, long[] exp);
        }




        private interface StatisDDAdd {











            SDD add(double x, double xx, double y, double yy, SDD s);
        }


        private One() {}









        static double sfMutable(double x, int n) {
            final double p = sfExact(x, n);
            if (p >= 0) {
                return p;
            }



            if (n > VERY_LARGE_N) {
                return sfAsymptotic(x, n);
            }
            return sfSDD(x, n, false);
        }









        static double sfMutableFullPow(double x, int n) {
            final double p = sfExact(x, n);
            if (p >= 0) {
                return p;
            }



            if (n > VERY_LARGE_N) {
                return sfAsymptotic(x, n);
            }
            return sfSDD(x, n, true);
        }











        private static double sfExact(double x, int n) {
            if (n * x * x >= 372.5 || x >= 1) {

                return 0;
            }
            if (x <= 0) {

                return 1;
            }
            if (n == 1) {
                return x;
            }


            final double nx = n * x;
            if (nx <= 1) {

                return 1 - x * Math.exp((n - 1) * Math.log1p(x));
            }


            if (n - 1 <= nx) {

                return Math.pow(1 - x, n);
            }
            return -1;
        }











        private static double sfAsymptotic(double x, int n) {

            return Math.exp(-Math.pow(6.0 * n * x + 1, 2) / (18.0 * n));
        }


















        private static int splitX(int n, double x, SDD z) {


            SDD.twoProd(n, x, z);
            final double u = z.hi();
            final double v = z.lo();


            int k = (int) Math.floor(u);

            if (k == u) {

                k += v < 0 ? -1 : 0;
            }

            SDD.fastAdd(u, v, -k, z);



            if (z.hi() == 1) {


                k += 1;
                SDD.set(0, z);
            }
            return k;
        }







        private static int log2(int n) {
            return 31 - Integer.numberOfLeadingZeros(n);
        }









        static double sfOO(double x, int n) {
            final double p = sfExact(x, n);
            if (p >= 0) {
                return p;
            }



            if (n > VERY_LARGE_N) {
                return sfAsymptotic(x, n);
            }
            return sfDD(x, n);
        }





















        private static double sfSDD(double x, int n, boolean fullPow) {


            final SDD z = SDD.create();


            final int k = splitX(n, x, z);
            final double alpha = z.hi();










            final int regN = n - k - 1;
            final int sdN = k - ((alpha == 0) ? 1 : 0);






















            boolean sd = false;
            if (sdN < regN) {


                sd = sdN <= 1;

                sd |= sdN <= SD_MAX_TERMS && n >= SD_MIN_N;
            }

            final int maxN = sd ? sdN : regN;











            StaticScaledPower fpow = sd || fullPow ? SDD::powScaled : SDD::fastPowScaled;




            final StatisDDAdd fadd = sd ? SDD::add : SDD::fastAdd;
            final int sumBits = sd ? SD_SUM_PRECISION_BITS : SUM_PRECISION_BITS + log2(maxN >> 1);


            long e;



            if (sd) {

                SDD.fastTwoSum(1, x, z);
                e = fpow.pow(z.hi(), z.lo(), n - 1, z);
            } else {

                SDD.fastTwoSum(1, -x, z);
                e = fpow.pow(z.hi(), z.lo(), n, z);

                SDD.divide(z.hi(), z.lo(), x, 0, z);
                e += SDD.frexp(z.hi(), z.lo(), z);
            }


            final SDD sum = z.copy();
            long esum = e;


            final SDD c = SDD.create(1);
            long ec = 0;
            for (int i = 1; i <= maxN; i++) {

                SDD.uncheckedDivide(n - i + 1, i, z);
                SDD.uncheckedMultiply(c.hi(), c.lo(), z.hi(), z.lo(), c);

                final int b = Math.getExponent(c.hi());
                if (b != 0) {
                    SDD.ldexp(c.hi(), c.lo(), -b, c);
                    ec += b;
                }

                final int j = sd ? n - i : i;



                SDD.uncheckedDivide(j, n, z);
                SDD.fastAdd(z.hi(), z.lo(), x, z);
                final long es = fpow.pow(z.hi(), z.lo(), j - 1, z);
                final double s = z.hi();
                final double ss = z.lo();
                SDD.uncheckedDivide(n - j, n, z);
                SDD.fastAdd(z.hi(), z.lo(), -x, z);
                final long et = fpow.pow(z.hi(), z.lo(), n - j, z);



                e = ec + es + et;

                if (e > esum - sumBits) {
                    SDD.uncheckedMultiply(c.hi(), c.lo(), z.hi(), z.lo(), z);
                    SDD.uncheckedMultiply(z.hi(), z.lo(), s, ss, z);

                    SDD.ldexp(z.hi(), z.lo(), (int) (e - esum), z);
                    fadd.add(sum.hi(), sum.lo(), z.hi(), z.lo(), sum);
                } else {



                    break;
                }


                esum += SDD.frexp(sum.hi(), sum.lo(), sum);
            }




            SDD.multiply(sum.hi(), sum.lo(), x, sum);

            SDD.ldexp(sum.hi(), sum.lo(), (int) esum, sum);
            if (sd) {

                SDD.add(-sum.hi(), -sum.lo(), 1, sum);
            }
            return clipProbability(sum.doubleValue());
        }


















        private static double sfDD(double x, int n) {




            final SDD zz = SDD.create();


            final int k = splitX(n, x, zz);
            final double alpha = zz.hi();










            final int regN = n - k - 1;
            final int sdN = k - ((alpha == 0) ? 1 : 0);






















            boolean sd = false;
            if (sdN < regN) {


                sd = sdN <= 1;

                sd |= sdN <= SD_MAX_TERMS && n >= SD_MIN_N;
            }

            final int maxN = sd ? sdN : regN;











            ScaledPower fpow = sd ? DDMath::pow : DD::pow;




            final int sumBits = sd ? SD_SUM_PRECISION_BITS : SUM_PRECISION_BITS + log2(maxN >> 1);


            long e;
            int[] ie = {0};
            long[] le = {0};
            DD z;



            if (sd) {


                z = DD.ofSum(1, x);
                z = fpow.pow(z, n - 1, le);
                e = le[0];
            } else {


                z = DD.ofDifference(1, x);
                z = fpow.pow(z, n, le);
                e = le[0];

                z = z.divide(x);
                z = z.frexp(ie);
                e += ie[0];
            }


            DD sum = z;
            long esum = e;


            DD c = DD.ONE;
            long ec = 0;
            for (int i = 1; i <= maxN; i++) {

                z = DD.fromQuotient(n - i + 1, i);
                c = c.multiply(z);

                final int b = Math.getExponent(c.hi());
                if (b != 0) {
                    c = c.scalb(-b);
                    ec += b;
                }

                final int j = sd ? n - i : i;



                z = DD.fromQuotient(j, n);
                z = z.add(x);
                DD s = fpow.pow(z, j - 1, le);
                final long es = le[0];
                z = DD.fromQuotient(n - j, n);
                z = z.add(-x);
                z = fpow.pow(z, n - j, le);
                final long et = le[0];



                e = ec + es + et;

                if (e > esum - sumBits) {
                    z = c.multiply(z);
                    z = z.multiply(s);

                    z = z.scalb((int) (e - esum));
                    sum = sum.add(z);
                } else {



                    break;
                }


                sum = sum.frexp(ie);
                esum += ie[0];
            }




            sum = sum.multiply(x);

            sum = sum.scalb((int) esum);
            if (sd) {

                sum = sum.negate().add(1);
            }
            return clipProbability(sum.doubleValue());
        }
    }







    static double clipProbability(double p) {
        return Math.min(1, Math.max(0, p));
    }
}
