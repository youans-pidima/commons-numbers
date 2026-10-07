















package org.apache.commons.numbers.angle;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;




public abstract class Angle implements DoubleSupplier {

    public static final double TWO_PI = 2 * Math.PI;

    public static final double PI_OVER_TWO = 0.5 * Math.PI;

    private static final double TURN_TO_DEG = 360d;

    private static final double RAD_TO_DEG = 180.0 / Math.PI;

    private static final double DEG_TO_RAD = Math.PI / 180.0;


    private final double value;




    Angle(final double value) {
        this.value = value;
    }



    @Override
    public double getAsDouble() {
        return value;
    }


    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }











    @Override
    public boolean equals(final Object other) {
        return other != null &&
                getClass().equals(other.getClass()) &&
                Double.doubleToLongBits(value) == Double.doubleToLongBits(((Angle) other).value);
    }






    public abstract Turn toTurn();






    public abstract Rad toRad();






    public abstract Deg toDeg();




    public static final class Turn extends Angle {

        public static final Turn ZERO = of(0d);

        public static final DoubleUnaryOperator WITHIN_0_AND_1 = normalizer(0d);






        private Turn(final double angle) {
            super(angle);
        }







        public static Turn of(final double angle) {
            return new Turn(angle);
        }


        @Override
        public Turn toTurn() {
            return this;
        }


        @Override
        public Rad toRad() {
            return Rad.of(getAsDouble() * TWO_PI);
        }


        @Override
        public Deg toDeg() {
            return Deg.of(getAsDouble() * TURN_TO_DEG);
        }








        public static DoubleUnaryOperator normalizer(final double lo) {
            return new Normalizer(lo, 1d);
        }
    }




    public static final class Rad extends Angle {

        public static final Rad ZERO = of(0d);

        public static final Rad PI = of(Math.PI);

        public static final Rad TWO_PI = of(Angle.TWO_PI);

        public static final DoubleUnaryOperator WITHIN_0_AND_2PI = normalizer(0d);

        public static final DoubleUnaryOperator WITHIN_MINUS_PI_AND_PI = normalizer(-Math.PI);






        private Rad(final double angle) {
            super(angle);
        }







        public static Rad of(final double angle) {
            return new Rad(angle);
        }


        @Override
        public Turn toTurn() {
            return Turn.of(getAsDouble() / Angle.TWO_PI);
        }


        @Override
        public Rad toRad() {
            return this;
        }


        @Override
        public Deg toDeg() {
            return Deg.of(getAsDouble() * RAD_TO_DEG);
        }








        public static DoubleUnaryOperator normalizer(final double lo) {
            return new Normalizer(lo, Angle.TWO_PI);
        }
    }




    public static final class Deg extends Angle {

        public static final Deg ZERO = of(0d);

        public static final DoubleUnaryOperator WITHIN_0_AND_360 = normalizer(0d);






        private Deg(final double angle) {
            super(angle);
        }







        public static Deg of(final double angle) {
            return new Deg(angle);
        }


        @Override
        public Turn toTurn() {
            return Turn.of(getAsDouble() / TURN_TO_DEG);
        }


        @Override
        public Rad toRad() {
            return Rad.of(getAsDouble() * DEG_TO_RAD);
        }


        @Override
        public Deg toDeg() {
            return this;
        }








        public static DoubleUnaryOperator normalizer(final double lo) {
            return new Normalizer(lo, TURN_TO_DEG);
        }
    }




    private static final class Normalizer implements DoubleUnaryOperator {

        private final double lo;

        private final double hi;

        private final double period;

        private final Reduce reduce;







        Normalizer(final double lo,
                   final double period) {
            this.period = period;
            this.lo = lo;
            this.hi = lo + period;
            reduce = new Reduce(lo, period);
        }






        @Override
        public double applyAsDouble(final double a) {
            if (lo <= a &&
                a < hi) {

                return a;
            }

            final double normalized = reduce.applyAsDouble(a) + lo;
            return normalized < hi ?
                normalized :






                Math.max(lo, normalized - period);
        }
    }
}
