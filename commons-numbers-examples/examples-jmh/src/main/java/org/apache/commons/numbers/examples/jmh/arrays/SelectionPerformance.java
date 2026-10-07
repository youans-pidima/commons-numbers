
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.numbers.arrays.Selection;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.ArraySampler;
import org.apache.commons.rng.sampling.PermutationSampler;
import org.apache.commons.rng.sampling.distribution.DiscreteUniformSampler;
import org.apache.commons.rng.sampling.distribution.SharedStateDiscreteSampler;
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
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx8192M"})
public class SelectionPerformance {

    private static final String JDK = "JDK";

    private static final String SORT = "Sort";

    private static final String BASELINE = "Baseline";

    private static final String HEAP_SELECT = "HeapSelect";

    private static final String SORT_SELECT = "SortSelect";










    private static final String SP = "SP";

    private static final String SPN = "SPN";


    private static final String SPH = "SPH";

    private static final String SBM = "SBM";

    private static final String BM = "BM";

    private static final String DP = "DP";

    private static final String DP5 = "5DP";










    private static final String SBM2 = "2SBM";


    private static final String FR = "FR";

    private static final String KFR = "KFR";












    private static final String ISP = "ISP";

    private static final String IDP = "IDP";





    private static final String LSP = "LSP";


    private static final String LINEAR = "Linear";


    private static final String QA = "QA";


    private static final String QA2 = "QA2";



    private static final String SELECT = "SELECT";


    private static final RandomSource RANDOM_SOURCE = RandomSource.XO_RO_SHI_RO_128_PP;





























































    @State(Scope.Benchmark)
    public abstract static class AbstractDataSource {

        private static final String ALL = "all";

        private static final String BM = "bm";


        private static final String VALOIS = "valois";



        private static final AtomicInteger LOG_SIZE = new AtomicInteger();




        enum Distribution {



            SAWTOOTH,

            RANDOM,

            STAGGER,

            PLATEAU,

            SHUFFLE,








            SHARKTOOTH,




            SORTED,

            ONEZERO,


            M3KILLER,

            ROTATED,

            TWOFACED,

            ORGANPIPE;
        }




        enum Modification {

            COPY,

            REVERSE,

            REVERSE_FRONT,

            REVERSE_BACK,

            SORT,










            DESCENDING,

            DITHER;
        }







        public static final class SampleInfo {

            private final Distribution dist;

            private final Modification mod;

            private final int n;

            private final int m;

            private final int o;








            SampleInfo(Distribution dist, Modification mod, int n, int m, int o) {
                this.dist = dist;
                this.mod = mod;
                this.n = n;
                this.m = m;
                this.o = o;
            }







            SampleInfo with(Distribution v) {
                return new SampleInfo(v, mod, n, m, o);
            }







            SampleInfo with(Modification v) {
                return new SampleInfo(dist, v, n, m, o);
            }




            Distribution getDistribution() {
                return dist;
            }




            Modification getModification() {
                return mod;
            }




            int getN() {
                return n;
            }




            int getM() {
                return m;
            }




            int getO() {
                return o;
            }

            @Override
            public String toString() {
                return String.format("%s, %s, n=%d, m=%d, o=%d", dist, mod, n, m, o);
            }
        }




        protected int[] order;

        protected UniformRandomProvider rng;



        @Param({BM})
        private String distribution = BM;



        @Param({BM})
        private String modification = BM;



        @Param({"1"})
        private int range = 1;



        @Param({"0"})
        private int seed;



        @Param({"0"})
        private int offset;



        @Param({"0"})
        private int samples;






        @Param({"-7450238124206088695"})
        private long rngSeed = -7450238124206088695L;






        private int[][] data;


        private List<SampleInfo> sampleInfo;









        public double[] getData(int index) {
            return getDataSample(order[index]);
        }









        public int[] getIntData(int index) {
            return getIntDataSample(order[index]);
        }







        protected double[] getDataSample(int index) {
            final int[] a = data[index];
            final double[] x = new double[a.length];
            for (int i = -1; ++i < a.length;) {
                x[i] = a[i];
            }
            return x;
        }







        protected int[] getIntDataSample(int index) {

            final int[] a = data[index];
            final int[] x = new int[a.length];
            for (int i = -1; ++i < a.length;) {
                x[i] = a[i];
            }
            return x;
        }







        public int getDataSize(int index) {
            return data[index].length;
        }








        SampleInfo getDataSampleInfo(int index) {
            return sampleInfo.get(index);
        }















        public int size() {
            return data.length;
        }




        @Setup(Level.Iteration)
        public void setup() {
            Objects.requireNonNull(distribution);
            Objects.requireNonNull(modification);


            final EnumSet<Distribution> dist = getDistributions();
            final int length = getLength();
            if (length < 1) {
                throw new IllegalStateException("Unsupported length: " + length);
            }


            final int r = range > 0 ? range : 0;
            if (length + (long) r > Integer.MAX_VALUE) {
                throw new IllegalStateException("Unsupported upper length: " + length);
            }
            final int length2 = length + r;


            if (rngSeed == 0) {
                rngSeed = RandomSource.createLong();
            }
            if (rng == null) {

                rng = RANDOM_SOURCE.create(rngSeed);
            }


            if (dist.contains(Distribution.RANDOM) && dist.size() == 1 && samples > 0) {
                data = new int[samples][];
                sampleInfo = new ArrayList<>(samples);
                final int upper = seed > 0 ? seed : Integer.MAX_VALUE;
                final SharedStateDiscreteSampler s1 = DiscreteUniformSampler.of(rng, 0, upper);
                final SharedStateDiscreteSampler s2 = DiscreteUniformSampler.of(rng, length, length2);
                for (int i = 0; i < data.length; i++) {
                    final int[] a = new int[s2.sample()];
                    for (int j = a.length; --j >= 0;) {
                        a[j] = s1.sample();
                    }
                    data[i] = a;
                    sampleInfo.add(new SampleInfo(Distribution.RANDOM, Modification.COPY, a.length, 0, 0));
                }
                return;
            }


            data = null;
            final int o = offset;
            offset = rng.nextInt();

            final EnumSet<Modification> mod = getModifications();




            final ArrayList<int[]> sampleData = new ArrayList<>();
            sampleInfo = new ArrayList<>();
            final List<SampleInfo> info = new ArrayList<>();
            for (int n = length; n <= length2; n++) {














                for (final int m : createSeeds(seed, n)) {
                    final List<int[]> d = createDistributions(dist, rng, n, m, o, info);
                    for (int i = 0; i < d.size(); i++) {
                        final int[] x = d.get(i);
                        final SampleInfo si = info.get(i);
                        if (mod.contains(Modification.COPY)) {


                            sampleData.add(x);
                            sampleInfo.add(si.with(Modification.COPY));
                        }
                        if (mod.contains(Modification.REVERSE)) {
                            sampleData.add(reverse(x, 0, n));
                            sampleInfo.add(si.with(Modification.REVERSE));
                        }
                        if (mod.contains(Modification.REVERSE_FRONT)) {
                            sampleData.add(reverse(x, 0, n >>> 1));
                            sampleInfo.add(si.with(Modification.REVERSE_FRONT));
                        }
                        if (mod.contains(Modification.REVERSE_BACK)) {
                            sampleData.add(reverse(x, n >>> 1, n));
                            sampleInfo.add(si.with(Modification.REVERSE_BACK));
                        }

                        if (mod.contains(Modification.SORT) ||
                            mod.contains(Modification.DESCENDING)) {
                            final int[] y = x.clone();
                            Arrays.sort(y);
                            if (mod.contains(Modification.DESCENDING)) {
                                sampleData.add(reverse(y, 0, n));
                                sampleInfo.add(si.with(Modification.DESCENDING));
                            }
                            if (mod.contains(Modification.SORT)) {
                                sampleData.add(y);
                                sampleInfo.add(si.with(Modification.SORT));
                            }
                        }
                        if (mod.contains(Modification.DITHER)) {
                            sampleData.add(dither(x));
                            sampleInfo.add(si.with(Modification.DITHER));
                        }
                    }
                }
            }
            data = sampleData.toArray(new int[0][]);
            if (LOG_SIZE.getAndSet(length) != length) {
                Logger.getLogger(getClass().getName()).info(
                    () -> String.format("Data length: [%d, %d] n=%d", length, length2, data.length));
            }
        }








