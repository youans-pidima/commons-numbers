















package org.apache.commons.numbers.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class AdditionTest {
    private static class AdditionTester implements Addition<AdditionTester> {
        private final int value;

        AdditionTester(int value) {
            this.value = value;
        }

        @Override
        public AdditionTester add(AdditionTester a) {
            throw new UnsupportedOperationException("not needed for testing");
        }

        @Override
        public AdditionTester zero() {
            return new AdditionTester(0);
        }

        @Override
        public AdditionTester negate() {
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
            AdditionTester that = (AdditionTester) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    @Test
    void testIsZero() {
        Assertions.assertTrue(new AdditionTester(0).isZero());
        Assertions.assertFalse(new AdditionTester(1).isZero());
    }
}
