















package org.apache.commons.numbers.examples.jmh.core;

import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

import org.apache.commons.numbers.core.Sum;
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
public class SumPerformance {




    private static final long SEED = System.currentTimeMillis();



    @State(Scope.Benchmark)
    public static class ArrayInput {


        @Param("100000")
        private int samples;


        @Param("50")
        private int len;


        @Param("-550")
        private int minExp;


        @Param("+550")
        private int maxExp;


        @Param("26")
        private int expRange;


        private double[][] a;


        private double[][] b;




        public double[][] getA() {
            return a;
        }




        public double[][] getB() {
            return b;
        }


        @Setup
        public void createArrays() {
            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create(SEED);

            a = new double[samples][];
            b = new double[samples][];
            for (int i = 0; i < samples; ++i) {


                final int vMidExp = rng.nextInt(maxExp - minExp + 1) + minExp;
                final int vExpRadius = expRange / 2;
                final int vMinExp = vMidExp - vExpRadius;
                final int vMaxExp = vMidExp + vExpRadius;

                a[i] = DoubleUtils.randomArray(len, vMinExp, vMaxExp, rng);
                b[i] = DoubleUtils.randomArray(len, vMinExp, vMaxExp, rng);
            }
        }
    }






    private static void runSingle(final ArrayInput input, final Blackhole bh,
            final ToDoubleFunction<double[]> fn) {
        final double[][] a = input.getA();
        for (int i = 0; i < a.length; ++i) {
            bh.consume(fn.applyAsDouble(a[i]));
        }
    }






    private static void runDouble(final ArrayInput input, final Blackhole bh,
            final ToDoubleBiFunction<double[], double[]> fn) {
        final double[][] a = input.getA();
        final double[][] b = input.getB();
        for (int i = 0; i < a.length; ++i) {
            bh.consume(fn.applyAsDouble(a[i], b[i]));
        }
    }





    @Benchmark
    public void baselineSingle(final ArrayInput input, final Blackhole bh) {
        runSingle(input, bh, a -> 0d);
    }





    @Benchmark
    public void baselineDouble(final ArrayInput input, final Blackhole bh) {
        runDouble(input, bh, (a, b) -> 0d);
    }





    @Benchmark
    public void sum(final ArrayInput input, final Blackhole bh) {
        runSingle(input, bh, a -> Sum.of(a).getAsDouble());
    }





    @Benchmark
    public void sumOfProducts(final ArrayInput input, final Blackhole bh) {
        runDouble(input, bh, (a, b) -> Sum.ofProducts(a, b).getAsDouble());
    }
}
