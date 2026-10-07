
















package org.apache.commons.numbers.examples.jmh.core;

import java.util.concurrent.TimeUnit;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;

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
public class DoubleSplitPerformance {

    private static final long SIGN_MATISSA_MASK = 0x800f_ffff_ffff_ffffL;







    private static final double MULTIPLIER = 1.34217729E8;




    private static final double SAFE_UPPER = 0x1.0p996;



    private static final double DOWN_SCALE = 0x1.0p-30;



    private static final double UP_SCALE = 0x1.0p30;


    private static final long ZERO_LOWER_27_BITS = 0xffff_ffff_f800_0000L;


    private static final String NONE = "none";




    @State(Scope.Benchmark)
    public static class Numbers {

        private static final long EXP_SMALL = Double.doubleToRawLongBits(1.0);

        private static final long EXP_BIG = Double.doubleToRawLongBits(SAFE_UPPER);




        @Param({"10000"})
        private int size;







        @Param({"1", "0.999", "0.99", "0.9"})
        private double edge;


        private double[] a;






        public double[] getNumbers() {
            return a;
        }




        @Setup
        public void setup() {
            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create();
            a = new double[size];
            for (int i = 0; i < size; i++) {
                long bits = rng.nextLong() & SIGN_MATISSA_MASK;

                if (rng.nextDouble() < edge) {
                    bits |= EXP_SMALL;
                } else {
                    bits |= EXP_BIG;
                }
                a[i] = Double.longBitsToDouble(bits);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class BiFactors {

        private static final long EXP_SMALL = Double.doubleToRawLongBits(1.0);




        @Param({"5000"})
        private int size;









        @Param({"600", "1000", "1023"})
        private int exp;









        @Param({"1", "0.95", "0.9"})
        private double edge;


        private double[] a;






        public double[] getFactors() {
            return a;
        }




        @Setup
        public void setup() {

            final double d = Math.scalb(1.0, exp);
            assert Double.isInfinite(d * d) : "Product of big numbers does not overflow";
            final long expBig = Double.doubleToRawLongBits(d);

            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create();
            a = new double[size * 2];
            for (int i = 0; i < a.length; i++) {
                long bits = rng.nextLong() & SIGN_MATISSA_MASK;

                if (rng.nextDouble() < edge) {
                    bits |= EXP_SMALL;
                } else {
                    bits |= expBig;
                }
                a[i] = Double.longBitsToDouble(bits);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class NonNormalNumbers {

        private static final double[] NON_NORMAL =
            {Double.POSITIVE_INFINITY, Double.NaN, Double.MIN_NORMAL};




        @Param({"10000"})
        private int size;




        @Param({"1", "0.999", "0.99", "0.9"})
        private double edge;


        private double[] a;






        public double[] getFactors() {
            return a;
        }




        @Setup
        public void setup() {
            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create();
            a = new double[size];
            for (int i = 0; i < size; i++) {

                double value = rng.nextDouble() * (rng.nextBoolean() ? -1 : 1);

                if (rng.nextDouble() < edge) {
                    value *= NON_NORMAL[rng.nextInt(NON_NORMAL.length)];
                }
                a[i] = value;
            }
        }
    }




    @State(Scope.Benchmark)
    public static class SplitMethod {



        @Param({NONE, "dekker", "dekkerAbs", "dekkerRaw", "bits"})
        private String name;


        private DoubleUnaryOperator fun;






        public DoubleUnaryOperator getFunction() {
            return fun;
        }




        @Setup
        public void setup() {
            if (NONE.equals(name)) {
                fun = a -> a;
            } else if ("dekker".equals(name)) {
                fun = DoubleSplitPerformance::splitDekker;
            } else if ("dekkerAbs".equals(name)) {
                fun = DoubleSplitPerformance::splitDekkerAbs;
            } else if ("dekkerRaw".equals(name)) {
                fun = DoubleSplitPerformance::splitDekkerRaw;
            } else if ("bits".equals(name)) {
                fun = DoubleSplitPerformance::splitBits;
            } else {
                throw new IllegalStateException("Unknown split method: " + name);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class NonNormalMethod {



        @Param({NONE, "if", "exponent", "exponent2"})
        private String name;


        private DoublePredicate fun;






        public DoublePredicate getFunction() {
            return fun;
        }




        @Setup
        public void setup() {
            if (NONE.equals(name)) {
                fun = a -> false;
            } else if ("if".equals(name)) {
                fun = DoubleSplitPerformance::isNotNormalIf;
            } else if ("exponent".equals(name)) {
                fun = DoubleSplitPerformance::isNotNormalExponent;
            } else if ("exponent2".equals(name)) {
                fun = DoubleSplitPerformance::isNotNormalExponent2;
            } else {
                throw new IllegalStateException("Unknown is non-normal method: " + name);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class RoundoffMethod {



        @Param({NONE, "multiply", "multiplyUnscaled",
            "productLow", "productLowS",
            "productLow0", "productLow1", "productLow2", "productLow3", "productLowSplit",
            "productLowUnscaled", "fma"})
        private String name;


        private DoubleBinaryOperator fun;






        public DoubleBinaryOperator getFunction() {
            return fun;
        }




        @Setup
        public void setup() {
            if (NONE.equals(name)) {


                fun = (x, y) -> x * y;
            } else if ("multiply".equals(name)) {
                final DoublePrecision.Quad result = new DoublePrecision.Quad();
                fun = (x, y) -> {
                    DoublePrecision.multiply(x, y, result);
                    return result.lo;
                };
            } else if ("productLow".equals(name)) {
                fun = (x, y) -> DoublePrecision.productLow(x, y, x * y);
            } else if ("productLowS".equals(name)) {
                fun = (x, y) -> DoublePrecision.productLowS(x, y, x * y);
            } else if ("productLow0".equals(name)) {
                fun = (x, y) -> DoublePrecision.productLow0(x, y, x * y);
            } else if ("productLow1".equals(name)) {
                fun = (x, y) -> DoublePrecision.productLow1(x, y, x * y);
            } else if ("productLow2".equals(name)) {
                fun = (x, y) -> DoublePrecision.productLow2(x, y, x * y);
            } else if ("productLow3".equals(name)) {
                fun = (x, y) -> DoublePrecision.productLow3(x, y, x * y);
            } else if ("productLowSplit".equals(name)) {
                fun = (x, y) -> DoublePrecision.productLowSplit(x, y, x * y);
            } else if ("multiplyUnscaled".equals(name)) {
                final DoublePrecision.Quad result = new DoublePrecision.Quad();
                fun = (x, y) -> {
                    DoublePrecision.multiplyUnscaled(x, y, result);
                    return result.lo;
                };
            } else if ("productLowUnscaled".equals(name)) {
                fun = (x, y) -> DoublePrecision.productLowUnscaled(x, y, x * y);
            } else if ("fma".equals(name)) {
                fun = (x, y) -> Math.fma(x, y, -x * y);
            } else {
                throw new IllegalStateException("Unknown round-off method: " + name);
            }
        }
    }






















    private static double splitDekker(double value) {

        if (value >= SAFE_UPPER || value <= -SAFE_UPPER) {

            final double x = value * DOWN_SCALE;
            final double c = MULTIPLIER * x;
            final double hi = (c - (c - x)) * UP_SCALE;
            if (Double.isInfinite(hi)) {











                return Double.longBitsToDouble(Double.doubleToRawLongBits(value) & ZERO_LOWER_27_BITS);
            }
            return hi;
        }

        final double c = MULTIPLIER * value;
        return c - (c - value);
    }









    private static double splitDekkerAbs(double value) {
        if (Math.abs(value) >= SAFE_UPPER) {
            final double x = value * DOWN_SCALE;
            final double c = MULTIPLIER * x;
            final double hi = (c - (c - x)) * UP_SCALE;
            if (Double.isInfinite(hi)) {
                return Double.longBitsToDouble(Double.doubleToRawLongBits(value) & ZERO_LOWER_27_BITS);
            }
            return hi;
        }
        final double c = MULTIPLIER * value;
        return c - (c - value);
    }








    private static double splitDekkerRaw(double value) {
        final double c = MULTIPLIER * value;
        return c - (c - value);
    }







    private static double splitBits(double value) {
        return Double.longBitsToDouble(Double.doubleToRawLongBits(value) & ZERO_LOWER_27_BITS);
    }









    private static boolean isNotNormalIf(double a) {
        final double abs = Math.abs(a);
        return abs <= Double.MIN_NORMAL || !(abs <= Double.MAX_VALUE);
    }







    private static boolean isNotNormalExponent(double a) {




        final int baisedExponent = (int) (Double.doubleToRawLongBits(a) >>> 52) & 0x7ff;
        return ((baisedExponent - 1) & 0xffff) >= 2046;
    }







    private static boolean isNotNormalExponent2(double a) {




        final int baisedExponent = (int) (Double.doubleToRawLongBits(a) >>> 52) & 0x7ff;

        return baisedExponent + Integer.MIN_VALUE - 1 >= 2046 + Integer.MIN_VALUE;
    }










    @Benchmark
    public void high(Numbers numbers, Blackhole bh, SplitMethod method) {
        final DoubleUnaryOperator fun = method.getFunction();
        final double[] a = numbers.getNumbers();
        for (int i = 0; i < a.length; i++) {
            bh.consume(fun.applyAsDouble(a[i]));
        }
    }








    @Benchmark
    public void low(Numbers numbers, Blackhole bh, SplitMethod method) {
        final DoubleUnaryOperator fun = method.getFunction();
        final double[] a = numbers.getNumbers();
        for (int i = 0; i < a.length; i++) {
            bh.consume(a[i] - fun.applyAsDouble(a[i]));
        }
    }








    @Benchmark
    public void nonNormal(NonNormalNumbers numbers, Blackhole bh, NonNormalMethod method) {
        final DoublePredicate fun = method.getFunction();
        final double[] a = numbers.getFactors();
        for (int i = 0; i < a.length; i++) {
            bh.consume(fun.test(a[i]));
        }
    }








    @Benchmark
    public void productLow(BiFactors factors, Blackhole bh, RoundoffMethod method) {
        final DoubleBinaryOperator fun = method.getFunction();
        final double[] a = factors.getFactors();
        for (int i = 0; i < a.length; i += 2) {
            bh.consume(fun.applyAsDouble(a[i], a[i + 1]));
        }
    }
}
