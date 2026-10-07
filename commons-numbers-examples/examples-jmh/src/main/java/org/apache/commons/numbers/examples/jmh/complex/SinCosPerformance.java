
















package org.apache.commons.numbers.examples.jmh.complex;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.numbers.core.Precision;
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
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;







@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class SinCosPerformance {




    private static final double[] EDGE_NUMBERS = {
        Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0, -0.0, Double.NaN};




    @State(Scope.Benchmark)
    public static class NumberSize {



        @Param({"1000"})
        private int size;






        public int getSize() {
            return size;
        }
    }




    public abstract static class BaseNumbers extends NumberSize {

        protected double[] numbers;






        public double[] getNumbers() {
            return numbers;
        }




        @Setup
        public void setup() {
            numbers = createNumbers(new SplittableRandom());

            for (final double x : numbers) {
                final double sin = Math.sin(x);
                assertEquals(sin, FastMath.sin(x), 1, () -> "sin " + x);
                final double cos = Math.cos(x);
                assertEquals(cos, FastMath.cos(x), 1, () -> "cos " + x);
            }
        }







        protected abstract double[] createNumbers(SplittableRandom rng);
    }




    @State(Scope.Benchmark)
    public static class Numbers extends BaseNumbers {



        @Param({"pi", "pi/2", "random", "edge"})
        private String type;


        @Override
        protected double[] createNumbers(SplittableRandom rng) {
            DoubleSupplier generator;
            if ("pi".equals(type)) {
                generator = () -> rng.nextDouble() * 2 * Math.PI - Math.PI;
            } else if ("pi/2".equals(type)) {
                generator = () -> rng.nextDouble() * Math.PI - Math.PI / 2;
            } else if ("random".equals(type)) {
                generator = () -> createRandomNumber(rng);
            } else if ("edge".equals(type)) {
                generator = () -> createEdgeNumber(rng);
            } else {
                throw new IllegalStateException("Unknown number type: " + type);
            }
            return DoubleStream.generate(generator).limit(getSize()).toArray();
        }
    }




    @State(Scope.Benchmark)
    public static class UniformNumbers extends BaseNumbers {






        @Param({"1.57079", "3.14159", "10", "100", "1e4", "1e8", "1e16", "1e32"})
        private double range;


        @Override
        protected double[] createNumbers(SplittableRandom rng) {
            return rng.doubles(getSize(), -range, range).toArray();
        }
    }









    static void assertEquals(double x, double y, int maxUlps, Supplier<String> msg) {
        if (!Precision.equalsIncludingNaN(x, y, maxUlps)) {
            throw new AssertionError(msg.get() + ": " + x + " != " + y);
        }
    }









    private static double createRandomNumber(SplittableRandom rng) {
        return rng.nextDouble(-1e200, 1e200);
    }








    private static double createEdgeNumber(SplittableRandom rng) {
        return EDGE_NUMBERS[rng.nextInt(EDGE_NUMBERS.length)];
    }








    private static void apply(double[] numbers, DoubleUnaryOperator fun, Blackhole bh) {
        for (int i = 0; i < numbers.length; i++) {
            bh.consume(fun.applyAsDouble(numbers[i]));
        }
    }







    private static double identity(double z) {
        return z;
    }















    public void mathSin2(Numbers numbers, Blackhole bh) {
        final double[] x = numbers.getNumbers();
        for (int i = 0; i < x.length; i++) {
            bh.consume(Math.sin(x[i]));
        }
    }








    @Benchmark
    public void baselineIdentity(Numbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), SinCosPerformance::identity, bh);
    }







    @Benchmark
    public void mathSin(Numbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Math::sin, bh);
    }







    @Benchmark
    public void mathCos(Numbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Math::cos, bh);
    }







    @Benchmark
    public void fastMathSin(Numbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), FastMath::sin, bh);
    }







    @Benchmark
    public void fastMathCos(Numbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), FastMath::cos, bh);
    }







    @Benchmark
    public void rangeMathSin(UniformNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Math::sin, bh);
    }







    @Benchmark
    public void rangeFastMathSin(UniformNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), FastMath::sin, bh);
    }
}
