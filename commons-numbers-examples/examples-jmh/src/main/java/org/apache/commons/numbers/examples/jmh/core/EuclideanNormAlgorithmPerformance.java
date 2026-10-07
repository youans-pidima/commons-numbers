















package org.apache.commons.numbers.examples.jmh.core;

import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

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
public class EuclideanNormAlgorithmPerformance {


    private static final String LOW = "low";


    private static final String MID = "mid";


    private static final String HIGH = "high";


    private static final String FULL = "full";



    @State(Scope.Benchmark)
    public static class VectorArrayInput {


        @Param("100000")
        private int samples;


        @Param("100")
        private int vectorLength;


        @Param({LOW, MID, HIGH, FULL})
        private String type;


        private double[][] vectors;




        public double[][] getVectors() {
            return vectors;
        }



        @Setup
        public void createVectors() {
            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create();

            int minExp;
            int maxExp;

            switch (type) {
            case LOW:
                minExp = -530;
                maxExp = -510;
                break;
            case MID:
                minExp = -10;
                maxExp = +10;
                break;
            case HIGH:
                minExp = +510;
                maxExp = +530;
                break;
            default:
                throw new IllegalArgumentException("Invalid vector type: " + type);
            }

            vectors = new double[samples][];
            for (int i = 0; i < vectors.length; ++i) {
                vectors[i] = DoubleUtils.randomArray(vectorLength, minExp, maxExp, rng);
            }
        }
    }






    private static void eval(final ToDoubleFunction<double[]> fn, final VectorArrayInput input,
            final Blackhole bh) {
        final double[][] vectors = input.getVectors();
        for (int i = 0; i < vectors.length; ++i) {
            bh.consume(fn.applyAsDouble(vectors[i]));
        }
    }





    @Benchmark
    public void exact(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.Exact(), input, bh);
    }





    @Benchmark
    public void direct(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.Direct(), input, bh);
    }





    @Benchmark
    public void enorm(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.Enorm(), input, bh);
    }





    @Benchmark
    public void enormMod(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.EnormMod(), input, bh);
    }





    @Benchmark
    public void enormModKahan(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.EnormModKahan(), input, bh);
    }





    @Benchmark
    public void enormModExt(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.EnormModExt(), input, bh);
    }





    @Benchmark
    public void extLinear(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.ExtendedPrecisionLinearCombination(), input, bh);
    }





    @Benchmark
    public void extLinearMod(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.ExtendedPrecisionLinearCombinationMod(), input, bh);
    }






    @Benchmark
    public void extLinearSinglePass(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.ExtendedPrecisionLinearCombinationSinglePass(), input, bh);
    }






    @Benchmark
    public void extLinearSqrt2(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNormAlgorithms.ExtendedPrecisionLinearCombinationSqrt2(), input, bh);
    }
}
