
















package org.apache.commons.numbers.combinatorics;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;






public final class Stirling {

    private static final String S1_ERROR_FORMAT = "s(n=%d, k=%d)";

    private static final String S2_ERROR_FORMAT = "S(n=%d, k=%d)";

    private static final int S1_OVERFLOW_K_EQUALS_1 = 21;

    private static final int S1_OVERFLOW_K_EQUALS_NM2 = 92682;

    private static final int S1_OVERFLOW_K_EQUALS_NM3 = 2761;

    private static final int S2_OVERFLOW_K_EQUALS_NM2 = 92683;

    private static final int S2_OVERFLOW_K_EQUALS_NM3 = 2762;





    private static final class StirlingS1Cache {



        static final int MAX_N = 21;

        static final long[][] S1;

        static {
            S1 = new long[MAX_N][];

            S1[0] = new long[] {1};
            S1[1] = new long[] {0, 1};
            for (int n = 2; n < S1.length; n++) {
                S1[n] = new long[n + 1];
                S1[n][0] = 0;
                S1[n][n] = 1;
                for (int k = 1; k < n; k++) {
                    S1[n][k] = S1[n - 1][k - 1] - (n - 1) * S1[n - 1][k];
                }
            }
        }
    }





    private static final class StirlingS2Cache {



        static final int MAX_N = 26;

        static final long[][] S2;

        static {
            S2 = new long[MAX_N][];
            S2[0] = new long[] {1};
            for (int n = 1; n < S2.length; n++) {
                S2[n] = new long[n + 1];
                S2[n][0] = 0;
                S2[n][1] = 1;
                S2[n][n] = 1;
                for (int k = 2; k < n; k++) {
                    S2[n][k] = k * S2[n - 1][k] + S2[n - 1][k - 1];
                }
            }
        }
    }


    private Stirling() {

    }















    public static long stirlingS1(int n, int k) {
        checkArguments(n, k);

        if (n < StirlingS1Cache.MAX_N) {

            return StirlingS1Cache.S1[n][k];
        }



        if (k == 0) {
            return 0;
        } else if (k == n) {
            return 1;
        } else if (k == 1) {
            checkN(n, k, S1_OVERFLOW_K_EQUALS_1, S1_ERROR_FORMAT);

            return Factorial.value(n - 1);
        } else if (k == n - 1) {
            return -BinomialCoefficient.value(n, 2);
        } else if (k == n - 2) {
            checkN(n, k, S1_OVERFLOW_K_EQUALS_NM2, S1_ERROR_FORMAT);

            return productOver4(3L * n - 1, BinomialCoefficient.value(n, 3));
        } else if (k == n - 3) {
            checkN(n, k, S1_OVERFLOW_K_EQUALS_NM3, S1_ERROR_FORMAT);
            return -BinomialCoefficient.value(n, 2) * BinomialCoefficient.value(n, 4);
        }









        final int reduction = Math.min(n - StirlingS1Cache.MAX_N, k - 2) + 1;
        int n0 = n - reduction;
        int k0 = k - reduction;

        long sum = stirlingS1(n0, k0);
        while (n0 < n) {
            k0++;
            sum = Math.subtractExact(
                sum,
                Math.multiplyExact(n0, stirlingS1(n0, k0))
            );
            n0++;
        }

        return sum;
    }















    public static long stirlingS2(int n, int k) {
        checkArguments(n, k);

        if (n < StirlingS2Cache.MAX_N) {

            return StirlingS2Cache.S2[n][k];
        }


        if (k == 0) {
            return 0;
        } else if (k == 1 || k == n) {
            return 1;
        } else if (k == 2) {
            checkN(n, k, 64, S2_ERROR_FORMAT);
            return (1L << (n - 1)) - 1L;
        } else if (k == n - 1) {
            return BinomialCoefficient.value(n, 2);
        } else if (k == n - 2) {
            checkN(n, k, S2_OVERFLOW_K_EQUALS_NM2, S2_ERROR_FORMAT);

            return productOver4(3L * n - 5, BinomialCoefficient.value(n, 3));
        } else if (k == n - 3) {
            checkN(n, k, S2_OVERFLOW_K_EQUALS_NM3, S2_ERROR_FORMAT);
            return BinomialCoefficient.value(n - 2, 2) * BinomialCoefficient.value(n, 4);
        }








        final int reduction = Math.min(n - StirlingS2Cache.MAX_N, k - 3) + 1;
        int n0 = n - reduction;
        int k0 = k - reduction;

        long sum = stirlingS2(n0, k0);
        while (n0 < n) {
            k0++;
            sum = Math.addExact(
                Math.multiplyExact(k0, stirlingS2(n0, k0)),
                sum
            );
            n0++;
        }

        return sum;
    }








    private static void checkArguments(int n, int k) {




        if ((n | k | (n - k)) < 0) {

            if (n < 0) {
                throw new CombinatoricsException(CombinatoricsException.NEGATIVE, n);
            }
            throw new CombinatoricsException(CombinatoricsException.OUT_OF_RANGE, k, 0, n);
        }
    }










    private static void checkN(int n, int k, int threshold, String msgFormat) {
        if (n > threshold) {
            throw new ArithmeticException(String.format(msgFormat, n, k));
        }
    }

