        @Setup(Level.Invocation)
        public void createOrder() {
            if (order == null) {

                order = PermutationSampler.natural(size());
            }
            ArraySampler.shuffle(rng, order);
        }




        private EnumSet<Distribution> getDistributions() {
            EnumSet<Distribution> dist;
            if (BM.equals(distribution)) {
                dist = EnumSet.of(
                    Distribution.SAWTOOTH,
                    Distribution.RANDOM,
                    Distribution.STAGGER,
                    Distribution.PLATEAU,
                    Distribution.SHUFFLE);
            } else if (VALOIS.equals(distribution)) {
                dist = EnumSet.of(
                    Distribution.RANDOM,
                    Distribution.SORTED,
                    Distribution.ONEZERO,
                    Distribution.M3KILLER,
                    Distribution.ROTATED,
                    Distribution.TWOFACED,
                    Distribution.ORGANPIPE);
            } else {
                dist = getEnumFromParam(Distribution.class, distribution);
            }
            return dist;
        }




        private EnumSet<Modification> getModifications() {
            EnumSet<Modification> mod;
            if (BM.equals(modification)) {

                mod = EnumSet.allOf(Modification.class);

                mod.remove(Modification.DESCENDING);
            } else if (VALOIS.equals(modification)) {

                mod = EnumSet.of(Modification.COPY);
            } else {
                mod = getEnumFromParam(Modification.class, modification);
            }
            return mod;
        }









        static <E extends Enum<E>> EnumSet<E> getEnumFromParam(Class<E> cls, String parameters) {
            if (ALL.equals(parameters)) {
                return EnumSet.allOf(cls);
            }
            final EnumSet<E> set = EnumSet.noneOf(cls);
            final String s = parameters.toUpperCase(Locale.ROOT);
            for (final E e : cls.getEnumConstants()) {

                for (int i = s.indexOf(e.name(), 0); i >= 0; i = s.indexOf(e.name(), i)) {


                    i += e.name().length();
                    if (i == s.length() || s.charAt(i) == ':') {
                        set.add(e);
                        break;
                    }
                }
            }
            if (set.isEmpty()) {
                throw new IllegalStateException("Unknown parameters: " + parameters);
            }
            return set;
        }












        private static int[] createSeeds(int seed, int n) {

            if (seed - 1 >= 0) {
                return new int[] {seed};
            }






            int c = 33 - Integer.numberOfLeadingZeros(n - 1);
            final int[] seeds = new int[c];
            c = 0;
            for (int m = 1; c != seeds.length; m *= 2) {
                seeds[c++] = m;
            }
            return seeds;
        }





















        private static List<int[]> createDistributions(EnumSet<Distribution> dist,
                UniformRandomProvider rng, int n, int m, int o, List<SampleInfo> info) {
            final ArrayList<int[]> distData = new ArrayList<>(6);
            int[] x;
            info.clear();
            SampleInfo si = new SampleInfo(null, null, n, m, o);

            if (dist.contains(Distribution.SAWTOOTH) && m != 1) {
                x = createSample(distData, info, si.with(Distribution.SAWTOOTH));



                final int mask = m - 1;
                if ((m & mask) == 0) {
                    for (int i = -1; ++i < n;) {
                        x[i] = (i + o) & mask;
                    }
                } else {

                    int j = Integer.remainderUnsigned(o, m);
                    for (int i = -1; ++i < n;) {
                        j = j % m;
                        x[i] = j++;
                    }
                }
            }
            if (dist.contains(Distribution.RANDOM) && m != 1) {
                x = createSample(distData, info, si.with(Distribution.RANDOM));


                final SharedStateDiscreteSampler s = DiscreteUniformSampler.of(rng, 0, m - 1);
                for (int i = -1; ++i < n;) {
                    x[i] = s.sample();
                }
            }
            if (dist.contains(Distribution.STAGGER)) {
                x = createSample(distData, info, si.with(Distribution.STAGGER));

                final long nn = n;
                final long oo = Integer.toUnsignedLong(o);
                for (int i = -1; ++i < n;) {
                    final long j = i + oo;
                    x[i] = (int) ((j * m + j) % nn);
                }
            }
            if (dist.contains(Distribution.PLATEAU)) {
                x = createSample(distData, info, si.with(Distribution.PLATEAU));

                for (int i = Math.min(n, m); --i >= 0;) {
                    x[i] = i;
                }
                for (int i = m - 1; ++i < n;) {
                    x[i] = m;
                }

                final int n1 = Integer.remainderUnsigned(o, n);
                if (n1 != 0) {
                    final int[] a = x.clone();
                    final int n2 = n - n1;
                    System.arraycopy(a, 0, x, n1, n2);
                    System.arraycopy(a, n2, x, 0, n1);
                }
            }
            if (dist.contains(Distribution.SHUFFLE) && m != 1) {
                x = createSample(distData, info, si.with(Distribution.SHUFFLE));

                final SharedStateDiscreteSampler s = DiscreteUniformSampler.of(rng, 0, m - 1);
                for (int i = -1, j = 0, k = 1; ++i < n;) {
                    x[i] = s.sample() != 0 ? (j += 2) : (k += 2);
                }
            }

            if (dist.contains(Distribution.SHARKTOOTH) && m != 1) {
                x = createSample(distData, info, si.with(Distribution.SHARKTOOTH));

                int i = -1;
                int j = (o & Integer.MAX_VALUE) % m - 1;
                OUTER:
                for (;;) {
                    while (++j < m) {
                        if (++i == n) {
                            break OUTER;
                        }
                        x[i] = j;
                    }
                    while (--j >= 0) {
                        if (++i == n) {
                            break OUTER;
                        }
                        x[i] = j;
                    }
                }
            }

            if (dist.contains(Distribution.SORTED)) {
                x = createSample(distData, info, si.with(Distribution.SORTED));
                for (int i = -1; ++i < n;) {
                    x[i] = i;
                }
            }
            if (dist.contains(Distribution.ONEZERO)) {
                x = createSample(distData, info, si.with(Distribution.ONEZERO));




                final int end = n & ~31;
                for (int i = 0; i < end; i += 32) {
                    int z = rng.nextInt();
                    for (int j = -1; ++j < 32;) {
                        x[i + j] = z & 1;
                        z >>>= 1;
                    }
                }
                for (int i = end; ++i < n;) {
                    x[i] = rng.nextBoolean() ? 1 : 0;
                }
            }
            if (dist.contains(Distribution.M3KILLER)) {
                x = createSample(distData, info, si.with(Distribution.M3KILLER));
                medianOf3Killer(x);
            }
            if (dist.contains(Distribution.ROTATED)) {
                x = createSample(distData, info, si.with(Distribution.ROTATED));


                for (int i = 1; i < n; i++) {
                    x[i - 1] = i;
                }
            }
            if (dist.contains(Distribution.TWOFACED)) {
                x = createSample(distData, info, si.with(Distribution.TWOFACED));


                medianOf3Killer(x);
                final int j = 4 * (31 - Integer.numberOfLeadingZeros(n));
                final int n2 = n >>> 1;
                ArraySampler.shuffle(rng, x, j, n2);
                ArraySampler.shuffle(rng, x, n2 + j, n);
            }
            if (dist.contains(Distribution.ORGANPIPE)) {
                x = createSample(distData, info, si.with(Distribution.ORGANPIPE));


                for (int i = -1, j = n; ++i <= --j;) {
                    x[i] = i;
                    x[j] = i;
                }
            }
            return distData;
        }









