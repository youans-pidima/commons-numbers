















package org.apache.commons.numbers.examples.jmh.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;




class EuclideanNormAccuracyTest {


    private static final int VECTOR_LENGTH = 100;


    private static final int SAMPLE_COUNT = 100_000;





    @Test
    @Disabled("This method is used to output a report of the accuracy of implementations.")
    void reportUlpErrors() throws IOException {
        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create();

        final EuclideanNormEvaluator eval = new EuclideanNormEvaluator();
        eval.addMethod("direct", new EuclideanNormAlgorithms.Direct())
            .addMethod("enorm", new EuclideanNormAlgorithms.Enorm())
            .addMethod("enormMod", new EuclideanNormAlgorithms.EnormMod())
            .addMethod("enormModKahan", new EuclideanNormAlgorithms.EnormModKahan())
            .addMethod("enormModExt", new EuclideanNormAlgorithms.EnormModExt())
            .addMethod("extLinear", new EuclideanNormAlgorithms.ExtendedPrecisionLinearCombination())
            .addMethod("extLinearMod", new EuclideanNormAlgorithms.ExtendedPrecisionLinearCombinationMod())
            .addMethod("extLinearSinglePass", new EuclideanNormAlgorithms.ExtendedPrecisionLinearCombinationSinglePass())
            .addMethod("extLinearSqrt2", new EuclideanNormAlgorithms.ExtendedPrecisionLinearCombinationSqrt2());

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("target/norms.csv"))) {
            writer.write("name, input type, error mean, error std dev, error min, error max, failed");
            writer.newLine();


            final double[][] baseInput = createInputVectors(-10, +10, rng);

            evaluate("high", scaleInputVectors(baseInput, 0x1.0p+530), eval, writer);
            evaluate("high-thresh", scaleInputVectors(baseInput, 0x1.0p+500), eval, writer);
            evaluate("mid", baseInput, eval, writer);
            evaluate("low-thresh", scaleInputVectors(baseInput, 0x1.0p-500), eval, writer);
            evaluate("low", scaleInputVectors(baseInput, 0x1.0p-530), eval, writer);

            final double[][] fullInput = createInputVectors(-550, +550, rng);
            evaluate("full", fullInput, eval, writer);
        }
    }








    private static void evaluate(final String inputType, final double[][] inputs, final EuclideanNormEvaluator eval,
            final BufferedWriter writer) throws IOException {
        final Map<String, EuclideanNormEvaluator.Stats> resultMap = eval.evaluate(inputs);
        writeResults(inputType, resultMap, writer);
    }







    private static void writeResults(final String inputType, final Map<String, EuclideanNormEvaluator.Stats> resultMap,
            final BufferedWriter writer) throws IOException {
        for (Map.Entry<String, EuclideanNormEvaluator.Stats> entry : resultMap.entrySet()) {
            EuclideanNormEvaluator.Stats stats = entry.getValue();

            writer.write(String.format("%s,%s,%.3g,%.3g,%.3g,%.3g,%d",
                    entry.getKey(),
                    inputType,
                    stats.getUlpErrorMean(),
                    stats.getUlpErrorStdDev(),
                    stats.getUlpErrorMin(),
                    stats.getUlpErrorMax(),
                    stats.getFailCount()));
            writer.newLine();
        }
    }







    private static double[][] createInputVectors(final int minExp, final int maxExp, final UniformRandomProvider rng) {
        final double[][] input = new double[SAMPLE_COUNT][];
        for (int i = 0; i < input.length; ++i) {
            input[i] = DoubleUtils.randomArray(VECTOR_LENGTH, minExp, maxExp, rng);
        }
        return input;
    }






    private static double[][] scaleInputVectors(final double[][] inputs, final double s) {
        final double[][] scaled = new double[inputs.length][];
        for (int i = 0; i < inputs.length; ++i) {
            scaled[i] = DoubleUtils.scalarMultiply(inputs[i], s);
        }
        return scaled;
    }
}
