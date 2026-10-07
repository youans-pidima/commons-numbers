















package org.apache.commons.numbers.core;







final class ExtendedPrecision {






    private static final double SAFE_UPPER = 0x1.0p996;




    private static final double SAFE_LOWER = 0x1.0p-968;



    private static final double DOWN_SCALE = 0x1.0p-30;


    private static final double UP_SCALE = 0x1.0p30;


    private static final double UP_SCALE2 = 0x1.0p60;

    private static final double DOWN_SCALE2 = 0x1.0p-60;


    private ExtendedPrecision() {

    }


































    static double productLow(double x, double y, double xy) {




        if (DD.isNotNormal(xy)) {

            return xy - xy;
        }















        final double a = Math.abs(x);
        final double b = Math.abs(y);
        final double ab = Math.abs(xy);
        if (a + b + ab >= SAFE_UPPER) {

            if (a > b) {
                return DD.twoProductLow(x * DOWN_SCALE, y, xy * DOWN_SCALE) * UP_SCALE;
            }
            return DD.twoProductLow(x, y * DOWN_SCALE, xy * DOWN_SCALE) * UP_SCALE;
        }





        if (ab <= SAFE_LOWER) {

            return DD.twoProductLow(x * UP_SCALE, y * UP_SCALE, xy * UP_SCALE2) * DOWN_SCALE2;
        }


        return DD.twoProductLow(x, y, xy);
    }
}
