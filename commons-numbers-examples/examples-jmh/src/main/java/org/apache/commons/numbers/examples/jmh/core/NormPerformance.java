















package org.apache.commons.numbers.examples.jmh.core;

import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

import org.apache.commons.numbers.core.Norm;
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
public class NormPerformance {



    @State(Scope.Benchmark)
    public static class VectorArrayInput {


        @Param("100000")
        private int samples;


        @Param("-550")
        private int minExp;


        @Param("+550")
        private int maxExp;


        @Param("26")
        private int vectorExpRange;


        private double[][] vectors;




        public double[][] getVectors() {
            return vectors;
        }



        @Setup
        public void createVectors() {
            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create();

            vectors = new double[samples][];
            for (int i = 0; i < vectors.length; ++i) {


                final int vMidExp = rng.nextInt(maxExp - minExp + 1) + minExp;
                final int vExpRadius = vectorExpRange / 2;
                final int vMinExp = vMidExp - vExpRadius;
                final int vMaxExp = vMidExp + vExpRadius;

                vectors[i] = DoubleUtils.randomArray(getLength(), vMinExp, vMaxExp, rng);
            }
        }




        protected int getLength() {
            return 3;
        }
    }



    @State(Scope.Benchmark)
    public static class VectorArrayInput2D extends VectorArrayInput {


        @Override
        protected int getLength() {
            return 2;
        }
    }






    private static void eval(final ToDoubleFunction<double[]> fn,
                             final VectorArrayInput input,
                             final Blackhole bh) {
        final double[][] vectors = input.getVectors();
        for (int i = 0; i < vectors.length; ++i) {
            bh.consume(fn.applyAsDouble(vectors[i]));
        }
    }






    private static void eval(final Norm fn,
                             final VectorArrayInput input,
                             final Blackhole bh) {
        final double[][] vectors = input.getVectors();
        for (int i = 0; i < vectors.length; ++i) {
            bh.consume(fn.of(vectors[i]));
        }
    }





    private static double directEuclideanNorm(final double[] v) {
        double n = 0;
        for (int i = 0; i < v.length; i++) {
            n += v[i] * v[i];
        }
        return Math.sqrt(n);
    }





    @Benchmark
    public void baseline(final VectorArrayInput input, final Blackhole bh) {
        eval(v -> 0d, input, bh);
    }






    @Benchmark
    public void directEuclideanArray(final VectorArrayInput input, final Blackhole bh) {
        eval(NormPerformance::directEuclideanNorm, input, bh);
    }





    @Benchmark
    public void hypot(final VectorArrayInput2D input, final Blackhole bh) {
        eval(v -> Math.hypot(v[0], v[1]), input, bh);
    }





    @Benchmark
    public void euclidean2d(final VectorArrayInput2D input, final Blackhole bh) {
        eval(v -> Norm.L2.of(v[0], v[1]), input, bh);
    }





    @Benchmark
    public void euclidean3d(final VectorArrayInput input, final Blackhole bh) {
        eval(v -> Norm.L2.of(v[0], v[1], v[2]), input, bh);
    }





    @Benchmark
    public void euclideanArray(final VectorArrayInput input, final Blackhole bh) {
        eval(Norm.L2, input, bh);
    }
}
