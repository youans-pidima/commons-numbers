















package org.apache.commons.numbers.gamma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;




class DataReader implements AutoCloseable {

    private static final char COMMENT = '#';

    private static final Pattern FIELD_PATTERN = Pattern.compile("[, ]+");


    private final BufferedReader in;


    private String[] tokens;




    DataReader(String filename) {
        final InputStream resourceAsStream = this.getClass().getResourceAsStream(filename);
        Assertions.assertNotNull(resourceAsStream, () -> "Could not find resource " + filename);
        in = new BufferedReader(new InputStreamReader(resourceAsStream));
    }







    boolean next() throws IOException {
        tokens = null;
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            if (line.isEmpty() || line.charAt(0) == COMMENT || line.trim().isEmpty()) {
                continue;
            }
            tokens = FIELD_PATTERN.split(line);
            return true;
        }
        return false;
    }











    double getDouble(int i) {
        return Double.parseDouble(tokens[i]);
    }











    BigDecimal getBigDecimal(int i) {
        return new BigDecimal(tokens[i]);
    }









    String[] getFields() {
        return tokens;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
