















package org.apache.commons.numbers.fraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;






final class CommonTestCases {




    private static final List<UnaryOperatorTestCase> numDenConstructorTestCasesList;




    private static final List<DoubleToFractionTestCase> doubleConstructorTestCasesList;




    private static final List<DoubleToFractionTestCase> doubleMaxDenomConstructorTestCasesList;




    private static final List<UnaryOperatorTestCase> absTestCasesList;




    private static final List<UnaryOperatorTestCase> reciprocalTestCasesList;




    private static final List<UnaryOperatorTestCase> negateTestCasesList;




    private static final List<BinaryOperatorTestCase> addFractionTestCasesList;




    private static final List<BinaryIntOperatorTestCase> addIntTestCasesList;




    private static final List<BinaryOperatorTestCase> divideByFractionTestCasesList;




    private static final List<BinaryIntOperatorTestCase> divideByIntTestCasesList;




    private static final List<BinaryOperatorTestCase> multiplyByFractionTestCasesList;




    private static final List<BinaryIntOperatorTestCase> multiplyByIntTestCasesList;




    private static final List<BinaryOperatorTestCase> subtractFractionTestCasesList;




    private static final List<BinaryIntOperatorTestCase> subtractIntTestCasesList;




    private static final List<BinaryIntOperatorTestCase> powTestCasesList;

    static {
        numDenConstructorTestCasesList = collectNumDenConstructorTestCases();
        doubleConstructorTestCasesList = collectDoubleConstructorTestCases();
        doubleMaxDenomConstructorTestCasesList = collectDoubleMaxDenomConstructorTestCases();
        absTestCasesList = collectAbsTestCases();
        reciprocalTestCasesList = collectReciprocalTestCases();
        negateTestCasesList = collectNegateTestCases();
        addFractionTestCasesList = collectAddFractionTestCases();
        addIntTestCasesList = collectAddIntTestCases();
        divideByFractionTestCasesList = collectDivideByFractionTestCases();
        divideByIntTestCasesList = collectDivideByIntTestCases();
        multiplyByFractionTestCasesList = collectMultiplyByFractionTestCases();
        multiplyByIntTestCasesList = collectMultiplyByIntTestCases();
        subtractFractionTestCasesList = collectSubtractFractionTestCases();
        subtractIntTestCasesList = collectSubtractIntTestCases();
        powTestCasesList = collectPowTestCases();
    }

    private CommonTestCases() {}







    private static List<UnaryOperatorTestCase> collectNumDenConstructorTestCases() {
        final List<UnaryOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new UnaryOperatorTestCase(0, 1, 0, 1));
        testCases.add(new UnaryOperatorTestCase(0, 2, 0, 1));
        testCases.add(new UnaryOperatorTestCase(0, -1, 0, 1));
        testCases.add(new UnaryOperatorTestCase(1, 2, 1, 2));
        testCases.add(new UnaryOperatorTestCase(2, 4, 1, 2));
        testCases.add(new UnaryOperatorTestCase(-1, 2, -1, 2));
        testCases.add(new UnaryOperatorTestCase(1, -2, 1, -2));
        testCases.add(new UnaryOperatorTestCase(-2, 4, -1, 2));
        testCases.add(new UnaryOperatorTestCase(2, -4, 1, -2));
        testCases.add(new UnaryOperatorTestCase(-2, -4, -1, -2));

        testCases.add(new UnaryOperatorTestCase(2, Integer.MIN_VALUE, 1, Integer.MIN_VALUE / 2));
        testCases.add(new UnaryOperatorTestCase(Integer.MIN_VALUE, -2, -Integer.MIN_VALUE / 2, -1));

