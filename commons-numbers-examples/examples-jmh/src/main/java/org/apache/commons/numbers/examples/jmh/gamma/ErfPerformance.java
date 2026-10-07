
















package org.apache.commons.numbers.examples.jmh.gamma;

import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import org.apache.commons.numbers.core.Precision;
import org.apache.commons.numbers.fraction.ContinuedFraction;
import org.apache.commons.numbers.gamma.Erf;
import org.apache.commons.numbers.gamma.Erfc;
import org.apache.commons.numbers.gamma.InverseErf;
import org.apache.commons.numbers.gamma.InverseErfc;
import org.apache.commons.numbers.gamma.LogGamma;
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
public class ErfPerformance {

    private static final double EXTREME_VALUE_BOUND = 40;

    private static final String IMP_NUMBERS_1_0 = "Numbers 1.0";

    private static final String IMP_NUMBERS_1_1 = "Boost";

    private static final String NUM_UNIFORM = "uniform";

    private static final String NUM_INVERSE_UNIFORM = "inverse uniform";

    private static final String UNKNOWN = "unknown parameter: ";

    private static final String ERF_DOMAIN_ERROR = "erf domain error: ";

    private static final String ERFC_DOMAIN_ERROR = "erfc domain error: ";










    private static final long SEED = ThreadLocalRandom.current().nextLong();




    public abstract static class NumberData {

        @Param({"1000"})
        private int size;


        private double[] numbers;






        public int getSize() {
            return size;
        }






        public double[] getNumbers() {
            return numbers;
        }




        @Setup
        public void setup() {
            numbers = createNumbers(new SplittableRandom(SEED));
        }








        protected abstract double[] createNumbers(SplittableRandom rng);
    }





    @State(Scope.Benchmark)
    public static class BaseData extends NumberData {

        @Override
        protected double[] createNumbers(SplittableRandom rng) {
            return rng.doubles().limit(getSize()).toArray();
        }
    }




    public abstract static class FunctionData extends NumberData {


        private DoubleUnaryOperator function;




        @Param({IMP_NUMBERS_1_0, IMP_NUMBERS_1_1})
        private String implementation;






        public String getImplementation() {
            return implementation;
        }






        public DoubleUnaryOperator getFunction() {
            return function;
        }




        @Override
        @Setup
        public void setup() {
            super.setup();
            function = createFunction();
            verify();
        }







        protected abstract DoubleUnaryOperator createFunction();








        protected abstract void verify();
    }




    @State(Scope.Benchmark)
    public static class ErfData extends FunctionData {

        @Param({NUM_UNIFORM, NUM_INVERSE_UNIFORM})
        private String type;


        @Override
        protected double[] createNumbers(SplittableRandom rng) {
            DoubleSupplier generator;
            if (NUM_INVERSE_UNIFORM.equals(type)) {


                generator = () -> InverseErf.value(makeSignedDouble(rng));
            } else if (NUM_UNIFORM.equals(type)) {


                generator = () -> makeSignedDouble(rng) * 6;
            } else {
                throw new IllegalStateException(UNKNOWN + type);
            }
            return DoubleStream.generate(generator).limit(getSize()).toArray();
        }


        @Override
        protected DoubleUnaryOperator createFunction() {
            final String impl = getImplementation();
            if (IMP_NUMBERS_1_0.equals(impl)) {
                return ErfPerformance::erf;
            } else if (IMP_NUMBERS_1_1.equals(impl)) {
                return Erf::value;
            } else {
                throw new IllegalStateException(UNKNOWN + impl);
            }
        }


