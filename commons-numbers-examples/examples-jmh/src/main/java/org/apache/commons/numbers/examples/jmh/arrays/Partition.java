
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

































































































final class Partition {




    static final PivotingStrategy PIVOTING_STRATEGY = PivotingStrategy.DYNAMIC;












    static final DualPivotingStrategy DUAL_PIVOTING_STRATEGY = DualPivotingStrategy.SORT_5B;



















    static final int MIN_QUICKSELECT_SIZE = 0;



    static final int MIN_HEAPSELECT_SIZE = 5;



    static final int MIN_SORTSELECT_SIZE = 4;


    static final int EDGESELECT_CONSTANT = 20;








    static final int LINEAR_SORTSELECT_SIZE = 24;



    static final int SUBSAMPLING_SIZE = Integer.MAX_VALUE;

    static final KeyStrategy KEY_STRATEGY = KeyStrategy.INDEX_SET;

    static final PairedKeyStrategy PAIRED_KEY_STRATEGY = PairedKeyStrategy.SEARCHABLE_INTERVAL;

    static final int RECURSION_MULTIPLE = 2;

    static final int RECURSION_CONSTANT = 0;

    static final int COMPRESSION_LEVEL = 1;

    static final int CONTROL_FLAGS = 0;

    static final int OPTION_FLAGS = 0;

    static final SPStrategy SP_STRATEGY = SPStrategy.KBM;


    static final ExpandStrategy EXPAND_STRATEGY = ExpandStrategy.T2;

    static final LinearStrategy LINEAR_STRATEGY = LinearStrategy.RSA;

    static final EdgeSelectStrategy EDGE_STRATEGY = EdgeSelectStrategy.ESS;

    static final StopperStrategy STOPPER_STRATEGY = StopperStrategy.SQA;

    static final AdaptMode ADAPT_MODE = AdaptMode.ADAPT3;


    static final int MODE_FR_SAMPLING = -1;

    static final int MODE_SAMPLING = 0;

    static final int MODE_ADAPTION = 1;

    static final int MODE_STRICT = 2;




    static final int FLAG_RANDOM_SAMPLING = 0x2;

    static final int FLAG_MOVE_SAMPLE = 0x4;


    static final int FLAG_SUBSET_SAMPLING = 0x8;




    static final int FLAG_BIASED_RANDOM = 0x1000;

    static final int FLAG_SPLITTABLE_RANDOM = 0x2000;

    static final int FLAG_MSWS = 0x4000;





    static final int FLAG_QA_PROPAGATE = 0x1;

    static final int FLAG_QA_RANDOM_SAMPLING = 0x4;









    static final int FLAG_QA_FAR_STEP = 0x8;













    static final int FLAG_QA_FAR_STEP_ADAPT_ORIGINAL = 0x10;



    static final int FLAG_QA_MIDDLE_12 = 0x20;



    static final int FLAG_QA_SAMPLE_K = 0x40;








    static final int SELECT_SUB_SAMPLING_SIZE = 1200;


    private static final String UNSUPPORTED_INTROSELECT = "Unsupported introselect: ";



    private static final Supplier<DoubleDataTransformer> SORT_TRANSFORMER =
        DoubleDataTransformers.createFactory(NaNPolicy.INCLUDE, false);


    private static final int SORT_BETWEEN_SIZE = 2;

    private static final double LOG2_E = 1.4426950408889634;


    private static final double STEP_LEFT = 0.4375;

    private static final double STEP_RIGHT = 0.5625;

    private static final double STEP_FAR_LEFT = 0.08333333333333333;

    private static final double STEP_FAR_RIGHT = 0.9166666666666666;


    private static int qaMode = MODE_FR_SAMPLING;

    private static int qaIncrement = 1;




    private final PivotingStrategy pivotingStrategy;

    private final DualPivotingStrategy dualPivotingStrategy;








    private final int minQuickSelectSize;

    private final int edgeSelectConstant;



    private int linearSortSelectSize = LINEAR_SORTSELECT_SIZE;





















































    private final int subSamplingSize;




    private KeyStrategy keyStrategy = KEY_STRATEGY;

    private PairedKeyStrategy pairedKeyStrategy = PAIRED_KEY_STRATEGY;







    private double recursionMultiple = RECURSION_MULTIPLE;




    private int recursionConstant = RECURSION_CONSTANT;

    private int compression = COMPRESSION_LEVEL;

    private int controlFlags = CONTROL_FLAGS;


    private IntConsumer recursionConsumer = i -> {             };


    private SPEPartition spFunction;

    private ExpandPartition expandFunction;

    private SPEPartition linearSpFunction;

    private SelectFunction edgeSelection;

    private SelectFunction stopperSelection;

    private AdaptMode adaptMode = ADAPT_MODE;


    private MapDistance samplingAdapt;


    private MapDistance samplingEdgeAdapt;

    private MapDistance noSamplingAdapt;


    private MapDistance noSamplingEdgeAdapt;




    enum KeyStrategy {

        SEQUENTIAL,


        INDEX_SET,


        COMPRESSED_INDEX_SET,

        PIVOT_CACHE,


        ORDERED_KEYS,

        SCANNING_KEY_SEARCHABLE_INTERVAL,

        SEARCH_KEY_SEARCHABLE_INTERVAL,

        INDEX_ITERATOR,

        COMPRESSED_INDEX_ITERATOR,

        INDEX_SET_UPDATING_INTERVAL,

        KEY_UPDATING_INTERVAL,

        INDEX_SET_SPLITTING_INTERVAL,

        KEY_SPLITTING_INTERVAL;
    }




    enum PairedKeyStrategy {


        PAIRED_KEYS,





        PAIRED_KEYS_2,


        PAIRED_KEYS_LEN,


        TWO_KEYS,





        KEY_RANGE,


        SEARCHABLE_INTERVAL,


        UPDATING_INTERVAL;
    }








































    enum SPStrategy {





        SP,





        BM,





        SBM,







        KBM,





        DNF1,






        DNF2,







        DNF3;
    }















    enum ExpandStrategy {



        PER,



        T1,


        T2,


        B1,


        B2,
    }

















    enum LinearStrategy {




        BFPRT,




        RS,




        BFPRT_IM,




        RS_IM,





        BFPRTA,





        RSA;
    }






    enum EdgeSelectStrategy {



        ESH,



        ESH2,


        ESS,



        ESS2;
    }








    enum StopperStrategy {




        SSH,



        SSH2,




        SLS,

        SQA;
    }











    private interface PartitionFunction {


















        void partition(double[] a, int left, int right, int ka, int kb,
            boolean leftInner, boolean rightInner);






















        void partitionSequential(double[] a, int left, int right, int ka, int kb,
            boolean leftInner, boolean rightInner, PivotStore pivots);





















        void partition(double[] a, int left, int right, int ka, int kb,
            boolean leftInner, boolean rightInner, PivotStore pivots);










        void sort(double[] a, int left, int right, boolean leftInner, boolean rightInner);
    }




    @FunctionalInterface
    private interface SPEPartitionFunction extends PartitionFunction {























        int partition(double[] a, int left, int right, int[] upper,
            boolean leftInner, boolean rightInner);





        @Override
        default void partition(double[] a, int left, int right, int ka, int kb,
            boolean leftInner, boolean rightInner) {

            if (right - left < 1) {
                return;
            }












            if (ka == right) {
                selectMax(a, left, ka);
            } else if (kb == left) {
                selectMin(a, kb, right);
            } else {
                final int[] upper = {0};
                final int k0 = partition(a, left, right, upper, leftInner, rightInner);
                final int k1 = upper[0];


                if (ka < k0) {
                    partition(a, left, k0 - 1, ka, kb, leftInner, true);
                }
                if (kb > k1) {
                    partition(a, k1 + 1, right, ka, kb, true, rightInner);
                }
            }
        }

        @Override
        default void partitionSequential(double[] a, int left, int right, int ka, int kb,
            boolean leftInner, boolean rightInner, PivotStore pivots) {



            if (right - left < 1) {
                return;
            }
            if (ka == right) {
                selectMax(a, left, ka);
                pivots.add(ka);
            } else if (kb == left) {
                selectMin(a, kb, right);
                pivots.add(kb);
            } else {
                final int[] upper = {0};
                final int k0 = partition(a, left, right, upper, leftInner, rightInner);
                final int k1 = upper[0];


                pivots.add(k0, k1);

                if (ka < k0) {
                    if (k0 - 1 < kb) {

                        partition(a, left, k0 - 1, ka, kb, leftInner, true);
                    } else {
                        partitionSequential(a, left, k0 - 1, ka, kb, leftInner, true, pivots);
                    }
                }
                if (kb > k1) {
                    partitionSequential(a, k1 + 1, right, ka, kb, true, rightInner, pivots);
                }
            }
        }

        @Override
        default void partition(double[] a, int left, int right, int ka, int kb,
            boolean leftInner, boolean rightInner, PivotStore pivots) {



            if (right - left < 1) {
                return;
            }
            if (ka == right) {
                selectMax(a, left, ka);
                pivots.add(ka);
            } else if (kb == left) {
                selectMin(a, kb, right);
                pivots.add(kb);
            } else {
                final int[] upper = {0};
                final int k0 = partition(a, left, right, upper, leftInner, rightInner);
                final int k1 = upper[0];


                pivots.add(k0, k1);

                if (ka < k0) {
                    partition(a, left, k0 - 1, ka, kb, leftInner, true, pivots);
                }
                if (kb > k1) {
                    partition(a, k1 + 1, right, ka, kb, true, rightInner, pivots);
                }
            }
        }

        @Override
        default void sort(double[] a, int left, int right, boolean leftInner, boolean rightInner) {



            if (right - left < 1) {
                return;
            }
            final int[] upper = {0};
            final int k0 = partition(a, left, right, upper, leftInner, rightInner);
            final int k1 = upper[0];


            sort(a, left, k0 - 1, leftInner, true);
            sort(a, k1 + 1, right, true, rightInner);
        }
    }




    @FunctionalInterface
    interface SPEPartition {






















        int partition(double[] a, int left, int right, int pivot, int[] upper);
    }




    @FunctionalInterface
    interface DPPartition {
































        int partition(double[] a, int left, int right, int pivot1, int pivot2, int[] bounds);
    }








    @FunctionalInterface
    interface SelectFunction {










        void partition(double[] a, int left, int right, int ka, int kb);
    }




    @FunctionalInterface
    interface ExpandPartition {































        int partition(double[] a, int left, int right, int start, int end,
            int pivot0, int pivot1, int[] upper);
    }







    enum MapDistance {

        MEDIAN {
            @Override
            int mapDistance(int d, int l, int r, int n) {
                return n >>> 1;
            }
        },

        ADAPT {
            @Override
            int mapDistance(int d, int l, int r, int n) {

                return (int) (d * (n - 1.0) / (r - l));
            }
        },

        HALF_ADAPT {
            @Override
            int mapDistance(int d, int l, int r, int n) {

                final int x = ADAPT.mapDistance(d, l, r, n);

                final int m = n >>> 1;
                return (m + x) >>> 1;
            }
        },










        EDGE_ADAPT {
            @Override
            int mapDistance(int d, int l, int r, int n) {
                return d >>> 1;
            }
        };














        abstract int mapDistance(int d, int l, int r, int n);
    }










































    enum AdaptMode {


        FIXED {
            @Override
            boolean isSampleMode() {
                return false;
            }
            @Override
            boolean isAdapt() {
                return false;
            }
            @Override
            AdaptMode update(int size, int l, int r) {

                return this;
            }
        },


        ADAPT1 {
            @Override
            boolean isSampleMode() {
                return true;
            }
            @Override
            boolean isAdapt() {
                return true;
            }
            @Override
            AdaptMode update(int size, int l, int r) {
                return r - l <= size ? this : ADAPT1B;
            }
        },

        ADAPT1B {
            @Override
            boolean isSampleMode() {
                return false;
            }
            @Override
            boolean isAdapt() {
                return true;
            }
            @Override
            AdaptMode update(int size, int l, int r) {

                return this;
            }
        },


        ADAPT2 {
            @Override
            boolean isSampleMode() {
                return true;
            }
            @Override
            boolean isAdapt() {
                return true;
            }
            @Override
            AdaptMode update(int size, int l, int r) {
                return r - l <= size ? this : FIXED;
            }
        },


        ADAPT3 {
            @Override
            boolean isSampleMode() {
                return true;
            }
            @Override
            boolean isAdapt() {
                return true;
            }
            @Override
            AdaptMode update(int size, int l, int r) {
                return r - l <= size ? this : ADAPT3B;
            }
        },


        ADAPT3B {
            @Override
            boolean isSampleMode() {
                return false;
            }
            @Override
            boolean isAdapt() {
                return true;
            }
            @Override
            AdaptMode update(int size, int l, int r) {
                return r - l <= size ? this : FIXED;
            }
        },


        ADAPT4 {
            @Override
            boolean isSampleMode() {
                return true;
            }
            @Override
            boolean isAdapt() {
                return false;
            }
            @Override
            AdaptMode update(int size, int l, int r) {
                return r - l <= size ? this : FIXED;
            }
        };






        abstract boolean isSampleMode();






        abstract boolean isAdapt();












        abstract AdaptMode update(int size, int l, int r);
    }




    Partition() {
        this(PIVOTING_STRATEGY, DUAL_PIVOTING_STRATEGY, MIN_QUICKSELECT_SIZE,
            EDGESELECT_CONSTANT, SUBSAMPLING_SIZE);
    }









    Partition(PivotingStrategy pivotingStrategy, int minQuickSelectSize) {
        this(pivotingStrategy, DUAL_PIVOTING_STRATEGY, minQuickSelectSize,
            EDGESELECT_CONSTANT, SUBSAMPLING_SIZE);
    }









    Partition(DualPivotingStrategy dualPivotingStrategy, int minQuickSelectSize) {
        this(PIVOTING_STRATEGY, dualPivotingStrategy, minQuickSelectSize,
            EDGESELECT_CONSTANT, SUBSAMPLING_SIZE);
    }











    Partition(PivotingStrategy pivotingStrategy,
        int minQuickSelectSize, int edgeSelectConstant, int subSamplingSize) {
        this(pivotingStrategy, DUAL_PIVOTING_STRATEGY, minQuickSelectSize, edgeSelectConstant,
            subSamplingSize);
    }










    Partition(DualPivotingStrategy dualPivotingStrategy,
        int minQuickSelectSize, int edgeSelectConstant) {
        this(PIVOTING_STRATEGY, dualPivotingStrategy, minQuickSelectSize,
            edgeSelectConstant, SUBSAMPLING_SIZE);
    }










    Partition(PivotingStrategy pivotingStrategy, DualPivotingStrategy dualPivotingStrategy,
        int minQuickSelectSize, int edgeSelectConstant, int subSamplingSize) {
        this.pivotingStrategy = pivotingStrategy;
        this.dualPivotingStrategy = dualPivotingStrategy;
        this.minQuickSelectSize = minQuickSelectSize;
        this.edgeSelectConstant = edgeSelectConstant;
        this.subSamplingSize = subSamplingSize;

        setSPStrategy(SP_STRATEGY);
        setEdgeSelectStrategy(EDGE_STRATEGY);
        setStopperStrategy(STOPPER_STRATEGY);
        setExpandStrategy(EXPAND_STRATEGY);
        setLinearStrategy(LINEAR_STRATEGY);

        setControlFlags(0);
    }







    Partition setSPStrategy(SPStrategy v) {
        switch (v) {
        case BM:
            spFunction = Partition::partitionBM;
            break;
        case DNF1:
            spFunction = Partition::partitionDNF1;
            break;
        case DNF2:
            spFunction = Partition::partitionDNF2;
            break;
        case DNF3:
            spFunction = Partition::partitionDNF3;
            break;
        case KBM:
            spFunction = Partition::partitionKBM;
            break;
        case SBM:
            spFunction = Partition::partitionSBM;
            break;
        case SP:
            spFunction = Partition::partitionSP;
            break;
        default:
            throw new IllegalArgumentException("Unknown single-pivot strategy: " + v);
        }
        return this;
    }







    Partition setExpandStrategy(ExpandStrategy v) {
        switch (v) {
        case PER:

            expandFunction = (a, left, right, start, end, pivot0, pivot1, upper) ->
                spFunction.partition(a, left, right, (pivot0 + pivot1) >>> 1, upper);
            break;
        case T1:
            expandFunction = Partition::expandPartitionT1;
            break;
        case B1:
            expandFunction = Partition::expandPartitionB1;
            break;
        case T2:
            expandFunction = Partition::expandPartitionT2;
            break;
        case B2:
            expandFunction = Partition::expandPartitionB2;
            break;
        default:
            throw new IllegalArgumentException("Unknown expand strategy: " + v);
        }
        return this;
    }












    Partition setLinearStrategy(LinearStrategy v) {
        switch (v) {
        case BFPRT:
            linearSpFunction = this::linearBFPRTBaseline;
            break;
        case RS:
            linearSpFunction = this::linearRepeatedStepBaseline;
            break;
        case BFPRT_IM:
            noSamplingAdapt = MapDistance.MEDIAN;
            linearSpFunction = this::linearBFPRTImproved;
            break;
        case BFPRTA:

            noSamplingAdapt = MapDistance.ADAPT;
            linearSpFunction = this::linearBFPRTImproved;
            break;
        case RS_IM:
            noSamplingAdapt = MapDistance.MEDIAN;
            linearSpFunction = this::linearRepeatedStepImproved;
            break;
        case RSA:

            noSamplingAdapt = MapDistance.ADAPT;
            linearSpFunction = this::linearRepeatedStepImproved;
            break;
        default:
            throw new IllegalArgumentException("Unknown linear strategy: " + v);
        }
        return this;
    }







    Partition setEdgeSelectStrategy(EdgeSelectStrategy v) {
        switch (v) {
        case ESH:
            edgeSelection = Partition::heapSelectRange;
            break;
        case ESH2:
            edgeSelection = Partition::heapSelectRange2;
            break;
        case ESS:
            edgeSelection = Partition::sortSelectRange;
            break;
        case ESS2:
            edgeSelection = Partition::sortSelectRange2;
            break;
        default:
            throw new IllegalArgumentException("Unknown edge select: " + v);
        }
        return this;
    }







