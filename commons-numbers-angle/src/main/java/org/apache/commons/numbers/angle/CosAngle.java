















package org.apache.commons.numbers.angle;

import org.apache.commons.numbers.core.Norm;
import org.apache.commons.numbers.core.Sum;




public final class CosAngle {


    private CosAngle() {

    }









    public static double value(double[] v1,
                               double[] v2) {
        return Sum.ofProducts(v1, v2).getAsDouble() / Norm.L2.of(v1) / Norm.L2.of(v2);
    }
}
