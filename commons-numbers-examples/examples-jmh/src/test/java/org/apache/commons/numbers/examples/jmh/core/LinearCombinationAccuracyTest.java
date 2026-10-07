















package org.apache.commons.numbers.examples.jmh.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.numbers.examples.jmh.core.LinearCombination.ND;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;




class LinearCombinationAccuracyTest {






    private static final int LENGTH = 100;


    private static final int SAMPLES = 10;








    static Stream<Arguments> provideLinearCombination() {
        return Stream.of(
            Arguments.of(LinearCombinations.Dekker.INSTANCE, 1e20, 1e30),
            Arguments.of(LinearCombinations.Dot2s.INSTANCE, 1e20, 1e30),
            Arguments.of(LinearCombinations.DotK.DOT_3, 1e35, 1e45),
            Arguments.of(LinearCombinations.DotK.DOT_4, 1e50, 1e65),
            Arguments.of(LinearCombinations.DotK.DOT_5, 1e65, 1e85),
            Arguments.of(LinearCombinations.DotK.DOT_6, 1e80, 1e100),
            Arguments.of(LinearCombinations.DotK.DOT_7, 1e95, 1e115),
            Arguments.of(LinearCombinations.ExtendedPrecision.INSTANCE, 1e300, -1),
            Arguments.of(LinearCombinations.ExtendedPrecision.DOUBLE, 1e300, -1),
            Arguments.of(LinearCombinations.ExtendedPrecision.EXACT, 1e300, -1),
            Arguments.of(LinearCombinations.Exact.INSTANCE, 1e300, -1)
        );
    }









    @ParameterizedTest
    @MethodSource("provideLinearCombination")
    void testDotProduct(ND fun, double passC, double failC) {
        final double[] x = new double[LENGTH];
        final double[] y = new double[LENGTH];

        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create(9283746);




        double sum = 0;
        for (int i = 0; i < SAMPLES; i++) {
            final double expected = LinearCombinationUtils.genDot(passC, rng, x, y, null);
            final double observed = fun.value(x, y);
            sum += relativeError(expected, observed);
        }
        final double error = sum / SAMPLES;
        Assertions.assertTrue(error < 1e-3, () -> "Expected to pass at C=" + passC + ". Error = " + error);
        if (failC < 0) {
            return;
        }
        sum = 0;
        for (int i = 0; i < SAMPLES; i++) {
            final double expected = LinearCombinationUtils.genDot(failC, rng, x, y, null);
            final double observed = fun.value(x, y);
            sum += relativeError(expected, observed);
        }
        final double error2 = sum / SAMPLES;
        Assertions.assertFalse(error2 < 1e-3, () -> "Expected to fail at C=" + failC + ". Error = " + error2);
    }










    @Test
    @Disabled("This method is used to output a report of the accuracy of implementations.")
    void reportRelativeError() throws IOException {





        final int samples = 2000;

        final double logA = Math.log(1e10);
        final double logB = Math.log(1e120);

        final ArrayList<double[]> data = new ArrayList<>(samples);
        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_1024_PP.create();
        final double[] x = new double[LENGTH];
        final double[] y = new double[LENGTH];
        final double[] cc = new double[1];


        final ArrayList<ND> methods = new ArrayList<>();
        final ArrayList<String> names = new ArrayList<>();
        addMethod(methods, names, LinearCombinations.Dekker.INSTANCE, "dekker");
        addMethod(methods, names, LinearCombinations.Dot2s.INSTANCE, "dot2s");
        addMethod(methods, names, LinearCombinations.DotK.DOT_3, "dot3");
        addMethod(methods, names, LinearCombinations.DotK.DOT_4, "dot4");
        addMethod(methods, names, LinearCombinations.DotK.DOT_5, "dot5");
        addMethod(methods, names, LinearCombinations.DotK.DOT_6, "dot6");
        addMethod(methods, names, LinearCombinations.DotK.DOT_7, "dot7");
        addMethod(methods, names, LinearCombinations.ExtendedPrecision.INSTANCE, "extended");
        addMethod(methods, names, LinearCombinations.ExtendedPrecision.DOUBLE, "extended2");
        addMethod(methods, names, LinearCombinations.ExtendedPrecision.EXACT, "extended_exact");
        addMethod(methods, names, LinearCombinations.Exact.INSTANCE, "exact");

        for (int i = 0; i < samples; i++) {


            final double u = rng.nextDouble();
            final double c = Math.exp(u * logB + (1 - u) * logA);
            final double dot = LinearCombinationUtils.genDot(c, rng, x, y, cc);


            final double[] e = new double[methods.size() + 1];
            int j = 0;
            e[j++] = cc[0];
            for (final ND method : methods) {
                e[j++] = compute(x, y, dot, method);
            }
            data.add(e);
        }


        Collections.sort(data, (a, b) -> Double.compare(a[0], b[0]));


        try (BufferedWriter out = Files.newBufferedWriter(Paths.get("target/dot.csv"))) {
            out.append("Condition no," + names.stream().collect(Collectors.joining(",")));
            out.newLine();
            final StringBuilder sb = new StringBuilder(200);
            try (Formatter formatter = new Formatter(sb)) {
                for (final double[] e : data) {
                    sb.setLength(0);
                    formatter.format("%.3g", e[0]);
                    for (int i = 1; i < e.length; i++) {
                        formatter.format(",%.3g", e[i]);
                    }
                    out.append(sb);
                    out.newLine();
                }
            }
        }
    }









    private static void addMethod(ArrayList<ND> methods, ArrayList<String> names,
                                  ND method, String name) {
        methods.add(method);
        names.add(name);
    }










    private static double compute(double[] x, double[] y, double expected, ND fun) {
        return relativeError(expected, fun.value(x, y));
    }













    private static double relativeError(double expected, double observed) {
        return Math.min(2, Math.abs(observed - expected) / Math.abs(expected));
    }
}
