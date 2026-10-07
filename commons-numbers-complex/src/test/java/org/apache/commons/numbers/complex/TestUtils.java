
















package org.apache.commons.numbers.complex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;




public final class TestUtils {





    public enum TestDataFlagOption {

        IGNORE,

        LOAD
    }




    private TestUtils() {
    }









    public static void assertSame(Complex expected, Complex actual) {
        Assertions.assertEquals(expected.getReal(), actual.getReal());
        Assertions.assertEquals(expected.getImaginary(), actual.getImaginary());
    }









    public static void assertEquals(Complex expected, Complex actual, double delta) {
        Assertions.assertEquals(expected.getReal(), actual.getReal(), delta);
        Assertions.assertEquals(expected.getImaginary(), actual.getImaginary(), delta);
    }






























    public static List<Complex[]> loadTestData(String name, TestDataFlagOption option,
            Consumer<String> flaggedDataConsumer) {
        final List<Complex[]> data = new ArrayList<>();
        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(name)))) {
            for  (String line = input.readLine(); line != null; line = input.readLine()) {
                line = preprocessTestData(line, option, flaggedDataConsumer);
                if (line == null) {
                    continue;
                }
                final String[] parts = line.split(" ");
                if ((parts.length & 0x1) == 1) {
                    Assertions.fail("Odd count of numbers on the line: " + line);
                }
                final Complex[] numbers = new Complex[parts.length / 2];
                for (int i = 0; i < parts.length; i += 2) {
                    final double a = Double.parseDouble(parts[i]);
                    final double b = Double.parseDouble(parts[i + 1]);
                    numbers[i / 2] = Complex.ofCartesian(a, b);
                }
                data.add(numbers);
            }
        } catch (NumberFormatException | IOException e) {
            Assertions.fail("Failed to load test data: " + name, e);
        }
        return data;
    }











    private static String preprocessTestData(String line, TestDataFlagOption option,
            Consumer<String> flaggedDataConsumer) {

        if (line.isEmpty() || line.charAt(0) == '#') {
            return null;
        }
        if (line.charAt(0) == ';') {
            switch (option) {
            case LOAD:

                line = line.substring(1);
                break;
            case IGNORE:
            default:
                if (flaggedDataConsumer != null) {
                    flaggedDataConsumer.accept(line.substring(1));
                }

                line = null;
            }
        }
        return line;
    }
}
