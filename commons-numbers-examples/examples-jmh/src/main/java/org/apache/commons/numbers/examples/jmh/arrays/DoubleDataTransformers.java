















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;
import java.util.function.Supplier;






final class DoubleDataTransformers {


    private DoubleDataTransformers() {}












    static Supplier<DoubleDataTransformer> createFactory(NaNPolicy nanPolicy, boolean copy) {
        if (nanPolicy == NaNPolicy.ERROR) {
            return () -> new NaNErrorTransformer(copy);
        }

        final boolean includeNaN = nanPolicy == NaNPolicy.INCLUDE;
        return () -> new SortTransformer(includeNaN, copy);
    }





    private abstract static class ReplaceSignedZerosTransformer implements DoubleDataTransformer {

        protected int negativeZeroCount;

        @Override
        public void postProcess(double[] data, int[] k, int n) {

            if (negativeZeroCount != 0) {



                int j = -1;
                for (int i = 0; i < n; i++) {
                    if (data[k[i]] < 0) {
                        j = Math.max(j, k[i]);
                    }
                }
                for (int cn = negativeZeroCount;;) {
                    if (data[++j] == 0) {
                        data[j] = -0.0;
                        if (--cn == 0) {
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void postProcess(double[] data) {
            if (negativeZeroCount != 0) {

                int j = Arrays.binarySearch(data, 0.0);

                while (--j >= 0) {
                    if (data[j] != 0) {
                        break;
                    }
                }


                for (int cn = negativeZeroCount; --cn >= 0;) {
                    data[++j] = -0.0;
                }
            }
        }
    }





    private static final class SortTransformer extends ReplaceSignedZerosTransformer {

        private final boolean includeNaN;

        private final boolean copy;

        private int size;

        private int len;





        private SortTransformer(boolean includeNaN, boolean copy) {
            this.includeNaN = includeNaN;
            this.copy = copy;
        }

        @Override
        public double[] preProcess(double[] data) {
            final double[] a = copy ? data.clone() : data;

            int cn = 0;
            int end = a.length;
            for (int i = end; i > 0;) {
                final double v = a[--i];




                if (Double.doubleToRawLongBits(v) == Long.MIN_VALUE) {
                    cn++;


                    a[i] = 0.0;
                } else if (v != v) {

                    a[i] = a[--end];
                    a[end] = v;
                }
            }
            negativeZeroCount = cn;
            len = end;
            size = includeNaN ? a.length : len;
            return a;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int length() {
            return len;
        }
    }





    private static final class NaNErrorTransformer extends ReplaceSignedZerosTransformer {

        private final boolean copy;

        private int size;




        private NaNErrorTransformer(boolean copy) {
            this.copy = copy;
        }

        @Override
        public double[] preProcess(double[] data) {


            double[] a = data;

            int cn = 0;
            for (int i = a.length; i > 0;) {
                final double v = a[--i];

                if (Double.doubleToRawLongBits(v) == Long.MIN_VALUE) {
                    cn++;
                } else if (v != v) {
                    throw new IllegalArgumentException("NaN at: " + i);
                }
            }
            negativeZeroCount = cn;
            size = a.length;

            if (copy) {
                a = a.clone();
            }

            if (cn != 0) {
                for (int i = a.length; i > 0;) {
                    if (Double.doubleToRawLongBits(a[--i]) == Long.MIN_VALUE) {
                        a[i] = 0.0;
                        if (--cn == 0) {
                            break;
                        }
                    }
                }
            }
            return a;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int length() {
            return size;
        }
    }
}