        private static int[] createSample(ArrayList<int[]> data, List<SampleInfo> info,
            SampleInfo s) {
            final int[] x = new int[s.getN()];
            data.add(x);
            info.add(s);
            return x;
        }






        private static void medianOf3Killer(int[] x) {









            final int n = x.length;
            final int k = n >>> 1;
            for (int i = 0; i < k; i++) {
                x[i] = ++i;
                x[i] = k + i;
            }
            for (int i = k - 1, j = 2; ++i < n; j += 2) {
                x[i] = j;
            }
        }









        private static int[] reverse(int[] x, int from, int to) {
            final int[] a = x.clone();
            for (int i = from - 1, j = to; ++i < --j;) {
                final int v = a[i];
                a[i] = a[j];
                a[j] = v;
            }
            return a;
        }







        private static int[] dither(int[] x) {
            final int[] a = x.clone();
            for (int i = a.length; --i >= 0;) {



                a[i] += i % 5;
            }
            return a;
        }







        protected abstract int getLength();






        final int getRange() {
            return range;
        }








        void setDistribution(Distribution... v) {
            if (v.length == 0 || v[0] == null) {
                distribution = ALL;
            } else {
                final EnumSet<Distribution> s = EnumSet.of(v[0], v);
                distribution = s.stream().map(Enum::name).collect(Collectors.joining(":"));
            }
        }








        void setModification(Modification... v) {
            if (v.length == 0 || v[0] == null) {
                modification = ALL;
            } else {
                final EnumSet<Modification> s = EnumSet.of(v[0], v);
                modification = s.stream().map(Enum::name).collect(Collectors.joining(":"));
            }
        }







        void setRange(int v) {
            range = v;
        }










        void setSeed(int v) {
            seed = v;
        }







        void setOffset(int v) {
            offset = v;
        }







        void setSamples(int v) {
            samples = v;
        }






        void setRngSeed(long v) {
            this.rngSeed = v;
        }
    }




    @State(Scope.Benchmark)
    public static class SortSource extends AbstractDataSource {

        @Param({"1023"})
        private int length;






        @Param({"1"})
        private int repeats;


        @Override
        protected int getLength() {
            return length;
        }


        @Override
        public int size() {
            return super.size() * repeats;
        }


        @Override
        public double[] getData(int index) {


            return super.getDataSample(order[index] / repeats);
        }
    }








    @State(Scope.Benchmark)
    public static class KSource extends AbstractDataSource {

        @Param({"1023"})
        private int length;

        @Param({"1", "2", "3", "5", "10"})
        private int k;

        @Param({"10"})
        private int repeats;













        @Param({"random"})
        private String mode;






        @Param({"0"})
        private int s;


        private int[][] indices;

        private PermutationSampler[] samplers;


        @Override
        protected int getLength() {
            return length;
        }


        @Override
        public int size() {
            return super.size() * repeats;
        }


        @Override
        public double[] getData(int index) {


            return super.getDataSample(order[index] / repeats);
        }


        @Override
        public int[] getIntData(int index) {
            return super.getIntDataSample(order[index] / repeats);
        }







        public int[] getIndices(int index) {


            return indices[order[index]];
        }




        @Override
        @Setup(Level.Iteration)
        public void setup() {
            if (s < 0 || s >= getLength()) {
                throw new IllegalStateException("Invalid separation: " + s);
            }
            super.setup();


            if (indices == null) {

                indices = new int[size()][];


                if (k > 1) {
                    samplers = new PermutationSampler[getRange() + 1];
                }
            }





            int index = 0;
            final int noOfSamples = super.size();
            if (mode.startsWith("random")) {

                if (k > 1) {
                    final int baseLength = getLength();
                    for (int i = 0; i < noOfSamples; i++) {
                        final int len = getDataSize(i);

                        PermutationSampler sampler = samplers[len - baseLength];
                        if (sampler == null) {

                            final int n = len - s;
                            samplers[len - baseLength] = sampler = new PermutationSampler(rng, n, k);
                        }
                        for (int j = repeats; --j >= 0;) {
                            indices[index++] = sampler.sample();
                        }
                    }
                } else {

                    for (int i = 0; i < noOfSamples; i++) {

                        final int n = getDataSize(i) - s;
                        for (int j = repeats; --j >= 0;) {
                            indices[index++] = new int[] {rng.nextInt(n)};
                        }
                    }
                }
            } else if (mode.startsWith("uniform")) {

                for (int i = 0; i < noOfSamples; i++) {

                    final int n = getDataSize(i) - s;
                    final int step = Math.max(1, (int) Math.round((double) n / k));
                    for (int j = repeats; --j >= 0;) {
                        final int[] k1 = new int[k];
                        int p = rng.nextInt(n);
                        for (int m = 0; m < k; m++) {
                            p = (p + step) % n;
                            k1[m] = p;
                        }
                        indices[index++] = k1;
                    }
                }
            } else if (mode.startsWith("single")) {

                for (int i = 0; i < noOfSamples; i++) {

                    final int n = getDataSize(i) - s;
                    int[] samples;



                    if (n < (k >> 2)) {
                        samples = rng.ints(k, 0, n).toArray();
                    } else {

                        final int step = n / k;
                        samples = new int[k];
                        for (int j = 0, x = step >> 1; j < k; j++, x += step) {
                            samples[j] = x;
                        }
                    }
                    for (int j = 0; j < repeats; j++) {
                        final int ii = j % k;
                        if (ii == 0) {
                            ArraySampler.shuffle(rng, samples);
                        }
                        indices[index++] = new int[] {samples[ii]};
                    }
                }
            } else if ("index".equals(mode)) {


                for (int i = 0; i < noOfSamples; i++) {

                    final int n = getDataSize(i) - s;
                    if (k >= n) {
                        throw new IllegalStateException("Invalid k: " + k + " >= " + n);
                    }
                }
                final int[] kk = s > 0 ? new int[] {k, k + s} : new int[] {k};
                Arrays.fill(indices, kk);
                return;
            } else {
                throw new IllegalStateException("Unknown index mode: " + mode);
            }

            if (s > 0) {
                for (int i = 0; i < indices.length; i++) {
                    final int[] k1 = indices[i];
                    final int[] k2 = new int[k1.length << 1];
                    for (int j = 0; j < k1.length; j++) {
                        k2[j << 1] = k1[j];
                        k2[(j << 1) + 1] = k1[j] + s;
                    }
                    indices[i] = k2;
                }
            }

            if (mode.endsWith("s")) {
                for (int i = 0; i < indices.length; i++) {
                    Arrays.sort(indices[i]);
                }
            }
        }
    }






