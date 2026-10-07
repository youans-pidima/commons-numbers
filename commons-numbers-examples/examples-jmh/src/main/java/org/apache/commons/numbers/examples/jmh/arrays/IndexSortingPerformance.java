
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx4096M"})
public class IndexSortingPerformance {

    private static final String INSERTION = "Insertion";

    private static final String BINARY_SEARCH = "BinarySearch";

    private static final String HEAP = "Heap";

    private static final String SORT_UNIQUE = "SortUnique";


    private static final String INDEX_SET = "IndexSet";

    private static final String HASH_INDEX_SET = "HashIndexSet";

    private static final String HYBRID = "Hybrid";




    interface IndexSort {







        int sort(int[] a, int n);
    }




    @State(Scope.Benchmark)
    public static class IndexDataSource {

        @Param({
            "10",
            "100",

            })
        private int n;

        @Param({

            "10",

            })
        private double range;

        @Param({

            "1",
            "2"
            })
        private double duplication;

        @Param({"100"})
        private int samples;



        @Param({"false"})
        private boolean ascending;



        private int[][] data;




        public int[][] getData() {
            return data;
        }




        @Setup(Level.Iteration)
        public void setup() {

            final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();


            final int length = (int) Math.floor(n * range);

            final int extra = (int) Math.floor(n * duplication);

            data = new int[samples][];
            for (int i = 0; i < samples; i++) {
                final int[] indices = new int[n + extra];

                for (int j = 0; j < n; j++) {
                    indices[j] = rng.nextInt(length);
                }

                for (int j = 0; j < extra; j++) {
                    indices[j + n] = indices[rng.nextInt(n)];
                }




                final int i1 = rng.nextInt(indices.length);

                final int i2 = (i1 + rng.nextInt(indices.length - 1)) % indices.length;
                indices[i1] = 0;
                indices[i2] = length - 1;
                if (ascending) {
                    Arrays.sort(indices);
                }
                data[i] = indices;
            }
        }
    }




    @State(Scope.Benchmark)
    public static class IndexSortSource {

        @Param({

            INSERTION,









            INDEX_SET, HASH_INDEX_SET,

            HYBRID})
        private String name;


        private IndexSort function;




        public IndexSort getFunction() {
            return function;
        }




        @Setup
        public void setup() {



            Objects.requireNonNull(name);
            if (INSERTION.equals(name)) {
                function = Sorting::sortIndicesInsertionSort;
            } else if (BINARY_SEARCH.equals(name)) {
                function = Sorting::sortIndicesBinarySearch;
            } else if (HEAP.equals(name)) {
                function = Sorting::sortIndicesHeapSort;
            } else if (SORT_UNIQUE.equals(name)) {
                function = Sorting::sortIndicesSort;
            } else if (INDEX_SET.equals(name)) {
                function = Sorting::sortIndicesIndexSet;
            } else if ((INDEX_SET + "2").equals(name)) {
                function = Sorting::sortIndicesIndexSet2;
            } else if (HASH_INDEX_SET.equals(name)) {
                function = Sorting::sortIndicesHashIndexSet;
            } else if (HYBRID.equals(name)) {
                function = Sorting::sortIndices;
            } else {
                throw new IllegalStateException("Unknown sort function: " + name);
            }
        }
    }








    @Benchmark
    public void indexSort(IndexSortSource function, IndexDataSource source, Blackhole bh) {
        for (final int[] a : source.getData()) {
            bh.consume(function.getFunction().sort(a.clone(), a.length));
        }
    }
}
