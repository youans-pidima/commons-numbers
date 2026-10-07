
















package org.apache.commons.numbers.quaternion;

import java.util.Arrays;
import java.util.function.ToDoubleFunction;
import java.util.function.BiPredicate;
import java.io.Serializable;
import org.apache.commons.numbers.core.Precision;











public final class Quaternion implements Serializable {

    public static final Quaternion ZERO = of(0, 0, 0, 0);

    public static final Quaternion ONE = new Quaternion(Type.POSITIVE_POLAR_FORM, 1, 0, 0, 0);

    public static final Quaternion I = new Quaternion(Type.POSITIVE_POLAR_FORM, 0, 1, 0, 0);

    public static final Quaternion J = new Quaternion(Type.POSITIVE_POLAR_FORM, 0, 0, 1, 0);

    public static final Quaternion K = new Quaternion(Type.POSITIVE_POLAR_FORM, 0, 0, 0, 1);


    private static final long serialVersionUID = 20170118L;

    private static final String ILLEGAL_NORM_MSG = "Illegal norm: ";


    private static final String FORMAT_START = "[";

    private static final String FORMAT_END = "]";

    private static final String FORMAT_SEP = " ";


    private static final int VECTOR_DIMENSIONS = 3;

    private static final int NUMBER_OF_PARTS = 4;


    private final Type type;

    private final double w;

    private final double x;

    private final double y;

    private final double z;




    private enum Type {

        DEFAULT(Default.NORMSQ,
                Default.NORM,
                Default.IS_UNIT),

        NORMALIZED(Normalized.NORM,
                   Normalized.NORM,
                   Normalized.IS_UNIT),

        POSITIVE_POLAR_FORM(Normalized.NORM,
                            Normalized.NORM,
                            Normalized.IS_UNIT);


        private final ToDoubleFunction<Quaternion> normSq;

        private final ToDoubleFunction<Quaternion> norm;

        private final BiPredicate<Quaternion, Double> testIsUnit;


        private static final class Default {

            static final ToDoubleFunction<Quaternion> NORMSQ = q ->
                q.w * q.w + q.x * q.x + q.y * q.y + q.z * q.z;


            private static final ToDoubleFunction<Quaternion> NORM = q ->
                Math.sqrt(NORMSQ.applyAsDouble(q));


            private static final BiPredicate<Quaternion, Double> IS_UNIT = (q, eps) ->
                Precision.equals(NORM.applyAsDouble(q), 1d, eps);
        }


        private static final class Normalized {

            static final ToDoubleFunction<Quaternion> NORM = q -> 1;

            static final BiPredicate<Quaternion, Double> IS_UNIT = (q, eps) -> true;
        }






        Type(ToDoubleFunction<Quaternion> normSq,
             ToDoubleFunction<Quaternion> norm,
             BiPredicate<Quaternion, Double> isUnit)  {
            this.normSq = normSq;
            this.norm = norm;
            this.testIsUnit = isUnit;
        }





        double normSq(Quaternion q) {
            return normSq.applyAsDouble(q);
        }




        double norm(Quaternion q) {
            return norm.applyAsDouble(q);
        }





        boolean isUnit(Quaternion q,
                       double eps) {
            return testIsUnit.test(q, eps);
        }
    }










    private Quaternion(Type type,
                       final double w,
                       final double x,
                       final double y,
                       final double z) {
        this.type = type;
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }







    private Quaternion(Type type,
                       Quaternion q) {
        this.type = type;
        w = q.w;
        x = q.x;
        y = q.y;
        z = q.z;
    }










    public static Quaternion of(final double w,
                                final double x,
                                final double y,
                                final double z) {
        return new Quaternion(Type.DEFAULT,
                              w, x, y, z);
    }










    public static Quaternion of(final double scalar,
                                final double[] v) {
        if (v.length != VECTOR_DIMENSIONS) {
            throw new IllegalArgumentException("Size of array must be 3");
        }

        return of(scalar, v[0], v[1], v[2]);
    }