    Partition setStopperStrategy(StopperStrategy v) {
        switch (v) {
        case SSH:
            stopperSelection = Partition::heapSelectRange;
            break;
        case SSH2:
            stopperSelection = Partition::heapSelectRange2;
            break;
        case SLS:



            stopperSelection = (a, l, r, ka, kb) -> linearSelect(getSPFunction(),
                a, l, r, ka, kb, new int[2]);
            break;
        case SQA:



            stopperSelection = (a, l, r, ka, kb) -> quickSelectAdaptive(a, l, r, ka, kb, new int[1],
                adaptMode);
            break;
        default:
            throw new IllegalArgumentException("Unknown stopper: " + v);
        }
        return this;
    }







    Partition setKeyStrategy(KeyStrategy v) {
        this.keyStrategy = v;
        return this;
    }







    Partition setPairedKeyStrategy(PairedKeyStrategy v) {
        this.pairedKeyStrategy = v;
        return this;
    }







    Partition setRecursionMultiple(double v) {
        this.recursionMultiple = v;
        return this;
    }







    Partition setRecursionConstant(int v) {
        this.recursionConstant = v;
        return this;
    }







    Partition setCompression(int v) {
        if (v < 1 || v > Integer.SIZE - 1) {
            throw new IllegalArgumentException("Bad compression: " + v);
        }
        this.compression = v;
        return this;
    }







    Partition setControlFlags(int v) {
        this.controlFlags = v;




        if ((v & FLAG_QA_FAR_STEP_ADAPT_ORIGINAL) != 0) {


            samplingAdapt = samplingEdgeAdapt = noSamplingAdapt = noSamplingEdgeAdapt = MapDistance.ADAPT;
        } else {

            samplingAdapt = MapDistance.ADAPT;
            if ((v & FLAG_QA_FAR_STEP) != 0) {



                samplingEdgeAdapt = MapDistance.ADAPT;
            } else {







                samplingEdgeAdapt = MapDistance.MEDIAN;
            }
            noSamplingAdapt = MapDistance.ADAPT;

            noSamplingEdgeAdapt = MapDistance.EDGE_ADAPT;
        }
        return this;
    }










    Partition setLinearSortSelectSize(int v) {
        if (v < 1) {
            throw new IllegalArgumentException("Bad linear sortselect size: " + v);
        }
        this.linearSortSelectSize = v;
        return this;
    }







    Partition setAdaptMode(AdaptMode v) {
        this.adaptMode = v;
        return this;
    }








    void setRecursionConsumer(IntConsumer v) {
        this.recursionConsumer = Objects.requireNonNull(v);
    }






    SPEPartition getSPFunction() {
        return spFunction;
    }








    static void configureQaAdaptive(int mode, int increment) {
        qaMode = mode;
        qaIncrement = increment;
    }












    static void selectMin(double[] data, int left, int right) {
        selectMinIgnoreZeros(data, left, right);

        if (data[left] == 0) {
            minZero(data, left, right);
        }
    }












    static void selectMax(double[] data, int left, int right) {
        selectMaxIgnoreZeros(data, left, right);

        if (data[right] == 0) {
            maxZero(data, left, right);
        }
    }











    private static void minZero(double[] data, int left, int right) {

        if (Double.doubleToRawLongBits(data[left]) >= 0) {


            for (int k = left; ++k <= right;) {
                if (data[k] == 0 && Double.doubleToRawLongBits(data[k]) < 0) {
                    data[k] = 0.0;
                    data[left] = -0.0;
                    break;
                }
            }
        }
    }











    private static void maxZero(double[] data, int left, int right) {

        if (Double.doubleToRawLongBits(data[right]) < 0) {


            for (int k = right; --k >= left;) {
                if (data[k] == 0 && Double.doubleToRawLongBits(data[k]) >= 0) {
                    data[k] = -0.0;
                    data[right] = 0.0;
                    break;
                }
            }
        }
    }










    static void selectMinIgnoreZeros(double[] data, int left, int right) {

        double min = data[left];
        for (int i = right + 1; --i > left;) {
            final double v = data[i];
            if (v < min) {
                data[i] = min;
                min = v;
            }
        }
        data[left] = min;
    }










    static void selectMin2IgnoreZeros(double[] data, int left, int right) {
        double min1 = data[left + 1];
        if (min1 < data[left]) {
            min1 = data[left];
            data[left] = data[left + 1];
        }

        for (int i = right + 1, end = left + 1; --i > end;) {
            final double v = data[i];
            if (v < min1) {
                data[i] = min1;
                if (v < data[left]) {
                    min1 = data[left];
                    data[left] = v;
                } else {
                    min1 = v;
                }
            }
        }
        data[left + 1] = min1;
    }










    static void selectMaxIgnoreZeros(double[] data, int left, int right) {

        double max = data[right];
        for (int i = left - 1; ++i < right;) {
            final double v = data[i];
            if (v > max) {
                data[i] = max;
                max = v;
            }
        }
        data[right] = max;
    }










    static void selectMax2IgnoreZeros(double[] data, int left, int right) {
        double max1 = data[right - 1];
        if (max1 > data[right]) {
            max1 = data[right];
            data[right] = data[right - 1];
        }

        for (int i = left - 1, end = right - 1; ++i < end;) {
            final double v = data[i];
            if (v > max1) {
                data[i] = max1;
                if (v > data[right]) {
                    max1 = data[right];
                    data[right] = v;
                } else {
                    max1 = v;
                }
            }
        }
        data[right - 1] = max1;
    }








    static void heapSort(double[] a, int left, int right) {

        heapSelectLeft(a, left, right, right, right - left);
    }


















    static void heapSelectPair(double[] a, int left, int right, int ka, int kb) {

        if (right - left < MIN_HEAPSELECT_SIZE) {
            Sorting.sort(a, left, right);
            return;
        }







        final int d1 = ka - left;
        final int d2 = kb - left;
        final int d3 = right - kb;
        final int d4 = right - ka;
        if (d1 + d3 < Math.min(d2, d4)) {




            heapSelectLeft(a, left, right, ka, 0);

            heapSelectRight(a, ka + 1, right, kb, 0);
        } else if (d2 < d4) {
            heapSelectLeft(a, left, right, kb, kb - ka);
        } else {

            heapSelectRight(a, left, right, ka, kb - ka);
        }
    }












    static void heapSelectRange(double[] a, int left, int right, int ka, int kb) {


        if (right - left < MIN_HEAPSELECT_SIZE) {
            Sorting.sort(a, left, right);
            return;
        }








        if (kb - left < right - ka) {
            heapSelectLeft(a, left, right, kb, kb - ka);
        } else {
            heapSelectRight(a, left, right, ka, kb - ka);
        }
    }



















    static void heapSelectLeft(double[] a, int left, int right, int k, int count) {





        int end = k + 1;
        for (int p = left + ((k - left - 1) >> 1); p >= left; p--) {
            maxHeapSiftDown(a, a[p], p, left, end);
        }


        double max = a[left];
        for (int i = right + 1; --i > k;) {
            final double v = a[i];
            if (v < max) {
                a[i] = max;
                maxHeapSiftDown(a, v, left, left, end);
                max = a[left];
            }
        }









        a[left] = a[k];
        a[k] = max;

        if (count > 0) {
            --end;

            for (int c = Math.min(count, end - left - 1); --c >= 0;) {
                maxHeapSiftDown(a, a[left], left, left, end--);

                max = a[left];
                a[left] = a[end];
                a[end] = max;
            }
        }
    }












    private static void maxHeapSiftDown(double[] a, double v, int p, int root, int end) {


        while (true) {

            int c = (p << 1) - root + 2;
            if (c > end) {

                break;
            }

            if (c == end || a[c] < a[c - 1]) {
                --c;
            }
            if (v >= a[c]) {

                break;
            }

            a[p] = a[c];
            p = c;
        }
        a[p] = v;
    }



















    static void heapSelectRight(double[] a, int left, int right, int k, int count) {





        int end = k - 1;
        for (int p = right - ((right - k - 1) >> 1); p <= right; p++) {
            minHeapSiftDown(a, a[p], p, right, end);
        }


        double min = a[right];
        for (int i = left - 1; ++i < k;) {
            final double v = a[i];
            if (v > min) {
                a[i] = min;
                minHeapSiftDown(a, v, right, right, end);
                min = a[right];
            }
        }









        a[right] = a[k];
        a[k] = min;

        if (count > 0) {
            ++end;

            for (int c = Math.min(count, right - end - 1); --c >= 0;) {
                minHeapSiftDown(a, a[right], right, right, end++);

                min = a[right];
                a[right] = a[end];
                a[end] = min;
            }
        }
    }












    private static void minHeapSiftDown(double[] a, double v, int p, int root, int end) {


        while (true) {

            int c = (p << 1) - root - 2;
            if (c < end) {

                break;
            }

            if (c == end || a[c] > a[c + 1]) {
                ++c;
            }
            if (v <= a[c]) {

                break;
            }

            a[p] = a[c];
            p = c;
        }
        a[p] = v;
    }















    static void heapSelectRange2(double[] a, int left, int right, int ka, int kb) {


        if (right - left < MIN_HEAPSELECT_SIZE) {
            Sorting.sort(a, left, right);
            return;
        }

        if (kb - left < right - ka) {
            heapSelectLeft2(a, left, right, ka, kb);
        } else {
            heapSelectRight2(a, left, right, ka, kb);
        }
    }

















    static void heapSelectLeft2(double[] a, int left, int right, int ka, int kb) {





        int end = kb + 1;
        for (int p = left + ((kb - left - 1) >> 1); p >= left; p--) {
            maxHeapSiftDown(a, a[p], p, left, end);
        }


        double max = a[left];
        for (int i = right + 1; --i > kb;) {
            final double v = a[i];
            if (v < max) {
                a[i] = max;
                maxHeapSiftDown(a, v, left, left, end);
                max = a[left];
            }
        }




        final int last = Math.max(left, ka - 1);
        while (--end > last) {
            maxHeapSiftDown(a, a[end], left, left, end);
            a[end] = max;
            max = a[left];
        }
    }

















    static void heapSelectRight2(double[] a, int left, int right, int ka, int kb) {





        int end = ka - 1;
        for (int p = right - ((right - ka - 1) >> 1); p <= right; p++) {
            minHeapSiftDown(a, a[p], p, right, end);
        }


        double min = a[right];
        for (int i = left - 1; ++i < ka;) {
            final double v = a[i];
            if (v > min) {
                a[i] = min;
                minHeapSiftDown(a, v, right, right, end);
                min = a[right];
            }
        }




        final int last = Math.min(right, kb + 1);
        while (++end < last) {
            minHeapSiftDown(a, a[end], right, right, end);
            a[end] = min;
            min = a[right];
        }
    }











    static void sortSelectRange(double[] a, int left, int right, int ka, int kb) {


        if (right - left <= MIN_SORTSELECT_SIZE) {
            Sorting.sort(a, left, right);
            return;
        }

        if (kb - left < right - ka) {
            sortSelectLeft(a, left, right, kb);
        } else {
            sortSelectRight(a, left, right, ka);
        }
    }

















    static void sortSelectLeft(double[] a, int left, int right, int k) {

        for (int i = left; ++i <= k;) {
            final double v = a[i];

            if (v < a[i - 1]) {
                int j = i;
                while (--j >= left && v < a[j]) {
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
            }
        }


        double m = a[k];
        for (int i = right + 1; --i > k;) {
            final double v = a[i];
            if (v < m) {
                a[i] = m;
                int j = k;
                while (--j >= left && v < a[j]) {
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
                m = a[k];
            }
        }
    }

















    static void sortSelectRight(double[] a, int left, int right, int k) {

        for (int i = right; --i >= k;) {
            final double v = a[i];

            if (v > a[i + 1]) {
                int j = i;
                while (++j <= right && v > a[j]) {
                    a[j - 1] = a[j];
                }
                a[j - 1] = v;
            }
        }


        double m = a[k];
        for (int i = left - 1; ++i < k;) {
            final double v = a[i];
            if (v > m) {
                a[i] = m;
                int j = k;
                while (++j <= right && v > a[j]) {
                    a[j - 1] = a[j];
                }
                a[j - 1] = v;
                m = a[k];
            }
        }
    }















    static void sortSelectRange2(double[] a, int left, int right, int ka, int kb) {


        if (right - left <= MIN_SORTSELECT_SIZE) {
            Sorting.sort(a, left, right);
            return;
        }

        if (kb - left < right - ka) {
            sortSelectLeft2(a, left, right, kb);
        } else {
            sortSelectRight2(a, left, right, ka);
        }
    }




















    static void sortSelectLeft2(double[] a, int left, int right, int k) {

        for (int i = left; ++i <= k;) {
            final double v = a[i];

            if (v < a[i - 1]) {
                int j = i;
                while (--j >= left && v < a[j]) {
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
            }
        }


        double m = a[k];

        final int p = (left + k) >>> 1;
        for (int i = right + 1; --i > k;) {
            final double v = a[i];
            if (v < m) {
                a[i] = m;
                int j = k;
                if (v < a[p]) {


                    while (j > p) {

                        a[j] = a[--j];
                    }

                    while (--j >= left && v < a[j]) {
                        a[j + 1] = a[j];
                    }
                } else {

                    while (v < a[--j]) {
                        a[j + 1] = a[j];
                    }
                }
                a[j + 1] = v;
                m = a[k];
            }
        }
    }




















    static void sortSelectRight2(double[] a, int left, int right, int k) {

        for (int i = right; --i >= k;) {
            final double v = a[i];

            if (v > a[i + 1]) {
                int j = i;
                while (++j <= right && v > a[j]) {
                    a[j - 1] = a[j];
                }
                a[j - 1] = v;
            }
        }


        double m = a[k];

        final int p = (right + k) >>> 1;
        for (int i = left - 1; ++i < k;) {
            final double v = a[i];
            if (v > m) {
                a[i] = m;
                int j = k;
                if (v > a[p]) {


                    while (j < p) {

                        a[j] = a[++j];
                    }

                    while (++j <= right && v > a[j]) {
                        a[j - 1] = a[j];
                    }
                } else {

                    while (v > a[++j]) {
                        a[j - 1] = a[j];
                    }
                }
                a[j - 1] = v;
                m = a[k];
            }
        }
    }
















































    private void partition(PartitionFunction part, double[] data, int right, int[] k, int count) {
        if (count < 1 || right < 1) {
            return;
        }

        final int n = countIndices(k, count, right);
        if (n < 1) {
            return;
        }
        if (n == 1) {
            part.partition(data, 0, right, k[0], k[0], false, false);
        } else if (n == 2 && Math.abs(k[0] - k[1]) <= (minQuickSelectSize >>> 1)) {
            final int ka = Math.min(k[0], k[1]);
            final int kb = Math.max(k[0], k[1]);
            part.partition(data, 0, right, ka, kb, false, false);
        } else {

            if (keyStrategy == KeyStrategy.SEQUENTIAL) {

                final ScanningPivotCache pivots = keyAnalysis(right + 1, k, n, minQuickSelectSize >>> 1);
                if (k[0] == Integer.MIN_VALUE) {


                    part.sort(data, 0, right, false, false);
                } else {
                    partitionSequential(part, data, k, n, right, pivots);
                }
            } else if (keyStrategy == KeyStrategy.INDEX_SET) {

                final IndexSet pivots = IndexSet.ofRange(0, right);

                part.partition(data, 0, right, k[0], k[0], false, false, pivots);
                for (int i = 1; i < n; i++) {
                    final int ki = k[i];
                    if (pivots.get(ki)) {
                        continue;
                    }
                    final int l = pivots.previousSetBit(ki);
                    int r = pivots.nextSetBit(ki);
                    if (r < 0) {
                        r = right + 1;
                    }
                    part.partition(data, l + 1, r - 1, ki, ki, l >= 0, r <= right, pivots);
                }
            } else if (keyStrategy == KeyStrategy.PIVOT_CACHE) {

                final PivotCache pivots = createPivotCacheForIndices(k, n);


                if ((pivots.right() - pivots.left()) <= (minQuickSelectSize >>> 1)) {
                    part.partition(data, 0, right, pivots.left(), pivots.right(), false, false);
                    return;
                }




                final int ka = pivots.left();
                part.partition(data, 0, right, ka, ka, false, false, pivots);
                final int kb = pivots.right();
                int l = pivots.previousPivot(kb);
                int r = pivots.nextPivot(kb);
                if (r < 0) {

                    r = right + 1;
                }
                part.partition(data, l + 1, r - 1, kb, kb, true, r <= right, pivots);
                for (int i = 0; i < n; i++) {
                    final int ki = k[i];
                    if (pivots.contains(ki)) {
                        continue;
                    }
                    l = pivots.previousPivot(ki);
                    r = pivots.nextPivot(ki);
                    part.partition(data, l + 1, r - 1, ki, ki, true, true, pivots);
                }
            } else {
                throw new IllegalStateException("Unsupported: " + keyStrategy);
            }
        }
    }









    private static PivotCache createPivotCacheForIndices(int[] indices, int n) {
        int min = indices[0];
        int max = min;
        for (int i = 1; i < n; i++) {
            final int k = indices[i];
            min = Math.min(min, k);
            max = Math.max(max, k);
        }
        return PivotCaches.ofFullRange(min, max);
    }




















































    ScanningPivotCache keyAnalysis(int size, int[] k, int n, int minSeparation) {

        if (size < minQuickSelectSize) {
            k[0] = Integer.MIN_VALUE;
            return null;
        }

        final IndexSet indices = Sorting.sortUnique(Math.max(6, minQuickSelectSize), k, n);

        int right = k[n - 1];
        if (right < 0) {
            right = ~right;
        }

        final int left = compressRange(k, n, minSeparation);
        if (left < 0) {



            if (n != 1 && k[0] >= 0 && size - (k[1] - k[0]) < minQuickSelectSize) {
                k[0] = Integer.MIN_VALUE;
            }
            return null;
        }

        if (indices != null) {

            return indices.asScanningPivotCache(left, right);
        }
        return IndexSet.createScanningPivotCache(left, right);
    }












    private static int compressRange(int[] k, int n, int minSeparation) {
        if (n == 1) {

            if (k.length > 1) {
                k[1] = Integer.MIN_VALUE;
            }
            return -1;
        }

        int j = 0;
        int p2 = k[0];
        int secondTarget = -1;
        for (int i = 0; ++i < n;) {
            if (k[i] < 0) {

                break;
            }
            if (k[i] <= p2 + minSeparation) {

                p2 = k[i];
            } else {

                if (k[j] == p2) {
                    k[j] = ~p2;
                } else {
                    k[++j] = p2;
                }
                j++;

                k[j] = p2 = k[i];

                if (secondTarget < 0) {
                    secondTarget = p2;
                }
            }
        }


        if (k[j] == p2) {
            k[j] = ~p2;
        } else {
            k[++j] = p2;
        }
        j++;

        if (k.length > j) {
            k[j] = Integer.MIN_VALUE;
        }
        return secondTarget;
    }




















    private static void partitionSequential(PartitionFunction part, double[] data, int[] k, int n,
        int right, ScanningPivotCache pivots) {






        int i = 1;
        int s = k[0];
        int e;
        if (s < 0) {
            e = s = ~s;
        } else {
            e = k[i++];
        }



        if (pivots == null) {
            part.partition(data, 0, right, s, e, false, false);
            return;
        }

        part.partitionSequential(data, 0, right, s, e, false, false, pivots);


        while (i < n) {
            s = k[i++];
            if (s < 0) {
                e = s = ~s;
            } else {
                e = k[i++];
            }
            if (s > right) {

                break;
            }







            final int l = pivots.previousPivot(s);
            final int r = pivots.nextPivotOrElse(e, right + 1);






            int p1 = -1;
            int p2 = -1;
            if (e - s > 1) {
                final int p = pivots.nextPivot(s + 1);
                if (p > s && p < e) {
                    p1 = p;
                    p2 = pivots.previousPivot(e - 1);
                    if (p2 - p1 > SORT_BETWEEN_SIZE) {





                        int e1 = pivots.previousNonPivot(p2);
                        while (p1 < e1) {
                            final int s1 = pivots.previousPivot(e1);
                            part.sort(data, s1 + 1, e1, true, true);
                            e1 = pivots.previousNonPivot(s1);
                        }
                    }
                }
            }


            int sn = right + 1;
            if (i < n) {
                sn = k[i];
                if (sn < 0) {
                    sn = ~sn;
                }
            }


            final boolean unsupportedCacheRange = !pivots.moveLeft(sn);





            if (p1 > s) {



                part.partition(data, l + 1, p1 - 1, s, p1 - 1, true, p1 <= right);
                if (unsupportedCacheRange) {
                    part.partition(data, p2 + 1, r - 1, p2 + 1, e, true, r <= right);
                } else {
                    part.partitionSequential(data, p2 + 1, r - 1, p2 + 1, e, true, r <= right, pivots);
                }
            } else {

                if (unsupportedCacheRange) {
                    part.partition(data, l + 1, r - 1, s, e, true, r <= right);
                } else {
                    part.partitionSequential(data, l + 1, r - 1, s, e, true, r <= right, pivots);
                }
            }
        }
    }









    void sortSBM(double[] data) {

        final int right = sortNaN(data);
        sort((SPEPartitionFunction) this::partitionSBMWithZeros, data, right);
    }








    private static void sort(PartitionFunction part, double[] data, int right) {
        if (right < 1) {
            return;
        }

        part.sort(data, 0, right, false, false);
    }









    void sortISP(double[] data) {

        introsort(getSPFunction(), data);
    }











    private void introsort(SPEPartition part, double[] a) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        if (end > 1) {
            introsort(part, a, 0, end - 1, createMaxDepthSinglePivot(end));
        }

        t.postProcess(a);
    }













    private void introsort(SPEPartition part, double[] a, int left, int right, int maxDepth) {


        final int l = left;
        int r = right;
        final int[] upper = {0};
        while (true) {

            if (r - l < minQuickSelectSize) {
                Sorting.sort(a, l, r);
                return;
            }
            if (maxDepth == 0) {

                heapSort(a, l, r);
                return;
            }


            final int p0 = part.partition(a, l, r,
                pivotingStrategy.pivotIndex(a, l, r, l),
                upper);
            final int p1 = upper[0];


            introsort(part, a, p1 + 1, r, --maxDepth);

            r = p0 - 1;
        }
    }









    void sortIDP(double[] data) {

        introsort((DPPartition) Partition::partitionDP, data);
    }











    private void introsort(DPPartition part, double[] a) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        if (end > 1) {
            introsort(part, a, 0, end - 1, createMaxDepthDualPivot(end));
        }

        t.postProcess(a);
    }













    private void introsort(DPPartition part, double[] a, int left, int right, int maxDepth) {


        final int l = left;
        int r = right;
        final int[] upper = {0, 0, 0};
        while (true) {

            if (r - l < minQuickSelectSize) {
                Sorting.sort(a, l, r);
                return;
            }
            if (maxDepth == 0) {

                heapSort(a, l, r);
                return;
            }


            int p0 = dualPivotingStrategy.pivotIndex(a, l, r, upper);
            p0 = part.partition(a, l, r, p0, upper[0], upper);
            final int p1 = upper[0];
            final int p2 = upper[1];
            final int p3 = upper[2];


            --maxDepth;
            introsort(part, a, p3 + 1, r, maxDepth);
            introsort(part, a, p1 + 1, p2 - 1, maxDepth);

            r = p0 - 1;
        }
    }























    private void introselect(SPEPartition part, double[] a, int[] k, int count) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        int n = count;
        if (end > 1) {

            if (end < a.length) {
                for (int i = n; --i >= 0;) {
                    final int v = k[i];
                    if (v >= end) {

                        k[i] = k[--n];
                        k[n] = v;
                    }
                }
            }
            introselect(part, a, end - 1, k, n);
        }

        t.postProcess(a, k, n);
    }






















