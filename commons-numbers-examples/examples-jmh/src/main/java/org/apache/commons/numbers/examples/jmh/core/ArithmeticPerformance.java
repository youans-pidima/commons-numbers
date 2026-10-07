
















package org.apache.commons.numbers.examples.jmh.core;

import java.util.concurrent.TimeUnit;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import org.apache.commons.numbers.core.ArithmeticUtils;
import org.apache.commons.rng.simple.RandomSource;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;








@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class ArithmeticPerformance {

    private static final String DIVIDE_UNSIGNED_1_0 = "divideUnsigned_1.0";

    private static final String REMAINDER_UNSIGNED_1_0 = "remainderUnsigned_1.0";

    private static final String DIVIDE_UNSIGNED_1_1 = "divideUnsigned_1.1";

    private static final String REMAINDER_UNSIGNED_1_1 = "remainderUnsigned_1.1";




    @State(Scope.Benchmark)
    public static class LongDataSource {

        @Param({"1024"})
        private int length;


        private long[] data;




        public long[] getData() {
            return data;
        }





        @Setup(Level.Iteration)
        public void setup() {
            data = RandomSource.XO_RO_SHI_RO_128_PP.create().longs(length).toArray();
        }
    }




    @State(Scope.Benchmark)
    public static class IntDataSource {

        @Param({"1024"})
        private int length;


        private int[] data;




        public int[] getData() {
            return data;
        }





        @Setup(Level.Iteration)
        public void setup() {
            data = RandomSource.XO_RO_SHI_RO_128_PP.create().ints(length).toArray();
        }
    }




    @State(Scope.Benchmark)
    public static class LongFunctionSource {

        @Param({"Long.divideUnsigned", DIVIDE_UNSIGNED_1_0, DIVIDE_UNSIGNED_1_1,
                "Long.remainderUnsigned", REMAINDER_UNSIGNED_1_0, REMAINDER_UNSIGNED_1_1})
        private String name;


        private LongBinaryOperator function;




        public LongBinaryOperator getFunction() {
            return function;
        }




        @Setup(Level.Iteration)
        public void setup() {
            if ("Long.divideUnsigned".equals(name)) {
                function = Long::divideUnsigned;
            } else if (DIVIDE_UNSIGNED_1_0.equals(name)) {
                function = ArithmeticPerformance::divideUnsigned;
            } else if (DIVIDE_UNSIGNED_1_1.equals(name)) {
                function = ArithmeticUtils::divideUnsigned;
            } else if ("Long.remainderUnsigned".equals(name)) {
                function = Long::remainderUnsigned;
            } else if (REMAINDER_UNSIGNED_1_0.equals(name)) {
                function = ArithmeticPerformance::remainderUnsigned;
            } else if (REMAINDER_UNSIGNED_1_1.equals(name)) {
                function = ArithmeticUtils::remainderUnsigned;
            } else {
                throw new IllegalStateException("Unknown long function: " + name);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class IntFunctionSource {

        @Param({"Integer.divideUnsigned", DIVIDE_UNSIGNED_1_0, DIVIDE_UNSIGNED_1_1,
                "Integer.remainderUnsigned", REMAINDER_UNSIGNED_1_0, REMAINDER_UNSIGNED_1_1})
        private String name;


        private IntBinaryOperator function;




        public IntBinaryOperator getFunction() {
            return function;
        }




        @Setup(Level.Iteration)
        public void setup() {
            if ("Integer.divideUnsigned".equals(name)) {
                function = Integer::divideUnsigned;
            } else if (DIVIDE_UNSIGNED_1_0.equals(name)) {
                function = ArithmeticPerformance::divideUnsigned;
            } else if (DIVIDE_UNSIGNED_1_1.equals(name)) {
                function = ArithmeticUtils::divideUnsigned;
            } else if ("Integer.remainderUnsigned".equals(name)) {
                function = Integer::remainderUnsigned;
            } else if (REMAINDER_UNSIGNED_1_0.equals(name)) {
                function = ArithmeticPerformance::remainderUnsigned;
            } else if (REMAINDER_UNSIGNED_1_1.equals(name)) {
                function = ArithmeticUtils::remainderUnsigned;
            } else {
                throw new IllegalStateException("Unknown int function: " + name);
            }
        }
    }














    public static int remainderUnsigned(int dividend, int divisor) {
        if (divisor >= 0) {
            if (dividend >= 0) {
                return dividend % divisor;
            }


            final int q = ((dividend >>> 1) / divisor) << 1;
            dividend -= q * divisor;
            if (dividend < 0 || dividend >= divisor) {
                dividend -= divisor;
            }
            return dividend;
        }
        return dividend >= 0 || dividend < divisor ? dividend : dividend - divisor;
    }














    public static long remainderUnsigned(long dividend, long divisor) {
        if (divisor >= 0L) {
            if (dividend >= 0L) {
                return dividend % divisor;
            }


            final long q = ((dividend >>> 1) / divisor) << 1;
            dividend -= q * divisor;
            if (dividend < 0L || dividend >= divisor) {
                dividend -= divisor;
            }
            return dividend;
        }
        return dividend >= 0L || dividend < divisor ? dividend : dividend - divisor;
    }



















    public static int divideUnsigned(int dividend, int divisor) {
        if (divisor >= 0) {
            if (dividend >= 0) {
                return dividend / divisor;
            }


            int q = ((dividend >>> 1) / divisor) << 1;
            dividend -= q * divisor;
            if (dividend < 0L || dividend >= divisor) {
                q++;
            }
            return q;
        }
        return dividend >= 0 || dividend < divisor ? 0 : 1;
    }



















    public static long divideUnsigned(long dividend, long divisor) {
        if (divisor >= 0L) {
            if (dividend >= 0L) {
                return dividend / divisor;
            }


            long q = ((dividend >>> 1) / divisor) << 1;
            dividend -= q * divisor;
            if (dividend < 0L || dividend >= divisor) {
                q++;
            }
            return q;
        }
        return dividend >= 0L || dividend < divisor ? 0L : 1L;
    }










    @Benchmark
    public long longOp(LongDataSource data, LongFunctionSource function) {
        final LongBinaryOperator fun = function.getFunction();
        final long[] a = data.getData();
        long s = 0;
        for (int i = 0; i < a.length; i += 2) {
            s += fun.applyAsLong(a[i], a[i + 1]);
        }
        return s;
    }








    @Benchmark
    public int intOp(IntDataSource data, IntFunctionSource function) {
        final IntBinaryOperator fun = function.getFunction();
        final int[] a = data.getData();
        int s = 0;
        for (int i = 0; i < a.length; i += 2) {
            s += fun.applyAsInt(a[i], a[i + 1]);
        }
        return s;
    }
}
