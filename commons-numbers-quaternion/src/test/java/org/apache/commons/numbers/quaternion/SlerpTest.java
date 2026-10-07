















package org.apache.commons.numbers.quaternion;

import org.apache.commons.numbers.core.Precision;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SlerpTest {

    private static final double EPS = 1e-7;

    private static final double SQRT_2 = Math.sqrt(2.0);
    private static final double INV_SQRT_2 = 1.0 / SQRT_2;

    @Test
    void testSlerp_sphericalAlgorithm() {

        Quaternion q1 = createZRotation(0.75 * Math.PI);
        Quaternion q2 = createZRotation(-0.75 * Math.PI);

        Slerp slerp = new Slerp(q1, q2);




        assertQuaternion(q1.positivePolarForm(), slerp.apply(0));
        assertQuaternion(createZRotation(0.875 * Math.PI), slerp.apply(0.25));
        assertQuaternion(createZRotation(Math.PI), slerp.apply(0.5));
        assertQuaternion(createZRotation(-0.875 * Math.PI), slerp.apply(0.75));
        assertQuaternion(q2.positivePolarForm(), slerp.apply(1));
    }

    @Test
    void testSlerp_sphericalAlgorithm_allOutputsAreInPositivePolarForm() {

        Quaternion q1 = createZRotation(0.75 * Math.PI);
        Quaternion q2 = createZRotation(-0.75 * Math.PI);

        Slerp slerp = new Slerp(q1, q2);

        final int numSteps = 200;
        final double delta = 1d / numSteps;
        for (int step = 0; step <= numSteps; step++) {
            final double t = -10 + step * delta;


            Quaternion result = slerp.apply(t);


            Assertions.assertEquals(1.0, result.norm(), EPS);
            Assertions.assertTrue(result.getW() >= 0.0);
        }
    }

    @Test
    void testSlerp_nonNormalizedInputs() {

        Quaternion q1 = createZRotation(0).multiply(10.0);
        Quaternion q2 = createZRotation(Math.PI).multiply(0.2);

        Slerp slerp = new Slerp(q1, q2);


        assertQuaternion(q1.positivePolarForm(), slerp.apply(0));
        assertQuaternion(createZRotation(0.25 * Math.PI), slerp.apply(0.25));
        assertQuaternion(createZRotation(0.5 * Math.PI), slerp.apply(0.5));
        assertQuaternion(createZRotation(0.75 * Math.PI), slerp.apply(0.75));
        assertQuaternion(q2.positivePolarForm(), slerp.apply(1));
    }

    @Test
    void testSlerp_linearAlgorithm() {

        Quaternion q1 = createZRotation(0.75 * Math.PI);
        Quaternion q2 = createZRotation(0.76 * Math.PI);

        Slerp slerp = new Slerp(q1, q2);


        assertQuaternion(q1.positivePolarForm(), slerp.apply(0));
        assertQuaternion(createZRotation(0.7525 * Math.PI), slerp.apply(0.25));
        assertQuaternion(createZRotation(0.755 * Math.PI), slerp.apply(0.5));
        assertQuaternion(createZRotation(0.7575 * Math.PI), slerp.apply(0.75));
        assertQuaternion(q2.positivePolarForm(), slerp.apply(1));
    }

    @Test
    void testSlerp_linearAlgorithm_allOutputsAreInPositivePolarForm() {

        Quaternion q1 = createZRotation(0.75 * Math.PI);
        Quaternion q2 = createZRotation(0.76 * Math.PI);

        Slerp slerp = new Slerp(q1, q2);

        final int numSteps = 200;
        final double delta = 1d / numSteps;
        for (int step = 0; step <= numSteps; step++) {
            final double t = -10 + step * delta;


            Quaternion result = slerp.apply(t);


            Assertions.assertEquals(1.0, result.norm(), EPS);
            Assertions.assertTrue(result.getW() >= 0.0);
        }
    }

    @Test
    void testSlerp_identicalInputs() {

        Quaternion q1 = createZRotation(0);
        Quaternion q2 = createZRotation(0);

        Slerp slerp = new Slerp(q1, q2);


        Quaternion expected = q1.positivePolarForm();

        assertQuaternion(expected, slerp.apply(0));
        assertQuaternion(expected, slerp.apply(0.5));
        assertQuaternion(expected, slerp.apply(1));
    }

    @Test
    void testSlerp_inputQuaternionsHaveMinusOneDotProduct() {

        Quaternion q1 = createZRotation(0.5 * Math.PI);
        Quaternion q2 = createZRotation(1.5 * Math.PI).conjugate();

        Slerp slerp = new Slerp(q1, q2);


        Assertions.assertEquals(-1.0, q1.dot(q2), EPS);

        Quaternion expected = q1.positivePolarForm();

        assertQuaternion(expected, slerp.apply(0));
        assertQuaternion(expected, slerp.apply(0.5));
        assertQuaternion(expected, slerp.apply(1));
    }

    @Test
    void testSlerp_tOutsideOfZeroToOne() {

        Quaternion q1 = createZRotation(0);
        Quaternion q2 = createZRotation(0.25 * Math.PI);

        Slerp slerp = new Slerp(q1, q2);


        assertQuaternion(createZRotation(-0.5 * Math.PI).positivePolarForm(), slerp.apply(-2));
        assertQuaternion(createZRotation(-0.25 * Math.PI).positivePolarForm(), slerp.apply(-1));

        assertQuaternion(createZRotation(0).positivePolarForm(), slerp.apply(0));

        assertQuaternion(createZRotation(0.25 * Math.PI), slerp.apply(1));
        assertQuaternion(createZRotation(0.5 * Math.PI), slerp.apply(2));
    }

    @Test
    void testVectorTransform_simple() {

        Quaternion q0 = Quaternion.of(1, 0, 0, 0);
        Quaternion q1 = Quaternion.of(0, 0, 0, 1);

        Slerp slerp = new Slerp(q0, q1);

        double[] vec = {2, 0, 1};


        Assertions.assertArrayEquals(new double[] {2, 0, 1},
            transformVector(slerp.apply(0), vec), EPS);

        Assertions.assertArrayEquals(new double[] {SQRT_2, SQRT_2, 1},
            transformVector(slerp.apply(0.25), vec), EPS);

        Assertions.assertArrayEquals(new double[] {0, 2, 1},
            transformVector(slerp.apply(0.5), vec), EPS);

        Assertions.assertArrayEquals(new double[] {-SQRT_2, SQRT_2, 1},
            transformVector(slerp.apply(0.75), vec), EPS);

        Assertions.assertArrayEquals(new double[] {-2, 0, 1},
            transformVector(slerp.apply(1), vec), EPS);
    }

    @Test
    void testVectorTransform_multipleCombinations() {

        Quaternion[] quaternions = {

                Quaternion.of(1, 0, 0, 0),
                Quaternion.of(INV_SQRT_2, INV_SQRT_2, 0, 0),
                Quaternion.of(0, 1, 0, 0),


                Quaternion.of(1, 0, 0, 0),
                Quaternion.of(INV_SQRT_2, -INV_SQRT_2, 0, 0),
                Quaternion.of(0, -1, 0, 0),


                Quaternion.of(1, 0, 0, 0),
                Quaternion.of(INV_SQRT_2, 0, INV_SQRT_2, 0),
                Quaternion.of(0, 0, 1, 0),


                Quaternion.of(1, 0, 0, 0),
                Quaternion.of(INV_SQRT_2, 0, -INV_SQRT_2, 0),
                Quaternion.of(0, 0, -1, 0),


                Quaternion.of(1, 0, 0, 0),
                Quaternion.of(INV_SQRT_2, 0, 0, INV_SQRT_2),
                Quaternion.of(0, 0, 0, 1),


                Quaternion.of(1, 0, 0, 0),
                Quaternion.of(INV_SQRT_2, 0, 0, -INV_SQRT_2),
                Quaternion.of(0, 0, 0, -1)
        };



        for (int i = 0; i < quaternions.length; ++i) {
            for (int j = 0; j < quaternions.length; ++j) {
                checkSlerpCombination(quaternions[i], quaternions[j]);
            }
        }
    }

    private void checkSlerpCombination(Quaternion start, Quaternion end) {
        Slerp slerp = new Slerp(start, end);

        double[] vec = {1, 2, 3};
        double vecNorm = norm(vec);

        double[] startVec = transformVector(start, vec);
        double[] endVec = transformVector(end, vec);


        Assertions.assertArrayEquals(startVec, transformVector(slerp.apply(0), vec), EPS);
        Assertions.assertArrayEquals(endVec, transformVector(slerp.apply(1), vec), EPS);


        double prevAngle = -1;
        final int numSteps = 100;
        final double delta = 1.0 / numSteps;
        for (int step = 0; step <= numSteps; ++step) {
            final double t = step * delta;
            Quaternion result = slerp.apply(t);

            double[] slerpVec = transformVector(result, vec);


            Assertions.assertEquals(vecNorm, norm(slerpVec), EPS);


            double angle = angle(slerpVec, startVec);
            Assertions.assertTrue(Precision.compareTo(angle, prevAngle, EPS) >= 0,
                    "Expected slerp angle to continuously increase; previous angle was " +
                            prevAngle + " and new angle is " + angle);
        }
    }

    @Test
    void testVectorTransform_tOutsideOfZeroToOne_() {

        double angle1 = Math.PI * 0.25;
        double angle2 = Math.PI * 0.75;

        double halfAngle1 = 0.5 * angle1;
        double halfAngle2 = 0.5 * angle2;

        Quaternion q1 = Quaternion.of(Math.cos(halfAngle1), 0, 0, Math.sin(halfAngle1));
        Quaternion q2 = Quaternion.of(Math.cos(halfAngle2), 0, 0, Math.sin(halfAngle2));

        double[] vec = new double[] {1, 0, 0};


        Slerp slerp12 = new Slerp(q1, q2);
        Assertions.assertArrayEquals(new double[] {1, 0, 0}, transformVector(slerp12.apply(-4.5), vec), EPS);
        Assertions.assertArrayEquals(new double[] {1, 0, 0}, transformVector(slerp12.apply(-0.5), vec), EPS);
        Assertions.assertArrayEquals(new double[] {-1, 0, 0}, transformVector(slerp12.apply(1.5), vec), EPS);
        Assertions.assertArrayEquals(new double[] {-1, 0, 0}, transformVector(slerp12.apply(5.5), vec), EPS);

        Slerp slerp21 = new Slerp(q2, q1);
        Assertions.assertArrayEquals(new double[] {-1, 0, 0}, transformVector(slerp21.apply(-4.5), vec), EPS);
        Assertions.assertArrayEquals(new double[] {-1, 0, 0}, transformVector(slerp21.apply(-0.5), vec), EPS);
        Assertions.assertArrayEquals(new double[] {1, 0, 0}, transformVector(slerp21.apply(1.5), vec), EPS);
        Assertions.assertArrayEquals(new double[] {1, 0, 0}, transformVector(slerp21.apply(5.5), vec), EPS);
    }






    static Quaternion createZRotation(final double theta) {
        double halfAngle = theta * 0.5;

        return Quaternion.of(Math.cos(halfAngle), 0, 0, Math.sin(halfAngle));
    }






    private static double norm(double[] vec) {
        double sum = 0.0;
        for (int i = 0; i < vec.length; ++i) {
            sum += vec[i] * vec[i];
        }
        return Math.sqrt(sum);
    }







    private static double angle(double[] a, double[] b) {
        double cos = dot(a, b) / (norm(a) * norm(b));
        return Math.acos(cos);
    }








    private static double dot(double[] a, double[] b) {
        double result = 0.0;
        for (int i = 0; i < a.length; ++i) {
            result += a[i] * b[i];
        }
        return result;
    }









    private static double[] transformVector(Quaternion q, double[] vec) {
        Quaternion qVec = Quaternion.of(0, vec[0], vec[1], vec[2]);
        Quaternion qConj = q.conjugate();

        Quaternion result = q.multiply(qVec).multiply(qConj);

        return new double[] {result.getX(), result.getY(), result.getZ()};
    }






    private static void assertQuaternion(Quaternion expected, Quaternion actual) {
        String msg = "Expected quaternion to equal " + expected + " but was " + actual;

        Assertions.assertEquals(expected.getW(), actual.getW(), EPS, msg);
        Assertions.assertEquals(expected.getX(), actual.getX(), EPS, msg);
        Assertions.assertEquals(expected.getY(), actual.getY(), EPS, msg);
        Assertions.assertEquals(expected.getZ(), actual.getZ(), EPS, msg);
    }
}