    private void introselect(SPEPartition part, double[] a, int right, int[] k, int n) {
        if (n < 1) {
            return;
        }
        final int maxDepth = createMaxDepthSinglePivot(right + 1);

        if (n == 1) {


            if (pairedKeyStrategy == PairedKeyStrategy.PAIRED_KEYS) {
                introselect(part, a, 0, right, k[0], maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.PAIRED_KEYS_2) {


                introselect2(part, a, 0, right, k[0]);
            } else if (pairedKeyStrategy == PairedKeyStrategy.PAIRED_KEYS_LEN) {
                introselect(part, a, 0, right, k[0]);
            } else if (pairedKeyStrategy == PairedKeyStrategy.TWO_KEYS) {

                introselect(part, a, 0, right, k[0], k[0], maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.KEY_RANGE) {

                introselect2(part, a, 0, right, k[0], k[0]);
            } else if (pairedKeyStrategy == PairedKeyStrategy.SEARCHABLE_INTERVAL) {

                introselect(part, a, 0, right, IndexIntervals.anyIndex(), k[0], k[0], maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.UPDATING_INTERVAL) {

                introselect(part, a, 0, right, IndexIntervals.interval(k[0]), maxDepth);
            } else {
                throw new IllegalStateException(UNSUPPORTED_INTROSELECT + pairedKeyStrategy);
            }
            return;
        }

        if (n == 2 && k[0] + 1 == k[1]) {

            if (pairedKeyStrategy == PairedKeyStrategy.PAIRED_KEYS) {
                final int p = introselect(part, a, 0, right, k[0], maxDepth);


                if (p > k[1]) {
                    selectMinIgnoreZeros(a, k[1], p);
                }
            } else if (pairedKeyStrategy == PairedKeyStrategy.PAIRED_KEYS_2) {
                final int p = introselect2(part, a, 0, right, k[0]);
                if (p > k[1]) {
                    selectMinIgnoreZeros(a, k[1], p);
                }
            } else if (pairedKeyStrategy == PairedKeyStrategy.PAIRED_KEYS_LEN) {
                final int p = introselect(part, a, 0, right, k[0]);
                if (p > k[1]) {
                    selectMinIgnoreZeros(a, k[1], p);
                }
            } else if (pairedKeyStrategy == PairedKeyStrategy.TWO_KEYS) {



                final int ka = Math.min(k[0], k[1]);
                final int kb = Math.max(k[0], k[1]);
                introselect(part, a, 0, right, ka, kb, maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.KEY_RANGE) {

                final int ka = Math.min(k[0], k[1]);
                final int kb = Math.max(k[0], k[1]);
                introselect2(part, a, 0, right, ka, kb);
            } else if (pairedKeyStrategy == PairedKeyStrategy.SEARCHABLE_INTERVAL) {

                introselect(part, a, 0, right, IndexIntervals.anyIndex(), k[0], k[1], maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.UPDATING_INTERVAL) {

                introselect(part, a, 0, right, IndexIntervals.interval(k[0], k[1]), maxDepth);
            } else {
                throw new IllegalStateException(UNSUPPORTED_INTROSELECT + pairedKeyStrategy);
            }
            return;
        }









        if (keyStrategy == KeyStrategy.ORDERED_KEYS) {
            final int unique = Sorting.sortIndices(k, n);
            introselect(part, a, 0, right, k, 0, unique - 1, maxDepth);
        } else if (keyStrategy == KeyStrategy.SCANNING_KEY_SEARCHABLE_INTERVAL) {
            final int unique = Sorting.sortIndices(k, n);
            final SearchableInterval keys = ScanningKeyInterval.of(k, unique);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.SEARCH_KEY_SEARCHABLE_INTERVAL) {
            final int unique = Sorting.sortIndices(k, n);
            final SearchableInterval keys = BinarySearchKeyInterval.of(k, unique);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.COMPRESSED_INDEX_SET) {
            final SearchableInterval keys = CompressedIndexSet.of(compression, k, n);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.INDEX_SET) {
            final SearchableInterval keys = IndexSet.of(k, n);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.KEY_UPDATING_INTERVAL) {
            final int unique = Sorting.sortIndices(k, n);
            final UpdatingInterval keys = KeyUpdatingInterval.of(k, unique);
            introselect(part, a, 0, right, keys, maxDepth);
        } else if (keyStrategy == KeyStrategy.INDEX_SET_UPDATING_INTERVAL) {
            final UpdatingInterval keys = BitIndexUpdatingInterval.of(k, n);
            introselect(part, a, 0, right, keys, maxDepth);
        } else if (keyStrategy == KeyStrategy.KEY_SPLITTING_INTERVAL) {
            final int unique = Sorting.sortIndices(k, n);
            final SplittingInterval keys = KeyUpdatingInterval.of(k, unique);
            introselect(part, a, 0, right, keys, maxDepth);
        } else if (keyStrategy == KeyStrategy.INDEX_SET_SPLITTING_INTERVAL) {
            final SplittingInterval keys = BitIndexUpdatingInterval.of(k, n);
            introselect(part, a, 0, right, keys, maxDepth);
        } else if (keyStrategy == KeyStrategy.INDEX_ITERATOR) {
            final int unique = Sorting.sortIndices(k, n);
            final IndexIterator keys = KeyIndexIterator.of(k, unique);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.COMPRESSED_INDEX_ITERATOR) {
            final IndexIterator keys = CompressedIndexSet.iterator(compression, k, n);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else {
            throw new IllegalStateException(UNSUPPORTED_INTROSELECT + keyStrategy);
        }
    }

























    private int introselect(SPEPartition part, double[] a, int left, int right,
        int k, int maxDepth) {
        int l = left;
        int r = right;
        final int[] upper = {0};
        while (true) {




            final int d1 = k - l;
            final int d2 = r - k;
            if (Math.min(d1, d2) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, k, k);

                return r;
            }

            if (maxDepth == 0) {



                recursionConsumer.accept(maxDepth);
                stopperSelection.partition(a, l, r, k, k);

                return r;
            }


            int pivot;

            int n = r - l;
            if (n > subSamplingSize) {



                ++n;
                final int ith = k - l + 1;
                final double z = Math.log(n);
                final double s = 0.5 * Math.exp(0.6666666666666666 * z);
                final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
                final int ll = Math.max(l, (int) (k - ith * s / n + sd));
                final int rr = Math.min(r, (int) (k + (n - ith) * s / n + sd));

                if ((controlFlags & FLAG_RANDOM_SAMPLING) != 0) {
                    final IntUnaryOperator rng = createRNG(n, k);

                    if (ll > l) {
                        for (int i = k; i > ll;) {

                            final int j = l + rng.applyAsInt(i - l);
                            final double t = a[--i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }

                    if (rr < r) {
                        for (int i = k; i < rr;) {

                            final int j = r - rng.applyAsInt(r - i);
                            final double t = a[++i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }
                }
                introselect(part, a, ll, rr, k, lnNtoMaxDepthSinglePivot(z));
                pivot = k;
            } else {

                pivot = pivotingStrategy.pivotIndex(a, l, r, k);
            }

            final int p0 = part.partition(a, l, r, pivot, upper);
            final int p1 = upper[0];

            maxDepth--;
            if (k < p0) {

                r = p0 - 1;
            } else if (k > p1) {

                l = p1 + 1;
            } else {



                return k < p1 ? k : r;
            }
        }
    }











































    private int introselect2(SPEPartition part, double[] a, int left, int right, int k) {
        int l = left;
        int r = right;
        final int[] upper = {0};
        int counter = (int) recursionMultiple;
        int threshold = (right - left) >>> recursionConstant;
        int depth = singlePivotMaxDepth(right - left);
        while (true) {




            final int d1 = k - l;
            final int d2 = r - k;
            if (Math.min(d1, d2) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, k, k);

                return r;
            }


            int n = r - l;
            depth--;
            if (--counter < 0) {
                if (n > threshold) {






                    recursionConsumer.accept(depth);
                    stopperSelection.partition(a, l, r, k, k);

                    return r;
                }


                if (counter == -5) {
                    counter = 1;
                }
                threshold >>>= 1;
            }


            int pivot;
            if (n > subSamplingSize) {



                ++n;
                final int ith = k - l + 1;
                final double z = Math.log(n);
                final double s = 0.5 * Math.exp(0.6666666666666666 * z);
                final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
                final int ll = Math.max(l, (int) (k - ith * s / n + sd));
                final int rr = Math.min(r, (int) (k + (n - ith) * s / n + sd));

                if ((controlFlags & FLAG_RANDOM_SAMPLING) != 0) {
                    final IntUnaryOperator rng = createRNG(n, k);

                    if (ll > l) {
                        for (int i = k; i > ll;) {

                            final int j = l + rng.applyAsInt(i - l);
                            final double t = a[--i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }

                    if (rr < r) {
                        for (int i = k; i < rr;) {

                            final int j = r - rng.applyAsInt(r - i);
                            final double t = a[++i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }
                }

                introselect2(part, a, ll, rr, k);
                pivot = k;
            } else {

                pivot = pivotingStrategy.pivotIndex(a, l, r, k);
            }

            final int p0 = part.partition(a, l, r, pivot, upper);
            final int p1 = upper[0];

            if (k < p0) {

                r = p0 - 1;
            } else if (k > p1) {

                l = p1 + 1;
            } else {



                return k < p1 ? k : r;
            }
        }
    }





























    private int introselect(SPEPartition part, double[] a, int left, int right, int k) {
        int l = left;
        int r = right;
        final int[] upper = {0};


        long limit = (long) ((1 + recursionMultiple) * (right - left));
        int depth = singlePivotMaxDepth(right - left);
        while (true) {




            final int d1 = k - l;
            final int d2 = r - k;
            if (Math.min(d1, d2) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, k, k);

                return r;
            }


            int n = r - l;
            limit -= n;
            depth--;

            if (limit < 0) {


                recursionConsumer.accept(depth);
                stopperSelection.partition(a, l, r, k, k);

                return r;
            }


            int pivot;
            if (n > subSamplingSize) {



                ++n;
                final int ith = k - l + 1;
                final double z = Math.log(n);
                final double s = 0.5 * Math.exp(0.6666666666666666 * z);
                final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
                final int ll = Math.max(l, (int) (k - ith * s / n + sd));
                final int rr = Math.min(r, (int) (k + (n - ith) * s / n + sd));

                if ((controlFlags & FLAG_RANDOM_SAMPLING) != 0) {
                    final IntUnaryOperator rng = createRNG(n, k);

                    if (ll > l) {
                        for (int i = k; i > ll;) {

                            final int j = l + rng.applyAsInt(i - l);
                            final double t = a[--i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }

                    if (rr < r) {
                        for (int i = k; i < rr;) {

                            final int j = r - rng.applyAsInt(r - i);
                            final double t = a[++i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }
                }

                introselect(part, a, ll, rr, k);
                pivot = k;
            } else {

                pivot = pivotingStrategy.pivotIndex(a, l, r, k);
            }

            final int p0 = part.partition(a, l, r, pivot, upper);
            final int p1 = upper[0];

            if (k < p0) {

                r = p0 - 1;
            } else if (k > p1) {

                l = p1 + 1;
            } else {



                return k < p1 ? k : r;
            }
        }
    }


























    private void introselect(SPEPartition part, double[] a, int left, int right,
        int ka, int kb, int maxDepth) {


        int l = left;
        int r = right;
        int ka1 = ka;
        int kb1 = kb;
        final int[] upper = {0};
        while (true) {

            final int n = r - l;

            if (n < minQuickSelectSize) {

                sortSelectRange(a, l, r, ka1, kb1);
                return;
            }







            final int d1 = ka1 - l;
            final int d2 = kb1 - l;
            final int d3 = r - kb1;
            final int d4 = r - ka1;
            if (maxDepth == 0 ||
                Math.min(d1 + d3, Math.min(d2, d4)) < edgeSelectConstant) {


                heapSelectPair(a, l, r, ka1, kb1);
                return;
            }


            final int p0 = part.partition(a, l, r,
                pivotingStrategy.pivotIndex(a, l, r, ka),
                upper);
            final int p1 = upper[0];





            maxDepth--;

            if (ka1 < p0) {
                if (kb1 <= p1) {

                    r = p0 - 1;
                    kb1 = r < kb1 ? ka1 : kb1;
                    continue;
                }
                introselect(part, a, l, p0 - 1, ka1, ka1, maxDepth);
                ka1 = kb1;
            }
            if (kb1 <= p1) {

                return;
            }

            l = p1 + 1;
            ka1 = ka1 < l ? kb1 : ka1;
        }
    }












































    private void introselect2(SPEPartition part, double[] a, int left, int right, int ka, int kb) {
        int l = left;
        int r = right;
        final int[] upper = {0};
        int counter = (int) recursionMultiple;
        int threshold = (right - left) >>> recursionConstant;
        while (true) {


            if (Math.min(kb - l, r - ka) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, ka, kb);
                return;
            }


            int n = r - l;
            if (--counter < 0) {
                if (n > threshold) {






                    recursionConsumer.accept(r - l);
                    stopperSelection.partition(a, l, r, ka, kb);
                    return;
                }


                if (counter == -5) {
                    counter = 1;
                }
                threshold >>>= 1;
            }


            int pivot;
            if (n > subSamplingSize) {



                ++n;
                final int ith = ka - l + 1;
                final double z = Math.log(n);
                final double s = 0.5 * Math.exp(0.6666666666666666 * z);
                final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
                final int ll = Math.max(l, (int) (ka - ith * s / n + sd));
                final int rr = Math.min(r, (int) (ka + (n - ith) * s / n + sd));

                if ((controlFlags & FLAG_RANDOM_SAMPLING) != 0) {
                    final IntUnaryOperator rng = createRNG(n, ka);

                    if (ll > l) {
                        for (int i = ka; i > ll;) {

                            final int j = l + rng.applyAsInt(i - l);
                            final double t = a[--i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }

                    if (rr < r) {
                        for (int i = ka; i < rr;) {

                            final int j = r - rng.applyAsInt(r - i);
                            final double t = a[++i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }
                }

                introselect2(part, a, ll, rr, ka, ka);
                pivot = ka;
            } else {

                pivot = pivotingStrategy.pivotIndex(a, l, r, ka);
            }

            final int p0 = part.partition(a, l, r, pivot, upper);
            final int p1 = upper[0];






            if (kb < p0) {

                r = p0 - 1;
            } else if (ka > p1) {

                l = p1 + 1;
            } else {

                if (ka < p0) {
                    sortSelectRight(a, l, p0 - 1, ka);
                }
                if (kb > p1) {
                    sortSelectLeft(a, p1 + 1, r, kb);
                }
                return;
            }
        }
    }


































    private void introselect(SPEPartition part, double[] a, int left, int right,
        int[] k, int ia, int ib, int maxDepth) {


        int l = left;
        int r = right;
        int ia1 = ia;
        int ib1 = ib;
        final int[] upper = {0};
        while (true) {





            if (ib1 - ia1 <= 1) {
                introselect(part, a, l, r, k[ia1], k[ib1], maxDepth);
                return;
            }


            final int n = r - l;
            int ka = k[ia1];
            final int kb = k[ib1];

            if (n < minQuickSelectSize) {

                sortSelectRange(a, l, r, ka, kb);
                return;
            }





            if (Math.min(kb - l, r - ka) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, ka, kb);
                return;
            }

            if (maxDepth == 0) {

                heapSelectRange(a, l, r, ka, kb);
                return;
            }


            final int p0 = part.partition(a, l, r,
                pivotingStrategy.pivotIndex(a, l, r, ka),
                upper);
            final int p1 = upper[0];









            maxDepth--;

            if (ka < p0) {
                if (kb <= p1) {

                    r = p0 - 1;
                    if (r < kb) {
                        ib1 = searchLessOrEqual(k, ia1, ib1, r);
                    }
                    continue;
                }

                introselect(part, a, l, p0 - 1, k, ia1, searchLessOrEqual(k, ia1, ib1, p0 - 1), maxDepth);
                ia1 = searchGreaterOrEqual(k, ia1, ib1, l);
                ka = k[ia1];
            }
            if (kb <= p1) {

                recursionConsumer.accept(maxDepth);
                return;
            }

            l = p1 + 1;
            if (ka < l) {
                ia1 = searchGreaterOrEqual(k, ia1, ib1, l);
            }
        }
    }

































    void introselect(SPEPartition part, double[] a, int left, int right,
        SearchableInterval k, int ka, int kb, int maxDepth) {


        int l = left;
        int r = right;
        int ka1 = ka;
        int kb1 = kb;
        final int[] upper = {0};
        while (true) {

            int n = r - l;

            if (n < minQuickSelectSize) {

                sortSelectRange(a, l, r, ka1, kb1);
                recursionConsumer.accept(maxDepth);
                return;
            }





            if (Math.min(kb1 - l, r - ka1) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, ka1, kb1);
                recursionConsumer.accept(maxDepth);
                return;
            }

            if (maxDepth == 0) {

                heapSelectRange(a, l, r, ka1, kb1);
                recursionConsumer.accept(maxDepth);
                return;
            }


            int pivot;
            if (n > subSamplingSize) {




                ++n;
                final int ith = ka1 - l + 1;
                final double z = Math.log(n);
                final double s = 0.5 * Math.exp(0.6666666666666666 * z);
                final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
                final int ll = Math.max(l, (int) (ka1 - ith * s / n + sd));
                final int rr = Math.min(r, (int) (ka1 + (n - ith) * s / n + sd));

                if ((controlFlags & FLAG_RANDOM_SAMPLING) != 0) {
                    final IntUnaryOperator rng = createRNG(n, ka1);

                    if (ll > l) {
                        for (int i = ka1; i > ll;) {

                            final int j = l + rng.applyAsInt(i - l);
                            final double t = a[--i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }

                    if (rr < r) {
                        for (int i = ka1; i < rr;) {

                            final int j = r - rng.applyAsInt(r - i);
                            final double t = a[++i];
                            a[i] = a[j];
                            a[j] = t;
                        }
                    }
                }
                introselect(part, a, ll, rr, k, ka1, ka1, lnNtoMaxDepthSinglePivot(z));
                pivot = ka1;
            } else {

                pivot = pivotingStrategy.pivotIndex(a, l, r, ka1);
            }

            final int p0 = part.partition(a, l, r, pivot, upper);
            final int p1 = upper[0];









            maxDepth--;

            if (ka1 < p0) {
                if (kb1 <= p1) {

                    r = p0 - 1;
                    if (r < kb1) {
                        kb1 = k.previousIndex(r);
                    }
                    continue;
                }
                introselect(part, a, l, p0 - 1, k, ka1, k.split(p0, p1, upper), maxDepth);
                ka1 = upper[0];
            }
            if (kb1 <= p1) {

                recursionConsumer.accept(maxDepth);
                return;
            }

            l = p1 + 1;
            if (ka1 < l) {
                ka1 = k.nextIndex(l);
            }
        }
    }



























    void introselect(SPEPartition part, double[] a, int left, int right,
        UpdatingInterval k, int maxDepth) {


        int l = left;
        int r = right;
        int ka = k.left();
        int kb = k.right();
        final int[] upper = {0};
        while (true) {

            final int n = r - l;

            if (n < minQuickSelectSize) {

                sortSelectRange(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }





            if (Math.min(kb - l, r - ka) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }

            if (maxDepth == 0) {

                heapSelectRange(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }


            final int p0 = part.partition(a, l, r,
                pivotingStrategy.pivotIndex(a, l, r, ka),
                upper);
            final int p1 = upper[0];








            maxDepth--;

            if (ka < p0) {
                if (kb <= p1) {

                    r = p0 - 1;
                    if (r < kb) {
                        kb = k.updateRight(r);
                    }
                    continue;
                }
                introselect(part, a, l, p0 - 1, k.splitLeft(p0, p1), maxDepth);
                ka = k.left();
            } else if (kb <= p1) {

                recursionConsumer.accept(maxDepth);
                return;
            } else if (ka <= p1) {
                ka = k.updateLeft(p1 + 1);
            }

            l = p1 + 1;
        }
    }


























    void introselect(SPEPartition part, double[] a, int left, int right,
        SplittingInterval keys, int maxDepth) {


        int l = left;
        int r = right;
        SplittingInterval k = keys;
        int ka = k.left();
        int kb = k.right();
        final int[] upper = {0};
        while (true) {

            final int n = r - l;

            if (n < minQuickSelectSize) {

                sortSelectRange(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }





            if (Math.min(kb - l, r - ka) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }

            if (maxDepth == 0) {

                heapSelectRange(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }


            final int p0 = part.partition(a, l, r,
                pivotingStrategy.pivotIndex(a, l, r, ka),
                upper);
            final int p1 = upper[0];








            maxDepth--;
            final SplittingInterval lk = k.split(p0, p1);

            if (lk != null) {

                if (k.empty()) {

                    r = p0 - 1;
                    kb = lk.right();
                    k = lk;
                    continue;
                }
                introselect(part, a, l, p0 - 1, lk, maxDepth);
            }
            if (k.empty()) {

                recursionConsumer.accept(maxDepth);
                return;
            }

            l = p1 + 1;
            ka = k.left();
        }
    }


































    void introselect(SPEPartition part, double[] a, int left, int right,
        IndexIterator k, int ka, int kb, int maxDepth) {


        int l = left;
        int lo = ka;
        int hi = kb;
        final int[] upper = {0};
        while (true) {
            if (maxDepth == 0) {




                while (hi < right && k.next()) {
                    hi = k.right();
                }
                heapSelectRange(a, l, right, lo, hi);
                recursionConsumer.accept(maxDepth);
                return;
            }


            final int n = right - l;






            if (Math.min(hi - l, right - lo) < edgeSelectConstant) {
                if (hi - l > right - lo) {

                    edgeSelection.partition(a, l, right, lo, right);
                    recursionConsumer.accept(maxDepth);
                    return;
                } else if (k.nextAfter(right)) {




                    edgeSelection.partition(a, l, right, lo, hi);
                    recursionConsumer.accept(maxDepth);

                    l = hi + 1;
                    if (!k.positionAfter(hi) || Math.max(k.left(), l) > right) {

                        return;
                    }
                    lo = Math.max(k.left(), l);
                    hi = Math.min(right, k.right());

                    continue;
                }
            }






            if (n - (hi - lo) < minQuickSelectSize) {




                if (n < minQuickSelectSize) {


                    sortSelectRight(a, l, right, lo);
                } else {


                    Arrays.sort(a, l, right + 1);
                }
                recursionConsumer.accept(maxDepth);
                return;
            }



            final int p0 = part.partition(a, l, right,
                pivotingStrategy.pivotIndex(a, l, right, ka),
                upper);
            final int p1 = upper[0];

            maxDepth--;

            if (lo < p0) {
                introselect(part, a, l, p0 - 1, k, lo, Math.min(hi, p0 - 1), maxDepth);


                if (!k.positionAfter(p1) || k.left() > right) {

                    return;
                }
                lo = k.left();
                hi = Math.min(right, k.right());
            }
            if (hi <= p1) {

                if (!k.positionAfter(p1) || k.left() > right) {

                    return;
                }
                lo = k.left();
                hi = Math.min(right, k.right());
            }

            l = p1 + 1;
            lo = Math.max(lo, l);
        }
    }























    void introselect(DPPartition part, double[] a, int[] k, int count) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        int n = count;
        if (end > 1) {

            if (end < a.length) {
                for (int i = n; --i >= 0;) {
                    final int v = k[i];
                    if (v >= end) {

                        k[i] = k[--n];
                        k[n] = v;
                    }
                }
            }
            introselect(part, a, end - 1, k, n);
        }

        t.postProcess(a, k, n);
    }

























    private void introselect(DPPartition part, double[] a, int right, int[] k, int n) {
        if (n < 1) {
            return;
        }
        final int maxDepth = createMaxDepthDualPivot(right + 1);

        if (n == 1) {
            if (pairedKeyStrategy == PairedKeyStrategy.PAIRED_KEYS) {

                introselect(part, a, 0, right, k[0], maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.TWO_KEYS) {

                introselect(part, a, 0, right, k[0], k[0], maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.SEARCHABLE_INTERVAL) {

                introselect(part, a, 0, right, IndexIntervals.anyIndex(), k[0], k[0], maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.UPDATING_INTERVAL) {

                introselect(part, a, 0, right, IndexIntervals.interval(k[0]), maxDepth);
            } else {
                throw new IllegalStateException(UNSUPPORTED_INTROSELECT + pairedKeyStrategy);
            }
            return;
        }

        if (n == 2 && k[0] + 1 == k[1]) {
            if (pairedKeyStrategy == PairedKeyStrategy.PAIRED_KEYS) {

                final int p = introselect(part, a, 0, right, k[0], maxDepth);


                if (p > k[1]) {
                    selectMinIgnoreZeros(a, k[1], p);
                }
            } else if (pairedKeyStrategy == PairedKeyStrategy.TWO_KEYS) {



                final int ka = Math.min(k[0], k[1]);
                final int kb = Math.max(k[0], k[1]);
                introselect(part, a, 0, right, ka, kb, maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.SEARCHABLE_INTERVAL) {

                introselect(part, a, 0, right, IndexIntervals.anyIndex(), k[0], k[1], maxDepth);
            } else if (pairedKeyStrategy == PairedKeyStrategy.UPDATING_INTERVAL) {

                introselect(part, a, 0, right, IndexIntervals.interval(k[0], k[1]), maxDepth);
            } else {
                throw new IllegalStateException(UNSUPPORTED_INTROSELECT + pairedKeyStrategy);
            }
            return;
        }













        if (keyStrategy == KeyStrategy.ORDERED_KEYS) {


            throw new IllegalStateException(UNSUPPORTED_INTROSELECT + keyStrategy);
        } else if (keyStrategy == KeyStrategy.SCANNING_KEY_SEARCHABLE_INTERVAL) {
            final int unique = Sorting.sortIndices(k, n);
            final SearchableInterval keys = ScanningKeyInterval.of(k, unique);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.SEARCH_KEY_SEARCHABLE_INTERVAL) {
            final int unique = Sorting.sortIndices(k, n);
            final SearchableInterval keys = BinarySearchKeyInterval.of(k, unique);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.COMPRESSED_INDEX_SET) {
            final SearchableInterval keys = CompressedIndexSet.of(compression, k, n);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.INDEX_SET) {
            final SearchableInterval keys = IndexSet.of(k, n);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.KEY_UPDATING_INTERVAL) {
            final int unique = Sorting.sortIndices(k, n);
            final UpdatingInterval keys = KeyUpdatingInterval.of(k, unique);
            introselect(part, a, 0, right, keys, maxDepth);
        } else if (keyStrategy == KeyStrategy.INDEX_SET_UPDATING_INTERVAL) {
            final UpdatingInterval keys = BitIndexUpdatingInterval.of(k, n);
            introselect(part, a, 0, right, keys, maxDepth);
        } else if (keyStrategy == KeyStrategy.KEY_SPLITTING_INTERVAL) {
            final int unique = Sorting.sortIndices(k, n);
            final SplittingInterval keys = KeyUpdatingInterval.of(k, unique);
            introselect(part, a, 0, right, keys, maxDepth);
        } else if (keyStrategy == KeyStrategy.INDEX_SET_SPLITTING_INTERVAL) {
            final SplittingInterval keys = BitIndexUpdatingInterval.of(k, n);
            introselect(part, a, 0, right, keys, maxDepth);
        } else if (keyStrategy == KeyStrategy.INDEX_ITERATOR) {
            final int unique = Sorting.sortIndices(k, n);
            final IndexIterator keys = KeyIndexIterator.of(k, unique);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else if (keyStrategy == KeyStrategy.COMPRESSED_INDEX_ITERATOR) {
            final IndexIterator keys = CompressedIndexSet.iterator(compression, k, n);
            introselect(part, a, 0, right, keys, keys.left(), keys.right(), maxDepth);
        } else {
            throw new IllegalStateException(UNSUPPORTED_INTROSELECT + keyStrategy);
        }
    }

























    private int introselect(DPPartition part, double[] a, int left, int right,
        int k, int maxDepth) {
        int l = left;
        int r = right;
        final int[] upper = {0, 0, 0};
        while (true) {




            final int d1 = k - l;
            final int d3 = r - k;
            if (Math.min(d1, d3) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, k, k);

                return r;
            }

            if (maxDepth == 0) {

                stopperSelection.partition(a, l, r, k, k);

                return r;
            }


            int p0 = dualPivotingStrategy.pivotIndex(a, l, r, upper);
            p0 = part.partition(a, l, r, p0, upper[0], upper);
            final int p1 = upper[0];
            final int p2 = upper[1];
            final int p3 = upper[2];

            maxDepth--;
            if (k < p0) {

                r = p0 - 1;
                continue;
            } else if (k > p3) {

                l = p3 + 1;
                continue;
            }




            if (k <= p1 || p2 <= k || p2 - p1 <= 2) {





                return k != p1 && k < p3 ? k : r;
            }

            l = p1 + 1;
            r = p2 - 1;
        }
    }


























    private void introselect(DPPartition part, double[] a, int left, int right,
        int ka, int kb, int maxDepth) {


        int l = left;
        int r = right;
        int ka1 = ka;
        int kb1 = kb;
        final int[] upper = {0, 0, 0};
        while (true) {

            final int n = r - l;

            if (n < minQuickSelectSize) {

                sortSelectRange(a, l, r, ka1, kb1);
                return;
            }







            final int s1 = ka1 - l;
            final int s2 = kb1 - l;
            final int s3 = r - kb1;
            final int s4 = r - ka1;
            if (maxDepth == 0 ||
                Math.min(s1 + s3, Math.min(s2, s4)) < edgeSelectConstant) {


                heapSelectPair(a, l, r, ka1, kb1);
                return;
            }


            int p0 = dualPivotingStrategy.pivotIndex(a, l, r, upper);
            p0 = part.partition(a, l, r, p0, upper[0], upper);
            final int p1 = upper[0];
            final int p2 = upper[1];
            final int p3 = upper[2];





            maxDepth--;

            if (ka1 < p0) {
                if (kb1 <= p1) {

                    r = p0 - 1;
                    kb1 = r < kb1 ? ka1 : kb1;
                    continue;
                }
                introselect(part, a, l, p0 - 1, ka1, ka1, maxDepth);

                ka1 = kb1;
            }




            if (ka1 < p2 && ka1 > p1 || kb1 < p2 && kb1 > p1) {

                l = p1 + 1;
                ka1 = ka1 < l ? kb1 : ka1;
                if (kb1 <= p3) {

                    r = p2 - 1;
                    kb1 = r < kb1 ? ka1 : kb1;
                    continue;
                }
                introselect(part, a, l, p2 - 1, ka1, ka1, maxDepth);

                ka1 = kb1;
            }
            if (kb1 <= p3) {

                return;
            }

            l = p3 + 1;
            ka1 = ka1 < l ? kb1 : ka1;
        }
    }

































    void introselect(DPPartition part, double[] a, int left, int right,
        SearchableInterval k, int ka, int kb, int maxDepth) {



        int l = left;
        int r = right;
        int ka1 = ka;
        int kb1 = kb;
        final int[] upper = {0, 0, 0};
        while (true) {

            final int n = r - l;

            if (n < minQuickSelectSize) {

                sortSelectRange(a, l, r, ka1, kb1);
                recursionConsumer.accept(maxDepth);
                return;
            }





            if (Math.min(kb1 - l, r - ka1) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, ka1, kb1);
                recursionConsumer.accept(maxDepth);
                return;
            }

            if (maxDepth == 0) {

                heapSelectRange(a, l, r, ka1, kb1);
                recursionConsumer.accept(maxDepth);
                return;
            }


            int p0 = dualPivotingStrategy.pivotIndex(a, l, r, upper);
            p0 = part.partition(a, l, r, p0, upper[0], upper);
            final int p1 = upper[0];
            final int p2 = upper[1];
            final int p3 = upper[2];









            maxDepth--;

            if (ka1 < p0) {
                if (kb1 <= p1) {

                    r = p0 - 1;
                    if (r < kb1) {
                        kb1 = k.previousIndex(r);
                    }
                    continue;
                }
                introselect(part, a, l, p0 - 1, k, ka1, k.split(p0, p1, upper), maxDepth);
                ka1 = upper[0];
            }

            if (kb1 > p3) {
                if (ka1 >= p2) {

                    l = p3 + 1;
                    if (ka1 < l) {
                        ka1 = k.nextIndex(l);
                    }
                    continue;
                }
                final int lo = k.split(p2, p3, upper);
                introselect(part, a, p3 + 1, r, k, upper[0], kb1, maxDepth);
                kb1 = lo;
            }




            if (kb1 <= p1 || p2 <= ka1 || p2 - p1 <= 2) {

                recursionConsumer.accept(maxDepth);
                return;
            }
            l = p1 + 1;
            r = p2 - 1;




            if (ka1 < l) {
                ka1 = k.nextIndex(l);
                if (ka1 > r) {

                    recursionConsumer.accept(maxDepth);
                    return;
                }
            }
            if (r < kb1) {
                kb1 = k.previousIndex(r);
            }
        }
    }


























    void introselect(DPPartition part, double[] a, int left, int right,
        UpdatingInterval k, int maxDepth) {



        int l = left;
        int r = right;
        int ka = k.left();
        int kb = k.right();
        final int[] upper = {0, 0, 0};
        while (true) {

            final int n = r - l;

            if (n < minQuickSelectSize) {

                sortSelectRange(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }





            if (Math.min(kb - l, r - ka) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }

            if (maxDepth == 0) {

                heapSelectRange(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }


            int p0 = dualPivotingStrategy.pivotIndex(a, l, r, upper);
            p0 = part.partition(a, l, r, p0, upper[0], upper);
            final int p1 = upper[0];
            final int p2 = upper[1];
            final int p3 = upper[2];









            maxDepth--;

            if (ka < p0) {
                if (kb <= p1) {

                    r = p0 - 1;
                    if (r < kb) {
                        kb = k.updateRight(r);
                    }
                    continue;
                }
                introselect(part, a, l, p0 - 1, k.splitLeft(p0, p1), maxDepth);
                ka = k.left();
            } else if (kb <= p1) {

                return;
            } else if (ka <= p1) {

                ka = k.updateLeft(p1 + 1);
            }

            if (ka < p2) {
                l = p1 + 1;
                if (kb <= p3) {

                    r = p2 - 1;
                    if (r < kb) {
                        kb = k.updateRight(r);
                    }
                    continue;
                }
                introselect(part, a, l, p2 - 1, k.splitLeft(p2, p3), maxDepth);
                ka = k.left();
            } else if (kb <= p3) {

                return;
            } else if (ka <= p3) {
                ka = k.updateLeft(p3 + 1);
            }

            l = p3 + 1;
        }
    }


























    void introselect(DPPartition part, double[] a, int left, int right,
        SplittingInterval k, int maxDepth) {



        int l = left;
        int r = right;
        int ka = k.left();
        int kb = k.right();
        final int[] upper = {0, 0, 0};
        while (true) {

            final int n = r - l;

            if (n < minQuickSelectSize) {

                sortSelectRange(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }





            if (Math.min(kb - l, r - ka) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }

            if (maxDepth == 0) {

                heapSelectRange(a, l, r, ka, kb);
                recursionConsumer.accept(maxDepth);
                return;
            }


            int p0 = dualPivotingStrategy.pivotIndex(a, l, r, upper);
            p0 = part.partition(a, l, r, p0, upper[0], upper);
            final int p1 = upper[0];
            final int p2 = upper[1];
            final int p3 = upper[2];









            maxDepth--;
            SplittingInterval lk = k.split(p0, p1);

            if (lk != null) {

                if (k.empty()) {

                    r = p0 - 1;
                    kb = lk.right();
                    k = lk;
                    continue;
                }
                introselect(part, a, l, p0 - 1, lk, maxDepth);
            }
            if (k.empty()) {

                recursionConsumer.accept(maxDepth);
                return;
            }
            lk = k.split(p2, p3);

            if (lk != null) {

                if (k.empty()) {

                    l = p1 + 1;
                    r = p2 - 1;
                    ka = lk.left();
                    kb = lk.right();
                    k = lk;
                    continue;
                }
                introselect(part, a, p1 + 1, p2 - 1, lk, maxDepth);
            }
            if (k.empty()) {

                recursionConsumer.accept(maxDepth);
                return;
            }

            l = p3 + 1;
            ka = k.left();
        }
    }



































    void introselect(DPPartition part, double[] a, int left, int right,
        IndexIterator k, int ka, int kb, int maxDepth) {



        int l = left;
        final int r = right;
        int lo = ka;
        int hi = kb;
        final int[] upper = {0, 0, 0};
        while (true) {
            if (maxDepth == 0) {




                while (hi < right && k.next()) {
                    hi = k.right();
                }
                heapSelectRange(a, l, right, lo, hi);
                recursionConsumer.accept(maxDepth);
                return;
            }


            final int n = right - l;






            if (Math.min(hi - l, right - lo) < edgeSelectConstant) {
                if (hi - l > right - lo) {

                    edgeSelection.partition(a, l, right, lo, right);
                    recursionConsumer.accept(maxDepth);
                    return;
                } else if (k.nextAfter(right)) {




                    edgeSelection.partition(a, l, right, lo, hi);
                    recursionConsumer.accept(maxDepth);

                    l = hi + 1;
                    if (!k.positionAfter(hi) || Math.max(k.left(), l) > right) {

                        return;
                    }
                    lo = Math.max(k.left(), l);
                    hi = Math.min(right, k.right());

                    continue;
                }
            }






            if (n - (hi - lo) < minQuickSelectSize) {




                if (n < minQuickSelectSize) {


                    sortSelectRight(a, l, right, lo);
                } else {


                    Arrays.sort(a, l, right + 1);
                }
                recursionConsumer.accept(maxDepth);
                return;
            }



            int p0 = dualPivotingStrategy.pivotIndex(a, l, r, upper);
            p0 = part.partition(a, l, r, p0, upper[0], upper);
            final int p1 = upper[0];
            final int p2 = upper[1];
            final int p3 = upper[2];

            maxDepth--;

            if (lo < p0) {
                introselect(part, a, l, p0 - 1, k, lo, Math.min(hi, p0 - 1), maxDepth);

                if (!k.positionAfter(p1) || k.left() > right) {

                    return;
                }
                lo = k.left();
                hi = Math.min(right, k.right());
            }
            if (hi <= p1) {

                if (!k.positionAfter(p1) || k.left() > right) {

                    return;
                }
                lo = k.left();
                hi = Math.min(right, k.right());
            }


            l = p1 + 1;
            lo = Math.max(lo, l);
            if (lo < p2) {
                introselect(part, a, l, p2 - 1, k, lo, Math.min(hi, p2 - 1), maxDepth);

                if (!k.positionAfter(p3) || k.left() > right) {

                    return;
                }
                lo = k.left();
                hi = Math.min(right, k.right());
            }
            if (hi <= p3) {

                if (!k.positionAfter(p3) || k.left() > right) {

                    return;
                }
                lo = k.left();
                hi = Math.min(right, k.right());
            }


            l = p3 + 1;
            lo = Math.max(lo, l);
        }
    }
















    void partitionSBM(double[] data, int[] k, int n) {

        final int right = sortNaN(data);
        partition((SPEPartitionFunction) this::partitionSBMWithZeros, data, right, k, n);
    }





















    void partitionISP(double[] data, int[] k, int n) {
        introselect(getSPFunction(), data, k, n);
    }





















    void partitionIDP(double[] data, int[] k, int n) {
        introselect((DPPartition) Partition::partitionDP, data, k, n);
    }
























    void partitionFR(double[] a, int[] k, int count) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        int n = count;
        if (end > 1) {

            if (end < a.length) {
                for (int i = n; --i >= 0;) {
                    final int v = k[i];
                    if (v >= end) {

                        k[i] = k[--n];
                        k[n] = v;
                    }
                }
            }

            if (n != 0) {
                selectFR(a, 0, end - 1, k[0], controlFlags);
            }
        }

        t.postProcess(a, k, n);
    }




















    private void selectFR(double[] a, int left, int right, int k, int flags) {
        int l = left;
        int r = right;
        while (true) {








            if (Math.min(k - l, r - k) < edgeSelectConstant) {
                edgeSelection.partition(a, l, r, k, k);
                return;
            }




            int pivot = k;
            int p = l;
            int q = r;

            int n = r - l;
            if (n > 600) {
                ++n;
                final int ith = k - l + 1;
                final double z = Math.log(n);
                final double s = 0.5 * Math.exp(0.6666666666666666 * z);
                final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
                final int ll = Math.max(l, (int) (k - ith * s / n + sd));
                final int rr = Math.min(r, (int) (k + (n - ith) * s / n + sd));

                if ((flags & FLAG_SUBSET_SAMPLING) != 0) {



                    final IntUnaryOperator rng = createRNG(n, k);
                    final int rs = l + rr - ll;
                    for (int i = l - 1; i < rs;) {

                        final int j = r - rng.applyAsInt(r - i);
                        final double t = a[++i];
                        a[i] = a[j];
                        a[j] = t;
                    }
                    selectFR(a, l, rs, k - ll + l, flags);






                    p = k - ll + l;
                    q = r - rs + p;
                    vectorSwap(a, p + 1, rs, r);
                    vectorSwap(a, p, p, k);
                } else {




                    if ((flags & FLAG_RANDOM_SAMPLING) != 0) {





                        final IntUnaryOperator rng = createRNG(n, k);

                        if (ll > l) {
                            for (int i = k; i > ll;) {

                                final int j = l + rng.applyAsInt(i - l);
                                final double t = a[--i];
                                a[i] = a[j];
                                a[j] = t;
                            }
                        }

                        if (rr < r) {
                            for (int i = k; i < rr;) {

                                final int j = r - rng.applyAsInt(r - i);
                                final double t = a[++i];
                                a[i] = a[j];
                                a[j] = t;
                            }
                        }
                    }
                    selectFR(a, ll, rr, k, flags);








                    if ((flags & FLAG_MOVE_SAMPLE) != 0) {
                        vectorSwap(a, l, ll - 1, k - 1);
                        vectorSwap(a, k + 1, rr, r);
                        p += k - ll;
                        q -= rr - k;
                    }
                }
            } else {

                pivot = pivotingStrategy.pivotIndex(a, l, r, k);
            }








            final double t = a[pivot];

            a[pivot] = a[p];
            if (a[q] > t) {

                a[p] = a[q];
                a[q] = t;

            } else {
                a[p] = t;

            }
            int i = p;
            int j = q;
            while (i < j) {

                final double temp = a[i];
                a[i] = a[j];
                a[j] = temp;
                do {
                    ++i;
                } while (a[i] < t);
                do {
                    --j;
                } while (a[j] > t);
            }
            if (a[p] == t) {

                a[p] = a[j];
                a[j] = t;
            } else {

                a[q] = a[++j];
                a[j] = t;
            }

            if (k < j) {
                r = j - 1;
            } else if (k > j) {
                l = j + 1;
            } else {
                return;
            }
        }
    }
























    void partitionKFR(double[] a, int[] k, int count) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        int n = count;
        if (end > 1) {

            if (end < a.length) {
                for (int i = n; --i >= 0;) {
                    final int v = k[i];
                    if (v >= end) {

                        k[i] = k[--n];
                        k[n] = v;
                    }
                }
            }

            if (n != 0) {
                final int[] bounds = new int[5];
                selectKFR(a, 0, end - 1, k[0], bounds, null);
            }
        }

        t.postProcess(a, k, n);
    }
























    private void selectKFR(double[] x, int left, int right, int k, int[] bounds,
        IntUnaryOperator rng) {
        int l = left;
        int r = right;
        while (true) {








            if (Math.min(k - l, r - k) < edgeSelectConstant) {
                edgeSelection.partition(x, l, r, k, k);
                bounds[0] = bounds[1] = k;
                return;
            }


            int n = r - l;
            if (n < 600) {

                final int p0 = partitionKBM(x, l, r,
                    pivotingStrategy.pivotIndex(x, l, r, k), bounds);
                final int p1 = bounds[0];
                if (k < p0) {

                    r = p0 - 1;
                } else if (k > p1) {

                    l = p1 + 1;
                } else {

                    bounds[0] = p0;
                    bounds[1] = p1;
                    return;
                }
                continue;
            }


            ++n;

            final double z = Math.log(n);


            final double s = 0.5 * Math.exp(0.6666666666666666 * z) * Math.cbrt(z);


            final double g = Math.sqrt(0.25 * s * z);
            final int rs = (int) (l + s - 1);


            if (rng == null) {
                rng = createRNG(n, k);
            }
            for (int i = l - 1; i < rs;) {

                final int j = r - rng.applyAsInt(r - i);
                final double t = x[++i];
                x[i] = x[j];
                x[j] = t;
            }


            final double isn = (k - l + 1) * s / n;
            final int ku = (int) Math.max(Math.floor(l - 1 + isn - g), l);
            final int kv = (int) Math.min(Math.ceil(l - 1 + isn + g), rs);

            selectKFR(x, l, rs, ku, bounds, rng);
            final int kum = bounds[0];
            int kup = bounds[1];
            int kvm;
            int kvp;
            if (kup >= kv) {
                kvm = kv;
                kvp = kup;
                kup = kv - 1;

            } else {
                selectKFR(x, kup + 1, rs, kv, bounds, rng);
                kvm = bounds[0];
                kvp = bounds[1];
            }


            final double u = x[kup];
            final double v = x[kvm];


            final int ll = kum;
            int pp = kup;
            final int rr = r - rs + kvp;
            int qq = rr - kvp + kvm;
            vectorSwap(x, kvp + 1, rs, r);
            vectorSwap(x, kvm, kvp, rr);



            int a;
            int b;
            int c;
            int d;




            if (u == v) {


                a = partitionKBM(x, ll, rr, pp, bounds);
                d = bounds[0];

                b = d + 1;
                c = a - 1;
            } else if (k < (r + l) >>> 1) {















                int p = kvm - 1;
                int q = qq;
                int i = p;
                int j = q;
                for (;;) {
                    while (x[++i] < v) {
                        if (x[i] < u) {
                            continue;
                        }

                        final double xi = x[i];
                        x[i] = x[++p];
                        if (xi > u) {
                            x[p] = xi;
                        } else {
                            x[p] = x[++pp];
                            x[pp] = xi;
                        }
                    }
                    while (x[--j] >= v) {
                        if (x[j] == v) {
                            final double xj = x[j];
                            x[j] = x[--q];
                            x[q] = xj;
                        }
                    }

                    if (i >= j) {
                        break;
                    }

                    final double xi = x[j];
                    final double xj = x[i];
                    x[i] = xi;
                    x[j] = xj;
                    if (xi > u) {
                        x[i] = x[++p];
                        x[p] = xi;
                    } else if (xi == u) {
                        x[i] = x[++p];
                        x[p] = x[++pp];
                        x[pp] = xi;
                    }
                    if (xj == v) {
                        x[j] = x[--q];
                        x[q] = xj;
                    }
                }
                a = ll + i - p - 1;
                b = a + pp + 1 - ll;
                d = rr - q + 1 + j;
                c = d - rr + q - 1;
                vectorSwap(x, pp + 1, p, j);


                vectorSwapL(x, ll, pp, b - 1, u);
                vectorSwapR(x, i, q - 1, rr, v);
            } else {













                int p = pp;
                int q = qq - kvm + kup + 1;
                int i = p;
                int j = q;
                vectorSwap(x, pp + 1, kvm - 1, qq - 1);
                for (;;) {
                    while (x[++i] <= u) {
                        if (x[i] == u) {
                            final double xi = x[i];
                            x[i] = x[++p];
                            x[p] = xi;
                        }
                    }
                    while (x[--j] > u) {
                        if (x[j] > v) {
                            continue;
                        }

                        final double xj = x[j];
                        x[j] = x[--q];
                        if (xj < v) {
                            x[q] = xj;
                        } else {
                            x[q] = x[--qq];
                            x[qq] = xj;
                        }
                    }

                    if (i >= j) {
                        break;
                    }

                    final double xi = x[j];
                    final double xj = x[i];
                    x[i] = xi;
                    x[j] = xj;
                    if (xi == u) {
                        x[i] = x[++p];
                        x[p] = xi;
                    }
                    if (xj < v) {
                        x[j] = x[--q];
                        x[q] = xj;
                    } else if (xj == v) {
                        x[j] = x[--q];
                        x[q] = x[--qq];
                        x[qq] = xj;
                    }
                }
                a = ll + i - p - 1;
                b = a + p + 1 - ll;
                d = rr - q + 1 + j;
                c = d - rr + qq - 1;
                vectorSwap(x, i, q - 1, qq - 1);


                vectorSwapL(x, ll, p, j, u);
                vectorSwapR(x, c + 1, qq - 1, rr, v);
            }




            if (a <= k) {
                l = b;
            }
            if (c < k) {
                l = d + 1;
            }
            if (k <= d) {
                r = c;
            }
            if (k < b) {
                r = a - 1;
            }
            if (l >= r) {
                if (l == r) {

                    bounds[0] = bounds[1] = k;
                } else {

                    bounds[0] = r + 1;
                    bounds[1] = l - 1;
                }
                return;
            }
        }
    }










    private static void vectorSwap(double[] x, int a, int b, int c) {
        for (int i = a - 1, j = c + 1, m = Math.min(b + 1 - a, c - b); --m >= 0;) {
            final double v = x[++i];
            x[i] = x[--j];
            x[j] = v;
        }
    }














    private static void vectorSwapL(double[] x, int a, int b, int c, double v) {
        for (int i = a - 1, j = c + 1, m = Math.min(b + 1 - a, c - b); --m >= 0;) {
            x[++i] = x[--j];
            x[j] = v;
        }
    }














    private static void vectorSwapR(double[] x, int a, int b, int c, double v) {
        for (int i = a - 1, j = c + 1, m = Math.min(b + 1 - a, c - b); --m >= 0;) {
            x[--j] = x[++i];
            x[i] = v;
        }
    }


























    void partitionLSP(double[] data, int[] k, int n) {
        linearSelect(getSPFunction(), data, k, n);
    }
























    private void linearSelect(SPEPartition part, double[] a, int[] k, int count) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        int n = count;
        if (end > 1) {

            if (end < a.length) {
                for (int i = n; --i >= 0;) {
                    final int v = k[i];
                    if (v >= end) {

                        k[i] = k[--n];
                        k[n] = v;
                    }
                }
            }
            if (n != 0) {
                final int ka = Math.min(k[0], k[n - 1]);
                final int kb = Math.max(k[0], k[n - 1]);
                linearSelect(part, a, 0, end - 1, ka, kb, new int[2]);
            }
        }

        t.postProcess(a, k, n);
    }































    private void linearSelect(SPEPartition part, double[] a, int left, int right, int ka, int kb,
            int[] bounds) {
        int l = left;
        int r = right;
        while (true) {





            if (Math.min(kb - l, r - ka) < linearSortSelectSize) {
                sortSelectRange(a, l, r, ka, kb);


                bounds[0] = ka;
                bounds[1] = kb;
                return;
            }
            int p0 = pivotMedianOfMedians(part, a, l, r, bounds);
            if ((controlFlags & FLAG_MOVE_SAMPLE) != 0) {
















                final int lp = bounds[0];
                final int rr = bounds[1];
                vectorSwap(a, p0 + 1, rr, r);

                final int p = part.partition(a, p0, r - rr + p0, p0, bounds);






                vectorSwapL(a, lp, p0 - 1, p - 1, a[p]);
                p0 = p - p0 + lp;
            } else {
                p0 = part.partition(a, l, r, p0, bounds);
            }
            final int p1 = bounds[0];






            if (kb < p0) {

                r = p0 - 1;
            } else if (ka > p1) {

                l = p1 + 1;
            } else {




                bounds[0] = p0;
                bounds[1] = p1;
                if (ka < p0) {
                    sortSelectRight(a, l, p0 - 1, ka);
                    bounds[0] = ka;
                }
                if (kb > p1) {
                    sortSelectLeft(a, p1 + 1, r, kb);
                    bounds[1] = kb;
                }
                return;
            }
        }
    }


















    private int pivotMedianOfMedians(SPEPartition part, double[] a, int l, int r, int[] bounds) {


        int rr = l - 1;
        for (int e = l + 5;; e += 5) {
            if (e > r) {

                Sorting.sort(a, e - 5, r);
                final int m = (e - 5 + r) >>> 1;
                final double v = a[m];
                a[m] = a[++rr];
                a[rr] = v;
                break;
            }











            final int m = e - 3;


            Sorting.median5d(a, e - 5, e - 4, e - 3, e - 2, e - 1);

            final double v = a[m];
            a[m] = a[++rr];
            a[rr] = v;
        }

        int m = (l + rr + 1) >>> 1;

        linearSelect(part, a, l, rr, m, m, bounds);


        m = bounds[1];
        bounds[1] = rr;
        return m;
    }























    void partitionLinear(double[] data, int[] k, int n) {
        quickSelect(linearSpFunction, data, k, n);
    }



























    private void quickSelect(SPEPartition part, double[] a, int[] k, int count) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        int n = count;
        if (end > 1) {

            if (end < a.length) {
                for (int i = n; --i >= 0;) {
                    final int v = k[i];
                    if (v >= end) {

                        k[i] = k[--n];
                        k[n] = v;
                    }
                }
            }
            if (n != 0) {
                final int ka = Math.min(k[0], k[n - 1]);
                final int kb = Math.max(k[0], k[n - 1]);
                quickSelect(part, a, 0, end - 1, ka, kb, new int[2]);
            }
        }

        t.postProcess(a, k, n);
    }
































    private void quickSelect(SPEPartition part, double[] a, int left, int right, int ka, int kb,
            int[] bounds) {
        int l = left;
        int r = right;
        while (true) {









            if (Math.min(kb - l, r - ka) < linearSortSelectSize) {
                sortSelectRange(a, l, r, ka, kb);


                bounds[0] = ka;
                bounds[1] = kb;
                return;
            }

            final int p0 = part.partition(a, l, r, ka, bounds);
            final int p1 = bounds[0];






            if (kb < p0) {

                r = p0 - 1;
            } else if (ka > p1) {

                l = p1 + 1;
            } else {




                bounds[0] = p0;
                bounds[1] = p1;
                if (ka < p0) {
                    sortSelectRight(a, l, p0 - 1, ka);
                    bounds[0] = ka;
                }
                if (kb > p1) {
                    sortSelectLeft(a, p1 + 1, r, kb);
                    bounds[1] = kb;
                }
                return;
            }
        }
    }





















    void partitionQA(double[] data, int[] k, int n) {
        quickSelectAdaptive(data, k, n);
    }





















    private void quickSelectAdaptive(double[] a, int[] k, int count) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        int n = count;
        if (end > 1) {

            if (end < a.length) {
                for (int i = n; --i >= 0;) {
                    final int v = k[i];
                    if (v >= end) {

                        k[i] = k[--n];
                        k[n] = v;
                    }
                }
            }
            if (n != 0) {
                final int ka = Math.min(k[0], k[n - 1]);
                final int kb = Math.max(k[0], k[n - 1]);
                quickSelectAdaptive(a, 0, end - 1, ka, kb, new int[1], adaptMode);
            }
        }

        t.postProcess(a, k, n);
    }

































    private int quickSelectAdaptive(double[] a, int left, int right, int ka, int kb,
            int[] bounds, AdaptMode mode) {
        int l = left;
        int r = right;
        AdaptMode m = mode;
        while (true) {





            if (Math.min(kb - l, r - ka) < linearSortSelectSize) {
                sortSelectRange(a, l, r, ka, kb);
                bounds[0] = kb;
                return ka;
            }


            int p0;
            int n = r - l;

            final double f = (double) (ka - l) / n;






            if (f <= STEP_LEFT) {
                if (m.isSampleMode() && n > subSamplingSize) {

                    if (f <= STEP_FAR_LEFT) {
                        n -= (n >> 2) + (n >> 5) + (n >> 6);
                    } else {
                        n -= (n >> 2) + (n >> 3);
                    }
                    p0 = sampleStep(a, l, r, ka, bounds, m);
                } else if (f <= STEP_FAR_LEFT) {
                    if ((controlFlags & FLAG_QA_FAR_STEP) != 0) {

                        n -= (n >> 2) + (n >> 5) + (n >> 6);
                        p0 = repeatedStepFarLeft(a, l, r, ka, bounds, m);
                    } else {

                        n -= (n >> 2) + (n >> 3);
                        p0 = repeatedStepLeft(a, l, r, ka, bounds, m, true);
                    }
                } else {

                    n -= n >> 2;
                    p0 = repeatedStepLeft(a, l, r, ka, bounds, m, false);
                }
            } else if (f >= STEP_RIGHT) {
                if (m.isSampleMode() && n > subSamplingSize) {

                    if (f >= STEP_FAR_RIGHT) {
                        n -= (n >> 2) + (n >> 5) + (n >> 6);
                    } else {
                        n -= (n >> 2) + (n >> 3);
                    }
                    p0 = sampleStep(a, l, r, ka, bounds, m);
                } else if (f >= STEP_FAR_RIGHT) {
                    if ((controlFlags & FLAG_QA_FAR_STEP) != 0) {

                        n -= (n >> 2) + (n >> 5) + (n >> 6);
                        p0 = repeatedStepFarRight(a, l, r, ka, bounds, m);
                    } else {

                        n -= (n >> 2) + (n >> 3);
                        p0 = repeatedStepRight(a, l, r, ka, bounds, m, true);
                    }
                } else {

                    n -= n >> 2;
                    p0 = repeatedStepRight(a, l, r, ka, bounds, m, false);
                }
            } else {
                if (m.isSampleMode() && n > subSamplingSize) {

                    p0 = sampleStep(a, l, r, ka, bounds, m);
                } else {
                    p0 = repeatedStep(a, l, r, ka, bounds, m);
                }

                n -= (n >> 2) - (n >> 5);
            }






            final int p1 = bounds[0];
            if (kb < p0) {

                r = p0 - 1;
            } else if (ka > p1) {

                l = p1 + 1;
            } else {




                if (kb > p1) {
                    sortSelectLeft(a, p1 + 1, r, kb);
                    bounds[0] = kb;
                }
                if (ka < p0) {
                    sortSelectRight(a, l, p0 - 1, ka);
                    p0 = ka;
                }
                return p0;
            }

            m = m.update(n, l, r);
        }
    }

































    static void partitionQA2(double[] data, int[] k, int n) {
        quickSelectAdaptive2(data, k, n, qaMode);
    }






















    static void quickSelectAdaptive2(double[] a, int[] k, int count, int flags) {

        final DoubleDataTransformer t = SORT_TRANSFORMER.get();

        t.preProcess(a);
        final int end = t.length();
        int n = count;
        if (end > 1) {

            if (end < a.length) {
                for (int i = n; --i >= 0;) {
                    final int v = k[i];
                    if (v >= end) {

                        k[i] = k[--n];
                        k[n] = v;
                    }
                }
            }
            if (n != 0) {
                final int ka = Math.min(k[0], k[n - 1]);
                final int kb = Math.max(k[0], k[n - 1]);
                quickSelectAdaptive2(a, 0, end - 1, ka, kb, new int[1], flags);
            }
        }

        t.postProcess(a, k, n);
    }

































    private static int quickSelectAdaptive2(double[] a, int left, int right, int ka, int kb,
            int[] bounds, int flags) {
        int l = left;
        int r = right;
        int m = flags;
        while (true) {


            if (Math.min(kb - l, r - ka) < LINEAR_SORTSELECT_SIZE) {
                sortSelectRange(a, l, r, ka, kb);
                bounds[0] = kb;
                return ka;
            }


            int p0;
            final int n = r - l;

            final double f = (double) (ka - l) / n;





            int margin = n >> 2;
            if (m < MODE_SAMPLING && r - l > SELECT_SUB_SAMPLING_SIZE) {

                p0 = sampleStep(a, l, r, ka, bounds, flags);
                if (f <= STEP_FAR_LEFT || f >= STEP_FAR_RIGHT) {
                    margin += (n >> 5) + (n >> 6);
                } else if (f > STEP_LEFT && f < STEP_RIGHT) {
                    margin -= n >> 5;
                }
            } else if (f <= STEP_LEFT) {
                if (f <= STEP_FAR_LEFT) {
                    margin += (n >> 5) + (n >> 6);
                    p0 = repeatedStepFarLeft(a, l, r, ka, bounds, m);
                } else {
                    p0 = repeatedStepLeft(a, l, r, ka, bounds, m);
                }
            } else if (f >= STEP_RIGHT) {
                if (f >= STEP_FAR_RIGHT) {
                    margin += (n >> 5) + (n >> 6);
                    p0 = repeatedStepFarRight(a, l, r, ka, bounds, m);
                } else {
                    p0 = repeatedStepRight(a, l, r, ka, bounds, m);
                }
            } else {
                margin -= n >> 5;
                p0 = repeatedStep(a, l, r, ka, bounds, m);
            }






            final int p1 = bounds[0];
            if (kb < p0) {

                r = p0 - 1;
            } else if (ka > p1) {

                l = p1 + 1;
            } else {




                if (kb > p1) {
                    sortSelectLeft(a, p1 + 1, r, kb);
                    bounds[0] = kb;
                }
                if (ka < p0) {
                    sortSelectRight(a, l, p0 - 1, ka);
                    p0 = ka;
                }
                return p0;
            }

            m += r - l > n - margin ? qaIncrement : 0;
        }
    }





















    private int partitionSBMWithZeros(double[] data, int left, int right, int[] upper,
        boolean leftInner, boolean rightInner) {


























        if (right - left <= minQuickSelectSize) {
            Sorting.sort(data, left, right, leftInner);
            fixContinuousSignedZeros(data, left, right);
            upper[0] = right;
            return left;
        }

        final int l = left;
        final int r = right;

        int p = l;
        int q = r;



        final int pivot = pivotingStrategy.pivotIndex(data, left, right, -1);
        final double v = data[pivot];
        data[pivot] = data[r];
        data[r] = v;


        int c = 0;
        if (v == 0) {
            c = countMixedSignedZeros(data, left, right);
        }


        while (data[p] == v) {
            if (++p == q) {

                if (c != 0) {
                    sortZero(data, left, right);
                }
                upper[0] = right;
                return left;
            }
        }

        while (data[q - 1] == v) {
            q--;
        }

        int i = p - 1;
        int j = q;

        for (;;) {
            do {
                ++i;
            } while (data[i] < v);
            while (v < data[--j]) {
                if (j == l) {
                    break;
                }
            }
            if (i >= j) {



                if (i == j && v == data[i]) {

                    data[i] = data[p];
                    data[p] = v;
                    i++;
                    p++;
                }
                break;
            }

            final double vi = data[j];
            final double vj = data[i];
            data[i] = vi;
            data[j] = vj;

            if (vi == v) {

                data[i] = data[p];
                data[p] = v;
                p++;
            }
            if (vj == v) {

                data[j] = data[--q];
                data[q] = v;
            }
        }




        data[r] = data[i];
        data[i] = v;



        j = i++;









        int move = Math.min(p - l, j - p);
        final int lower = j - (p - l);
        for (int k = l; move-- > 0; k++) {
            data[k] = data[--j];
            data[j] = v;
        }

        move = Math.min(r - q, q - i);
        upper[0] = i + (r - q) - 1;
        for (int k = r; move-- > 0; i++) {
            data[--k] = data[i];
            data[i] = v;
        }


        if (c != 0) {
            p = lower;
            while (c-- > 0) {
                data[p++] = -0.0;
            }
            while (p <= upper[0]) {
                data[p++] = 0.0;
            }
        }


        return lower;
    }

















    private static int partitionSP(double[] data, int l, int r, int pivot, int[] upper) {















        final double v = data[pivot];

        data[pivot] = data[l];
        if (data[r] > v) {

            data[l] = data[r];
            data[r] = v;

        } else {
            data[l] = v;

        }
        int i = l;
        int j = r;
        while (i < j) {

            final double temp = data[i];
            data[i] = data[j];
            data[j] = temp;
            do {
                ++i;
            } while (data[i] < v);
            do {
                --j;
            } while (data[j] > v);
        }

        if (data[l] == v) {

            data[l] = data[j];
            data[j] = v;
        } else {

            data[r] = data[++j];
            data[j] = v;
        }
        upper[0] = j;
        return j;
    }















    private static int partitionBM(double[] data, int l, int r, int pivot, int[] upper) {






















        int i = l;
        int j = r;
        int p = l;
        int q = r;

        final double v = data[pivot];

        for (;;) {
            while (i <= j && data[i] <= v) {
                if (data[i] == v) {

                    data[i] = data[p];
                    data[p] = v;
                    p++;
                }
                i++;
            }
            while (j >= i && data[j] >= v) {
                if (v == data[j]) {

                    data[j] = data[q];
                    data[q] = v;
                    q--;
                }
                j--;
            }
            if (i > j) {
                break;
            }

            final double tmp = data[j];
            data[j] = data[i];
            data[i] = tmp;
        }


        int s = Math.min(p - l, i - p);
        for (int k = l; s > 0; k++, s--) {

            data[k] = data[i - s];
            data[i - s] = v;
        }
        s = Math.min(q - j, r - q);
        for (int k = i; --s >= 0; k++) {

            data[r - s] = data[k];
            data[k] = v;
        }


        i = i - p + l;
        j = j - q + r;
        upper[0] = j;

        return i;
    }















    static int partitionSBM(double[] data, int l, int r, int pivot, int[] upper) {




























        final double v = data[pivot];
        data[pivot] = data[r];
        data[r] = v;

        int p = l;
        int q = r;


        while (data[p] == v) {
            if (++p == q) {

                upper[0] = r;
                return l;
            }
        }

        while (data[q - 1] == v) {
            q--;
        }

        int i = p - 1;
        int j = q;

        for (;;) {
            do {
                ++i;
            } while (data[i] < v);
            while (v < data[--j]) {

                if (j == l) {
                    break;
                }
            }
            if (i >= j) {



                if (i == j && v == data[i]) {

                    data[i] = data[p];
                    data[p] = v;
                    i++;
                    p++;
                }
                break;
            }

            final double vi = data[j];
            final double vj = data[i];
            data[i] = vi;
            data[j] = vj;

            if (vi == v) {

                data[i] = data[p];
                data[p] = v;
                p++;
            }
            if (vj == v) {

                data[j] = data[--q];
                data[q] = v;
            }
        }




        data[r] = data[i];
        data[i] = v;



        j = i++;









        int move = Math.min(p - l, j - p);
        final int lower = j - (p - l);
        for (int k = l; --move >= 0; k++) {
            data[k] = data[--j];
            data[j] = v;
        }

        move = Math.min(r - q, q - i);
        upper[0] = i + (r - q) - 1;
        for (int k = r; --move >= 0; i++) {
            data[--k] = data[i];
            data[i] = v;
        }


        return lower;
    }















    static int partitionKBM(double[] x, int l, int r, int pivot, int[] upper) {




































        final double v = x[pivot];
        x[pivot] = x[l];
        x[l] = v;

        int ll = l;
        int rr = r;


        if (v < x[r]) {
            --rr;
        } else if (v > x[r]) {
            x[l] = x[r];
            x[r] = v;
            ++ll;
        }



        int p = l;
        while (x[p + 1] == v) {
            if (++p == rr) {





                upper[0] = rr;
                return l;
            }
        }

        int q = r;
        while (x[q - 1] == v) {
            --q;
        }



        int i = p;
        int j = q;

        for (;;) {
            do {
                ++i;
            } while (x[i] < v);
            do {
                --j;
            } while (x[j] > v);

            if (i >= j) {
                if (i == j) {

                    ++i;
                    --j;
                }
                break;
            }

            final double vi = x[j];
            final double vj = x[i];
            x[i] = vi;
            x[j] = vj;

            if (vi == v) {
                x[i] = x[++p];
                x[p] = v;
            }
            if (vj == v) {
                x[j] = x[--q];
                x[q] = v;
            }
        }


        final int a = ll + j - p;
        upper[0] = rr - q + i;






        for (int m = Math.min(p + 1 - ll, j - p); --m >= 0; ++ll, --j) {
            x[ll] = x[j];
            x[j] = v;
        }

        for (int m = Math.min(q - i, rr - q + 1); --m >= 0; ++i, --rr) {
            x[rr] = x[i];
            x[i] = v;
        }
        return a;
    }















    private static int partitionDNF1(double[] data, int left, int right, int pivot, int[] upper) {













        final double value = data[pivot];


        int lt = left;
        while (data[lt] < value) {
            lt++;
        }


        lt--;
        int gt = right + 1;


        for (int i = lt; ++i < gt;) {
            final double v = data[i];
            if (v < value) {
                data[++lt] = v;
            } else if (v > value) {
                data[i] = data[--gt];
                data[gt] = v;

                i--;
            }

        }


        ++lt;
        upper[0] = --gt;


        for (int i = lt; i <= gt; i++) {
            data[i] = value;
        }

        return lt;
    }















    private static int partitionDNF2(double[] data, int left, int right, int pivot, int[] upper) {













        final double value = data[pivot];


        int lt = left;
        while (data[lt] < value) {
            lt++;
        }


        lt--;
        int gt = right + 1;



        for (int i = lt; ++i < gt;) {
            final double v = data[i];
            if (v < value) {
                data[++lt] = v;
            } else if (v > value) {

                do {
                    --gt;
                } while (gt > i && data[gt] > value);


                if (data[gt] < value) {
                    data[++lt] = data[gt];
                }

                data[gt] = v;
            }

        }


        ++lt;
        upper[0] = --gt;


        for (int i = lt; i <= gt; i++) {
            data[i] = value;
        }

        return lt;
    }















    private static int partitionDNF3(double[] data, int left, int right, int pivot, int[] upper) {













        final double value = data[pivot];


        int lt = left;
        while (data[lt] < value) {
            lt++;
        }


        lt--;
        int gt = right + 1;














        for (int i = lt; ++i < gt;) {
            final double v = data[i];
            if (v != value) {

                data[i] = value;
                if (v < value) {

                    data[++lt] = v;
                } else {


                    do {
                        --gt;
                    } while (data[gt] > value);


                    if (data[gt] < value) {
                        data[++lt] = data[gt];
                    }

                    data[gt] = v;
                }
            }
        }


        ++lt;
        upper[0] = --gt;




        return lt;
    }











































    static int partitionDP(double[] a, int left, int right, int pivot1, int pivot2, int[] bounds) {

        if (pivot1 == pivot2) {



            final int lower = partitionDNF3(a, left, right, pivot1, bounds);

            bounds[2] = bounds[0];


            bounds[1] = lower;
            return lower;
        }



































        final double v1 = a[pivot1];
        final double v2 = a[pivot2];


        a[pivot1] = a[left];
        a[pivot2] = a[right];
        a[left] = v1;
        a[right] = v2;


        int less = left;
        int great = right;



        do {
            ++less;
        } while (a[less] < v1);
        do {
            --great;
        } while (a[great] > v2);



        SORTING:
        for (int k = less - 1; ++k <= great;) {
            final double v = a[k];
            if (v < v1) {

                a[k] = a[less];
                a[less] = v;
                less++;
            } else if (v > v2) {


                while (a[great] > v2) {
                    if (great-- == k) {

                        break SORTING;
                    }
                }



                final double w = a[great];
                a[great] = v;
                great--;

                if (w < v1) {
                    a[k] = a[less];
                    a[less] = w;
                    less++;
                } else {
                    a[k] = w;
                }
            }
        }


        less--;
        great++;

        a[left] = a[less];
        a[less] = v1;
        a[right] = a[great];
        a[great] = v2;


        final int lower = less;
        bounds[2] = great;













        if ((great - less) > ((right - left) >>> 1) + ((right - left) >>> 3) && v1 != v2) {



            do {
                ++less;
            } while (a[less] == v1);
            do {
                --great;
            } while (a[great] == v2);


            EQUAL:
            for (int k = less - 1; ++k <= great;) {
                final double v = a[k];
                if (v == v1) {
                    a[k] = a[less];
                    a[less] = v;
                    less++;
                } else if (v == v2) {
                    while (a[great] == v2) {
                        if (great-- == k) {

                            break EQUAL;
                        }
                    }
                    final double w = a[great];
                    a[great] = v;
                    great--;
                    if (w == v1) {
                        a[k] = a[less];
                        a[less] = w;
                        less++;
                    } else {
                        a[k] = w;
                    }
                }
            }


            less--;
            great++;
        }


        if (v1 < v2 && less < great - 1) {

            bounds[0] = less;
            bounds[1] = great;
        } else {

            bounds[0] = bounds[2];
            bounds[1] = lower;
        }

        return lower;
    }

























    private static int expandPartitionT1(double[] a, int left, int right, int start, int end,
        int pivot0, int pivot1, int[] upper) {



















        int p0 = pivot0;
        int p1 = pivot1;
        final double v = a[p0];
        if (a[left] < v) {

            final double w = a[left];
            if (a[right] > v) {

                a[left] = a[right];
                a[right] = w;
            } else {

                a[left] = v;
                a[p0] = w;
                p0++;
            }
        } else if (a[right] > v) {

            a[p1] = a[right];
            p1--;
            a[right] = v;
        }


        assert left < start && end < right;
        int i = start;
        int j = end;
        while (true) {
            do {
                --i;
            } while (a[i] < v);
            do {
                ++j;
            } while (a[j] > v);
            final double vj = a[i];
            final double vi = a[j];
            a[i] = vi;
            a[j] = vj;

            if (vi == v) {
                a[i] = a[--p0];
                a[p0] = v;
            }
            if (vj == v) {
                a[j] = a[++p1];
                a[p1] = v;
            }




            if (i == left) {
                while (j < right) {
                    do {
                        ++j;
                    } while (a[j] > v);
                    final double w = a[j];

                    a[j] = a[++p1];
                    a[p1] = v;
                    if (w != v) {

                        a[p0] = w;
                        p0++;
                    }
                }
                break;
            }
            if (j == right) {
                while (i > left) {
                    do {
                        --i;
                    } while (a[i] < v);
                    final double w = a[i];

                    a[i] = a[--p0];
                    a[p0] = v;
                    if (w != v) {

                        a[p1] = w;
                        p1--;
                    }
                }
                break;
            }
        }

        upper[0] = p1;
        return p0;
    }


























    private static int expandPartitionB1(double[] a, int left, int right, int start, int end,
        int pivot0, int pivot1, int[] upper) {

















        int p = pivot0;
        final double v = a[p];
        if (a[left] < v) {

            final double w = a[left];
            if (a[right] > v) {

                a[left] = a[right];
                a[right] = w;
            } else {

                a[left] = v;
                a[p] = w;
                p++;
            }
        } else if (a[right] > v) {

            a[p] = a[right];
            p--;
            a[right] = v;
        }


        assert left < start && end < right;
        int i = start;
        int j = end;
        while (true) {
            do {
                --i;
            } while (a[i] < v);
            do {
                ++j;
            } while (a[j] > v);
            final double vj = a[i];
            final double vi = a[j];
            a[i] = vi;
            a[j] = vj;


            if (i == left) {

                if (p < pivot0) {

                    a[right] = a[p];
                    a[p] = v;
                } else if (p > pivot0) {

                    a[j] = a[p];
                    a[p] = v;
                }
                if (j == right) {
                    break;
                }
                while (j < right) {
                    do {
                        ++j;
                    } while (a[j] > v);

                    a[p] = a[j];
                    a[j] = a[++p];
                    a[p] = v;
                }
                break;
            }
            if (j == right) {

                if (p < pivot0) {

                    a[i] = a[p];
                    a[p] = v;
                } else if (p > pivot0) {

                    a[left] = a[p];
                    a[p] = v;
                }
                if (i == left) {
                    break;
                }
                while (i > left) {
                    do {
                        --i;
                    } while (a[i] < v);

                    a[p] = a[i];
                    a[i] = a[--p];
                    a[p] = v;
                }
                break;
            }
        }

        upper[0] = p;
        return p;
    }




























    private static int expandPartitionT2(double[] a, int left, int right, int start, int end,
        int pivot0, int pivot1, int[] upper) {


















        final double v = a[pivot0];


        assert start != end;
        double vi = a[start];
        double vj = a[end];
        a[start] = a[left];
        a[end] = a[right];
        a[left] = vj;
        a[right] = vi;

        int i = start + 1;
        int j = end - 1;


        int p0 = pivot0 == start ? i : pivot0;
        int p1 = pivot1 == end ? j : pivot1;

        while (true) {
            do {
                --i;
            } while (a[i] < v);
            do {
                ++j;
            } while (a[j] > v);
            vj = a[i];
            vi = a[j];
            a[i] = vi;
            a[j] = vj;

            if (vi == v) {
                a[i] = a[--p0];
                a[p0] = v;
            }
            if (vj == v) {
                a[j] = a[++p1];
                a[p1] = v;
            }




            if (i == left) {
                while (j < right) {
                    do {
                        ++j;
                    } while (a[j] > v);
                    final double w = a[j];

                    a[j] = a[++p1];
                    a[p1] = v;


                    if (w != v) {
                        a[p0] = w;
                        p0++;
                    }
                }
                break;
            }
            if (j == right) {
                while (i > left) {
                    do {
                        --i;
                    } while (a[i] < v);
                    final double w = a[i];

                    a[i] = a[--p0];
                    a[p0] = v;


                    if (w != v) {
                        a[p1] = w;
                        p1--;
                    }
                }
                break;
            }
        }

        upper[0] = p1;
        return p0;
    }




























    private static int expandPartitionB2(double[] a, int left, int right, int start, int end,
        int pivot0, int pivot1, int[] upper) {

















        int p = pivot0;
        final double v = a[p];


        assert start != end;





        if (p == start || p == end) {
            return expandPartitionB1(a, left, right, start, end, pivot0, pivot1, upper);
        }
        double vi = a[start];
        double vj = a[end];
        a[start] = a[left];
        a[end] = a[right];
        a[left] = vj;
        a[right] = vi;

        int i = start + 1;
        int j = end - 1;
        while (true) {
            do {
                --i;
            } while (a[i] < v);
            do {
                ++j;
            } while (a[j] > v);
            vj = a[i];
            vi = a[j];
            a[i] = vi;
            a[j] = vj;

            if (i == left) {
                while (j < right) {
                    do {
                        ++j;
                    } while (a[j] > v);

                    a[p] = a[j];
                    a[j] = a[++p];
                    a[p] = v;
                }
                break;
            }
            if (j == right) {
                while (i > left) {
                    do {
                        --i;
                    } while (a[i] < v);

                    a[p] = a[i];
                    a[i] = a[--p];
                    a[p] = v;
                }
                break;
            }
        }

        upper[0] = p;
        return p;
    }





















    private int linearBFPRTBaseline(double[] a, int l, int r, int k, int[] upper) {



        int rr = l - 1;
        for (int e = l + 4; e <= r; e += 5) {
            Sorting.median5d(a, e - 4, e - 3, e - 2, e - 1, e);

            final double v = a[e - 2];
            a[e - 2] = a[++rr];
            a[rr] = v;
        }
        final int m = (l + rr + 1) >>> 1;

        quickSelect(this::linearBFPRTBaseline, a, l, rr, m, m, upper);

        return spFunction.partition(a, l, r, m, upper);
    }





















    private int linearRepeatedStepBaseline(double[] a, int l, int r, int k, int[] upper) {



        int j = l - 1;
        for (int e = l + 2; e <= r; e += 3) {
            Sorting.sort3(a, e - 2, e - 1, e);

            final double v = a[e - 1];
            a[e - 1] = a[++j];
            a[j] = v;
        }
        int rr = l - 1;
        for (int e = l + 2; e <= j; e += 3) {
            Sorting.sort3(a, e - 2, e - 1, e);

            final double v = a[e - 1];
            a[e - 1] = a[++rr];
            a[rr] = v;
        }
        final int m = (l + rr + 1) >>> 1;

        quickSelect(this::linearRepeatedStepBaseline, a, l, rr, m, m, upper);

        return spFunction.partition(a, l, r, m, upper);
    }





















    private int linearBFPRTImproved(double[] a, int l, int r, int k, int[] upper) {



        final int f = (r - l + 1) / 5;
        final int f3 = 3 * f;

        final int s = l + (f << 1);
        final int e = s + f - 1;
        for (int i = l, j = s; i < s; i += 2, j++) {
            Sorting.median5d(a, i, i + 1, j, f3 + i, f3 + i + 1);
        }


        final int p = s + noSamplingAdapt.mapDistance(k - l, l, r, f);

        quickSelect(this::linearBFPRTImproved, a, s, e, p, p, upper);
        return expandFunction.partition(a, l, r, s, e, upper[0], upper[1], upper);
    }





















    private int linearRepeatedStepImproved(double[] a, int l, int r, int k, int[] upper) {



        final int f = (r - l + 1) / 9;
        final int f3 = 3 * f;

        for (int i = l + f3, e = l + (f3 << 1); i < e; i++) {
            Sorting.sort3(a, i - f3, i, i + f3);
        }

        final int s = l + (f << 2);
        final int e = s + f - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - f, i, i + f);
        }


        final int p = s + noSamplingAdapt.mapDistance(k - l, l, r, f);

        quickSelect(this::linearRepeatedStepImproved, a, s, e, p, p, upper);
        return expandFunction.partition(a, l, r, s, e, upper[0], upper[1], upper);
    }





















    private int repeatedStep(double[] a, int l, int r, int k, int[] upper, AdaptMode mode) {


        int f;
        int s;
        int p;
        if (!mode.isSampleMode()) {

            f = (r - l + 1) / 9;
            final int f3 = 3 * f;
            for (int i = l + f3, end = l + (f3 << 1); i < end; i++) {
                Sorting.sort3(a, i - f3, i, i + f3);
            }

            s = l + (f << 2);
            p = s + (mode.isAdapt() ? noSamplingAdapt.mapDistance(k - l, l, r, f) : (f >>> 1));
        } else {
            if ((controlFlags & FLAG_QA_MIDDLE_12) != 0) {

                f = (r - l + 1) / 12;

                s = ((r + l) >>> 1) - (f >> 1);
            } else {
                f = (r - l + 1) / 9;
                s = l + (f << 2);
            }

            int kp = mode.isAdapt() ? samplingAdapt.mapDistance(k - l, l, r, f) : (f >>> 1);

            if ((controlFlags & FLAG_QA_SAMPLE_K) != 0) {
                s = k - kp;
            }
            p = s + kp;
        }
        final int e = s + f - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - f, i, i + f);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper,
            (controlFlags & FLAG_QA_PROPAGATE) != 0 ? mode : adaptMode);
        return expandFunction.partition(a, l, r, s, e, p, upper[0], upper);
    }























    private int repeatedStepLeft(double[] a, int l, int r, int k, int[] upper, AdaptMode mode,
        boolean far) {


        final int f = (r - l + 1) >> 2;
        if (!mode.isSampleMode()) {

            final int f2 = f + f;
            for (int i = l + f, e = l + f2; i < e; i++) {
                Sorting.lowerMedian4(a, i - f, i, i + f, i + f2);
            }
        }
        int fp = f / 3;
        int s;
        int e;
        int p;
        if (far) {

            s = l + f;

            int kp;
            if (!mode.isSampleMode()) {
                kp = mode.isAdapt() ? noSamplingEdgeAdapt.mapDistance(k - l, l, r, fp) : fp >>> 1;
            } else {
                kp = mode.isAdapt() ? samplingEdgeAdapt.mapDistance(k - l, l, r, fp) : fp >>> 1;

            }
            e = s + fp - 1;
            p = s + kp;
            final int fp2 = fp << 1;
            for (int i = s; i <= e; i++) {

                if (a[i] > a[i + fp]) {
                    final double u = a[i];
                    a[i] = a[i + fp];
                    a[i + fp] = u;
                }
                if (a[i] > a[i + fp2]) {
                    final double v = a[i];
                    a[i] = a[i + fp2];
                    a[i + fp2] = v;
                }
            }
        } else {

            s = l + f + fp;

            int kp;
            if (!mode.isSampleMode()) {
                kp = mode.isAdapt() ? noSamplingAdapt.mapDistance(k - l, l, r, fp) : fp >>> 1;
            } else {
                kp = mode.isAdapt() ? samplingAdapt.mapDistance(k - l, l, r, fp) : fp >>> 1;

                if ((controlFlags & FLAG_QA_SAMPLE_K) != 0) {

                    s = Math.max(k - kp, l + fp);
                }
            }
            e = s + fp - 1;
            p = s + kp;
            for (int i = s; i <= e; i++) {
                Sorting.sort3(a, i - fp, i, i + fp);
            }
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper,
            (controlFlags & FLAG_QA_PROPAGATE) != 0 ? mode : adaptMode);
        return expandFunction.partition(a, l, r, s, e, p, upper[0], upper);
    }























    private int repeatedStepRight(double[] a, int l, int r, int k, int[] upper, AdaptMode mode,
        boolean far) {

        final int f = (r - l + 1) >> 2;
        if (!mode.isSampleMode()) {

            final int f2 = f + f;
            for (int i = r - f, e = r - f2; i > e; i--) {
                Sorting.upperMedian4(a, i - f2, i - f, i, i + f);
            }
        }
        int fp = f / 3;
        int s;
        int e;
        int p;
        if (far) {

            e = r - f;

            int kp;
            if (!mode.isSampleMode()) {
                kp = mode.isAdapt() ? noSamplingEdgeAdapt.mapDistance(r - k, l, r, fp) : fp >>> 1;
            } else {
                kp = mode.isAdapt() ? samplingEdgeAdapt.mapDistance(r - k, l, r, fp) : fp >>> 1;

            }
            s = e - fp + 1;
            p = e - kp;
            final int fp2 = fp << 1;
            for (int i = s; i <= e; i++) {

                if (a[i] < a[i - fp]) {
                    final double u = a[i];
                    a[i] = a[i - fp];
                    a[i - fp] = u;
                }
                if (a[i] < a[i - fp2]) {
                    final double v = a[i];
                    a[i] = a[i - fp2];
                    a[i - fp2] = v;
                }
            }
        } else {

            e = r - f - fp;

            int kp;
            if (!mode.isSampleMode()) {
                kp = mode.isAdapt() ? noSamplingAdapt.mapDistance(r - k, l, r, fp) : fp >>> 1;
            } else {
                kp = mode.isAdapt() ? samplingAdapt.mapDistance(r - k, l, r, fp) : fp >>> 1;

                if ((controlFlags & FLAG_QA_SAMPLE_K) != 0) {

                    e = Math.min(k + kp, r - fp);
                }
            }
            s = e - fp + 1;
            p = e - kp;
            for (int i = s; i <= e; i++) {
                Sorting.sort3(a, i - fp, i, i + fp);
            }
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper,
            (controlFlags & FLAG_QA_PROPAGATE) != 0 ? mode : adaptMode);
        return expandFunction.partition(a, l, r, s, e, p, upper[0], upper);
    }























    private int repeatedStepFarLeft(double[] a, int l, int r, int k, int[] upper, AdaptMode mode) {

        final int f = (r - l + 1) >> 2;
        int fp = f / 3;

        int s = l + fp;
        final int e = s + fp - 1;
        int p;
        if (!mode.isSampleMode()) {
            p = s + (mode.isAdapt() ? noSamplingEdgeAdapt.mapDistance(k - l, l, r, fp) : fp >>> 1);

            final int f2 = f + f;
            for (int i = l + f, end = l + f2; i < end; i++) {
                if (a[i + f] < a[i - f]) {
                    final double u = a[i + f];
                    a[i + f] = a[i - f];
                    a[i - f] = u;
                }
                if (a[i + f2] < a[i]) {
                    final double v = a[i + f2];
                    a[i + f2] = a[i];
                    a[i] = v;
                }
                if (a[i] < a[i - f]) {
                    final double u = a[i];
                    a[i] = a[i - f];
                    a[i - f] = u;
                }
            }
        } else {
            int kp = mode.isAdapt() ? samplingEdgeAdapt.mapDistance(k - l, l, r, fp) : fp >>> 1;
            p = s + kp;
        }
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper,
            (controlFlags & FLAG_QA_PROPAGATE) != 0 ? mode : adaptMode);
        return expandFunction.partition(a, l, r, s, e, p, upper[0], upper);
    }























    private int repeatedStepFarRight(double[] a, int l, int r, int k, int[] upper, AdaptMode mode) {

        final int f = (r - l + 1) >> 2;
        int fp = f / 3;

        int e = r - fp;
        final int s = e - fp + 1;
        int p;
        if (!mode.isSampleMode()) {
            p = e - (mode.isAdapt() ? noSamplingEdgeAdapt.mapDistance(r - k, l, r, fp) : fp >>> 1);

            final int f2 = f + f;
            for (int i = r - f, end = r - f2; i > end; i--) {
                if (a[i - f] > a[i + f]) {
                    final double u = a[i - f];
                    a[i - f] = a[i + f];
                    a[i + f] = u;
                }
                if (a[i - f2] > a[i]) {
                    final double v = a[i - f2];
                    a[i - f2] = a[i];
                    a[i] = v;
                }
                if (a[i] > a[i + f]) {
                    final double u = a[i];
                    a[i] = a[i + f];
                    a[i + f] = u;
                }
            }
        } else {
            int kp = mode.isAdapt() ? samplingEdgeAdapt.mapDistance(r - k, l, r, fp) : fp >>> 1;
            p = e - kp;
        }
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper,
            (controlFlags & FLAG_QA_PROPAGATE) != 0 ? mode : adaptMode);
        return expandFunction.partition(a, l, r, s, e, p, upper[0], upper);
    }




















    private int sampleStep(double[] a, int l, int r, int k, int[] upper, AdaptMode mode) {



        final int n = r - l + 1;
        final int ith = k - l + 1;
        final double z = Math.log(n);

        final double s = 0.5 * Math.exp(0.6666666666666666 * z);
        final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
        final int ll = Math.max(l, (int) (k - ith * s / n + sd));
        final int rr = Math.min(r, (int) (k + (n - ith) * s / n + sd));


        if ((controlFlags & FLAG_QA_RANDOM_SAMPLING) != 0) {
            final IntUnaryOperator rng = createRNG(n, k);
            if (ll == l) {

                for (int i = l - 1; i < rr;) {

                    final int j = r - rng.applyAsInt(r - i);
                    final double t = a[++i];
                    a[i] = a[j];
                    a[j] = t;
                }
            } else if (rr == r) {

                for (int i = r + 1; i > ll;) {

                    final int j = l + rng.applyAsInt(i - l);
                    final double t = a[--i];
                    a[i] = a[j];
                    a[j] = t;
                }
            } else {


                for (int i = k; i > ll;) {

                    final int j = l + rng.applyAsInt(i - l);
                    final double t = a[--i];
                    a[i] = a[j];
                    a[j] = t;
                }

                for (int i = k; i < rr;) {

                    final int j = r - rng.applyAsInt(r - i);
                    final double t = a[++i];
                    a[i] = a[j];
                    a[j] = t;
                }
            }
        } else if ((controlFlags & FLAG_RANDOM_SAMPLING) != 0) {
            final IntUnaryOperator rng = createRNG(n, k);

            if (ll > l) {
                for (int i = k; i > ll;) {

                    final int j = l + rng.applyAsInt(i - l);
                    final double t = a[--i];
                    a[i] = a[j];
                    a[j] = t;
                }
            }

            if (rr < r) {
                for (int i = k; i < rr;) {

                    final int j = r - rng.applyAsInt(r - i);
                    final double t = a[++i];
                    a[i] = a[j];
                    a[j] = t;
                }
            }
        }

        final int p = quickSelectAdaptive(a, ll, rr, k, k, upper,
            (controlFlags & FLAG_QA_PROPAGATE) != 0 ? mode : adaptMode);





        return expandFunction.partition(a, l, r, ll, rr, p, upper[0], upper);
    }















    private static int mapDistance(int d, int l, int r, int n) {
        return (int) (d * (n - 1.0) / (r - l));
    }























    private static int repeatedStep(double[] a, int l, int r, int k, int[] upper, int flags) {

        int fp;
        int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;

            s = k - mapDistance(k - l, l, r, fp);
            p = k;
        } else {

            fp = (r - l + 1) / 9;
            final int f3 = 3 * fp;
            for (int i = l + f3, end = l + (f3 << 1); i < end; i++) {
                Sorting.sort3(a, i - f3, i, i + f3);
            }

            s = l + (fp << 2);

            p = s + (flags == MODE_ADAPTION ? mapDistance(k - l, l, r, fp) : (fp >>> 1));
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive2(a, s, e, p, p, upper, qaMode);
        return expandPartitionT2(a, l, r, s, e, p, upper[0], upper);
    }
























    private static int repeatedStepLeft(double[] a, int l, int r, int k, int[] upper, int flags) {

        int fp;
        int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;


            s = Math.max(k - mapDistance(k - l, l, r, fp), l + fp);
            p = k;
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            for (int i = l + f, end = l + f2; i < end; i++) {
                Sorting.lowerMedian4(a, i - f, i, i + f, i + f2);
            }

            fp = f / 3;
            s = l + f + fp;

            p = s + (flags == MODE_ADAPTION ? mapDistance(k - l, l, r, fp) : (fp >>> 1));
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive2(a, s, e, p, p, upper, qaMode);
        return expandPartitionT2(a, l, r, s, e, p, upper[0], upper);
    }
























    private static int repeatedStepRight(double[] a, int l, int r, int k, int[] upper, int flags) {

        int fp;
        int e;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;


            e = Math.min(k + mapDistance(r - k, l, r, fp), r - fp);
            p = k;
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            for (int i = r - f, end = r - f2; i > end; i--) {
                Sorting.upperMedian4(a, i - f2, i - f, i, i + f);
            }

            fp = f / 3;
            e = r - f - fp;

            p = e - (flags == MODE_ADAPTION ? mapDistance(r - k, l, r, fp) : (fp >>> 1));
        }
        final int s = e - fp + 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive2(a, s, e, p, p, upper, qaMode);
        return expandPartitionT2(a, l, r, s, e, p, upper[0], upper);
    }























    private static int repeatedStepFarLeft(double[] a, int l, int r, int k, int[] upper, int flags) {









        int fp;
        int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;
            s = l + fp;

            p = s + mapDistance(k - l, l, r, fp);
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            for (int i = l + f, end = l + f2; i < end; i++) {
                if (a[i + f] < a[i - f]) {
                    final double u = a[i + f];
                    a[i + f] = a[i - f];
                    a[i - f] = u;
                }
                if (a[i + f2] < a[i]) {
                    final double v = a[i + f2];
                    a[i + f2] = a[i];
                    a[i] = v;
                }
                if (a[i] < a[i - f]) {
                    final double u = a[i];
                    a[i] = a[i - f];
                    a[i - f] = u;
                }
            }

            fp = f / 3;
            s = l + fp;


            p = s + ((k - l) >>> 1);
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive2(a, s, e, p, p, upper, qaMode);
        return expandPartitionT2(a, l, r, s, e, p, upper[0], upper);
    }























    private static int repeatedStepFarRight(double[] a, int l, int r, int k, int[] upper, int flags) {

        int fp;
        int e;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;
            e = r - fp;

            p = e - mapDistance(r - k, l, r, fp);
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            for (int i = r - f, end = r - f2; i > end; i--) {
                if (a[i - f] > a[i + f]) {
                    final double u = a[i - f];
                    a[i - f] = a[i + f];
                    a[i + f] = u;
                }
                if (a[i - f2] > a[i]) {
                    final double v = a[i - f2];
                    a[i - f2] = a[i];
                    a[i] = v;
                }
                if (a[i] > a[i + f]) {
                    final double u = a[i];
                    a[i] = a[i + f];
                    a[i + f] = u;
                }
            }

            fp = f / 3;
            e = r - fp;


            p = e - ((r - k) >>> 1);
        }
        final int s = e - fp + 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive2(a, s, e, p, p, upper, qaMode);
        return expandPartitionT2(a, l, r, s, e, p, upper[0], upper);
    }



















    private static int sampleStep(double[] a, int l, int r, int k, int[] upper, int flags) {



        final int n = r - l + 1;
        final int ith = k - l + 1;
        final double z = Math.log(n);

        final double s = 0.5 * Math.exp(0.6666666666666666 * z);
        final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
        final int ll = Math.max(l, (int) (k - ith * s / n + sd));
        final int rr = Math.min(r, (int) (k + (n - ith) * s / n + sd));


        final int p = quickSelectAdaptive2(a, ll, rr, k, k, upper, qaMode);
        return expandPartitionT2(a, l, r, ll, rr, p, upper[0], upper);
    }









    static int sortNaN(double[] data) {
        int end = data.length;

        while (--end >= 0) {
            if (!Double.isNaN(data[end])) {
                break;
            }
        }
        for (int i = end; --i >= 0;) {
            final double v = data[i];
            if (Double.isNaN(v)) {

                data[i] = data[end];
                data[end] = v;
                end--;
            }
        }
        return end;
    }









    static int countIndices(int[] indices, int count, int right) {
        int end = count;

        while (--end >= 0) {
            if (indices[end] <= right) {
                break;
            }
        }
        for (int i = end; --i >= 0;) {
            final int k = indices[i];
            if (k > right) {

                indices[i] = indices[end];
                indices[end] = k;
                end--;
            }
        }
        return end + 1;
    }















    static int countMixedSignedZeros(double[] data, int left, int right) {

        int c = 0;
        int cn = 0;
        for (int i = left; i <= right; i++) {
            if (data[i] == 0) {
                c++;
                if (Double.doubleToRawLongBits(data[i]) < 0) {
                    cn++;
                }
            }
        }
        return c == cn ? 0 : cn;
    }











    static void sortZero(double[] data, int left, int right) {

        int c = 0;
        for (int i = left; i <= right; i++) {
            if (Double.doubleToRawLongBits(data[i]) < 0) {
                c++;
            }
        }

        if (c != 0) {
            int i = left;
            while (c-- > 0) {
                data[i++] = -0.0;
            }
            while (i <= right) {
                data[i++] = 0.0;
            }
        }
    }












    private static void fixContinuousSignedZeros(double[] data, int left, int right) {
        int j;
        if (data[left] <= 0 && data[right] >= 0) {
            int i = left;
            while (data[i] < 0) {
                i++;
            }
            j = right;
            while (data[j] > 0) {
                j--;
            }
            sortZero(data, i, j);
        }
    }











    private int createMaxDepthSinglePivot(int n) {


        final int maxDepth = floorLog2(n);

        return (int) Math.floor(maxDepth * recursionMultiple) + recursionConstant;
    }








    private static int singlePivotMaxDepth(int x) {
        return (31 - Integer.numberOfLeadingZeros(x)) << 1;
    }









    static int floorLog2(int x) {
        return 31 - Integer.numberOfLeadingZeros(x);
    }







    private int lnNtoMaxDepthSinglePivot(double x) {
        final double maxDepth = x * LOG2_E;
        return (int) Math.floor(maxDepth * recursionMultiple) + recursionConstant;
    }











    private int createMaxDepthDualPivot(int n) {


        final int maxDepth = log3(n);

        return (int) Math.floor(maxDepth * recursionMultiple) + recursionConstant;
    }










    static int log3(int x) {





        return ((32 - Integer.numberOfLeadingZeros(x)) * 323) >>> 9;
    }


















































    static int searchLessOrEqual(int[] a, int left, int right, int k) {
        int l = left;
        int r = right;
        while (l <= r) {

            final int m = (l + r) >>> 1;
            final int v = a[m];







            if (v < k) {
                l = m + 1;
            } else if (v > k) {
                r = m - 1;
            } else {

                return m;
            }








        }


        return r;
    }


















































    static int searchGreaterOrEqual(int[] a, int left, int right, int k) {
        int l = left;
        int r = right;
        while (l <= r) {

            final int m = (l + r) >>> 1;
            final int v = a[m];







            if (v < k) {
                l = m + 1;
            } else if (v > k) {
                r = m - 1;
            } else {

                return m;
            }








        }


        return l;
    }









    private IntUnaryOperator createRNG(int n, int k) {

        if ((controlFlags & FLAG_MSWS) != 0) {

            final UniformRandomProvider rng = RandomSource.MSWS.create(n * 31L + k);
            if ((controlFlags & FLAG_BIASED_RANDOM) != 0) {

                return i -> (int) ((i * Integer.toUnsignedLong(rng.nextInt())) >>> Integer.SIZE);
            }
            return rng::nextInt;
        }
        if ((controlFlags & FLAG_SPLITTABLE_RANDOM) != 0) {
            final SplittableRandom rng = new SplittableRandom(n * 31L + k);
            if ((controlFlags & FLAG_BIASED_RANDOM) != 0) {

                return i -> (int) ((i * Integer.toUnsignedLong(rng.nextInt())) >>> Integer.SIZE);
            }
            return rng::nextInt;
        }
        return createFastRNG(n, k);
    }











    static IntUnaryOperator createFastRNG(int n, int k) {
        return new Gen(n * 31L + k);
    }









    private static final class Gen implements IntUnaryOperator {

        private long s;




        Gen(long seed) {

            this.s = seed * 6364136223846793005L + 1442695040888963407L;
        }

        @Override
        public int applyAsInt(int n) {
            final long x = s;

            s = s * 6364136223846793005L + 1442695040888963407L;


            return (int) ((n * (x >>> Integer.SIZE)) >>> Integer.SIZE);
        }
    }
}