        @Override
        protected void verify() {
            final DoubleUnaryOperator function = getFunction();
            final double relativeEps = 1e-6;
            for (final double x : getNumbers()) {
                final double p = function.applyAsDouble(x);
                assert -1 <= p & p <= 1 : ERF_DOMAIN_ERROR + p;





                if (p < 1e-10 || Math.abs(p - 1) < 1e-10) {
                    continue;
                }
                assertEquals(x, InverseErf.value(p), Math.abs(x) * relativeEps,
                    () -> getImplementation() + " inverse erf " + p);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class ErfcData extends FunctionData {

        @Param({NUM_UNIFORM, NUM_INVERSE_UNIFORM})
        private String type;


        @Override
        protected double[] createNumbers(SplittableRandom rng) {
            DoubleSupplier generator;
            if (NUM_INVERSE_UNIFORM.equals(type)) {


                generator = () -> InverseErfc.value(rng.nextDouble() * 2);
            } else if (NUM_UNIFORM.equals(type)) {



                generator = () -> makeSignedDouble(rng) * 17 + 11;
            } else {
                throw new IllegalStateException(UNKNOWN + type);
            }
            return DoubleStream.generate(generator).limit(getSize()).toArray();
        }


        @Override
        protected DoubleUnaryOperator createFunction() {
            final String impl = getImplementation();
            if (IMP_NUMBERS_1_0.equals(impl)) {
                return ErfPerformance::erfc;
            } else if (IMP_NUMBERS_1_1.equals(impl)) {
                return Erfc::value;
            } else {
                throw new IllegalStateException(UNKNOWN + impl);
            }
        }


        @Override
        protected void verify() {
            final DoubleUnaryOperator function = getFunction();
            final double relativeEps = 1e-6;
            for (final double x : getNumbers()) {
                final double q = function.applyAsDouble(x);
                assert 0 <= q && q <= 2 : ERFC_DOMAIN_ERROR + q;






                if (q < 1e-10 || Math.abs(q - 1) < 1e-10 || q > 2 - 1e-10) {
                    continue;
                }
                assertEquals(x, InverseErfc.value(q), Math.abs(x) * relativeEps,
                    () -> getImplementation() + " inverse erfc " + q);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class InverseErfData extends FunctionData {



        @Param({NUM_UNIFORM})
        private String type;


        @Override
        protected double[] createNumbers(SplittableRandom rng) {
            DoubleSupplier generator;
            if (NUM_UNIFORM.equals(type)) {

                generator = () -> makeSignedDouble(rng);
            } else {
                throw new IllegalStateException(UNKNOWN + type);
            }
            return DoubleStream.generate(generator).limit(getSize()).toArray();
        }


        @Override
        protected DoubleUnaryOperator createFunction() {
            final String impl = getImplementation();
            if (IMP_NUMBERS_1_0.equals(impl)) {
                return ErfPerformance::inverseErf;
            } else if (IMP_NUMBERS_1_1.equals(impl)) {
                return InverseErf::value;
            } else {
                throw new IllegalStateException(UNKNOWN + impl);
            }
        }


        @Override
        protected void verify() {
            final DoubleUnaryOperator function = getFunction();
            final double relativeEps = 1e-12;
            for (final double x : getNumbers()) {
                assert -1 <= x && x <= 1 : ERF_DOMAIN_ERROR + x;





                if (x < 1e-10 || Math.abs(x - 1) < 1e-10) {
                    continue;
                }
                final double t = function.applyAsDouble(x);
                assertEquals(x, Erf.value(t), Math.abs(x) * relativeEps,
                    () -> getImplementation() + " erf " + t);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class InverseErfcData extends FunctionData {



        @Param({NUM_UNIFORM})
        private String type;


        @Override
        protected double[] createNumbers(SplittableRandom rng) {
            DoubleSupplier generator;
            if (NUM_UNIFORM.equals(type)) {

                generator = () -> rng.nextDouble() * 2;
            } else {
                throw new IllegalStateException(UNKNOWN + type);
            }
            return DoubleStream.generate(generator).limit(getSize()).toArray();
        }


        @Override
        protected DoubleUnaryOperator createFunction() {
            final String impl = getImplementation();
            if (IMP_NUMBERS_1_0.equals(impl)) {
                return ErfPerformance::inverseErfc;
            } else if (IMP_NUMBERS_1_1.equals(impl)) {
                return InverseErfc::value;
            } else {
                throw new IllegalStateException(UNKNOWN + impl);
            }
        }


        @Override
        protected void verify() {
            final DoubleUnaryOperator function = getFunction();
            final double relativeEps = 1e-12;
            for (final double x : getNumbers()) {
                assert 0 <= x && x <= 2 : ERFC_DOMAIN_ERROR + x;






                if (x < 1e-10 || Math.abs(x - 1) < 1e-10 || x > 2 - 1e-10) {
                    continue;
                }
                final double t = function.applyAsDouble(x);
                assertEquals(x, Erfc.value(t), Math.abs(x) * relativeEps,
                    () -> getImplementation() + " erfc " + t);
            }
        }
    }







    private static double makeSignedDouble(SplittableRandom rng) {





        return (rng.nextLong() >> 10) * 0x1.0p-53;
    }









    private static double inverseErfc(final double x) {
        return inverseErf(1 - x);
    }
















    private static double inverseErf(final double x) {




        double w = -Math.log((1 - x) * (1 + x));
        double p;

        if (w < 6.25) {
            w -= 3.125;
            p =  -3.6444120640178196996e-21;
            p =   -1.685059138182016589e-19 + p * w;
            p =   1.2858480715256400167e-18 + p * w;
            p =    1.115787767802518096e-17 + p * w;
            p =   -1.333171662854620906e-16 + p * w;
            p =   2.0972767875968561637e-17 + p * w;
            p =   6.6376381343583238325e-15 + p * w;
            p =  -4.0545662729752068639e-14 + p * w;
            p =  -8.1519341976054721522e-14 + p * w;
            p =   2.6335093153082322977e-12 + p * w;
            p =  -1.2975133253453532498e-11 + p * w;
            p =  -5.4154120542946279317e-11 + p * w;
            p =    1.051212273321532285e-09 + p * w;
            p =  -4.1126339803469836976e-09 + p * w;
            p =  -2.9070369957882005086e-08 + p * w;
            p =   4.2347877827932403518e-07 + p * w;
            p =  -1.3654692000834678645e-06 + p * w;
            p =  -1.3882523362786468719e-05 + p * w;
            p =    0.0001867342080340571352 + p * w;
            p =  -0.00074070253416626697512 + p * w;
            p =   -0.0060336708714301490533 + p * w;
            p =      0.24015818242558961693 + p * w;
            p =       1.6536545626831027356 + p * w;
        } else if (w < 16.0) {
            w = Math.sqrt(w) - 3.25;
            p =   2.2137376921775787049e-09;
            p =   9.0756561938885390979e-08 + p * w;
            p =  -2.7517406297064545428e-07 + p * w;
            p =   1.8239629214389227755e-08 + p * w;
            p =   1.5027403968909827627e-06 + p * w;
            p =   -4.013867526981545969e-06 + p * w;
            p =   2.9234449089955446044e-06 + p * w;
            p =   1.2475304481671778723e-05 + p * w;
            p =  -4.7318229009055733981e-05 + p * w;
            p =   6.8284851459573175448e-05 + p * w;
            p =   2.4031110387097893999e-05 + p * w;
            p =   -0.0003550375203628474796 + p * w;
            p =   0.00095328937973738049703 + p * w;
            p =   -0.0016882755560235047313 + p * w;
            p =    0.0024914420961078508066 + p * w;
            p =   -0.0037512085075692412107 + p * w;
            p =     0.005370914553590063617 + p * w;
            p =       1.0052589676941592334 + p * w;
            p =       3.0838856104922207635 + p * w;
        } else if (w < Double.POSITIVE_INFINITY) {
            w = Math.sqrt(w) - 5;
            p =  -2.7109920616438573243e-11;
            p =  -2.5556418169965252055e-10 + p * w;
            p =   1.5076572693500548083e-09 + p * w;
            p =  -3.7894654401267369937e-09 + p * w;
            p =   7.6157012080783393804e-09 + p * w;
            p =  -1.4960026627149240478e-08 + p * w;
            p =   2.9147953450901080826e-08 + p * w;
            p =  -6.7711997758452339498e-08 + p * w;
            p =   2.2900482228026654717e-07 + p * w;
            p =  -9.9298272942317002539e-07 + p * w;
            p =   4.5260625972231537039e-06 + p * w;
            p =  -1.9681778105531670567e-05 + p * w;
            p =   7.5995277030017761139e-05 + p * w;
            p =  -0.00021503011930044477347 + p * w;
            p =  -0.00013871931833623122026 + p * w;
            p =       1.0103004648645343977 + p * w;
            p =       4.8499064014085844221 + p * w;
        } else if (w == Double.POSITIVE_INFINITY) {








            p = Double.POSITIVE_INFINITY;
        } else {


            return Double.NaN;
        }

        return p * x;
    }













    private static double erfc(final double x) {
        if (Math.abs(x) > EXTREME_VALUE_BOUND) {
            return x > 0 ? 0 : 2;
        }
        final double ret = RegularizedGamma.Q.value(0.5, x * x, 1e-15, 10000);
        return x < 0 ? 2 - ret : ret;
    }













    private static double erf(final double x) {
        if (Math.abs(x) > EXTREME_VALUE_BOUND) {
            return x > 0 ? 1 : -1;
        }
        final double ret = RegularizedGamma.P.value(0.5, x * x, 1e-15, 10000);
        return x < 0 ? -ret : ret;
    }












    private static final class RegularizedGamma {

        private RegularizedGamma() {

        }







        static final class P {

            private P() {}



























            static double value(double a,
                                double x,
                                double epsilon,
                                int maxIterations) {
                if (Double.isNaN(a) ||
                    Double.isNaN(x) ||
                    a <= 0 ||
                    x < 0) {
                    return Double.NaN;
                } else if (x == 0) {
                    return 0;
                } else if (x >= a + 1) {

                    return 1 - RegularizedGamma.Q.value(a, x, epsilon, maxIterations);
                } else {

                    double n = 0;
                    double an = 1 / a;
                    double sum = an;
                    while (Math.abs(an / sum) > epsilon &&
                           n < maxIterations &&
                           sum < Double.POSITIVE_INFINITY) {

                        n += 1;
                        an *= x / (a + n);


                        sum += an;
                    }
                    if (n >= maxIterations) {
                        throw new ArithmeticException("Max iterations exceeded: " + maxIterations);
                    } else if (Double.isInfinite(sum)) {
                        return 1;
                    } else {

                        final double result = Math.exp(-x + (a * Math.log(x)) - LogGamma.value(a)) * sum;
                        return result > 1.0 ? 1.0 : result;
                    }
                }
            }
        }







        static final class Q {

            private Q() {}
























            static double value(final double a,
                                double x,
                                double epsilon,
                                int maxIterations) {
                if (Double.isNaN(a) ||
                    Double.isNaN(x) ||
                    a <= 0 ||
                    x < 0) {
                    return Double.NaN;
                } else if (x == 0) {
                    return 1;
                } else if (x < a + 1) {

                    return 1 - RegularizedGamma.P.value(a, x, epsilon, maxIterations);
                } else {
                    final ContinuedFraction cf = new ContinuedFraction() {

                            @Override
                            protected double getA(int n, double x) {
                                return n * (a - n);
                            }


                            @Override
                            protected double getB(int n, double x) {
                                return ((2 * n) + 1) - a + x;
                            }
                        };

                    return Math.exp(-x + (a * Math.log(x)) - LogGamma.value(a)) /
                        cf.evaluate(x, epsilon, maxIterations);
                }
            }
        }
    }









    static void assertEquals(double x, double y, double eps, Supplier<String> msg) {
        if (!Precision.equalsIncludingNaN(x, y, eps)) {
            throw new AssertionError(msg.get() + ": " + x + " != " + y);
        }
    }








    private static void apply(double[] numbers, DoubleUnaryOperator fun, Blackhole bh) {
        for (int i = 0; i < numbers.length; i++) {
            bh.consume(fun.applyAsDouble(numbers[i]));
        }
    }








    private static double identity(double z) {
        return z;
    }











    @Benchmark
    public void baseline(BaseData numbers, Blackhole bh) {
        apply(numbers.getNumbers(), ErfPerformance::identity, bh);
    }







    @Benchmark
    public void erf(ErfData data, Blackhole bh) {
        apply(data.getNumbers(), data.getFunction(), bh);
    }







    @Benchmark
    public void erfc(ErfcData data, Blackhole bh) {
        apply(data.getNumbers(), data.getFunction(), bh);
    }







    @Benchmark
    public void inverseErf(InverseErfData data, Blackhole bh) {
        apply(data.getNumbers(), data.getFunction(), bh);
    }







    @Benchmark
    public void inverseErfc(InverseErfcData data, Blackhole bh) {
        apply(data.getNumbers(), data.getFunction(), bh);
    }
}