    public static Quaternion of(final double[] v) {
        return of(0, v);
    }







    public Quaternion conjugate() {
        return of(w, -x, -y, -z);
    }








    public static Quaternion multiply(final Quaternion q1,
                                      final Quaternion q2) {

        final double q1a = q1.w;
        final double q1b = q1.x;
        final double q1c = q1.y;
        final double q1d = q1.z;


        final double q2a = q2.w;
        final double q2b = q2.x;
        final double q2c = q2.y;
        final double q2d = q2.z;


        final double w = q1a * q2a - q1b * q2b - q1c * q2c - q1d * q2d;
        final double x = q1a * q2b + q1b * q2a + q1c * q2d - q1d * q2c;
        final double y = q1a * q2c - q1b * q2d + q1c * q2a + q1d * q2b;
        final double z = q1a * q2d + q1b * q2c - q1c * q2b + q1d * q2a;

        return of(w, x, y, z);
    }







    public Quaternion multiply(final Quaternion q) {
        return multiply(this, q);
    }








    public static Quaternion add(final Quaternion q1,
                                 final Quaternion q2) {
        return of(q1.w + q2.w,
                  q1.x + q2.x,
                  q1.y + q2.y,
                  q1.z + q2.z);
    }







    public Quaternion add(final Quaternion q) {
        return add(this, q);
    }








    public static Quaternion subtract(final Quaternion q1,
                                      final Quaternion q2) {
        return of(q1.w - q2.w,
                  q1.x - q2.x,
                  q1.y - q2.y,
                  q1.z - q2.z);
    }







    public Quaternion subtract(final Quaternion q) {
        return subtract(this, q);
    }








    public static double dot(final Quaternion q1,
                             final Quaternion q2) {
        return q1.w * q2.w +
            q1.x * q2.x +
            q1.y * q2.y +
            q1.z * q2.z;
    }







    public double dot(final Quaternion q) {
        return dot(this, q);
    }






    public double norm() {
        return type.norm(this);
    }






    public double normSq() {
        return type.normSq(this);
    }









