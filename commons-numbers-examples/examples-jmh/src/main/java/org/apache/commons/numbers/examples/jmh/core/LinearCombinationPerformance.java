
















package org.apache.commons.numbers.examples.jmh.core;

import java.math.MathContext;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

import org.apache.commons.numbers.core.Sum;
import org.apache.commons.numbers.examples.jmh.core.LinearCombination.FourD;
import org.apache.commons.numbers.examples.jmh.core.LinearCombination.ND;
import org.apache.commons.numbers.examples.jmh.core.LinearCombination.ThreeD;
import org.apache.commons.numbers.examples.jmh.core.LinearCombination.TwoD;
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
public class LinearCombinationPerformance {





    private static final long SEED = System.currentTimeMillis();




    @State(Scope.Benchmark)
    public static class Factors {



        @Param({"1e20"})
        private double c;




        @Param({"1000"})
        private int size;


        private double[][] a;


        private double[][] b;









        public int getLength() {
            return 4;
        }






        public int getSize() {
            return size;
        }







        public double[] getA(int index) {
            return a[index];
        }







        public double[]  getB(int index) {
            return b[index];
        }




        @Setup
        public void setup() {
            final UniformRandomProvider rng =
                    RandomSource.XO_RO_SHI_RO_1024_PP.create(SEED);


            final int n = Math.max(6, getLength());
            final double[] x = new double[n];
            final double[] y = new double[n];
            a = new double[size][];
            b = new double[size][];

            final MathContext mathContext = new MathContext(100);
            for (int i = 0; i < size; i++) {
                LinearCombinationUtils.genDot(c, rng, x, y, null, mathContext);
                a[i] = Arrays.copyOf(x, getLength());
                b[i] = Arrays.copyOf(y, getLength());
            }
        }
    }




    @State(Scope.Benchmark)
    public static class LengthFactors extends Factors {



        @Param({"2", "3", "4", "8", "16", "32", "64"})
        private int length;


        @Override
        public int getLength() {
            return length;
        }
    }




    @State(Scope.Benchmark)
    public static class Calculator {



        @Param({"standard",
                "current",
                "dekker",
                "dot2s",
                "dot2", "dot3", "dot4", "dot5", "dot6", "dot7",
                "exact",
                "extended", "extended2", "extended_exact", "extended_exact2",




                "dot3c", "extendedc"})
        private String name;


        private TwoD twod;

        private ThreeD threed;

        private FourD fourd;

        private ND nd;




        public TwoD getTwoD() {
            return twod;
        }




        public ThreeD getThreeD() {
            return threed;
        }




        public FourD getFourD() {
            return fourd;
        }




        public ND getND() {
            return nd;
        }




        @Setup
        public void setup() {
            if ("current".endsWith(name)) {
                twod = (a1, b1, a2, b2) ->
                    Sum.create()
                        .addProduct(a1, b1)
                        .addProduct(a2, b2).getAsDouble();
                threed = (a1, b1, a2, b2, a3, b3) ->
                    Sum.create()
                        .addProduct(a1, b1)
                        .addProduct(a2, b2)
                        .addProduct(a3, b3).getAsDouble();
                fourd = (a1, b1, a2, b2, a3, b3, a4, b4) ->
                    Sum.create()
                        .addProduct(a1, b1)
                        .addProduct(a2, b2)
                        .addProduct(a3, b3)
                        .addProduct(a4, b4).getAsDouble();
                nd = (a, b) -> Sum.ofProducts(a, b).getAsDouble();
                return;
            }

            if ("standard".endsWith(name)) {
                nd = LinearCombinations.StandardPrecision.INSTANCE;
            } else if ("dekker".equals(name)) {
                nd = LinearCombinations.Dekker.INSTANCE;
            } else if ("dot2s".equals(name)) {
                nd = LinearCombinations.Dot2s.INSTANCE;
            } else if ("dot2".equals(name)) {
                nd = new LinearCombinations.DotK(2);
            } else if ("dot3".equals(name)) {
                nd = LinearCombinations.DotK.DOT_3;
            } else if ("dot4".equals(name)) {
                nd = LinearCombinations.DotK.DOT_4;
            } else if ("dot5".equals(name)) {
                nd = LinearCombinations.DotK.DOT_5;
            } else if ("dot6".equals(name)) {
                nd = LinearCombinations.DotK.DOT_6;
            } else if ("dot7".equals(name)) {
                nd = LinearCombinations.DotK.DOT_7;
            } else if ("exact".equals(name)) {
                nd = LinearCombinations.Exact.INSTANCE;
            } else if ("extended".equals(name)) {
                nd = LinearCombinations.ExtendedPrecision.INSTANCE;
            } else if ("extended2".equals(name)) {
                nd = LinearCombinations.ExtendedPrecision.DOUBLE;
            } else if ("extended_exact".equals(name)) {
                nd = LinearCombinations.ExtendedPrecision.EXACT;
            } else if ("extended_exact2".equals(name)) {
                nd = LinearCombinations.ExtendedPrecision.EXACT2;
            } else if ("dot3c".equals(name)) {
                nd = new LinearCombinations.DotK(3, new CachedArrayFactory());
            } else if ("extendedc".equals(name)) {
                nd = LinearCombinations.ExtendedPrecision.of(
                        LinearCombinations.ExtendedPrecision.Summation.STANDARD, new CachedArrayFactory());
            } else {
                throw new IllegalStateException("Unknown implementation: " + name);
            }

            twod = (TwoD) nd;
            threed = (ThreeD) nd;
            fourd = (FourD) nd;
        }
    }




    static final class CachedArrayFactory implements IntFunction<double[]> {

        private static final double[] EMPTY = new double[0];


        private double[] array = EMPTY;

        @Override
        public double[] apply(int value) {
            double[] a = array;
            if (a.length < value) {
                array = a = new double[value];
            }
            return a;
        }
    }








    @Benchmark
    public void twoD(Factors factors, Blackhole bh, Calculator calc) {
        final TwoD fun = calc.getTwoD();
        for (int i = 0; i < factors.getSize(); i++) {
            final double[] a = factors.getA(i);
            final double[] b = factors.getB(i);
            bh.consume(fun.value(a[0], b[0], a[1], b[1]));
        }
    }








    @Benchmark
    public void threeD(Factors factors, Blackhole bh, Calculator calc) {
        final ThreeD fun = calc.getThreeD();
        for (int i = 0; i < factors.getSize(); i++) {
            final double[] a = factors.getA(i);
            final double[] b = factors.getB(i);
            bh.consume(fun.value(a[0], b[0], a[1], b[1], a[2], b[2]));
        }
    }








    @Benchmark
    public void fourD(Factors factors, Blackhole bh, Calculator calc) {
        final FourD fun = calc.getFourD();
        for (int i = 0; i < factors.getSize(); i++) {
            final double[] a = factors.getA(i);
            final double[] b = factors.getB(i);
            bh.consume(fun.value(a[0], b[0], a[1], b[1], a[2], b[2], a[3], b[3]));
        }
    }








    @Benchmark
    public void nD(LengthFactors factors, Blackhole bh, Calculator calc) {
        final ND fun = calc.getND();
        for (int i = 0; i < factors.getSize(); i++) {

            final double[] a = factors.getA(i);
            final double[] b = factors.getB(i);
            bh.consume(fun.value(a, b));
        }
    }
}
