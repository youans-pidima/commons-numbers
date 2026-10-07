















package org.apache.commons.numbers.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class MultiplicationTest {
    private static class MultiplicationTester implements Multiplication<MultiplicationTester> {
        private final int value;

        MultiplicationTester(int value) {
            this.value = value;
        }

        @Override
        public MultiplicationTester multiply(MultiplicationTester a) {
            throw new UnsupportedOperationException("not needed for testing");
        }

        @Override
        public MultiplicationTester one() {
            return new MultiplicationTester(1);
        }

        @Override
        public MultiplicationTester reciprocal() {
            throw new UnsupportedOperationException("not needed for testing");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MultiplicationTester that = (MultiplicationTester) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    @Test
    void testIsZero() {
        Assertions.assertTrue(new MultiplicationTester(1).isOne());
        Assertions.assertFalse(new MultiplicationTester(0).isOne());
    }
}
