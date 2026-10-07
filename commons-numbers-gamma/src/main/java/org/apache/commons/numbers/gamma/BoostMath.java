





















package org.apache.commons.numbers.gamma;








final class BoostMath {

    private BoostMath() {

    }












    static double powm1(double x, double y) {
        if (x > 0) {






            if (Math.abs(y * (x - 1)) < 0.5 || Math.abs(y) < 0.2) {


                final double l = y * Math.log(x);
                if (l < 0.5) {
                    return Math.expm1(l);
                }

            }
        } else if (x < 0 &&




                   Math.rint(y * 0.5) == y * 0.5) {
            return powm1(-x, y);
        }
        return Math.pow(x, y) - 1;
    }
}
