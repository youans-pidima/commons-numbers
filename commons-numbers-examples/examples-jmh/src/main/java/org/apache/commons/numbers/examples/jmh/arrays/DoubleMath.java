















package org.apache.commons.numbers.examples.jmh.arrays;






final class DoubleMath {

    private DoubleMath() {}














    static boolean greaterThan(double x, double y) {
        if (x > y) {
            return true;
        }
        if (x < y) {
            return false;
        }

        final long a = Double.doubleToLongBits(x);
        final long b = Double.doubleToLongBits(y);
        return a > b;
    }














    static boolean lessThan(double x, double y) {
        if (x < y) {
            return true;
        }
        if (x > y) {
            return false;
        }

        final long a = Double.doubleToLongBits(x);
        final long b = Double.doubleToLongBits(y);
        return a < b;
    }
}