        return testCases;
    }






    private static List<DoubleToFractionTestCase> collectDoubleConstructorTestCases() {
        final List<DoubleToFractionTestCase> testCases = new ArrayList<>();

        testCases.add(new DoubleToFractionTestCase(1d / 2d, 1, 2));
        testCases.add(new DoubleToFractionTestCase(1d / 3d, 1, 3));
        testCases.add(new DoubleToFractionTestCase(2d / 3d, 2, 3));
        testCases.add(new DoubleToFractionTestCase(1d / 4d, 1, 4));
        testCases.add(new DoubleToFractionTestCase(3d / 4d, 3, 4));
        testCases.add(new DoubleToFractionTestCase(1d / 5d, 1, 5));
        testCases.add(new DoubleToFractionTestCase(2d / 5d, 2, 5));
        testCases.add(new DoubleToFractionTestCase(3d / 5d, 3, 5));
        testCases.add(new DoubleToFractionTestCase(4d / 5d, 4, 5));
        testCases.add(new DoubleToFractionTestCase(1d / 6d, 1, 6));
        testCases.add(new DoubleToFractionTestCase(5d / 6d, 5, 6));
        testCases.add(new DoubleToFractionTestCase(1d / 7d, 1, 7));
        testCases.add(new DoubleToFractionTestCase(2d / 7d, 2, 7));
        testCases.add(new DoubleToFractionTestCase(3d / 7d, 3, 7));
        testCases.add(new DoubleToFractionTestCase(4d / 7d, 4, 7));
        testCases.add(new DoubleToFractionTestCase(5d / 7d, 5, 7));
        testCases.add(new DoubleToFractionTestCase(6d / 7d, 6, 7));
        testCases.add(new DoubleToFractionTestCase(1d / 8d, 1, 8));
        testCases.add(new DoubleToFractionTestCase(3d / 8d, 3, 8));
        testCases.add(new DoubleToFractionTestCase(5d / 8d, 5, 8));
        testCases.add(new DoubleToFractionTestCase(7d / 8d, 7, 8));
        testCases.add(new DoubleToFractionTestCase(1d / 9d, 1, 9));
        testCases.add(new DoubleToFractionTestCase(2d / 9d, 2, 9));
        testCases.add(new DoubleToFractionTestCase(4d / 9d, 4, 9));
        testCases.add(new DoubleToFractionTestCase(5d / 9d, 5, 9));
        testCases.add(new DoubleToFractionTestCase(7d / 9d, 7, 9));
        testCases.add(new DoubleToFractionTestCase(8d / 9d, 8, 9));
        testCases.add(new DoubleToFractionTestCase(1d / 10d, 1, 10));
        testCases.add(new DoubleToFractionTestCase(3d / 10d, 3, 10));
        testCases.add(new DoubleToFractionTestCase(7d / 10d, 7, 10));
        testCases.add(new DoubleToFractionTestCase(9d / 10d, 9, 10));
        testCases.add(new DoubleToFractionTestCase(1d / 11d, 1, 11));
        testCases.add(new DoubleToFractionTestCase(2d / 11d, 2, 11));
        testCases.add(new DoubleToFractionTestCase(3d / 11d, 3, 11));
        testCases.add(new DoubleToFractionTestCase(4d / 11d, 4, 11));
        testCases.add(new DoubleToFractionTestCase(5d / 11d, 5, 11));
        testCases.add(new DoubleToFractionTestCase(6d / 11d, 6, 11));
        testCases.add(new DoubleToFractionTestCase(7d / 11d, 7, 11));
        testCases.add(new DoubleToFractionTestCase(8d / 11d, 8, 11));
        testCases.add(new DoubleToFractionTestCase(9d / 11d, 9, 11));
        testCases.add(new DoubleToFractionTestCase(10d / 11d, 10, 11));

        testCases.add(new DoubleToFractionTestCase(-1d / 2d, -1, 2));
        testCases.add(new DoubleToFractionTestCase(-1d / 3d, -1, 3));

        testCases.add(new DoubleToFractionTestCase(0.00000000000001, 0, 1));
        testCases.add(new DoubleToFractionTestCase(0.40000000000001, 2, 5));
        testCases.add(new DoubleToFractionTestCase(15.0000000000001, 15, 1));
        testCases.add(new DoubleToFractionTestCase(15.0, 15, 1));
        testCases.add(new DoubleToFractionTestCase(0.0, 0, 1));
        testCases.add(new DoubleToFractionTestCase(-0.0, 0, 1));

        return testCases;
    }






    private static List<DoubleToFractionTestCase> collectDoubleMaxDenomConstructorTestCases() {
        final List<DoubleToFractionTestCase> testCases = new ArrayList<>();
        testCases.add(new DoubleToFractionTestCase(0.4,   9, 2, 5));
        testCases.add(new DoubleToFractionTestCase(0.4,  99, 2, 5));
        testCases.add(new DoubleToFractionTestCase(0.4, 999, 2, 5));
        testCases.add(new DoubleToFractionTestCase(-0.4,   9, -2, 5));
        testCases.add(new DoubleToFractionTestCase(-0.4,  99, -2, 5));
        testCases.add(new DoubleToFractionTestCase(-0.4, 999, -2, 5));

        testCases.add(new DoubleToFractionTestCase(0.6152,    9, 3, 5));
        testCases.add(new DoubleToFractionTestCase(0.6152,   99, 8, 13));
        testCases.add(new DoubleToFractionTestCase(0.6152,  999, 510, 829));
        testCases.add(new DoubleToFractionTestCase(0.6152, 9999, 769, 1250));
        testCases.add(new DoubleToFractionTestCase(-0.6152,    9, -3, 5));
        testCases.add(new DoubleToFractionTestCase(-0.6152,   99, -8, 13));
        testCases.add(new DoubleToFractionTestCase(-0.6152,  999, -510, 829));
        testCases.add(new DoubleToFractionTestCase(-0.6152, 9999, -769, 1250));


        testCases.add(new DoubleToFractionTestCase(0x1.0p-40, Integer.MAX_VALUE, 0, 1));
        testCases.add(new DoubleToFractionTestCase(-0x1.0p-40, Integer.MAX_VALUE, 0, 1));


        testCases.add(new DoubleToFractionTestCase(Math.nextUp((double) Integer.MAX_VALUE), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        testCases.add(new DoubleToFractionTestCase(-Math.nextUp((double) Integer.MAX_VALUE), Integer.MIN_VALUE, -Integer.MAX_VALUE, 1));
        testCases.add(new DoubleToFractionTestCase(Math.nextUp((double) Integer.MAX_VALUE) / (1 << 15), Integer.MIN_VALUE, Integer.MAX_VALUE, 1 << 15));
        testCases.add(new DoubleToFractionTestCase(-Math.nextUp((double) Integer.MAX_VALUE) / (1 << 15), Integer.MIN_VALUE, -Integer.MAX_VALUE, 1 << 15));
        testCases.add(new DoubleToFractionTestCase(Math.nextUp(1.0), Integer.MIN_VALUE, 1, 1));
        testCases.add(new DoubleToFractionTestCase(-Math.nextUp(1.0), Integer.MIN_VALUE, -1, 1));


        testCases.add(new DoubleToFractionTestCase(0.5000000001, 10, 1, 2));
        testCases.add(new DoubleToFractionTestCase(-0.5000000001, 10, -1, 2));


        testCases.add(new DoubleToFractionTestCase(Integer.MAX_VALUE * 1.0, 2, Integer.MAX_VALUE, 1));
        testCases.add(new DoubleToFractionTestCase(Integer.MAX_VALUE * -1.0, 2, -Integer.MAX_VALUE, 1));
        testCases.add(new DoubleToFractionTestCase(1.0 / Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE));
        testCases.add(new DoubleToFractionTestCase(-1.0 / Integer.MAX_VALUE, Integer.MAX_VALUE, -1, Integer.MAX_VALUE));
        testCases.add(new DoubleToFractionTestCase(Integer.MIN_VALUE * 1.0, 2, Integer.MIN_VALUE, 1));









        return testCases;
    }






    private static List<UnaryOperatorTestCase> collectAbsTestCases() {
        final List<UnaryOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new UnaryOperatorTestCase(10, 21, 10, 21));
        testCases.add(new UnaryOperatorTestCase(-11, 23, 11, 23));
        testCases.add(new UnaryOperatorTestCase(13, -24, -13, -24));
        testCases.add(new UnaryOperatorTestCase(0, 1, 0, 1));
        testCases.add(new UnaryOperatorTestCase(0, -1, 0, 1));

        return testCases;
    }






    private static List<UnaryOperatorTestCase> collectReciprocalTestCases() {
        final List<UnaryOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new UnaryOperatorTestCase(50, 75, 3, 2));
        testCases.add(new UnaryOperatorTestCase(4, 3, 3, 4));
        testCases.add(new UnaryOperatorTestCase(-15, 47, 47, -15));
        testCases.add(new UnaryOperatorTestCase(Integer.MAX_VALUE, 1, 1, Integer.MAX_VALUE));

        return testCases;
    }






    private static List<UnaryOperatorTestCase> collectNegateTestCases() {
        final List<UnaryOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new UnaryOperatorTestCase(50, 75, -2, 3));
        testCases.add(new UnaryOperatorTestCase(-50, 75, 2, 3));
        testCases.add(new UnaryOperatorTestCase(Integer.MAX_VALUE - 1, Integer.MAX_VALUE, Integer.MIN_VALUE + 2, Integer.MAX_VALUE));
        testCases.add(new UnaryOperatorTestCase(1, Integer.MIN_VALUE, -1, Integer.MIN_VALUE));
        testCases.add(new UnaryOperatorTestCase(0, 1, 0, 1));
        testCases.add(new UnaryOperatorTestCase(0, -1, 0, 1));






        return testCases;
    }






    private static List<BinaryOperatorTestCase> collectAddFractionTestCases() {
        final List<BinaryOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new BinaryOperatorTestCase(1, 2, 1, 2, 1, 1));
        testCases.add(new BinaryOperatorTestCase(1, 2, 2, 3, 7, 6));
        testCases.add(new BinaryOperatorTestCase(2, 3, 1, 2, 7, 6));
        testCases.add(new BinaryOperatorTestCase(2, 3, 2, 3, 4, 3));
        testCases.add(new BinaryOperatorTestCase(2, 3, 0, 5, 2, 3));
        testCases.add(new BinaryOperatorTestCase(2, 3, 0, -5, 2, 3));
        testCases.add(new BinaryOperatorTestCase(0, 7, 2, 3, 2, 3));
        testCases.add(new BinaryOperatorTestCase(0, -7, 2, 3, 2, 3));
        testCases.add(new BinaryOperatorTestCase(2, 3, -2, 3, 0, 1));

        testCases.add(new BinaryOperatorTestCase(
                -1, 13 * 13 * 2 * 2,
                -2, 13 * 17 * 2,
                -17 - 2 * 13 * 2,
                13 * 13 * 17 * 2 * 2));



        testCases.add(new BinaryOperatorTestCase(
                1, 32768 * 3,
                1, 59049,
                52451, 1934917632));

        testCases.add(new BinaryOperatorTestCase(
                Integer.MIN_VALUE, 3,
                1, 3,
                Integer.MIN_VALUE + 1, 3));

        testCases.add(new BinaryOperatorTestCase(
                Integer.MAX_VALUE - 1, 1,
                1, 1,
                Integer.MAX_VALUE, 1));


        testCases.add(new BinaryOperatorTestCase(
                362564597, 10,
                274164323, 6,
                1229257703, 15));

        return testCases;
    }






    private static List<BinaryIntOperatorTestCase> collectAddIntTestCases() {
        final List<BinaryIntOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new BinaryIntOperatorTestCase(1, 3, 0, 1, 3));
        testCases.add(new BinaryIntOperatorTestCase(-1, 3, 0, -1, 3));
        testCases.add(new BinaryIntOperatorTestCase(1, 3, 1, 4, 3));
        testCases.add(new BinaryIntOperatorTestCase(1, 3, -1, -2, 3));
        testCases.add(new BinaryIntOperatorTestCase(2, -1, 2, 0, 1));
        testCases.add(new BinaryIntOperatorTestCase(Integer.MAX_VALUE - 1, 1, 1, Integer.MAX_VALUE, 1));

        return testCases;
    }






    private static List<BinaryOperatorTestCase> collectDivideByFractionTestCases() {
        final List<BinaryOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new BinaryOperatorTestCase(1, 2, 1, 2, 1, 1));
        testCases.add(new BinaryOperatorTestCase(1, 2, 2, 3, 3, 4));
        testCases.add(new BinaryOperatorTestCase(2, 3, 1, 2, 4, 3));
        testCases.add(new BinaryOperatorTestCase(2, 3, 2, 3, 1, 1));
        testCases.add(new BinaryOperatorTestCase(0, 3, 2, 3, 0, 1));

        testCases.add(new BinaryOperatorTestCase(0, -3, 2, 3, 0, 1));

        testCases.add(new BinaryOperatorTestCase(
                2, 7,
                1, 1,
                2, 7));
        testCases.add(new BinaryOperatorTestCase(
                1, Integer.MAX_VALUE,
                1, Integer.MAX_VALUE,
                1, 1));
        testCases.add(new BinaryOperatorTestCase(
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                1, Integer.MAX_VALUE,
                Integer.MIN_VALUE, 1));

        return testCases;
    }






    private static List<BinaryIntOperatorTestCase> collectDivideByIntTestCases() {
        final List<BinaryIntOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new BinaryIntOperatorTestCase(1, 3, 1, 1, 3));
        testCases.add(new BinaryIntOperatorTestCase(0, 5, 11, 0, 1));
        testCases.add(new BinaryIntOperatorTestCase(6, 35, 15, 2, 175));


        testCases.add(new BinaryIntOperatorTestCase(1, 3, -1, 1, -3));

        return testCases;
    }






    private static List<BinaryOperatorTestCase> collectMultiplyByFractionTestCases() {
        final List<BinaryOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new BinaryOperatorTestCase(1, 2, 1, 2, 1, 4));
        testCases.add(new BinaryOperatorTestCase(1, 2, 2, 3, 1, 3));
        testCases.add(new BinaryOperatorTestCase(2, 3, 1, 2, 1, 3));
        testCases.add(new BinaryOperatorTestCase(2, 3, 2, 3, 4, 9));
        testCases.add(new BinaryOperatorTestCase(0, 3, 2, 3, 0, 1));
        testCases.add(new BinaryOperatorTestCase(0, -3, 2, 3, 0, 1));
        testCases.add(new BinaryOperatorTestCase(2, 3, 0, 3, 0, 1));
        testCases.add(new BinaryOperatorTestCase(2, 3, 0, -3, 0, 1));

        testCases.add(new BinaryOperatorTestCase(
                Integer.MAX_VALUE, 1,
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                Integer.MIN_VALUE, 1));

        return testCases;
    }






    private static List<BinaryIntOperatorTestCase> collectMultiplyByIntTestCases() {
        final List<BinaryIntOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new BinaryIntOperatorTestCase(1, 3, 1, 1, 3));
        testCases.add(new BinaryIntOperatorTestCase(6, 35, 15, 18, 7));
        testCases.add(new BinaryIntOperatorTestCase(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, 1));

        testCases.add(new BinaryIntOperatorTestCase(0, 1, 42, 0, 1));
        testCases.add(new BinaryIntOperatorTestCase(1, 1, 0, 0, 1));


        testCases.add(new BinaryIntOperatorTestCase(1, 3, -1, -1, 3));

        return testCases;
    }






    private static List<BinaryOperatorTestCase> collectSubtractFractionTestCases() {
        final List<BinaryOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new BinaryOperatorTestCase(1, 2, 1, 2, 0, 1));
        testCases.add(new BinaryOperatorTestCase(1, 2, 2, 3, -1, 6));
        testCases.add(new BinaryOperatorTestCase(2, 3, 1, 2, 1, 6));
        testCases.add(new BinaryOperatorTestCase(2, 3, 2, 3, 0, 1));
        testCases.add(new BinaryOperatorTestCase(0, 3, 1, 5, -1, 5));
        testCases.add(new BinaryOperatorTestCase(0, -3, 1, 5, -1, 5));
        testCases.add(new BinaryOperatorTestCase(2, 3, 0, 5, 2, 3));
        testCases.add(new BinaryOperatorTestCase(2, 3, 0, -5, 2, 3));



        testCases.add(new BinaryOperatorTestCase(
                1, 32768 * 3,
                1, 59049,
                -13085, 1934917632));

        testCases.add(new BinaryOperatorTestCase(
                Integer.MIN_VALUE, 3,
                -1, 3,
                Integer.MIN_VALUE + 1, 3
        ));

        testCases.add(new BinaryOperatorTestCase(
                Integer.MAX_VALUE, 1,
                1, 1,
                Integer.MAX_VALUE - 1, 1
        ));

        return testCases;
    }






    private static List<BinaryIntOperatorTestCase> collectSubtractIntTestCases() {
        final List<BinaryIntOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new BinaryIntOperatorTestCase(1, 3, 1, -2, 3));
        testCases.add(new BinaryIntOperatorTestCase(0, 1, 3, -3, 1));
        testCases.add(new BinaryIntOperatorTestCase(2, 3, 3, -7, 3));
        testCases.add(new BinaryIntOperatorTestCase(2, 3, 0, 2, 3));
        testCases.add(new BinaryIntOperatorTestCase(2, -1, -2, 0, 1));

        return testCases;
    }






    private static List<BinaryIntOperatorTestCase> collectPowTestCases() {
        final List<BinaryIntOperatorTestCase> testCases = new ArrayList<>();

        testCases.add(new BinaryIntOperatorTestCase(3, 7, 0, 1, 1));
        testCases.add(new BinaryIntOperatorTestCase(3, 7, 1, 3, 7));
        testCases.add(new BinaryIntOperatorTestCase(3, 7, -1, 7, 3));
        testCases.add(new BinaryIntOperatorTestCase(3, 7, 2, 9, 49));
        testCases.add(new BinaryIntOperatorTestCase(3, 7, -2, 49, 9));

        testCases.add(new BinaryIntOperatorTestCase(3, -7, 0, 1, 1));
        testCases.add(new BinaryIntOperatorTestCase(3, -7, 1, 3, -7));
        testCases.add(new BinaryIntOperatorTestCase(3, -7, -1, -7, 3));
        testCases.add(new BinaryIntOperatorTestCase(3, -7, 2, 9, 49));
        testCases.add(new BinaryIntOperatorTestCase(3, -7, -2, 49, 9));

        testCases.add(new BinaryIntOperatorTestCase(2, 3, 13, 8192, 1594323));
        testCases.add(new BinaryIntOperatorTestCase(2, 3, -13, 1594323, 8192));

        testCases.add(new BinaryIntOperatorTestCase(0, 1, Integer.MAX_VALUE, 0, 1));
        testCases.add(new BinaryIntOperatorTestCase(0, -1, Integer.MAX_VALUE, 0, 1));

        testCases.add(new BinaryIntOperatorTestCase(1, 1, Integer.MIN_VALUE, 1, 1));
        testCases.add(new BinaryIntOperatorTestCase(1, -1, Integer.MIN_VALUE, 1, 1));
        testCases.add(new BinaryIntOperatorTestCase(-1, 1, Integer.MIN_VALUE, 1, 1));
        testCases.add(new BinaryIntOperatorTestCase(-1, -1, Integer.MIN_VALUE, 1, 1));
        return testCases;
    }









    static List<UnaryOperatorTestCase> numDenConstructorTestCases() {
        return Collections.unmodifiableList(numDenConstructorTestCasesList);
    }











    static List<DoubleToFractionTestCase> doubleConstructorTestCases() {
        return Collections.unmodifiableList(doubleConstructorTestCasesList);
    }









    static List<DoubleToFractionTestCase> doubleMaxDenomConstructorTestCases() {
        return Collections.unmodifiableList(doubleMaxDenomConstructorTestCasesList);
    }








    static List<UnaryOperatorTestCase> absTestCases() {
        return Collections.unmodifiableList(absTestCasesList);
    }










    static List<UnaryOperatorTestCase> reciprocalTestCases() {
        return Collections.unmodifiableList(reciprocalTestCasesList);
    }









    static List<UnaryOperatorTestCase> negateTestCases() {
        return Collections.unmodifiableList(negateTestCasesList);
    }









    static List<BinaryOperatorTestCase> addFractionTestCases() {
        return Collections.unmodifiableList(addFractionTestCasesList);
    }










    static List<BinaryIntOperatorTestCase> addIntTestCases() {
        return Collections.unmodifiableList(addIntTestCasesList);
    }














    static List<BinaryOperatorTestCase> divideByFractionTestCases() {
        return Collections.unmodifiableList(divideByFractionTestCasesList);
    }













    static List<BinaryIntOperatorTestCase> divideByIntTestCases() {
        return Collections.unmodifiableList(divideByIntTestCasesList);
    }











    static List<BinaryOperatorTestCase> multiplyByFractionTestCases() {
        return Collections.unmodifiableList(multiplyByFractionTestCasesList);
    }










    static List<BinaryIntOperatorTestCase> multiplyByIntTestCases() {
        return Collections.unmodifiableList(multiplyByIntTestCasesList);
    }














    static List<BinaryOperatorTestCase> subtractFractionTestCases() {
        return Collections.unmodifiableList(subtractFractionTestCasesList);
    }













    static List<BinaryIntOperatorTestCase> subtractIntTestCases() {
        return Collections.unmodifiableList(subtractIntTestCasesList);
    }










    static List<BinaryIntOperatorTestCase> powTestCases() {
        return Collections.unmodifiableList(powTestCasesList);
    }









    static class UnaryOperatorTestCase {
        final int operandNumerator;
        final int operandDenominator;
        final int expectedNumerator;
        final int expectedDenominator;

        UnaryOperatorTestCase(
                int operandNumerator,
                int operandDenominator,
                int expectedNumerator,
                int expectedDenominator) {
            this.operandNumerator = operandNumerator;
            this.operandDenominator = operandDenominator;
            this.expectedNumerator = expectedNumerator;
            this.expectedDenominator = expectedDenominator;
        }
    }








    static class BinaryOperatorTestCase {
        final int firstOperandNumerator;
        final int firstOperandDenominator;
        final int secondOperandNumerator;
        final int secondOperandDenominator;
        final int expectedNumerator;
        final int expectedDenominator;

        BinaryOperatorTestCase(
                int firstOperandNumerator,
                int firstOperandDenominator,
                int secondOperandNumerator,
                int secondOperandDenominator,
                int expectedNumerator,
                int expectedDenominator) {
            this.firstOperandNumerator = firstOperandNumerator;
            this.firstOperandDenominator = firstOperandDenominator;
            this.secondOperandNumerator = secondOperandNumerator;
            this.secondOperandDenominator = secondOperandDenominator;
            this.expectedNumerator = expectedNumerator;
            this.expectedDenominator = expectedDenominator;
        }
    }








    static class BinaryIntOperatorTestCase {
        final int firstOperandNumerator;
        final int firstOperandDenominator;
        final int secondOperand;
        final int expectedNumerator;
        final int expectedDenominator;

        BinaryIntOperatorTestCase(
                int firstOperandNumerator,
                int firstOperandDenominator,
                int secondOperand,
                int expectedNumerator,
                int expectedDenominator) {
            this.firstOperandNumerator = firstOperandNumerator;
            this.firstOperandDenominator = firstOperandDenominator;
            this.secondOperand = secondOperand;
            this.expectedNumerator = expectedNumerator;
            this.expectedDenominator = expectedDenominator;
        }
    }










    static class DoubleToFractionTestCase {
        final double operand;
        final int maxDenominator;
        final int expectedNumerator;
        final int expectedDenominator;

        DoubleToFractionTestCase(
                double operand,
                int maxDenominator,
                int expectedNumerator,
                int expectedDenominator) {
            this.operand = operand;
            this.maxDenominator = maxDenominator;
            this.expectedNumerator = expectedNumerator;
            this.expectedDenominator = expectedDenominator;
        }

        DoubleToFractionTestCase(
                double operand,
                int expectedNumerator,
                int expectedDenominator) {
            this(operand, 0, expectedNumerator, expectedDenominator);
        }
    }
}
