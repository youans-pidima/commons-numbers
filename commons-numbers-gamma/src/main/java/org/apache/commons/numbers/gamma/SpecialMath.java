















package org.apache.commons.numbers.gamma;






final class SpecialMath {

    private static final double X_MIN = -1;

    private static final double X_LOW = -0.79149064;

    private static final double X_HIGH = 1;

    private static final double TWO_POW_M6 = 0x1.0p-6;

    private static final double TWO_POW_M12 = 0x1.0p-12;

    private static final double TWO_POW_M20 = 0x1.0p-20;

    private static final double TWO_POW_M53 = 0x1.0p-53;


    private SpecialMath() {

    }





































    static double log1pmx(double x) {

        if (x <= X_MIN) {
            return x == X_MIN ? Double.NEGATIVE_INFINITY : Double.NaN;
        }

        if (x < X_LOW || x > X_HIGH) {
            return Math.log1p(x) - x;
        }
        final double a = Math.abs(x);




        if (a < TWO_POW_M6) {
            return log1pmxSmall(x, a);
        }











        final double z = x / (2 + x);
        final double zz = z * z;




        double sum = 1.0 / 3;
        double numerator = 1;
        int denominator = 3;
        for (;;) {
            numerator *= zz;
            denominator += 2;
            final double sum2 = sum + numerator / denominator;









            if (sum2 == sum) {
                break;
            }
            sum = sum2;
        }
        return z * (2 * zz * sum - x);
    }



















    private static double log1pmxSmall(double x, double a) {











        final double x2 = x * x;

        if (a < TWO_POW_M53) {


            return 0 - x2 / 2;
        }

        final double x4 = x2 * x2;




        if (a < TWO_POW_M20) {

            return x * x4 / 5 -
                       x4 / 4 +
                   x * x2 / 3 -
                       x2 / 2;
        }



        if (a < TWO_POW_M12) {

            return x * x2 * x4 / 7 -
                       x2 * x4 / 6 +
                        x * x4 / 5 -
                            x4 / 4 +
                        x * x2 / 3 -
                            x2 / 2;
        }






        final double x8 = x4 * x4;
        return x * x2 * x8 / 11 -
                   x2 * x8 / 10 +
                    x * x8 /  9 -
                        x8 /  8 +
               x * x2 * x4 /  7 -
                   x2 * x4 /  6 +
                    x * x4 /  5 -
                        x4 /  4 +
                    x * x2 /  3 -
                        x2 /  2;
    }
}
