















package org.apache.commons.numbers.examples.jmh.core;

import org.apache.commons.numbers.core.ArithmeticUtils;
import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;




@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class GcdPerformance {



    @State(Scope.Benchmark)
    public static class Ints {



        @Param("42")
        private long seed;



        @Param("100000")
        private int numPairs;




        private int[] values;




        @Setup(Level.Iteration)
        public void setup() {
            values = getRandomProvider(seed).ints()
                    .filter(i -> i != Integer.MIN_VALUE).
                    limit(numPairs * 2)
                    .toArray();

            seed = ((long) values[0] << Integer.SIZE) | values[1];
        }
    }




    @State(Scope.Benchmark)
    public static class Longs {



        @Param("42")
        private long seed;




        @Param("100000")
        private int numPairs;




        private long[] values;




        @Setup(Level.Iteration)
        public void setup() {
            values = getRandomProvider(seed).longs()
                    .filter(i -> i != Long.MIN_VALUE)
                    .limit(numPairs * 2)
                    .toArray();

            seed = values[0];
        }
    }







    private static RestorableUniformRandomProvider getRandomProvider(long seed) {
        return RandomSource.XO_RO_SHI_RO_128_PP.create(seed);
    }







    @Benchmark
    public void gcdInt(Ints ints, Blackhole blackhole) {
        calcAndConsumeGcds(ints, blackhole, ArithmeticUtils::gcd);
    }







    @Benchmark
    public void gcdLongAdaptedForInt(Ints ints, Blackhole blackhole) {
        calcAndConsumeGcds(ints, blackhole, GcdPerformance::gcdLongAdaptedForInt);
    }







    @Benchmark
    public void gcdLong(Longs longs, Blackhole blackhole) {
        calcAndConsumeGcds(longs, blackhole, ArithmeticUtils::gcd);
    }







    @Benchmark
    public void gcdLongWithInts(Ints ints, Blackhole blackhole) {
        calcAndConsumeIntGcdsWithLongImpl(ints, blackhole, ArithmeticUtils::gcd);
    }







    @Benchmark
    public void oldGcdLong(Longs longs, Blackhole blackhole) {
        calcAndConsumeGcds(longs, blackhole, GcdPerformance::oldGcdLong);
    }







    @Benchmark
    public void gcdIntAdaptedForLong(Longs longs, Blackhole blackhole) {
        calcAndConsumeGcds(longs, blackhole, GcdPerformance::gcdIntAdaptedForLong);
    }







    @Benchmark
    public void gcdBigInteger(Longs longs, Blackhole blackhole) {
        calcAndConsumeGcds(longs, blackhole, GcdPerformance::gcdBigInteger);
    }








    private void calcAndConsumeGcds(Longs longs, Blackhole blackhole, LongBinaryOperator gcdImpl) {
        long[] values = longs.values;
        for (int i = 0; i < values.length; i += 2) {
            blackhole.consume(gcdImpl.applyAsLong(values[i], values[i + 1]));
        }
    }








    private void calcAndConsumeIntGcdsWithLongImpl(Ints ints, Blackhole blackhole, LongBinaryOperator gcdImpl) {
        int[] values = ints.values;
        for (int i = 0; i < values.length; i += 2) {
            blackhole.consume(gcdImpl.applyAsLong(values[i], values[i + 1]));
        }
    }








    private void calcAndConsumeGcds(Ints ints, Blackhole blackhole, IntBinaryOperator gcdImpl) {
        int[] values = ints.values;
        for (int i = 0; i < values.length; i += 2) {
            blackhole.consume(gcdImpl.applyAsInt(values[i], values[i + 1]));
        }
    }








    private static long gcdBigInteger(long p, long q) {
        return BigInteger.valueOf(p).gcd(BigInteger.valueOf(q)).longValue();
    }








    private static long gcdIntAdaptedForLong(long p, long q) {


        long a = p > 0 ? -p : p;
        long b = q > 0 ? -q : q;

        long negatedGcd;
        if (a == 0) {
            negatedGcd = b;
        } else if (b == 0) {
            negatedGcd = a;
        } else {

            final int aTwos = Long.numberOfTrailingZeros(a);
            final int bTwos = Long.numberOfTrailingZeros(b);
            a >>= aTwos;
            b >>= bTwos;
            final int shift = Math.min(aTwos, bTwos);







            while (a != b) {
                final long delta = a - b;
                b = Math.max(a, b);
                a = delta > 0 ? -delta : delta;


                a >>= Long.numberOfTrailingZeros(a);
            }


            negatedGcd = a << shift;
        }
        if (negatedGcd == Long.MIN_VALUE) {
            throw new ArithmeticException();
        }

        return -negatedGcd;
    }








    private static long oldGcdLong(final long p, final long q) {
        long u = p;
        long v = q;
        if (u == 0 || v == 0) {
            if (u == Long.MIN_VALUE || v == Long.MIN_VALUE) {
                throw new ArithmeticException();
            }
            return Math.abs(u) + Math.abs(v);
        }





        if (u > 0) {
            u = -u;
        }
        if (v > 0) {
            v = -v;
        }

        int k = 0;
        while ((u & 1) == 0 && (v & 1) == 0 && k < 63) {

            u /= 2;
            v /= 2;
            k++;
        }
        if (k == 63) {
            throw new ArithmeticException();
        }


        long t = ((u & 1) == 1) ? v : -(u / 2)        ;


        do {


            while ((t & 1) == 0) {
                t /= 2;
            }

            if (t > 0) {
                u = -t;
            } else {
                v = t;
            }

            t = (v - u) / 2;


        } while (t != 0);
        return -u * (1L << k);
    }








    private static int gcdLongAdaptedForInt(int p, int q) {


        int a = p > 0 ? -p : p;
        int b = q > 0 ? -q : q;

        int negatedGcd;
        if (a == 0) {
            negatedGcd = b;
        } else if (b == 0) {
            negatedGcd = a;
        } else {

            final int aTwos = Integer.numberOfTrailingZeros(a);
            final int bTwos = Integer.numberOfTrailingZeros(b);
            a >>= aTwos;
            b >>= bTwos;
            final int shift = Math.min(aTwos, bTwos);







            while (true) {
                final int delta = a - b;

                if (delta == 0) {
                    break;
                }

                b = Math.max(a, b);
                a = delta > 0 ? -delta : delta;


                a >>= Integer.numberOfTrailingZeros(a);
            }


            negatedGcd = a << shift;
        }
        if (negatedGcd == Integer.MIN_VALUE) {
            throw new ArithmeticException();
        }
        return -negatedGcd;
    }
}