    @State(Scope.Benchmark)
    public static class IndexSource {

        protected int[][] indices;

        @Param({"1000", "1000000", "1000000000"})
        private int length;

        @Param({"10", "20", "40", "80", "160"})
        private int k;

        @Param({"1000"})
        private int repeats;




        @Param({"-7450238124206088695"})
        private long rngSeed;

        @Param({"false"})
        private boolean ordered;

        @Param({"32"})
        private int separation;




        public int[][] getIndices() {
            return indices;
        }







        public int getMinSeparation() {
            return separation;
        }




        @Setup(Level.Iteration)
        public void setup() {
            if (k < 2) {
                throw new IllegalStateException("Require multiple indices");
            }




            if (rngSeed == 0) {
                rngSeed = RandomSource.createLong();
            }
            final UniformRandomProvider rng = RANDOM_SOURCE.create(rngSeed);

            rngSeed = rng.nextLong();

            final SharedStateDiscreteSampler s = DiscreteUniformSampler.of(rng, 0, length - 1);

            indices = new int[repeats][];

            for (int i = repeats; --i >= 0;) {

                final int[] x = new int[k];
                for (int j = k; --j >= 0;) {
                    x[j] = s.sample();
                }
                indices[i] = x;
                if (ordered) {
                    Sorting.sortIndices(x, x.length);
                }
            }
        }




        long getRngSeed() {
            return rngSeed;
        }
    }






    @State(Scope.Benchmark)
    public static class SplitIndexSource extends IndexSource {

        @Param({"RANDOM", "BINARY"})
        private DivisionMode mode;


        private int[][] points;




        private long[] samples;


        public enum DivisionMode {

            RANDOM,

            BINARY;
        }







        public int[][] getPoints() {
            return points;
        }




        int samples() {
            return samples.length;
        }







        int[] getIndices(int index) {
            return indices[(int) (samples[index] >>> Integer.SIZE)];
        }







        int getPoint(int index) {
            return (int) samples[index];
        }




        @Override
        @Setup(Level.Iteration)
        public void setup() {
            super.setup();

            final UniformRandomProvider rng = RANDOM_SOURCE.create(getRngSeed());

            final int[][] indices = getIndices();
            points = new int[indices.length][];

            final int s = getMinSeparation();


            final boolean random = Objects.requireNonNull(mode) == DivisionMode.RANDOM;

            int size = 0;

            for (int i = points.length; --i >= 0;) {

                final int[] y = indices[i].clone();
                final int unique = Sorting.sortIndices(y, y.length);


                int[] p = new int[unique - 1];
                if (random) {
                    int c = 0;
                    for (int j = 0; j < p.length; j++) {

                        if (y[j] + s < y[j + 1]) {
                            p[c++] = (y[j] + y[j + 1]) >>> 1;
                        }
                    }
                    p = Arrays.copyOf(p, c);
                    ArraySampler.shuffle(rng, p);
                    points[i] = p;
                } else {

                    final int c = divide(y, 0, unique - 1, p, 0, s);
                    points[i] = Arrays.copyOf(p, c);
                }
                size += points[i].length;
            }


            samples = new long[size];
            for (int i = points.length; --i >= 0;) {
                final long l = (long) i << Integer.SIZE;
                for (final int p : points[i]) {
                    samples[--size] = l | p;
                }
            }
            ArraySampler.shuffle(rng, samples);
        }













        private static int divide(int[] indices, int lo, int hi, int[] p, int c, int s) {
            if (lo < hi) {

                final int m = (lo + hi) >>> 1;

                final int m1 = m + 1;

                if (indices[m] + s < indices[m1]) {
                    final int k = (indices[m] + indices[m1]) >>> 1;
                    p[c++] = k;
                }


                c = divide(indices, lo, m, p, c, s);
                c = divide(indices, m1, hi, p, c, s);
            }
            return c;
        }
    }




    @State(Scope.Benchmark)
    public static class SearchableIntervalSource {

        @Param({"ScanningKeyInterval",
            "BinarySearchKeyInterval",
            "IndexSetInterval",
            "CompressedIndexSet",


            })
        private String name;


        private Function<int[], SearchableInterval> factory;





        public SearchableInterval create(int[] indices) {
            return factory.apply(indices);
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);
            if ("ScanningKeyInterval".equals(name)) {
                factory = k -> {
                    k = k.clone();
                    final int unique = Sorting.sortIndices(k, k.length);
                    return ScanningKeyInterval.of(k, unique);
                };
            } else if ("BinarySearchKeyInterval".equals(name)) {
                factory = k -> {
                    k = k.clone();
                    final int unique = Sorting.sortIndices(k, k.length);
                    return BinarySearchKeyInterval.of(k, unique);
                };
            } else if ("IndexSetInterval".equals(name)) {
                factory = IndexSet::of;
            } else if (name.equals("CompressedIndexSet2")) {
                factory = CompressedIndexSet2::of;
            } else if (name.startsWith("CompressedIndexSet")) {


                final int c = getCompression(name);
                factory = k -> CompressedIndexSet.of(c, k);
            } else {
                throw new IllegalStateException("Unknown SearchableInterval: " + name);
            }
        }







