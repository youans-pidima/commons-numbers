





















package org.apache.commons.numbers.gamma;

import java.util.function.DoubleSupplier;








final class BoostTools {






















    private static final double EPSILON = 0x1.0p-52;




















    private static final double KAHAN_EPSILON = 0x1.0p-62;

    private static final String MSG_FAILED_TO_CONVERGE = "Failed to converge within %d iterations";


    private BoostTools() {

    }











    static double sumSeries(DoubleSupplier func, double epsilon, int maxTerms) {
        return sumSeries(func, epsilon, maxTerms, 0);
    }












    static double sumSeries(DoubleSupplier func, double epsilon, int maxTerms, double initValue) {













        final double eps = getEpsilon(epsilon, EPSILON);

        int counter = maxTerms;

        double result = initValue;
        double nextTerm;
        do {
            nextTerm = func.getAsDouble();
            result += nextTerm;
        } while (Math.abs(eps * result) < Math.abs(nextTerm) && --counter > 0);

        if (counter <= 0) {
            throw new ArithmeticException(
               String.format(MSG_FAILED_TO_CONVERGE, maxTerms));
        }

        return result;
    }











    static double kahanSumSeries(DoubleSupplier func, double epsilon, int maxTerms) {
        return kahanSumSeries(func, epsilon, maxTerms, 0);
    }












    static double kahanSumSeries(DoubleSupplier func, double epsilon, int maxTerms, double initValue) {
        final double eps = getEpsilon(epsilon, KAHAN_EPSILON);

        int counter = maxTerms;








        double result = initValue;
        double carry = 0;
        double nextTerm;
        do {
            nextTerm = func.getAsDouble();
            final double y = nextTerm - carry;
            final double t = result + y;
            carry = t - result;
            carry -= y;
            result = t;
        } while (Math.abs(eps * result) < Math.abs(nextTerm) && --counter > 0);

        if (counter <= 0) {
            throw new ArithmeticException(
               String.format(MSG_FAILED_TO_CONVERGE, maxTerms));
        }

        return result;
    }















    private static double getEpsilon(double epsilon, double minEpsilon) {
        return epsilon > minEpsilon ? epsilon : minEpsilon;
    }













    static double evaluatePolynomial(double[] c, double x) {
        final int count = c.length;
        double sum = c[count - 1];
        for (int i = count - 2; i >= 0; --i) {
            sum *= x;
            sum += c[i];
        }
        return sum;
    }
}
