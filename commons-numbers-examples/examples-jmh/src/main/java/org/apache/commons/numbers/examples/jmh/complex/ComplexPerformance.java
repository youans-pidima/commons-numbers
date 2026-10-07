
















package org.apache.commons.numbers.examples.jmh.complex;

import org.apache.commons.numbers.complex.Complex;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.ZigguratNormalizedGaussianSampler;
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
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;




@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class ComplexPerformance {




    private static final double[] EDGE_NUMBERS = {
        Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.MAX_VALUE,
        -Double.MAX_VALUE, Double.MIN_VALUE, -Double.MIN_VALUE, 0.0, -0.0, Double.NaN};


    private static final double RANGE = 3.456789;




    @State(Scope.Benchmark)
    public static class ComplexNumberSize {



        @Param({"10000"})
        private int size;






        public int getSize() {
            return size;
        }
    }




    @State(Scope.Benchmark)
    public static class ComplexNumbers extends ComplexNumberSize {

        protected Complex[] numbers;




        @Param({"cis", "vector", "log-uniform", "uniform", "edge"})
        private String type;






        public Complex[] getNumbers() {
            return numbers;
        }




        @Setup
        public void setup() {
            numbers = createNumbers(RandomSource.XO_RO_SHI_RO_128_PP.create());
        }







        Complex[] createNumbers(UniformRandomProvider rng) {
            Supplier<Complex> generator;
            if ("cis".equals(type)) {
                generator = () -> Complex.ofCis(rng.nextDouble() * 2 * Math.PI);
            } else if ("vector".equals(type)) {





                final ZigguratNormalizedGaussianSampler s = ZigguratNormalizedGaussianSampler.of(rng);
                generator = () -> Complex.ofCartesian(s.sample(), s.sample());
            } else if ("log-uniform".equals(type)) {
                generator = () -> Complex.ofCartesian(createLogUniformNumber(rng), createLogUniformNumber(rng));
            } else if ("uniform".equals(type)) {
                generator = () -> Complex.ofCartesian(createUniformNumber(rng), createUniformNumber(rng));
            } else if ("edge".equals(type)) {
                generator = () -> Complex.ofCartesian(createEdgeNumber(rng), createEdgeNumber(rng));
            } else {
                throw new IllegalStateException("Unknown number type: " + type);
            }
            return Stream.generate(generator).limit(getSize()).toArray(Complex[]::new);
        }
    }




    @State(Scope.Benchmark)
    public static class TwoComplexNumbers extends ComplexNumbers {

        private Complex[] numbers2;






        public Complex[] getNumbers2() {
            return numbers2;
        }




        @Override
        @Setup
        public void setup() {

            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
            numbers = createNumbers(rng);
            numbers2 = createNumbers(rng);
        }
    }




    @State(Scope.Benchmark)
    public static class ComplexAndRealNumbers extends ComplexNumbers {

        private double[] numbers2;






        public double[] getNumbers2() {
            return numbers2;
        }




        @Override
        @Setup
        public void setup() {

            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
            numbers = createNumbers(rng);
            numbers2 = Arrays.stream(createNumbers(rng)).mapToDouble(Complex::real).toArray();
        }
    }




    private interface ComplexRealFunction {







        Complex apply(Complex z, double x);
    }










    private static double createLogUniformNumber(UniformRandomProvider rng) {



        final long mask = ((1L << 52) - 1) | 1L << 63;
        final long bits = rng.nextLong() & mask;

        final long exp = rng.nextInt(129) - 64 + 1023;
        return Double.longBitsToDouble(bits | (exp << 52));
    }







    private static double createUniformNumber(UniformRandomProvider rng) {






        return (rng.nextDouble() - rng.nextInt(1)) * RANGE;
    }








    private static double createEdgeNumber(UniformRandomProvider rng) {
        return EDGE_NUMBERS[rng.nextInt(EDGE_NUMBERS.length)];
    }








    private static void apply(Complex[] numbers, Predicate<Complex> fun, Blackhole bh) {
        for (int i = 0; i < numbers.length; i++) {
            bh.consume(fun.test(numbers[i]));
        }
    }








    private static void apply(Complex[] numbers, ToDoubleFunction<Complex> fun, Blackhole bh) {
        for (int i = 0; i < numbers.length; i++) {
            bh.consume(fun.applyAsDouble(numbers[i]));
        }
    }








    private static void apply(Complex[] numbers, UnaryOperator<Complex> fun, Blackhole bh) {
        for (int i = 0; i < numbers.length; i++) {
            bh.consume(fun.apply(numbers[i]));
        }
    }









    private static void apply(Complex[] numbers, Complex[] numbers2,
            BiFunction<Complex, Complex, Complex> fun, Blackhole bh) {
        for (int i = 0; i < numbers.length; i++) {
            bh.consume(fun.apply(numbers[i], numbers2[i]));
        }
    }









    private static void apply(Complex[] numbers, double[] numbers2,
            ComplexRealFunction fun, Blackhole bh) {
        for (int i = 0; i < numbers.length; i++) {
            bh.consume(fun.apply(numbers[i], numbers2[i]));
        }
    }







    private static Complex identity(Complex z) {
        return z;
    }








    private static Complex copy(Complex z) {
        return Complex.ofCartesian(z.real(), z.imag());
    }




















    public void real2(ComplexNumbers numbers, Blackhole bh) {
        final Complex[] z = numbers.getNumbers();
        for (int i = 0; i < z.length; i++) {
            bh.consume(z[i].real());
        }
    }










    public void conj2(ComplexNumbers numbers, Blackhole bh) {
        final Complex[] z = numbers.getNumbers();
        for (int i = 0; i < z.length; i++) {
            bh.consume(z[i].conj());
        }
    }







    @Benchmark
    public void baselineIdentity(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), ComplexPerformance::identity, bh);
    }









    @Benchmark
    public void baselineCopy(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), ComplexPerformance::copy, bh);
    }



    @Benchmark
    public void isNaN(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::isNaN, bh);
    }

    @Benchmark
    public void isInfinite(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::isInfinite, bh);
    }

    @Benchmark
    public void isFinite(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::isFinite, bh);
    }



    @Benchmark
    public void real(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::real, bh);
    }

    @Benchmark
    public void imag(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::imag, bh);
    }

    @Benchmark
    public void abs(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::abs, bh);
    }

    @Benchmark
    public void arg(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::arg, bh);
    }

    @Benchmark
    public void norm(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::norm, bh);
    }










    @Benchmark
    public void sqrtNorm(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), (ToDoubleFunction<Complex>) z -> Math.sqrt(z.norm()), bh);
    }








    @Benchmark
    public void absMathHypot(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), (ToDoubleFunction<Complex>) z -> Math.hypot(z.real(), z.imag()), bh);
    }



    @Benchmark
    public void conj(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::conj, bh);
    }

    @Benchmark
    public void negate(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::negate, bh);
    }

    @Benchmark
    public void proj(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::proj, bh);
    }

    @Benchmark
    public void cos(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::cos, bh);
    }

    @Benchmark
    public void cosh(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::cosh, bh);
    }

    @Benchmark
    public void exp(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::exp, bh);
    }

    @Benchmark
    public void log(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::log, bh);
    }

    @Benchmark
    public void log10(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::log10, bh);
    }

    @Benchmark
    public void sin(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::sin, bh);
    }

    @Benchmark
    public void sinh(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::sinh, bh);
    }

    @Benchmark
    public void sqrt(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::sqrt, bh);
    }

    @Benchmark
    public void tan(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::tan, bh);
    }

    @Benchmark
    public void tanh(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::tanh, bh);
    }

    @Benchmark
    public void acos(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::acos, bh);
    }

    @Benchmark
    public void acosh(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::acosh, bh);
    }

    @Benchmark
    public void asin(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::asin, bh);
    }

    @Benchmark
    public void asinh(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::asinh, bh);
    }

    @Benchmark
    public void atan(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::atan, bh);
    }

    @Benchmark
    public void atanh(ComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), Complex::atanh, bh);
    }



    @Benchmark
    public void pow(TwoComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::pow, bh);
    }

    @Benchmark
    public void multiply(TwoComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::multiply, bh);
    }

    @Benchmark
    public void divide(TwoComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::divide, bh);
    }

    @Benchmark
    public void add(TwoComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::add, bh);
    }

    @Benchmark
    public void subtract(TwoComplexNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::subtract, bh);
    }












    @Benchmark
    public void powReal(ComplexAndRealNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::pow, bh);
    }

    @Benchmark
    public void multiplyReal(ComplexAndRealNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::multiply, bh);
    }

    @Benchmark
    public void divideReal(ComplexAndRealNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::divide, bh);
    }

    @Benchmark
    public void addReal(ComplexAndRealNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::add, bh);
    }

    @Benchmark
    public void subtractReal(ComplexAndRealNumbers numbers, Blackhole bh) {
        apply(numbers.getNumbers(), numbers.getNumbers2(), Complex::subtract, bh);
    }
}
