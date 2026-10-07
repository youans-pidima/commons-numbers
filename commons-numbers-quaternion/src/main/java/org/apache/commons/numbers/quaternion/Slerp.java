















package org.apache.commons.numbers.quaternion;

import java.util.function.DoubleFunction;










public class Slerp implements DoubleFunction<Quaternion> {






    private static final double MAX_DOT_THRESHOLD = 0.9995;

    private final Quaternion start;

    private final Quaternion end;

    private final DoubleFunction<Quaternion> algo;







    public Slerp(Quaternion start,
                 Quaternion end) {
        this.start = start.positivePolarForm();

        final Quaternion e = end.positivePolarForm();
        double dot = this.start.dot(e);




        if (dot < 0) {
            dot = -dot;
            this.end = e.negate();
        } else {
            this.end = e;
        }

        algo = dot > MAX_DOT_THRESHOLD ?
            new Linear() :
            new Spherical(dot);
    }













    @Override
    public Quaternion apply(double t) {

        if (t == 0) {
            return start;
        } else if (t == 1) {


            return end.positivePolarForm();
        }

        return algo.apply(t);
    }




    private final class Linear implements DoubleFunction<Quaternion> {

        Linear() {}


        @Override
        public Quaternion apply(double t) {
            final double f = 1 - t;
            return Quaternion.of(f * start.getW() + t * end.getW(),
                                 f * start.getX() + t * end.getX(),
                                 f * start.getY() + t * end.getY(),
                                 f * start.getZ() + t * end.getZ()).positivePolarForm();
        }
    }






    private final class Spherical implements DoubleFunction<Quaternion> {

        private final double theta;

        private final double sinTheta;




        Spherical(double dot) {
            theta = Math.acos(dot);
            sinTheta = Math.sin(theta);
        }


        @Override
        public Quaternion apply(double t) {
            final double f1 = Math.sin((1 - t) * theta) / sinTheta;
            final double f2 = Math.sin(t * theta) / sinTheta;

            return Quaternion.of(f1 * start.getW() + f2 * end.getW(),
                                 f1 * start.getX() + f2 * end.getX(),
                                 f1 * start.getY() + f2 * end.getY(),
                                 f1 * start.getZ() + f2 * end.getZ()).positivePolarForm();
        }
    }
}