        private static int getCompression(String name) {
            final char c = name.charAt(name.length() - 1);
            if (Character.isDigit(c)) {
                return Character.digit(c, 10);
            }
            return 1;
        }
    }




    @State(Scope.Benchmark)
    public static class UpdatingIntervalSource {

        @Param({"KeyUpdatingInterval",


            "BitIndexUpdatingInterval",
            })
        private String name;


        private Function<int[], UpdatingInterval> factory;





        public UpdatingInterval create(int[] indices) {
            return factory.apply(indices);
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);
            if ("KeyUpdatingInterval".equals(name)) {
                factory = k -> {
                    k = k.clone();
                    final int unique = Sorting.sortIndices(k, k.length);
                    return KeyUpdatingInterval.of(k, unique);
                };
            } else if ("IndexSet".equals(name)) {
                factory = k -> IndexSet.of(k).interval();
            } else if (name.equals("BitIndexUpdatingInterval")) {
                factory = k -> BitIndexUpdatingInterval.of(k, k.length);
            } else {
                throw new IllegalStateException("Unknown UpdatingInterval: " + name);
            }
        }
    }












    @State(Scope.Benchmark)
    public static class EdgeSource extends AbstractDataSource {

        @Param({"1023"})
        private int length;

        @Param({"SHIFT"})
        private Mode mode;

        @Param({"1", "2", "3", "4", "5", "6", "7", "8", "9"})
        private int p;

        private int[][] indices;


        public enum Mode {

            SHIFT,

            INDEX;
        }


        @Override
        public int size() {
            return super.size() * 2;
        }


        @Override
        public double[] getData(int index) {


            return super.getDataSample(order[index] >> 1);
        }








        public int[] getIndices(int index) {


            return indices[order[index]];
        }


        @Override
        protected int getLength() {
            return length;
        }




        @Override
        @Setup(Level.Iteration)
        public void setup() {

            super.setup();


            int k;
            if (mode == Mode.SHIFT) {
                k = length >>> p;
                if (k == 0 && length >>> (p - 1) == 0) {
                    throw new IllegalStateException(length + " >>> (" + p + " - 1) == 0");
                }
            } else if (mode == Mode.INDEX) {
                k = p;
                if (k < 0 || k >= length) {
                    throw new IllegalStateException("Invalid index [0, " + length + "): " + p);
                }
            } else {
                throw new IllegalStateException("Unknown mode: " + mode);
            }

            if (indices == null) {

                indices = new int[size()][];
            }




            final int[] lower = {k, k};
            final int noOfSamples = super.size();
            for (int i = 0; i < noOfSamples; i++) {
                final int len = getDataSize(i);
                final int k1 = len - 1 - k;
                indices[i << 1] = lower;
                indices[(i << 1) + 1] = new int[] {k1, k1};
            }
        }
    }




    @State(Scope.Benchmark)
    public static class SortFunctionSource {

        @Param({JDK, SP, BM, SBM, DP, DP5,
            SBM2,




            ISP, IDP,
            })
        private String name;


        @Param({"0"})
        private int qs;


        private Consumer<double[]> function;




        public Consumer<double[]> getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);
            if (JDK.equals(name)) {
                function = Arrays::sort;

            } else if (name.startsWith(SP)) {
                function = PartitionFactory.createKthSelector(name, SP, qs)::sortSP;
            } else if (name.startsWith(SBM)) {
                function = PartitionFactory.createKthSelector(name, SBM, qs)::sortSBM;
            } else if (name.startsWith(BM)) {
                function = PartitionFactory.createKthSelector(name, BM, qs)::sortBM;
            } else if (name.startsWith(DP)) {
                function = PartitionFactory.createKthSelector(name, DP, qs)::sortDP;
            } else if (name.startsWith(DP5)) {
                function = PartitionFactory.createKthSelector(name, DP5, qs)::sortDP5;

            } else if (name.startsWith(SBM2)) {
                function = PartitionFactory.createPartition(name, SBM2, qs, 0)::sortSBM;

            } else if (name.startsWith(ISP)) {
                function = PartitionFactory.createPartition(name, ISP, qs, 0)::sortISP;
            } else if (name.startsWith(IDP)) {
                function = PartitionFactory.createPartition(name, IDP, qs, 0)::sortIDP;


            } else if ("InsertionSortIF".equals(name)) {
                function = x -> {

                    x[0] = Double.NEGATIVE_INFINITY;
                    Sorting.sort(x, 1, x.length - 1, false);
                };
            } else if ("InsertionSortIT".equals(name)) {

                function = x -> {

                    x[0] = Double.NEGATIVE_INFINITY;
                    Sorting.sort(x, 1, x.length - 1, true);
                };
            } else if ("InsertionSort".equals(name)) {
                function = x -> {
                    x[0] = Double.NEGATIVE_INFINITY;
                    Sorting.sort(x, 1, x.length - 1);
                };
            } else if (name.startsWith("PairedInsertionSort")) {
                if (name.endsWith("1")) {
                    function = x -> {
                        x[0] = Double.NEGATIVE_INFINITY;
                        Sorting.sortPairedInternal1(x, 1, x.length - 1);
                    };
                } else if (name.endsWith("2")) {
                    function = x -> {
                        x[0] = Double.NEGATIVE_INFINITY;
                        Sorting.sortPairedInternal2(x, 1, x.length - 1);
                    };
                } else if (name.endsWith("3")) {
                    function = x -> {
                        x[0] = Double.NEGATIVE_INFINITY;
                        Sorting.sortPairedInternal3(x, 1, x.length - 1);
                    };
                } else if (name.endsWith("4")) {
                    function = x -> {
                        x[0] = Double.NEGATIVE_INFINITY;
                        Sorting.sortPairedInternal4(x, 1, x.length - 1);
                    };
                }
            } else if ("InsertionSortB".equals(name)) {
                function = x -> {
                    x[0] = Double.NEGATIVE_INFINITY;
                    Sorting.sortb(x, 1, x.length - 1);
                };


            } else if (name.startsWith(HEAP_SELECT)) {
                final char c = name.charAt(name.length() - 1);

                final int k = Character.isDigit(c) ? Character.digit(c, 10) + 1 : 1;
                function = x -> Partition.heapSelectLeft(x, 1, x.length - 1, k, 0);
            }
            if (function == null) {
                throw new IllegalStateException("Unknown sort function: " + name);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class Sort5FunctionSource {

        @Param({"sort5", "sort5b", "sort5c",

            })
        private String name;


        private Consumer<double[]> function;




        public Consumer<double[]> getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);




            if ("sort5".equals(name)) {
                function = x -> {
                    final int s = x.length >> 2;
                    Sorting.sort5(x, 0, s, s << 1, x.length - 1 - s, x.length - 1);
                };
            } else if ("sort5b".equals(name)) {
                function = x -> {
                    final int s = x.length >> 2;
                    Sorting.sort5b(x, 0, s, s << 1, x.length - 1 - s, x.length - 1);
                };
            } else if ("sort5c".equals(name)) {
                function = x -> {
                    final int s = x.length >> 2;
                    Sorting.sort5c(x, 0, s, s << 1, x.length - 1 - s, x.length - 1);
                };
            } else if ("sort".equals(name)) {
                function = x -> Sorting.sort(x, 0, 4);
            } else if ("sort5head".equals(name)) {
                function = x -> Sorting.sort5(x, 0, 1, 2, 3, 4);

            } else if ("median5".equals(name)) {
                function = x -> {
                    final int s = x.length >> 2;
                    x[0] = Sorting.median5(x, 0, s, s << 1, x.length - 1 - s, x.length - 1);
                };
            } else if ("median5head".equals(name)) {
                function = x -> x[0] = Sorting.median5(x, 0, 1, 2, 3, 4);

            } else if ("med5".equals(name)) {
                function = x -> x[0] = Sorting.median5(x, 0);
            } else if ("med5b".equals(name)) {
                function = x -> x[0] = Sorting.median5b(x, 0);
            } else if ("med5c".equals(name)) {
                function = x -> x[0] = Sorting.median5c(x, 0);
            } else if ("med5d".equals(name)) {
                function = x -> Sorting.median5d(x, 0, 1, 2, 3, 4);
            } else {
                throw new IllegalStateException("Unknown sort5 function: " + name);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class Median4FunctionSource {

        @Param({"lower4", "lower4b", "lower4c", "lower4d", "lower4e",
            "upper4", "upper4c", "upper4d",


            })
        private String name;


        private Consumer<double[]> function;




        public Consumer<double[]> getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);


            if ("lower4".equals(name)) {
                function = x -> {
                    final int f = x.length >>> 2;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.lowerMedian4(x, i - f, i, i + f, i + f2);
                    }
                };
            } else if ("lower4b".equals(name)) {
                function = x -> {
                    final int f = x.length >>> 2;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.lowerMedian4b(x, i - f, i, i + f, i + f2);
                    }
                };
            } else if ("lower4c".equals(name)) {
                function = x -> {
                    final int f = x.length >>> 2;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.lowerMedian4c(x, i - f, i, i + f, i + f2);
                    }
                };
            } else if ("lower4d".equals(name)) {
                function = x -> {
                    final int f = x.length >>> 2;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.lowerMedian4d(x, i - f, i, i + f, i + f2);
                    }
                };
            } else if ("lower4e".equals(name)) {
                function = x -> {
                    final int f = x.length >>> 2;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.lowerMedian4e(x, i - f, i, i + f, i + f2);
                    }
                };
            } else if ("upper4".equals(name)) {
                function = x -> {
                    final int f = x.length >>> 2;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.upperMedian4(x, i - f, i, i + f, i + f2);
                    }
                };
            } else if ("upper4c".equals(name)) {
                function = x -> {
                    final int f = x.length >>> 2;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.upperMedian4c(x, i - f, i, i + f, i + f2);
                    }
                };
            } else if ("upper4d".equals(name)) {
                function = x -> {
                    final int f = x.length >>> 2;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.upperMedian4d(x, i - f, i, i + f, i + f2);
                    }
                };
            } else if ("sort4".equals(name)) {
                function = x -> {
                    final int f = x.length >>> 2;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.sort4(x, i - f, i, i + f, i + f2);
                    }
                };
            } else {
                throw new IllegalStateException("Unknown median4 function: " + name);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class Median3FunctionSource {

        @Param({"sort3", "sort3b", "sort3c"})
        private String name;


        private Consumer<double[]> function;




        public Consumer<double[]> getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);


            if ("sort3".equals(name)) {
                function = x -> {
                    final int f = x.length / 3;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.sort3(x, i - f, i, i + f);
                    }
                };
            } else if ("sort3b".equals(name)) {
                function = x -> {
                    final int f = x.length / 3;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.sort3b(x, i - f, i, i + f);
                    }
                };
            } else if ("sort3c".equals(name)) {
                function = x -> {
                    final int f = x.length / 3;
                    final int f2 = f + f;
                    for (int i = f; i < f2; i++) {
                        Sorting.sort3c(x, i - f, i, i + f);
                    }
                };
            } else {
                throw new IllegalStateException("Unknown sort3 function: " + name);
            }
        }
    }




    @State(Scope.Benchmark)
    public static class DoubleKFunctionSource {

        @Param({SORT + JDK, SPH,
            SP, BM, SBM,
            DP, DP5,
            SBM2,
            ISP, IDP,
            LSP, LINEAR, SELECT})
        private String name;


        @Param({"0"})
        private int qs;


        @Param({"0"})
        private int ec;


        private BiFunction<double[], int[], double[]> function;




        public BiFunction<double[], int[], double[]> getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);


            if (name.equals(BASELINE)) {
                function = (data, indices) -> extractIndices(data, indices.clone());
            } else  if (name.startsWith(SORT)) {

                if (name.contains(ISP)) {
                    final Partition part = PartitionFactory.createPartition(name.substring(SORT.length()), ISP, qs, ec);
                    function = (data, indices) -> {
                        part.sortISP(data);
                        return extractIndices(data, indices);
                    };
                } else if (name.contains(IDP)) {
                    final Partition part = PartitionFactory.createPartition(name.substring(SORT.length()), IDP, qs, ec);
                    function = (data, indices) -> {
                        part.sortIDP(data);
                        return extractIndices(data, indices);
                    };
                } else if (name.contains(JDK)) {
                    function = (data, indices) -> {
                        Arrays.sort(data);
                        return extractIndices(data, indices);
                    };
                }

            } else if (name.startsWith(SPH)) {

                final KthSelector selector = PartitionFactory.createKthSelector(name, SPH, qs);
                function = (data, indices) -> {
                    final int n = indices.length;


                    final int[] pivots = n <= 1 ?
                        KthSelector.NO_PIVOTS :
                        new int[1023];
                    final double[] x = new double[indices.length];
                    for (int i = 0; i < indices.length; i++) {
                        x[i] = selector.selectSPH(data, pivots, indices[i], null);
                    }
                    return x;
                };

            } else if (name.startsWith(SPN)) {
                final KthSelector selector = PartitionFactory.createKthSelector(name, SPN, qs);
                function = (data, indices) -> {
                    selector.partitionSPN(data, indices.clone());
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(SP)) {
                final KthSelector selector = PartitionFactory.createKthSelector(name, SP, qs);
                function = (data, indices) -> {
                    selector.partitionSP(data, indices.clone());
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(BM)) {
                final KthSelector selector = PartitionFactory.createKthSelector(name, BM, qs);
                function = (data, indices) -> {
                    selector.partitionBM(data, indices.clone());
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(SBM)) {
                final KthSelector selector = PartitionFactory.createKthSelector(name, SBM, qs);
                function = (data, indices) -> {
                    selector.partitionSBM(data, indices.clone());
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(DP)) {
                final KthSelector selector = PartitionFactory.createKthSelector(name, DP, qs);
                function = (data, indices) -> {
                    selector.partitionDP(data, indices.clone());
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(DP5)) {
                final KthSelector selector = PartitionFactory.createKthSelector(name, DP5, qs);
                function = (data, indices) -> {
                    selector.partitionDP5(data, indices.clone());
                    return extractIndices(data, indices);
                };

            } else if (name.startsWith(SBM2)) {
                final Partition part = PartitionFactory.createPartition(name, SBM2, qs, ec);
                function = (data, indices) -> {
                    part.partitionSBM(data, indices.clone(), indices.length);
                    return extractIndices(data, indices);
                };

            } else if (name.startsWith(FR)) {
                final Partition part = PartitionFactory.createPartition(name, FR, qs, ec);
                function = (data, indices) -> {
                    part.partitionFR(data, indices.clone(), indices.length);
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(KFR)) {
                final Partition part = PartitionFactory.createPartition(name, KFR, qs, ec);
                function = (data, indices) -> {
                    part.partitionKFR(data, indices.clone(), indices.length);
                    return extractIndices(data, indices);
                };

            } else if (name.startsWith(ISP)) {
                final Partition part = PartitionFactory.createPartition(name, ISP, qs, ec);
                function = (data, indices) -> {
                    part.partitionISP(data, indices.clone(), indices.length);
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(IDP)) {
                final Partition part = PartitionFactory.createPartition(name, IDP, qs, ec);
                function = (data, indices) -> {
                    part.partitionIDP(data, indices.clone(), indices.length);
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(SELECT)) {

                function = (data, indices) -> {
                    Selection.select(data, indices.clone());
                    return extractIndices(data, indices);
                };

            } else if (name.startsWith(LSP)) {
                final Partition part = PartitionFactory.createPartition(name, LSP, qs, ec);
                function = (data, indices) -> {
                    part.partitionLSP(data, indices.clone(), indices.length);
                    return extractIndices(data, indices);
                };

            } else if (name.startsWith(LINEAR)) {
                final Partition part = PartitionFactory.createPartition(name, LINEAR, qs, ec);
                function = (data, indices) -> {
                    part.partitionLinear(data, indices.clone(), indices.length);
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(QA2)) {


                final int mode = PartitionFactory.getControlFlags(new String[] {name}, -1);
                final int inc = PartitionFactory.getOptionFlags(new String[] {name}, 1);
                Partition.configureQaAdaptive(mode, inc);
                function = (data, indices) -> {
                    Partition.partitionQA2(data, indices.clone(), indices.length);
                    return extractIndices(data, indices);
                };
            } else if (name.startsWith(QA)) {
                final Partition part = PartitionFactory.createPartition(name, QA, qs, ec);
                function = (data, indices) -> {
                    part.partitionQA(data, indices.clone(), indices.length);
                    return extractIndices(data, indices);
                };

            } else if (name.startsWith(HEAP_SELECT)) {
                function = (data, indices) -> {
                    int min = indices[indices.length - 1];
                    int max = min;
                    for (int i = indices.length - 1; --i >= 0;) {
                        min = Math.min(min, indices[i]);
                        max = Math.max(max, indices[i]);
                    }
                    Partition.heapSelectRange(data, 0, data.length - 1, min, max);
                    return extractIndices(data, indices);
                };
            }
            if (function == null) {
                throw new IllegalStateException("Unknown selector function: " + name);
            }
        }








        private static double[] extractIndices(double[] data, int[] indices) {
            final double[] x = new double[indices.length];
            for (int i = 0; i < indices.length; i++) {
                x[i] = data[indices[i]];
            }
            return x;
        }
    }




    @State(Scope.Benchmark)
    public static class IntKFunctionSource {

        @Param({SORT + JDK, SELECT})
        private String name;


        private BiFunction<int[], int[], int[]> function;




        public BiFunction<int[], int[], int[]> getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);

            if (name.equals(BASELINE)) {
                function = (data, indices) -> extractIndices(data, indices.clone());
            } else  if (name.startsWith(SORT)) {
                function = (data, indices) -> {
                    Arrays.sort(data);
                    return extractIndices(data, indices.clone());
                };
            } else if (name.startsWith(SELECT)) {
                function = (data, indices) -> {
                    Selection.select(data, indices.clone());
                    return extractIndices(data, indices);
                };
            }
            if (function == null) {
                throw new IllegalStateException("Unknown int selector function: " + name);
            }
        }








        private static int[] extractIndices(int[] data, int[] indices) {
            final int[] x = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                x[i] = data[indices[i]];
            }
            return x;
        }
    }








    @State(Scope.Benchmark)
    public static class SortNaNFunctionSource {

        @Param({"RawZeroNaN", "ZeroSignNaN", "NaNRawZero", "NaNZeroSign"})
        private String name;


        private BiConsumer<double[], Blackhole> function;




        public BiConsumer<double[], Blackhole> getFunction() {
            return function;
        }




        @Setup
        public void setup() {



            if ("RawZeroNaN".equals(name)) {
                function = (a, bh) -> {
                    int cn = 0;
                    int end = a.length;
                    for (int i = end; --i >= 0;) {
                        final double v = a[i];
                        if (Double.doubleToRawLongBits(v) == Long.MIN_VALUE) {
                            cn++;
                            a[i] = 0.0;
                        } else if (v != v) {
                            a[i] = a[--end];
                            a[end] = v;
                        }
                    }
                    bh.consume(cn);
                    bh.consume(end);
                };
            } else if ("ZeroSignNaN".equals(name)) {
                function = (a, bh) -> {
                    int cn = 0;
                    int end = a.length;
                    for (int i = end; --i >= 0;) {
                        final double v = a[i];
                        if (v == 0.0 && Double.doubleToRawLongBits(v) < 0) {
                            cn++;
                            a[i] = 0.0;
                        } else if (v != v) {
                            a[i] = a[--end];
                            a[end] = v;
                        }
                    }
                    bh.consume(cn);
                    bh.consume(end);
                };
            } else if ("NaNRawZero".equals(name)) {
                function = (a, bh) -> {
                    int cn = 0;
                    int end = a.length;
                    for (int i = end; --i >= 0;) {
                        final double v = a[i];
                        if (v != v) {
                            a[i] = a[--end];
                            a[end] = v;
                        } else if (Double.doubleToRawLongBits(v) == Long.MIN_VALUE) {
                            cn++;
                            a[i] = 0.0;
                        }
                    }
                    bh.consume(cn);
                    bh.consume(end);
                };
            } else if ("NaNZeroSign".equals(name)) {
                function = (a, bh) -> {
                    int cn = 0;
                    int end = a.length;
                    for (int i = end; --i >= 0;) {
                        final double v = a[i];
                        if (v != v) {
                            a[i] = a[--end];
                            a[end] = v;
                        } else if (v == 0.0 && Double.doubleToRawLongBits(v) < 0) {
                            cn++;
                            a[i] = 0.0;
                        }
                    }
                    bh.consume(cn);
                    bh.consume(end);
                };
            } else {
                throw new IllegalStateException("Unknown sort NaN function: " + name);
            }
        }
    }








    @State(Scope.Benchmark)
    public static class EdgeFunctionSource {


        @Param({HEAP_SELECT, ISP + "_EC0", IDP + "_EC0",



            })
        private String name;


        private BiFunction<double[], int[], double[]> function;




        public BiFunction<double[], int[], double[]> getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);















            if (HEAP_SELECT.equals(name)) {
                function = (data, indices) -> {
                    heapSelectRange0(data, 0, data.length - 1, indices[0], indices[1]);
                    return extractIndices(data, indices[0], indices[1]);
                };
            } else if ((HEAP_SELECT + "1").equals(name)) {
                function = (data, indices) -> {
                    heapSelectRange1(data, 0, data.length - 1, indices[0], indices[1]);
                    return extractIndices(data, indices[0], indices[1]);
                };
            } else if ((HEAP_SELECT + "2").equals(name)) {
                function = (data, indices) -> {
                    heapSelectRange2(data, 0, data.length - 1, indices[0], indices[1]);
                    return extractIndices(data, indices[0], indices[1]);
                };
            } else if ((HEAP_SELECT + "12").equals(name)) {
                function = (data, indices) -> {
                    heapSelectRange12(data, 0, data.length - 1, indices[0], indices[1]);
                    return extractIndices(data, indices[0], indices[1]);
                };

            } else if (SORT_SELECT.equals(name)) {
                function = (data, indices) -> {
                    Partition.sortSelectRange(data, 0, data.length - 1, indices[0], indices[1]);
                    return extractIndices(data, indices[0], indices[1]);
                };


            } else if (name.startsWith(ISP)) {
                final Partition part = PartitionFactory.createPartition(name, ISP);
                function = (data, indices) -> {
                    part.introselect(part.getSPFunction(), data,
                        0, data.length - 1, IndexIntervals.interval(indices[0], indices[1]), 10000);
                    return extractIndices(data, indices[0], indices[1]);
                };
            } else if (name.startsWith(IDP)) {
                final Partition part = PartitionFactory.createPartition(name, IDP);
                function = (data, indices) -> {
                    part.introselect(Partition::partitionDP, data,
                        0, data.length - 1, IndexIntervals.interval(indices[0], indices[1]), 10000);
                    return extractIndices(data, indices[0], indices[1]);
                };
            } else {
                throw new IllegalStateException("Unknown edge selector function: " + name);
            }
        }
















        static void heapSelectRange0(double[] a, int left, int right, int ka, int kb) {
            if (right - left < Partition.MIN_HEAPSELECT_SIZE) {
                Sorting.sort(a, left, right);
                return;
            }
            if (kb - left < right - ka) {
                Partition.heapSelectLeft(a, left, right, kb, kb - ka);
            } else {
                Partition.heapSelectRight(a, left, right, ka, kb - ka);
            }
        }
















        static void heapSelectRange1(double[] a, int left, int right, int ka, int kb) {
            if (right - left < Partition.MIN_HEAPSELECT_SIZE) {
                Sorting.sort(a, left, right);
                return;
            }
            if (kb - left < right - ka) {

                if (kb == left) {
                    Partition.selectMinIgnoreZeros(a, left, right);
                } else {
                    Partition.heapSelectLeft(a, left, right, kb, kb - ka);
                }
            } else {

                if (ka == right) {
                    Partition.selectMaxIgnoreZeros(a, left, right);
                } else {
                    Partition.heapSelectRight(a, left, right, ka, kb - ka);
                }
            }
        }
















        static void heapSelectRange2(double[] a, int left, int right, int ka, int kb) {
            if (right - left < Partition.MIN_HEAPSELECT_SIZE) {
                Sorting.sort(a, left, right);
                return;
            }
            if (kb - left < right - ka) {

                if (kb - 1 <= left) {
                    Partition.selectMin2IgnoreZeros(a, left, right);
                } else {
                    Partition.heapSelectLeft(a, left, right, kb, kb - ka);
                }
            } else {

                if (ka + 1 >= right) {
                    Partition.selectMax2IgnoreZeros(a, left, right);
                } else {
                    Partition.heapSelectRight(a, left, right, ka, kb - ka);
                }
            }
        }
















        static void heapSelectRange12(double[] a, int left, int right, int ka, int kb) {
            if (right - left < Partition.MIN_HEAPSELECT_SIZE) {
                Sorting.sort(a, left, right);
                return;
            }
            if (kb - left < right - ka) {

                if (kb - 1 <= left) {
                    if (kb == left) {
                        Partition.selectMinIgnoreZeros(a, left, right);
                    } else {
                        Partition.selectMin2IgnoreZeros(a, left, right);
                    }
                } else {
                    Partition.heapSelectLeft(a, left, right, kb, kb - ka);
                }
            } else {

                if (ka + 1 >= right) {
                    if (ka == right) {
                        Partition.selectMaxIgnoreZeros(a, left, right);
                    } else {
                        Partition.selectMax2IgnoreZeros(a, left, right);
                    }
                } else {
                    Partition.heapSelectRight(a, left, right, ka, kb - ka);
                }
            }
        }









        private static double[] extractIndices(double[] data, int l, int r) {
            final double[] x = new double[r - l + 1];
            for (int i = l; i <= r; i++) {
                x[i - l] = data[i];
            }
            return x;
        }
    }





    @State(Scope.Benchmark)
    public static class IndexSearchFunctionSource {

        @Param({"Binary",

            "Scan"})
        private String name;


        private SearchFunction function;




        public interface SearchFunction {








            int find(int[] a, int k);
        }




        public SearchFunction getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            Objects.requireNonNull(name);
            if ("Binary".equals(name)) {
                function = (keys, k) -> Partition.searchLessOrEqual(keys, 0, keys.length - 1, k);
            } else if ("binarySearch".equals(name)) {
                function = (keys, k) -> Arrays.binarySearch(keys, 0, keys.length, k);
            } else if ("Scan".equals(name)) {
                function = (keys, k) -> {

                    int i = keys.length;
                    do {
                        --i;
                    } while (keys[i] > k);
                    return i;
                };
            } else {
                throw new IllegalStateException("Unknown index search function: " + name);
            }
        }
    }








    @Benchmark
    public void sort(SortFunctionSource function, SortSource source, Blackhole bh) {
        final int size = source.size();
        final Consumer<double[]> fun = function.getFunction();
        for (int j = -1; ++j < size;) {
            final double[] y = source.getData(j);
            fun.accept(y);
            bh.consume(y);
        }
    }









    @Benchmark
    public void fiveSort(Sort5FunctionSource function, SortSource source, Blackhole bh) {
        final int size = source.size();
        final Consumer<double[]> fun = function.getFunction();
        for (int j = -1; ++j < size;) {
            final double[] y = source.getData(j);
            fun.accept(y);
            bh.consume(y);
        }
    }









    @Benchmark
    public void fourMedian(Median4FunctionSource function, SortSource source, Blackhole bh) {
        final int size = source.size();
        final Consumer<double[]> fun = function.getFunction();
        for (int j = -1; ++j < size;) {
            final double[] y = source.getData(j);
            fun.accept(y);
            bh.consume(y);
        }
    }









    @Benchmark
    public void threeMedian(Median3FunctionSource function, SortSource source, Blackhole bh) {
        final int size = source.size();
        final Consumer<double[]> fun = function.getFunction();
        for (int j = -1; ++j < size;) {
            final double[] y = source.getData(j);
            fun.accept(y);
            bh.consume(y);
        }
    }








    @Benchmark
    public void doublePartition(DoubleKFunctionSource function, KSource source, Blackhole bh) {
        final int size = source.size();
        final BiFunction<double[], int[], double[]> fun = function.getFunction();
        for (int j = -1; ++j < size;) {


            bh.consume(fun.apply(source.getData(j), source.getIndices(j)));
        }
    }








    @Benchmark
    public void intPartition(IntKFunctionSource function, KSource source, Blackhole bh) {
        final int size = source.size();
        final BiFunction<int[], int[], int[]> fun = function.getFunction();
        for (int j = -1; ++j < size;) {


            bh.consume(fun.apply(source.getIntData(j), source.getIndices(j)));
        }
    }









    @Benchmark
    public void edgeSelect(EdgeFunctionSource function, EdgeSource source, Blackhole bh) {
        final int size = source.size();
        final BiFunction<double[], int[], double[]> fun = function.getFunction();
        for (int j = -1; ++j < size;) {
            bh.consume(fun.apply(source.getData(j), source.getIndices(j)));
        }
    }








    @Benchmark
    public void nanZero(SortNaNFunctionSource function, SortSource source, Blackhole bh) {
        final int size = source.size();
        final BiConsumer<double[], Blackhole> fun = function.getFunction();
        for (int j = -1; ++j < size;) {
            fun.accept(source.getData(j), bh);
        }
    }








    @Benchmark
    public long indexSearch(IndexSearchFunctionSource function, SplitIndexSource source) {
        final IndexSearchFunctionSource.SearchFunction fun = function.getFunction();

        long sum = 0;
        for (int i = source.samples(); --i >= 0;) {

            sum += fun.find(source.getIndices(i), source.getPoint(i));
        }
        return sum;
    }































    @Benchmark
    public long searchableIntervalNextPrevious(SearchableIntervalSource function, SplitIndexSource source) {
        final int[][] indices = source.getIndices();
        final int[][] points = source.getPoints();

        long sum = 0;
        for (int i = 0; i < indices.length; i++) {
            final int[] x = indices[i];
            final int[] p = points[i];
            final SearchableInterval interval = function.create(x);
            for (final int k : p) {
                sum += interval.nextIndex(k);
                sum += interval.previousIndex(k);
            }
        }
        return sum;
    }















    @Benchmark
    public long searchableIntervalSplit(SearchableIntervalSource function, SplitIndexSource source) {
        final int[][] indices = source.getIndices();
        final int[][] points = source.getPoints();

        long sum = 0;
        final int[] bound = {0};
        for (int i = 0; i < indices.length; i++) {
            final int[] x = indices[i];
            final int[] p = points[i];


            final SearchableInterval interval = function.create(x);
            final int left = interval.left();
            final int right = interval.right();
            for (final int k : p) {

                if (left < k && k < right) {
                    sum += interval.split(k, k, bound);
                    sum += bound[0];
                }
            }
        }
        return sum;
    }
















    @Benchmark
    public void createSearchableInterval(SearchableIntervalSource function, IndexSource source, Blackhole bh) {
        final int[][] indices = source.getIndices();
        for (final int[] x : indices) {
            bh.consume(function.create(x));
        }
    }













    @Benchmark
    public void updatingIntervalSplit(UpdatingIntervalSource function, IndexSource source, Blackhole bh) {
        final int[][] indices = source.getIndices();
        final int s = source.getMinSeparation();
        for (int i = 0; i < indices.length; i++) {
            split(function.create(indices[i]), s, bh);
        }
    }










    private static void split(UpdatingInterval interval, int s, Blackhole bh) {
        int l = interval.left();
        final int r = interval.right();


        if (r - l > s) {
            final int middle = (l + r) >>> 1;

            split(interval.splitLeft(middle, middle), s, bh);

            l = interval.left();
        }
        bh.consume(interval);
    }
}
