
















package org.apache.commons.numbers.examples.jmh.core;

import java.util.concurrent.TimeUnit;
import java.util.function.DoubleBinaryOperator;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;











@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class StickySumPerformance {

    private static final long SIGN_MATISSA_MASK = 0x800f_ffff_ffff_ffffL;


    private static final String NONE = "none";

    private static final String BRANCHED = "branched";

    private static final String BRANCHLESS = "branchless";

    private static final String BRANCH_ON_HI = "branch_on_hi";

    private static final String BRANCH_ON_LO = "branch_on_lo";




    @State(Scope.Benchmark)
    public static class BiFactors {

        private static final long EXP = Double.doubleToRawLongBits(1.0);




        @Param({"10000"})
        private int size;




        @Param({"0", "0.1"})
        private double zeroRoundoff;


        private double[] a;






        public double[] getFactors() {
            return a;
        }




        @Setup
        public void setup() {
            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create();
            a = new double[size * 2];

            int nonZero = 0;
            int unsetSticky = 0;
            for (int i = 0; i < a.length; i += 2) {



                final double x = nextDouble(rng);

                final double y = (rng.nextDouble() < zeroRoundoff) ?
                    0.0 :
                    nextDouble(rng) * 0x1.0p52;


                double e1 = x + y;
                double e0 = DoublePrecision.twoSumLow(x, y, e1);


                final double expected = fastSumWithStickyBitBranched(e0, e1);
                assertEqual(expected, fastSumWithStickyBitBranchless(e0, e1), BRANCHLESS);
                assertEqual(expected, fastSumWithStickyBitBranchedOnHigh(e0, e1), BRANCH_ON_HI);
                assertEqual(expected, fastSumWithStickyBitBranchedOnLow(e0, e1), BRANCH_ON_LO);


                a[i] = e0;
                a[i + 1] = e1;


                final double sum = e1 + e0;
                final double r = e0 - (sum - e1);
                if (r != 0) {
                    nonZero++;
                }
                if ((Double.doubleToRawLongBits(sum) & 0x1) == 0) {
                    unsetSticky++;
                }
            }

            System.out.printf("%n%nNon-zero %d/%d (%.3f) : Unset sticky %d/%d (%.3f)%n%n",
                nonZero, size,
                (double) nonZero / size, unsetSticky, size, (double) unsetSticky / size);

        }








        private static double nextDouble(UniformRandomProvider rng) {
            final long bits = rng.nextLong() & SIGN_MATISSA_MASK;
            return Double.longBitsToDouble(bits | EXP);
        }








        private static void assertEqual(double expected, double actual, String msg) {
            if (Double.compare(expected, actual) != 0) {
                throw new IllegalStateException("Methods do not match: " + msg);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class SumMethod {



        @Param({NONE, BRANCHED, BRANCHLESS, BRANCH_ON_HI, BRANCH_ON_LO})
        private String name;


        private DoubleBinaryOperator fun;






        public DoubleBinaryOperator getFunction() {
            return fun;
        }




        @Setup
        public void setup() {
            if (NONE.equals(name)) {
                fun = (a, b) -> a + b;
            } else if (BRANCHED.equals(name)) {
                fun = StickySumPerformance::fastSumWithStickyBitBranched;
            } else if (BRANCHLESS.equals(name)) {
                fun = StickySumPerformance::fastSumWithStickyBitBranchless;
            } else if (BRANCH_ON_HI.equals(name)) {
                fun = StickySumPerformance::fastSumWithStickyBitBranchedOnHigh;
            } else if (BRANCH_ON_LO.equals(name)) {
                fun = StickySumPerformance::fastSumWithStickyBitBranchedOnLow;
            } else {
                throw new IllegalStateException("Unknown sum method: " + name);
            }
        }
    }

















    private static double fastSumWithStickyBitBranched(double a, double b) {
        double sum = a + b;


        final double r = b - (sum - a);

        if (r != 0) {




















            long hi = Double.doubleToRawLongBits(sum);

            if ((hi & 0x1) == 0) {




                if (sum > 0) {
                    hi += (r > 0) ? 1 : -1;
                } else {
                    hi += (r < 0) ? 1 : -1;
                }
                sum = Double.longBitsToDouble(hi);
            }
        }
        return sum;
    }

















    private static double fastSumWithStickyBitBranchless(double a, double b) {
        final double sum = a + b;


        final double r = b - (sum - a);











































        final long hi = Double.doubleToRawLongBits(sum);







        final long lo = Double.doubleToRawLongBits(r);


        long sticky = lo;
        sticky |= sticky >>> 31;
        sticky |= sticky >>> 16;
        sticky |= sticky >>> 8;
        sticky |= sticky >>> 4;
        sticky |= sticky >>> 2;
        sticky |= sticky >>> 1;



        sticky = sticky & ~hi;


        sticky = sticky & 0x1;



        final long fNegate = (hi ^ lo) >> 63;








        sticky = (sticky ^ fNegate) - fNegate;

        return Double.longBitsToDouble(hi + sticky);
    }

















    private static double fastSumWithStickyBitBranchedOnHigh(double a, double b) {
        final double sum = a + b;


        final double r = b - (sum - a);











































        final long hi = Double.doubleToRawLongBits(sum);

        if ((hi & 0x1) == 0x1) {

            return sum;
        }

        final long lo = Double.doubleToRawLongBits(r);


        long sticky = lo;
        sticky |= sticky >>> 31;
        sticky |= sticky >>> 16;
        sticky |= sticky >>> 8;
        sticky |= sticky >>> 4;
        sticky |= sticky >>> 2;
        sticky |= sticky >>> 1;





        sticky = sticky & 0x1;



        final long fNegate = (hi ^ lo) >> 63;








        sticky = (sticky ^ fNegate) - fNegate;

        return Double.longBitsToDouble(hi + sticky);
    }

















    private static double fastSumWithStickyBitBranchedOnLow(double a, double b) {
        final double sum = a + b;


        final double r = b - (sum - a);










































        if (r != 0) {

            final long hi = Double.doubleToRawLongBits(sum);
            final long lo = Double.doubleToRawLongBits(r);










            int sticky = ~(int) hi & 0x1;



            final int fNegate = (int) ((hi ^ lo) >> 63);








            sticky = (sticky ^ fNegate) - fNegate;

            return Double.longBitsToDouble(hi + sticky);
        }

        return sum;
    }










    @Benchmark
    public void stickySum(BiFactors factors, Blackhole bh, SumMethod method) {
        final DoubleBinaryOperator fun = method.getFunction();
        final double[] a = factors.getFactors();
        for (int i = 0; i < a.length; i += 2) {
            bh.consume(fun.applyAsDouble(a[i], a[i + 1]));
        }
    }
}
