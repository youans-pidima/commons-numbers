























package org.apache.commons.numbers.examples.jmh.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.math3.dfp.DfpField;
import org.apache.commons.numbers.core.DD;
import org.apache.commons.numbers.core.DDExt;
import org.apache.commons.numbers.core.DDMath;
import org.apache.commons.rng.UniformRandomProvider;
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
import org.openjdk.jmh.infra.Blackhole;





@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class DDPerformance {


    static final String IMP_DD_STATIC_MUTABLE = "static mutable";

    static final String IMP_DD_STATIC_MUTABLE_FULL_POW = "static mutable full-pow";

    static final String IMP_DD_OO_IMMUTABLE = "OO immutable";

    static final String IMP_ACCURATE_POW_SCALED = "accuratePowScaled";

    static final String IMP_POW_SCALED = "powScaled";

    static final String IMP_SIMPLE_POW_SCALED = "simplePowScaled";




    public interface DoubleIntFunction {







        double apply(double x, int n);
    }




    public interface DDIntFunction {







        Object apply(DD x, int n);
    }




    public static class DoubleInt {

        private final double x;

        private final int n;





        DoubleInt(double x, int n) {
            this.x = x;
            this.n = n;
        }




        double getX() {
            return x;
        }




        int getN() {
            return n;
        }
    }




    public static class DDInt {

        private final DD x;

        private final int n;





        DDInt(DD x, int n) {
            this.x = x;
            this.n = n;
        }




        DD getX() {
            return x;
        }




        int getN() {
            return n;
        }
    }





    @State(Scope.Benchmark)
    public static class KSMethod {

        @Param({IMP_DD_STATIC_MUTABLE, IMP_DD_STATIC_MUTABLE_FULL_POW, IMP_DD_OO_IMMUTABLE})
        private String implementation;


        private DoubleIntFunction function;






        public DoubleIntFunction getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            function = createFunction(implementation);
        }







        static DoubleIntFunction createFunction(String implementation) {
            if (IMP_DD_STATIC_MUTABLE.equals(implementation)) {
                return KolmogorovSmirnovDistribution.One::sfMutable;
            } else if (IMP_DD_STATIC_MUTABLE_FULL_POW.equals(implementation)) {
                return KolmogorovSmirnovDistribution.One::sfMutableFullPow;
            } else if (IMP_DD_OO_IMMUTABLE.equals(implementation)) {
                return KolmogorovSmirnovDistribution.One::sfOO;
            } else {
                throw new IllegalStateException("unknown KS method: " + implementation);
            }
        }







        static Stream<String> getImplementations() {
            return Stream.of(IMP_DD_STATIC_MUTABLE, IMP_DD_STATIC_MUTABLE_FULL_POW, IMP_DD_OO_IMMUTABLE);
        }
    }





    @State(Scope.Benchmark)
    public static class KSData {







        @Param({"1000"})
        private int n;

        @Param({"500"})
        private int values;

        @Param({"0.001"})
        private double lx;

        @Param({"0.5"})
        private double ux;


        private DoubleInt[] data;






        public DoubleInt[] getData() {
            return data;
        }




        @Setup
        public void setup() {
            data = createData(n, values, lx, ux);
        }











        static DoubleInt[] createData(int n, int values, double lx, double ux) {
            assert n > 0 : "Invalid n";
            assert lx <= ux : "Invalid range";
            if (values <= 1) {

                return new DoubleInt[] {new DoubleInt((lx + ux) * 0.5, n)};
            }

            final double inc = (ux - lx) / (values - 1);
            return IntStream.range(0, values)
                            .mapToObj(i -> new DoubleInt(lx + inc * i, n))
                            .toArray(DoubleInt[]::new);
        }
    }





    @State(Scope.Benchmark)
    public static class KSSample {

        @Param({"10000", "100000"})
        private int n;








        @Param({"0.01", "0.05"})
        private double x;




        double getX() {
            return x;
        }




        int getN() {
            return n;
        }
    }




    @State(Scope.Benchmark)
    public static class OperatorData {

        @Param({"1000"})
        private int n;


        private DD[] data;

        private DD[] data2;






        public DD[] getData() {
            return data;
        }






        public DD[] getData2() {
            return data2;
        }




        @Setup(Level.Iteration)
        public void setup() {
            data = createData(n);
            data2 = createData(n);
        }







        static DD[] createData(int n) {
            UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
            return IntStream.range(0, n)
                            .mapToObj(i -> makeSignedDoubleDouble(rng))
                            .toArray(DD[]::new);
        }
    }







    @State(Scope.Benchmark)
    public static class BinaryOperatorMethod {

        @Param({"baseline",

                "add", "accurateAdd",



                "addDouble", "accurateAddDouble",


                "twoSum", "fastTwoSum",

                "multiply", "accurateMultiply", "checkedMultiply",
                "square", "accurateSquare",

                "multiplyDouble", "accurateMultiplyDouble", "checkedMultiplyDouble",

                "divide", "accurateDivide",

                "divideDouble",
                "reciprocal", "accurateReciprocal",
                "sqrt", "accurateSqrt"})
        private String implementation;


        private BiFunction<DD, DD, Object> function;






        public BiFunction<DD, DD, Object> getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            function = createFunction(implementation);
        }







        static BiFunction<DD, DD, Object> createFunction(String implementation) {
            if ("baseline".equals(implementation)) {
                return (a, b) -> DD.of(a.hi());
            } else if ("add".equals(implementation)) {
                return DD::add;
            } else if ("accurateAdd".equals(implementation)) {
                return DDExt::add;
            } else if ("addDouble".equals(implementation)) {
                return (a, b) -> a.add(b.hi());
            } else if ("accurateAddDouble".equals(implementation)) {
                return (a, b) -> DDExt.add(a, b.hi());
            } else if ("twoSum".equals(implementation)) {

                return (a, b) -> SDD.twoSum(a.hi(), a.lo() + b.hi(), SDD.create());
            } else if ("fastTwoSum".equals(implementation)) {

                return (a, b) -> SDD.fastTwoSum(a.hi(), a.lo() + b.hi(), SDD.create());
            } else if ("multiply".equals(implementation)) {
                return DD::multiply;
            } else if ("accurateMultiply".equals(implementation)) {
                return DDExt::multiply;
            } else if ("checkedMultiply".equals(implementation)) {

                return (a, b) -> SDD.multiply(a.hi(), a.lo(), b.hi(), b.lo(), SDD.create());
            } else if ("square".equals(implementation)) {
                return (a, b) -> a.square();
            } else if ("accurateSquare".equals(implementation)) {
                return (a, b) -> DDExt.square(a);
            } else if ("multiplyDouble".equals(implementation)) {
                return (a, b) -> a.multiply(b.hi());
            } else if ("accurateMultiplyDouble".equals(implementation)) {
                return (a, b) -> DDExt.multiply(a, b.hi());
            } else if ("checkedMultiplyDouble".equals(implementation)) {

                return (a, b) -> SDD.multiply(a.hi(), a.lo(), b.hi(), SDD.create());
            } else if ("divide".equals(implementation)) {
                return DD::divide;
            } else if ("accurateDivide".equals(implementation)) {
                return DDExt::divide;
            } else if ("divideDouble".equals(implementation)) {
                return (a, b) -> a.divide(b.hi());
            } else if ("reciprocal".equals(implementation)) {
                return (a, b) -> a.reciprocal();
            } else if ("accurateReciprocal".equals(implementation)) {
                return (a, b) -> DDExt.reciprocal(a);
            } else if ("sqrt".equals(implementation)) {
                return (a, b) -> a.sqrt();
            } else if ("accurateSqrt".equals(implementation)) {
                return (a, b) -> DDExt.sqrt(a);
            } else {
                throw new IllegalStateException("unknown binary operator: " + implementation);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class PowSample {



        @Param({"500"})
        private int values;

        @Param({"-1022"})
        private int lower;

        @Param({"1022"})
        private int upper;


        private DDInt[] data;






        public DDInt[] getData() {
            return data;
        }




        @Setup
        public void setup() {
            if (lower > upper) {
                throw new IllegalStateException(String.format("invalid range: %s to %s", lower, upper));
            }
            if (!Double.isFinite(Math.pow(0.5, lower))) {
                throw new IllegalStateException("lower overflow 0.5^" + lower);
            }
            if (!Double.isFinite(Math.pow(0.5, upper))) {
                throw new IllegalStateException("upper overflow 0.5^" + upper);
            }
            if (!Double.isFinite(Math.pow(2, lower))) {
                throw new IllegalStateException("lower overflow 2^" + lower);
            }
            if (!Double.isFinite(Math.pow(2, upper))) {
                throw new IllegalStateException("upper overflow 2^" + upper);
            }
            UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
            data = IntStream.range(0, values)
                            .mapToObj(i -> {

                                DD dd = makeSignedNormalDoubleDouble(rng);
                                if (rng.nextBoolean()) {

                                    dd = dd.scalb(-1);
                                }
                                return new DDInt(dd, rng.nextInt(lower, upper));
                            }).toArray(DDInt[]::new);
        }
    }




    @State(Scope.Benchmark)
    public static class PowMethod {

        @Param({"pow", IMP_POW_SCALED, IMP_ACCURATE_POW_SCALED, IMP_SIMPLE_POW_SCALED})
        private String implementation;


        private DDIntFunction function;






        public DDIntFunction getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            function = createFunction(implementation);
        }







        static DDIntFunction createFunction(String implementation) {
            if ("pow".equals(implementation)) {
                return DD::pow;
            } else if (IMP_POW_SCALED.equals(implementation)) {
                final long[] exp = {0};
                return (x, n) -> x.pow(n, exp);
            } else if (IMP_ACCURATE_POW_SCALED.equals(implementation)) {
                final long[] exp = {0};
                return (x, n) -> DDMath.pow(x, n, exp);
            } else if (IMP_SIMPLE_POW_SCALED.equals(implementation)) {
                final long[] exp = {0};
                return (x, n) -> DDExt.simplePowScaled(x.hi(), x.lo(), n, exp);
            } else {
                throw new IllegalStateException("unknown pow: " + implementation);
            }
        }
    }









    @State(Scope.Benchmark)
    public static class PowScaledSample {



        @Param({"1000", "10000"})
        private int n;

        @Param({"1.4146512942500389",

            })
        private double x;


        private DD dd;




        DD getDD() {
            return dd;
        }




        int getN() {
            return n;
        }




        @Setup(Level.Iteration)
        public void setup() {



            final double xx = 0.25 * Math.ulp(x) * makeSignedNormalDouble(ThreadLocalRandom.current().nextLong());
            dd = DD.ofSum(x, xx);
        }
    }




    @State(Scope.Benchmark)
    public static class PowScaledMethod {

        @Param({IMP_POW_SCALED, IMP_ACCURATE_POW_SCALED, IMP_SIMPLE_POW_SCALED, "BigDecimal", "Dfp"})
        private String implementation;


        private DDIntFunction function;






        public DDIntFunction getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            function = createFunction(implementation);
        }







        static DDIntFunction createFunction(String implementation) {
            if (IMP_POW_SCALED.equals(implementation)) {
                final long[] exp = {0};
                return (x, n) -> x.pow(n, exp);
            } else if (IMP_ACCURATE_POW_SCALED.equals(implementation)) {
                final long[] exp = {0};
                return (x, n) -> DDMath.pow(x, n, exp);
            } else if (IMP_SIMPLE_POW_SCALED.equals(implementation)) {
                final long[] exp = {0};
                return (x, n) -> DDExt.simplePowScaled(x.hi(), x.lo(), n, exp);
            } else if ("BigDecimal".equals(implementation)) {
                return (x, n) -> new BigDecimal(x.hi()).add(new BigDecimal(x.lo())).pow(n, MathContext.DECIMAL128);
            } else if ("Dfp".equals(implementation)) {
                final DfpField df = new DfpField(MathContext.DECIMAL128.getPrecision());
                return (x, n) -> df.newDfp(x.hi()).add(x.lo()).pow(n);
            } else {
                throw new IllegalStateException("unknown pow scaled: " + implementation);
            }
        }
    }










    private static double makeSignedDouble(long bits) {



        return (bits >> 10) * 0x1.0p-53d;
    }








    private static double makeSignedNormalDouble(long bits) {


        return Double.longBitsToDouble((1023L << 52) | (bits >>> 12) | (bits << 63));
    }







    private static DD makeSignedDoubleDouble(UniformRandomProvider rng) {

        double x = makeSignedDouble(rng.nextLong());


        final double xx = 0.25 * Math.ulp(x) * makeSignedNormalDouble(rng.nextLong());
        return DD.ofSum(x, xx);
    }







    private static DD makeSignedNormalDoubleDouble(UniformRandomProvider rng) {
        final double x = makeSignedNormalDouble(rng.nextLong());



        final double xx = 0x1.0p-54 * makeSignedNormalDouble(rng.nextLong());
        return DD.ofSum(x, xx);
    }








    private static void apply(DoubleIntFunction fun, DoubleInt[] data, Blackhole bh) {
        for (final DoubleInt d : data) {
            bh.consume(fun.apply(d.getX(), d.getN()));
        }
    }









    private static void apply(BiFunction<DD, DD, Object> fun, DD[] data, DD[] data2, Blackhole bh) {
        for (int i = 0; i < data.length; i++) {
            bh.consume(fun.apply(data[i], data2[i]));
        }
    }








    private static void apply(DDIntFunction fun, DDInt[] data, Blackhole bh) {
        for (int i = 0; i < data.length; i++) {
            bh.consume(fun.apply(data[i].getX(), data[i].getN()));
        }
    }











    @Benchmark
    public void ksRange(KSMethod method, KSData data, Blackhole bh) {
        apply(method.getFunction(), data.getData(), bh);
    }








    @Benchmark
    public double ksSample(KSMethod method, KSSample data) {
        return method.getFunction().apply(data.getX(), data.getN());
    }








    @Benchmark
    public void binaryOperator(BinaryOperatorMethod method, OperatorData data, Blackhole bh) {
        apply(method.getFunction(), data.getData(), data.getData2(), bh);
    }








    @Benchmark
    public void pow(PowMethod method, PowSample data, Blackhole bh) {
        apply(method.getFunction(), data.getData(), bh);
    }








    @Benchmark
    public Object powScaled(PowScaledMethod method, PowScaledSample data) {
        return method.getFunction().apply(data.getDD(), data.getN());
    }
}