    public Quaternion normalize() {
        switch (type) {
        case NORMALIZED:
        case POSITIVE_POLAR_FORM:
            return this;
        case DEFAULT:
            final double norm = norm();

            if (norm < Precision.SAFE_MIN ||
                !Double.isFinite(norm)) {
                throw new IllegalStateException(ILLEGAL_NORM_MSG + norm);
            }

            final Quaternion unit = divide(norm);

            return w >= 0 ?
                new Quaternion(Type.POSITIVE_POLAR_FORM, unit) :
                new Quaternion(Type.NORMALIZED, unit);
        default:
            throw new IllegalStateException();
        }
    }




    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Quaternion) {
            final Quaternion q = (Quaternion) other;
            return ((Double) w).equals(q.w) &&
                ((Double) x).equals(q.x) &&
                ((Double) y).equals(q.y) &&
                ((Double) z).equals(q.z);
        }

        return false;
    }




    @Override
    public int hashCode() {
        return Arrays.hashCode(new double[] {w, x, y, z});
    }










    public boolean equals(final Quaternion q,
                          final double eps) {
        return Precision.equals(w, q.w, eps) &&
            Precision.equals(x, q.x, eps) &&
            Precision.equals(y, q.y, eps) &&
            Precision.equals(z, q.z, eps);
    }









    public boolean isUnit(double eps) {
        return type.isUnit(this, eps);
    }








    public boolean isPure(double eps) {
        return Math.abs(w) <= eps;
    }






    public Quaternion positivePolarForm() {
        switch (type) {
        case POSITIVE_POLAR_FORM:
            return this;
        case NORMALIZED:
            return w >= 0 ?
                new Quaternion(Type.POSITIVE_POLAR_FORM, this) :
                new Quaternion(Type.POSITIVE_POLAR_FORM, negate());
        case DEFAULT:
            return w >= 0 ?
                normalize() :


                negate().normalize();
        default:
            throw new IllegalStateException();
        }
    }







    public Quaternion negate() {
        switch (type) {
        case POSITIVE_POLAR_FORM:
        case NORMALIZED:
            return new Quaternion(Type.NORMALIZED, -w, -x, -y, -z);
        case DEFAULT:
            return new Quaternion(Type.DEFAULT, -w, -x, -y, -z);
        default:
            throw new IllegalStateException();
        }
    }









    public Quaternion inverse() {
        switch (type) {
        case POSITIVE_POLAR_FORM:
        case NORMALIZED:
            return new Quaternion(type, w, -x, -y, -z);
        case DEFAULT:
            final double squareNorm = normSq();
            if (squareNorm < Precision.SAFE_MIN ||
                !Double.isFinite(squareNorm)) {
                throw new IllegalStateException(ILLEGAL_NORM_MSG + Math.sqrt(squareNorm));
            }

            return of(w / squareNorm,
                      -x / squareNorm,
                      -y / squareNorm,
                      -z / squareNorm);
        default:
            throw new IllegalStateException();
        }
    }






    public double getW() {
        return w;
    }







    public double getX() {
        return x;
    }







    public double getY() {
        return y;
    }







    public double getZ() {
        return z;
    }







    public double getScalarPart() {
        return getW();
    }









    public double[] getVectorPart() {
        return new double[] {x, y, z};
    }







    public Quaternion multiply(final double alpha) {
        return of(alpha * w,
                  alpha * x,
                  alpha * y,
                  alpha * z);
    }







    public Quaternion divide(final double alpha) {
        return of(w / alpha,
                  x / alpha,
                  y / alpha,
                  z / alpha);
    }










    public static Quaternion parse(String s) {
        final int startBracket = s.indexOf(FORMAT_START);
        if (startBracket != 0) {
            throw new QuaternionParsingException("Expected start string: " + FORMAT_START);
        }
        final int len = s.length();
        final int endBracket = s.indexOf(FORMAT_END);
        if (endBracket != len - 1) {
            throw new QuaternionParsingException("Expected end string: " + FORMAT_END);
        }
        final String[] elements = s.substring(1, s.length() - 1).split(FORMAT_SEP);
        if (elements.length != NUMBER_OF_PARTS) {
            throw new QuaternionParsingException("Incorrect number of parts: Expected 4 but was " +
                                                 elements.length +
                                                 " (separator is '" + FORMAT_SEP + "')");
        }

        final double a;
        try {
            a = Double.parseDouble(elements[0]);
        } catch (NumberFormatException ex) {
            throw new QuaternionParsingException("Could not parse scalar part" + elements[0], ex);
        }
        final double b;
        try {
            b = Double.parseDouble(elements[1]);
        } catch (NumberFormatException ex) {
            throw new QuaternionParsingException("Could not parse i part" + elements[1], ex);
        }
        final double c;
        try {
            c = Double.parseDouble(elements[2]);
        } catch (NumberFormatException ex) {
            throw new QuaternionParsingException("Could not parse j part" + elements[2], ex);
        }
        final double d;
        try {
            d = Double.parseDouble(elements[3]);
        } catch (NumberFormatException ex) {
            throw new QuaternionParsingException("Could not parse k part" + elements[3], ex);
        }

        return of(a, b, c, d);
    }




    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append(FORMAT_START)
            .append(w).append(FORMAT_SEP)
            .append(x).append(FORMAT_SEP)
            .append(y).append(FORMAT_SEP)
            .append(z)
            .append(FORMAT_END);

        return s.toString();
    }


    private static final class QuaternionParsingException extends NumberFormatException {

        private static final long serialVersionUID = 20181128L;




        QuaternionParsingException(String msg) {
            super(msg);
        }





        QuaternionParsingException(String msg, Throwable cause) {
            super(msg);
            initCause(cause);
        }
    }
}