    private static long productOver4(long a, long b) {




        return (b & 1) == 0 ?
            ((b >>> 1) * a) >>> 1 :
            ((a >>> 1) * b) >>> 1;
    }


































    public static final class S2 {

        private final int numberOfSubsets;

        private final long stirlingS2;

        private final int numberOfElements;

        private final int nMinusK;

        private final int nMinusOne;

        private final int kMinusOne;








        private S2(int n,
                   int k) {
            stirlingS2 = stirlingS2(n, k);
            numberOfElements = n;
            numberOfSubsets = k;
            nMinusK = n - k;
            nMinusOne = n - 1;
            kMinusOne = k - 1;
        }








        public static S2 of(int n,
                            int k) {
            return new S2(n, k);
        }






        public long get() {
            return stirlingS2;
        }







        public Stream<int[][]> stream() {
            return streamInternal(partitionGenerator());
        }











        public static <T> Stream<List<List<T>>> stream(List<T> items,
                                                       int k) {
            return of(items.size(), k).stream().map(o -> mapPartition(o, items, k));
        }







        public Iterable<int[][]> partitionGenerator() {
            return new Iterable<int[][]>() {

                @Override
                public Iterator<int[][]> iterator() {
                    return new PartitionIterator();
                }
            };
        }



























        private <T> Stream<T> streamInternal(Iterable<T> gen) {
            final int characteristics = Spliterator.DISTINCT | Spliterator.NONNULL;
            return StreamSupport.stream(Spliterators.spliterator(gen.iterator(),
                                                                 stirlingS2,
                                                                 characteristics),
                                        false);
        }











        private static <T> List<List<T>> mapPartition(int[][] p,
                                                      List<T> items,
                                                      int k) {
            final List<List<T>> out = new ArrayList<>(k);

            for (int[] subset : p) {
                final List<T> customSubset = new ArrayList<>(subset.length);

                for (int i : subset) {
                    customSubset.add(items.get(i));
                }

                out.add(customSubset);
            }

            return out;
        }




        private final class PartitionIterator implements Iterator<int[][]> {

            private final RestrictedGrowthStringIterator delegate = new RestrictedGrowthStringIterator();


            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }


            @Override
            public int[][] next() {
                return rgs2partition(delegate.next());
            }







            private int[][] rgs2partition(int[] rgs) {

                final int[] sizes = new int[numberOfSubsets];
                for (int i = 0; i < numberOfElements; i++) {
                    ++sizes[rgs[i]];
                }

                final int[][] out = new int[numberOfSubsets][];
                for (int i = 0; i < numberOfSubsets; i++) {
                    out[i] = new int[sizes[i]];
                }

                final int[] counts = new int[numberOfSubsets];
                for (int i = 0; i < numberOfElements; i++) {
                    final int groupIdx = rgs[i];
                    out[groupIdx][counts[groupIdx]++] = i;
                }

                return out;
            }
        }




        private final class RestrictedGrowthStringIterator implements Iterator<int[]> {

            private final int[] rgs = new int[numberOfElements];

            private final int[] maxRgs = new int[numberOfElements];

            private final int[] currentRGS = new int[numberOfElements];

            private long partitionCount = 0;

            private boolean hasNext = true;




                                  RestrictedGrowthStringIterator() {

                for (int i = nMinusK + 1; i < numberOfElements; i++) {
                    rgs[i] = i - nMinusK;
                }
                for (int i = 1; i < numberOfElements; i++) {
                    final int iMinusOne = i - 1;
                    maxRgs[i] = Math.max(maxRgs[iMinusOne],
                                         rgs[iMinusOne]);
                }

                calculateNext();
            }


            @Override
            public boolean hasNext() {
                return hasNext;
            }


            @Override
            public int[] next() {
                if (!hasNext) {
                    throw new NoSuchElementException();
                }


                final int[] c = Arrays.copyOf(currentRGS, numberOfElements);

                calculateNext();

                return c;
            }


            private void calculateNext() {
                hasNext = partitionCount < stirlingS2;

                if (numberOfElements > 0) {
                    while (true) {
                        if (maxRgs[nMinusOne] == kMinusOne ||
                            rgs[nMinusOne] == kMinusOne) {
                            build();
                            updateRgs();
                            return;
                        }

                        updateRgs();
                    }
                } else {
                    build();
                }
            }


            private void updateRgs() {
                int i = nMinusOne;
                while (i > 0) {
                    if (rgs[i] < kMinusOne &&
                        rgs[i] <= maxRgs[i]) {
                        ++rgs[i];
                        break;
                    }
                    --i;
                }

                if (i == 0) {
                    return;
                }

                Arrays.fill(rgs, i + 1, numberOfElements, 0);

                for (int j = i; j < numberOfElements; j++) {
                    final int jMinusOne = j - 1;
                    maxRgs[j] = Math.max(maxRgs[jMinusOne],
                                         rgs[jMinusOne]);
                }
            }




            private void build() {
                System.arraycopy(rgs, 0, currentRGS, 0, numberOfElements);


                ++partitionCount;
            }
        }
    }
}
