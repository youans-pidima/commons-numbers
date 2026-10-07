















package org.apache.commons.numbers.examples.jmh.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;





public class EuclideanNormEvaluator {


    private final Map<String, ToDoubleFunction<double[]>> methods = new LinkedHashMap<>();






    public EuclideanNormEvaluator addMethod(final String name, final ToDoubleFunction<double[]> method) {
        methods.put(name, method);
        return this;
    }





    public Map<String, Stats> evaluate(final double[][] inputs) {

        final Map<String, StatsAccumulator> accumulators = new HashMap<>();
        for (final String name : methods.keySet()) {
            accumulators.put(name, new StatsAccumulator(inputs.length * 2));
        }

        for (int i = 0; i < inputs.length; ++i) {


            final double[] vec = inputs[i];

            final double[] reverseVec = new double[vec.length];
            for (int j = 0; j < vec.length; ++j) {
                reverseVec[vec.length - 1 - j] = vec[j];
            }

            final double exact = computeExact(vec);

            for (final Map.Entry<String, ToDoubleFunction<double[]>> entry : methods.entrySet()) {
                final ToDoubleFunction<double[]> fn = entry.getValue();

                final StatsAccumulator acc = accumulators.get(entry.getKey());

                final double forwardSample = fn.applyAsDouble(vec);
                acc.report(exact, forwardSample);

                final double reverseSample = fn.applyAsDouble(reverseVec);
                acc.report(exact, reverseSample);
            }
        }

        final Map<String, Stats> stats = new LinkedHashMap<>();
        for (final String name : methods.keySet()) {
            stats.put(name, accumulators.get(name).computeStats());
        }

        return stats;
    }






    private static double computeExact(final double[] vec) {
        final MathContext ctx = MathContext.DECIMAL128;

        BigDecimal sum = BigDecimal.ZERO;
        for (final double v : vec) {
            sum = sum.add(new BigDecimal(v).pow(2), ctx);
        }

        return sum.sqrt(ctx).doubleValue();
    }






    private static int computeUlpDifference(final double a, final double b) {
        return (int) (Double.doubleToLongBits(a) - Double.doubleToLongBits(b));
    }



    public static final class Stats {


        private final double ulpErrorMean;


        private final double ulpErrorStdDev;


        private final double ulpErrorMin;


        private final double ulpErrorMax;


        private final int failCount;








        Stats(final double ulpErrorMean, final double ulpErrorStdDev, final double ulpErrorMin,
                final double ulpErrorMax, final int failCount) {
            this.ulpErrorMean = ulpErrorMean;
            this.ulpErrorStdDev = ulpErrorStdDev;
            this.ulpErrorMin = ulpErrorMin;
            this.ulpErrorMax = ulpErrorMax;
            this.failCount = failCount;
        }




        public double getUlpErrorMean() {
            return ulpErrorMean;
        }




        public double getUlpErrorStdDev() {
            return ulpErrorStdDev;
        }




        public double getUlpErrorMin() {
            return ulpErrorMin;
        }




        public double getUlpErrorMax() {
            return ulpErrorMax;
        }





        public int getFailCount() {
            return failCount;
        }
    }



    private static final class StatsAccumulator {


        private int sampleIdx;


        private final double[] ulpErrors;




        StatsAccumulator(final int count) {
            ulpErrors = new double[count];
        }





        public void report(final double expected, final double actual) {
            ulpErrors[sampleIdx++] = Double.isFinite(actual) && actual != 0.0 ?
                    computeUlpDifference(expected, actual) :
                    Double.NaN;
        }




        public Stats computeStats() {
            int successCount = 0;
            double sum = 0d;
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;

            for (double ulpError : ulpErrors) {
                if (Double.isFinite(ulpError)) {
                    ++successCount;
                    min = Math.min(ulpError, min);
                    max = Math.max(ulpError, max);
                    sum += ulpError;
                }
            }

            final double mean = sum / successCount;

            double diffSumSq = 0d;
            double diff;
            for (double ulpError : ulpErrors) {
                if (Double.isFinite(ulpError)) {
                    diff = ulpError - mean;
                    diffSumSq += diff * diff;
                }
            }

            final double stdDev = successCount > 1 ?
                    Math.sqrt(diffSumSq / (successCount - 1)) :
                    0d;

            return new Stats(mean, stdDev, min, max, ulpErrors.length - successCount);
        }
    }
}
