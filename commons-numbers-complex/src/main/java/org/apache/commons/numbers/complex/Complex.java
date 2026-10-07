
















package org.apache.commons.numbers.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;








































public final class Complex implements Serializable  {





    public static final Complex I = new Complex(0, 1);





    public static final Complex ONE = new Complex(1, 0);





    public static final Complex ZERO = new Complex(0, 0);


    private static final Complex NAN = new Complex(Double.NaN, Double.NaN);

    private static final double PI_OVER_2 = 0.5 * Math.PI;

    private static final double PI_OVER_4 = 0.25 * Math.PI;

    private static final double LN_2 = Math.log(2);

    private static final double LOG10_E_O_2 = Math.log10(Math.E) * 0.5;

    private static final double LOG10_2 = Math.log10(2);

    private static final double HALF = 0.5;

    private static final double ROOT2 = 1.4142135623730951;




    private static final double ONE_OVER_ROOT2 = 0.7071067811865476;

    private static final long NEGATIVE_ZERO_LONG_BITS = Double.doubleToLongBits(-0.0);

    private static final int EXPONENT_OFFSET = 1023;











    private static final double EPSILON = Double.longBitsToDouble((EXPONENT_OFFSET - 53L) << 52);

    private static final long UNSIGN_MASK = 0x7fff_ffff_ffff_ffffL;

    private static final long MANTISSA_MASK = 0x000f_ffff_ffff_ffffL;



    private static final double MULTIPLIER = 1.34217729E8;







    private static final double A_CROSSOVER = 10.0;

    private static final double B_CROSSOVER = 0.6471;




    private static final double SAFE_MAX = Math.sqrt(Double.MAX_VALUE) / 8;




    private static final double SAFE_MIN = Math.sqrt(Double.MIN_NORMAL) * 4;




    private static final double SAFE_UPPER = Math.sqrt(Double.MAX_VALUE) / 2;




    private static final double SAFE_LOWER = Math.sqrt(Double.MIN_NORMAL) * 2;

    private static final double SQRT_SAFE_UPPER = Double.MAX_VALUE / 8;


























    private static final double SAFE_EXP = 708;




    private static final double EXP_M = Math.exp(SAFE_EXP);


    private static final int EXP_54 = 0x36_00000;

    private static final int EXP_500 = 0x5f3_00000;


    private static final int EXP_1024 = 0x7ff_00000;

    private static final int EXP_NEG_500 = 0x20b_00000;

    private static final double TWO_POW_600 = 0x1.0p+600;

    private static final double TWO_POW_NEG_600 = 0x1.0p-600;


    private static final long serialVersionUID = 20180201L;









    private static final int TO_STRING_SIZE = 64;

    private static final int FORMAT_MIN_LEN = 5;

    private static final char FORMAT_START = '(';

    private static final char FORMAT_END = ')';

    private static final char FORMAT_SEP = ',';

    private static final int BEFORE_SEP = 2;


    private final double imaginary;

    private final double real;





    @FunctionalInterface
    private interface ComplexConstructor {







        Complex create(double real, double imaginary);
    }







    private Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }








    public static Complex ofCartesian(double real, double imaginary) {
        return new Complex(real, imaginary);
    }










































    public static Complex ofPolar(double rho, double theta) {

        if (!Double.isFinite(theta) || negative(rho) || Double.isNaN(rho)) {
            return NAN;
        }
        final double x = rho * Math.cos(theta);
        final double y = rho * Math.sin(theta);
        return new Complex(x, y);
    }










    public static Complex ofCis(double x) {
        return new Complex(Math.cos(x), Math.sin(x));
    }





























    public static Complex parse(String s) {
        final int len = s.length();
        if (len < FORMAT_MIN_LEN) {
            throw new NumberFormatException(
                parsingExceptionMsg("Input too short, expected format",
                                    FORMAT_START + "x" + FORMAT_SEP + "y" + FORMAT_END, s));
        }


        if (s.charAt(0) != FORMAT_START) {
            throw new NumberFormatException(
                parsingExceptionMsg("Expected start delimiter", FORMAT_START, s));
        }


        if (s.charAt(len - 1) != FORMAT_END) {
            throw new NumberFormatException(
                parsingExceptionMsg("Expected end delimiter", FORMAT_END, s));
        }




        final int sep = s.lastIndexOf(FORMAT_SEP, len - 3);
        if (sep < BEFORE_SEP) {
            throw new NumberFormatException(
                parsingExceptionMsg("Expected separator between two numbers", FORMAT_SEP, s));
        }


        if (s.indexOf(FORMAT_SEP, sep + 1) != -1) {
            throw new NumberFormatException(
                parsingExceptionMsg("Incorrect number of parts, expected only 2 using separator",
                                    FORMAT_SEP, s));
        }



        final String rePart = s.substring(1, sep);
        final double re;
        try {
            re = Double.parseDouble(rePart);
        } catch (final NumberFormatException ex) {
            throw new NumberFormatException(
                parsingExceptionMsg("Could not parse real part", rePart, s));
        }

        final String imPart = s.substring(sep + 1, len - 1);
        final double im;
        try {
            im = Double.parseDouble(imPart);
        } catch (final NumberFormatException ex) {
            throw new NumberFormatException(
                parsingExceptionMsg("Could not parse imaginary part", imPart, s));
        }

        return ofCartesian(re, im);
    }









    private static String parsingExceptionMsg(String message,
                                              Object error,
                                              String s) {
        final StringBuilder sb = new StringBuilder(100)
            .append(message)
            .append(" '").append(error)
            .append("' for input \"").append(s).append('"');
        return sb.toString();
    }






    public double getReal() {
        return real;
    }









    public double real() {
        return getReal();
    }






    public double getImaginary() {
        return imaginary;
    }









    public double imag() {
        return getImaginary();
    }































    public double abs() {
        return abs(real, imaginary);
    }






















    private static double abs(double real, double imaginary) {


        return hypot(real, imaginary);
    }
























    public double arg() {

        return Math.atan2(imaginary, real);
    }























    public double norm() {
        if (isInfinite()) {
            return Double.POSITIVE_INFINITY;
        }
        return real * real + imaginary * imaginary;
    }

















    public boolean isNaN() {
        if (Double.isNaN(real) || Double.isNaN(imaginary)) {
            return !isInfinite();
        }
        return false;
    }










    public boolean isInfinite() {
        return Double.isInfinite(real) || Double.isInfinite(imaginary);
    }







    public boolean isFinite() {
        return Double.isFinite(real) && Double.isFinite(imaginary);
    }












    public Complex conj() {
        return new Complex(real, -imaginary);
    }











    public Complex negate() {
        return new Complex(-real, -imaginary);
    }
















    public Complex proj() {
        if (isInfinite()) {
            return new Complex(Double.POSITIVE_INFINITY, Math.copySign(0.0, imaginary));
        }
        return this;
    }











    public Complex add(Complex addend) {
        return new Complex(real + addend.real,
                           imaginary + addend.imaginary);
    }





















    public Complex add(double addend) {
        return new Complex(real + addend, imaginary);
    }





















    public Complex addImaginary(double addend) {
        return new Complex(real, imaginary + addend);
    }











    public Complex subtract(Complex subtrahend) {
        return new Complex(real - subtrahend.real,
                           imaginary - subtrahend.imaginary);
    }















    public Complex subtract(double subtrahend) {
        return new Complex(real - subtrahend, imaginary);
    }















    public Complex subtractImaginary(double subtrahend) {
        return new Complex(real, imaginary - subtrahend);
    }




















    public Complex subtractFrom(double minuend) {
        return new Complex(minuend - real, -imaginary);
    }




















    public Complex subtractFromImaginary(double minuend) {
        return new Complex(-real, minuend - imaginary);
    }













    public Complex multiply(Complex factor) {
        return multiply(real, imaginary, factor.real, factor.imaginary);
    }
















    private static Complex multiply(double re1, double im1, double re2, double im2) {
        double a = re1;
        double b = im1;
        double c = re2;
        double d = im2;
        final double ac = a * c;
        final double bd = b * d;
        final double ad = a * d;
        final double bc = b * c;
        double x = ac - bd;
        double y = ad + bc;














        if (Double.isNaN(x) && Double.isNaN(y)) {

            boolean recalc = false;
            if ((Double.isInfinite(a) || Double.isInfinite(b)) &&
                isNotZero(c, d)) {


                a = boxInfinity(a);
                b = boxInfinity(b);
                c = changeNaNtoZero(c);
                d = changeNaNtoZero(d);
                recalc = true;
            }
            if ((Double.isInfinite(c) || Double.isInfinite(d)) &&
                isNotZero(a, b)) {


                c = boxInfinity(c);
                d = boxInfinity(d);
                a = changeNaNtoZero(a);
                b = changeNaNtoZero(b);
                recalc = true;
            }
            if (!recalc && (Double.isInfinite(ac) || Double.isInfinite(bd) ||
                            Double.isInfinite(ad) || Double.isInfinite(bc))) {


                a = changeNaNtoZero(a);
                b = changeNaNtoZero(b);
                c = changeNaNtoZero(c);
                d = changeNaNtoZero(d);
                recalc = true;
            }
            if (recalc) {
                x = Double.POSITIVE_INFINITY * (a * c - b * d);
                y = Double.POSITIVE_INFINITY * (a * d + b * c);
            }
        }
        return new Complex(x, y);
    }
















    private static double boxInfinity(double component) {
        return Math.copySign(Double.isInfinite(component) ? 1.0 : 0.0, component);
    }








    private static boolean isNotZero(double real, double imaginary) {



        return !(real == 0.0 && imaginary == 0.0);
    }







    private static double changeNaNtoZero(double value) {
        return Double.isNaN(value) ? Math.copySign(0.0, value) : value;
    }






















    public Complex multiply(double factor) {
        return new Complex(real * factor, imaginary * factor);
    }






























    public Complex multiplyImaginary(double factor) {
        return new Complex(-imaginary * factor, real * factor);
    }













    public Complex divide(Complex divisor) {
        return divide(real, imaginary, divisor.real, divisor.imaginary);
    }




























    private static Complex divide(double re1, double im1, double re2, double im2) {
        double a = re1;
        double b = im1;
        double c = re2;
        double d = im2;
        int ilogbw = 0;

        final int exponent = getScale(c, d);
        if (exponent <= Double.MAX_EXPONENT) {
            ilogbw = exponent;
            c = Math.scalb(c, -ilogbw);
            d = Math.scalb(d, -ilogbw);
        }
        final double denom = c * c + d * d;







        if (getMaxExponent(a, b) > Double.MAX_EXPONENT - 2) {
            ilogbw -= 2;
            a /= 4;
            b /= 4;
        }

        double x = Math.scalb((a * c + b * d) / denom, -ilogbw);
        double y = Math.scalb((b * c - a * d) / denom, -ilogbw);


        if (Double.isNaN(x) && Double.isNaN(y)) {
            if (denom == 0.0 &&
                    (!Double.isNaN(a) || !Double.isNaN(b))) {



                x = Math.copySign(Double.POSITIVE_INFINITY, c) * a;
                y = Math.copySign(Double.POSITIVE_INFINITY, c) * b;
            } else if ((Double.isInfinite(a) || Double.isInfinite(b)) &&
                    Double.isFinite(c) && Double.isFinite(d)) {

                a = boxInfinity(a);
                b = boxInfinity(b);
                x = Double.POSITIVE_INFINITY * (a * c + b * d);
                y = Double.POSITIVE_INFINITY * (b * c - a * d);
            } else if ((Double.isInfinite(c) || Double.isInfinite(d)) &&
                    Double.isFinite(a) && Double.isFinite(b)) {

                c = boxInfinity(c);
                d = boxInfinity(d);
                x = 0.0 * (a * c + b * d);
                y = 0.0 * (b * c - a * d);
            }
        }
        return new Complex(x, y);
    }






















    public Complex divide(double divisor) {
        return new Complex(real / divisor, imaginary / divisor);
    }































    public Complex divideImaginary(double divisor) {
        return new Complex(imaginary / divisor, -real / divisor);
    }



































    public Complex exp() {
        if (Double.isInfinite(real)) {

            final double zeroOrInf;
            if (real < 0) {
                if (!Double.isFinite(imaginary)) {



                    return new Complex(0, Math.copySign(0, imaginary));
                }

                zeroOrInf = 0;
            } else {

                if (imaginary == 0) {
                    return this;
                }



                if (!Double.isFinite(imaginary)) {
                    return new Complex(real, Double.NaN);
                }

                zeroOrInf = real;
            }
            return new Complex(zeroOrInf * Math.cos(imaginary),
                               zeroOrInf * Math.sin(imaginary));
        } else if (Double.isNaN(real)) {



            return imaginary == 0 ? this : NAN;
        } else if (!Double.isFinite(imaginary)) {


            return NAN;
        }





        final double exp = Math.exp(real);
        if (imaginary == 0) {
            return new Complex(exp, imaginary);
        }
        return new Complex(exp * Math.cos(imaginary),
                           exp * Math.sin(imaginary));
    }














































    public Complex log() {
        return log(Math::log, HALF, LN_2, Complex::ofCartesian);
    }






















    public Complex log10() {
        return log(Math::log10, LOG10_E_O_2, LOG10_2, Complex::ofCartesian);
    }




















    private Complex log(DoubleUnaryOperator log, double logOfeOver2, double logOf2, ComplexConstructor constructor) {

        if (Double.isNaN(real) || Double.isNaN(imaginary)) {

            if (isInfinite()) {
                return constructor.create(Double.POSITIVE_INFINITY, Double.NaN);
            }

            return NAN;
        }






        double x = Math.abs(real);
        double y = Math.abs(imaginary);


        if (x < y) {
            final double tmp = x;
            x = y;
            y = tmp;
        }

        if (x == 0) {

            return constructor.create(Double.NEGATIVE_INFINITY,
                                      negative(real) ? Math.copySign(Math.PI, imaginary) : imaginary);
        }

        double re;








        if (x > HALF && x < ROOT2) {

            re = Math.log1p(x2y2m1(x, y)) * logOfeOver2;
        } else {




            re = 0;
            if (x > Double.MAX_VALUE / 2) {

                if (isPosInfinite(x)) {

                    return constructor.create(x, arg());
                }

                x /= 2;
                y /= 2;

                re = logOf2;
            } else if (y < Double.MIN_NORMAL) {

                if (y == 0) {

                    return constructor.create(log.applyAsDouble(x), arg());
                }


                x *= 0x1.0p54;
                y *= 0x1.0p54;

                re = -54 * logOf2;
            }
            re += log.applyAsDouble(abs(x, y));
        }


        return constructor.create(re, arg());
    }



















    public Complex pow(Complex x) {
        if (real == 0 &&
            imaginary == 0) {

            if (x.real > 0 &&
                x.imaginary == 0) {

                return ZERO;
            }

            return NAN;
        }
        return log().multiply(x).exp();
    }



















    public Complex pow(double x) {
        if (real == 0 &&
            imaginary == 0) {

            if (x > 0) {

                return ZERO;
            }

            return NAN;
        }
        return log().multiply(x).exp();
    }
















































    public Complex sqrt() {
        return sqrt(real, imaginary);
    }








    private static Complex sqrt(double real, double imaginary) {

        if (Double.isNaN(real) || Double.isNaN(imaginary)) {

            if (Double.isInfinite(imaginary)) {
                return new Complex(Double.POSITIVE_INFINITY, imaginary);
            }
            if (Double.isInfinite(real)) {
                if (real == Double.NEGATIVE_INFINITY) {
                    return new Complex(Double.NaN, Math.copySign(Double.POSITIVE_INFINITY, imaginary));
                }
                return new Complex(Double.POSITIVE_INFINITY, Double.NaN);
            }
            return NAN;
        }


        final double x = Math.abs(real);
        final double y = Math.abs(imaginary);


        final double t;








        if (inRegion(x, y, Double.MIN_NORMAL, SQRT_SAFE_UPPER)) {

            t = Math.sqrt(2 * (abs(x, y) + x));
        } else {



            if (isPosInfinite(y)) {
                return new Complex(Double.POSITIVE_INFINITY, imaginary);
            } else if (isPosInfinite(x)) {
                if (real == Double.NEGATIVE_INFINITY) {
                    return new Complex(0, Math.copySign(Double.POSITIVE_INFINITY, imaginary));
                }
                return new Complex(Double.POSITIVE_INFINITY, Math.copySign(0, imaginary));
            } else if (y == 0) {

                final double sqrtAbs = Math.sqrt(x);
                if (real < 0) {
                    return new Complex(0, Math.copySign(sqrtAbs, imaginary));
                }
                return new Complex(sqrtAbs, imaginary);
            } else if (x == 0) {






                final double sqrtAbs = Math.sqrt(y) * ONE_OVER_ROOT2;
                return new Complex(sqrtAbs, Math.copySign(sqrtAbs, imaginary));
            } else {







                final double rescale;
                final double sx;
                final double sy;
                if (Math.max(x, y) > SQRT_SAFE_UPPER) {

                    sx = x / 16;
                    sy = y / 16;
                    rescale = 4;
                } else {


                    sx = x * 0x1.0p54;
                    sy = y * 0x1.0p54;
                    rescale = 0x1.0p-27;
                }
                t = rescale * Math.sqrt(2 * (abs(sx, sy) + sx));
            }
        }

        if (real >= 0) {
            return new Complex(t / 2, imaginary / t);
        }
        return new Complex(y / t, Math.copySign(t / 2, imaginary));
    }






















    public Complex sin() {



        return sinh(-imaginary, real, Complex::multiplyNegativeI);
    }






















    public Complex cos() {



        return cosh(-imaginary, real, Complex::ofCartesian);
    }




















    public Complex tan() {



        return tanh(-imaginary, real, Complex::multiplyNegativeI);
    }







































    public Complex asin() {
        return asin(real, imaginary, Complex::ofCartesian);
    }





















    private static Complex asin(final double real, final double imaginary,
                                final ComplexConstructor constructor) {

        final double x = Math.abs(real);
        final double y = Math.abs(imaginary);

        final double re;
        final double im;


        if (Double.isNaN(x)) {
            if (isPosInfinite(y)) {
                re = x;
                im = y;
            } else {

                return NAN;
            }
        } else if (Double.isNaN(y)) {
            if (x == 0) {
                re = 0;
                im = y;
            } else if (isPosInfinite(x)) {
                re = y;
                im = x;
            } else {

                return NAN;
            }
        } else if (isPosInfinite(x)) {
            re = isPosInfinite(y) ? PI_OVER_4 : PI_OVER_2;
            im = x;
        } else if (isPosInfinite(y)) {
            re = 0;
            im = y;
        } else {

            if (y == 0 && x <= 1) {
                return constructor.create(Math.asin(real), imaginary);
            }

            final double xp1 = x + 1;
            final double xm1 = x - 1;

            if (inRegion(x, y, SAFE_MIN, SAFE_MAX)) {
                final double yy = y * y;
                final double r = Math.sqrt(xp1 * xp1 + yy);
                final double s = Math.sqrt(xm1 * xm1 + yy);
                final double a = 0.5 * (r + s);
                final double b = x / a;

                if (b <= B_CROSSOVER) {
                    re = Math.asin(b);
                } else {
                    final double apx = a + x;
                    if (x <= 1) {
                        re = Math.atan(x / Math.sqrt(0.5 * apx * (yy / (r + xp1) + (s - xm1))));
                    } else {
                        re = Math.atan(x / (y * Math.sqrt(0.5 * (apx / (r + xp1) + apx / (s + xm1)))));
                    }
                }

                if (a <= A_CROSSOVER) {
                    final double am1;
                    if (x < 1) {
                        am1 = 0.5 * (yy / (r + xp1) + yy / (s - xm1));
                    } else {
                        am1 = 0.5 * (yy / (r + xp1) + (s + xm1));
                    }
                    im = Math.log1p(am1 + Math.sqrt(am1 * (a + 1)));
                } else {
                    im = Math.log(a + Math.sqrt(a * a - 1));
                }
            } else {

                if (y <= (EPSILON * Math.abs(xm1))) {
                    if (x < 1) {
                        re = Math.asin(x);
                        im = y / Math.sqrt(xp1 * (1 - x));
                    } else {
                        re = PI_OVER_2;
                        if ((Double.MAX_VALUE / xp1) > xm1) {

                            im = Math.log1p(xm1 + Math.sqrt(xp1 * xm1));
                        } else {
                            im = LN_2 + Math.log(x);
                        }
                    }
                } else if (y <= SAFE_MIN) {






                    re = PI_OVER_2 - Math.sqrt(y);
                    im = Math.sqrt(y);
                } else if (EPSILON * y - 1 >= x) {

                    re = x / y;
                    im = LN_2 + Math.log(y);
                } else if (x > 1) {
                    re = Math.atan(x / y);
                    final double xoy = x / y;
                    im = LN_2 + Math.log(y) + 0.5 * Math.log1p(xoy * xoy);
                } else {
                    final double a = Math.sqrt(1 + y * y);

                    re = x / a;
                    im = 0.5 * Math.log1p(2 * y * (y + a));
                }
            }
        }

        return constructor.create(changeSign(re, real),
                                  changeSign(im, imaginary));
    }






















































    public Complex acos() {
        return acos(real, imaginary, Complex::ofCartesian);
    }





















    private static Complex acos(final double real, final double imaginary,
                                final ComplexConstructor constructor) {

        final double x = Math.abs(real);
        final double y = Math.abs(imaginary);

        final double re;
        final double im;


        if (isPosInfinite(x)) {
            if (isPosInfinite(y)) {
                re = PI_OVER_4;
                im = y;
            } else if (Double.isNaN(y)) {

                return constructor.create(imaginary, real);
            } else {
                re = 0;
                im = Double.POSITIVE_INFINITY;
            }
        } else if (Double.isNaN(x)) {
            if (isPosInfinite(y)) {
                return constructor.create(x, -imaginary);
            }

            return NAN;
        } else if (isPosInfinite(y)) {
            re = PI_OVER_2;
            im = y;
        } else if (Double.isNaN(y)) {
            return constructor.create(x == 0 ? PI_OVER_2 : y, y);
        } else {

            if (y == 0 && x <= 1) {
                return constructor.create(x == 0 ? PI_OVER_2 : Math.acos(real), -imaginary);
            }

            final double xp1 = x + 1;
            final double xm1 = x - 1;

            if (inRegion(x, y, SAFE_MIN, SAFE_MAX)) {
                final double yy = y * y;
                final double r = Math.sqrt(xp1 * xp1 + yy);
                final double s = Math.sqrt(xm1 * xm1 + yy);
                final double a = 0.5 * (r + s);
                final double b = x / a;

                if (b <= B_CROSSOVER) {
                    re = Math.acos(b);
                } else {
                    final double apx = a + x;
                    if (x <= 1) {
                        re = Math.atan(Math.sqrt(0.5 * apx * (yy / (r + xp1) + (s - xm1))) / x);
                    } else {
                        re = Math.atan((y * Math.sqrt(0.5 * (apx / (r + xp1) + apx / (s + xm1)))) / x);
                    }
                }

                if (a <= A_CROSSOVER) {
                    final double am1;
                    if (x < 1) {
                        am1 = 0.5 * (yy / (r + xp1) + yy / (s - xm1));
                    } else {
                        am1 = 0.5 * (yy / (r + xp1) + (s + xm1));
                    }
                    im = Math.log1p(am1 + Math.sqrt(am1 * (a + 1)));
                } else {
                    im = Math.log(a + Math.sqrt(a * a - 1));
                }
            } else {

                if (y <= (EPSILON * Math.abs(xm1))) {
                    if (x < 1) {
                        re = Math.acos(x);
                        im = y / Math.sqrt(xp1 * (1 - x));
                    } else {


                        if ((Double.MAX_VALUE / xp1) > xm1) {

                            re = y / Math.sqrt(xm1 * xp1);
                            im = Math.log1p(xm1 + Math.sqrt(xp1 * xm1));
                        } else {
                            re = y / x;
                            im = LN_2 + Math.log(x);
                        }
                    }
                } else if (y <= SAFE_MIN) {






                    re = Math.sqrt(y);
                    im = Math.sqrt(y);
                } else if (EPSILON * y - 1 >= x) {
                    re = PI_OVER_2;
                    im = LN_2 + Math.log(y);
                } else if (x > 1) {
                    re = Math.atan(y / x);
                    final double xoy = x / y;
                    im = LN_2 + Math.log(y) + 0.5 * Math.log1p(xoy * xoy);
                } else {
                    re = PI_OVER_2;
                    final double a = Math.sqrt(1 + y * y);
                    im = 0.5 * Math.log1p(2 * y * (y + a));
                }
            }
        }

        return constructor.create(negative(real) ? Math.PI - re : re,
                                  negative(imaginary) ? im : -im);
    }





















    public Complex atan() {



        return atanh(-imaginary, real, Complex::multiplyNegativeI);
    }




































    public Complex sinh() {
        return sinh(real, imaginary, Complex::ofCartesian);
    }












    private static Complex sinh(double real, double imaginary, ComplexConstructor constructor) {
        if (Double.isInfinite(real) && !Double.isFinite(imaginary)) {
            return constructor.create(real, Double.NaN);
        }
        if (real == 0) {

            if (Double.isFinite(imaginary)) {


                return constructor.create(changeSign(real, Math.cos(imaginary)),
                                          Math.sin(imaginary));
            }



            return constructor.create(real, Double.NaN);
        }
        if (imaginary == 0) {

            return constructor.create(Math.sinh(real), imaginary);
        }
        final double x = Math.abs(real);
        if (x > SAFE_EXP) {

            return coshsinh(x, real, imaginary, true, constructor);
        }

        return constructor.create(Math.sinh(real) * Math.cos(imaginary),
                                  Math.cosh(real) * Math.sin(imaginary));
    }




































    public Complex cosh() {
        return cosh(real, imaginary, Complex::ofCartesian);
    }












    private static Complex cosh(double real, double imaginary, ComplexConstructor constructor) {


        if (Double.isInfinite(real) && !Double.isFinite(imaginary)) {
            return constructor.create(Math.abs(real), Double.NaN);
        }
        if (real == 0) {

            if (Double.isFinite(imaginary)) {


                return constructor.create(Math.cos(imaginary),
                                          changeSign(real, Math.sin(imaginary)));
            }



            return constructor.create(Double.NaN, changeSign(real, imaginary));
        }
        if (imaginary == 0) {





            return constructor.create(Math.cosh(real), changeSign(imaginary, real));
        }
        final double x = Math.abs(real);
        if (x > SAFE_EXP) {

            return coshsinh(x, real, imaginary, false, constructor);
        }

        return constructor.create(Math.cosh(real) * Math.cos(imaginary),
                                  Math.sinh(real) * Math.sin(imaginary));
    }



















    private static Complex coshsinh(double x, double real, double imaginary, boolean sinh,
                                    ComplexConstructor constructor) {

        double re = Math.cos(imaginary);
        double im = Math.sin(imaginary);

        if (sinh) {
            re = changeSign(re, real);
        } else {
            im = changeSign(im, real);
        }



        if (x > SAFE_EXP * 3) {




            re *= Double.MAX_VALUE * Double.MAX_VALUE * Double.MAX_VALUE;
            im *= Double.MAX_VALUE * Double.MAX_VALUE * Double.MAX_VALUE;
        } else {

            re *= EXP_M / 2;
            im *= EXP_M / 2;
            final double xm;
            if (x > SAFE_EXP * 2) {

                re *= EXP_M;
                im *= EXP_M;
                xm = x - SAFE_EXP * 2;
            } else {

                xm = x - SAFE_EXP;
            }
            final double exp = Math.exp(xm);
            re *= exp;
            im *= exp;
        }
        return constructor.create(re, im);
    }













































    public Complex tanh() {
        return tanh(real, imaginary, Complex::ofCartesian);
    }












    private static Complex tanh(double real, double imaginary, ComplexConstructor constructor) {

        final double x = Math.abs(real);


        if (!isPosFinite(x) || !Double.isFinite(imaginary)) {
            if (isPosInfinite(x)) {
                if (Double.isFinite(imaginary)) {




                    final double sign = Math.abs(imaginary) < PI_OVER_2 ?
                                        imaginary :
                                        Math.sin(imaginary) * Math.cos(imaginary);
                    return constructor.create(Math.copySign(1, real),
                                              Math.copySign(0, sign));
                }

                return constructor.create(Math.copySign(1, real), Math.copySign(0, imaginary));
            }








            return constructor.create(real == 0 ? real : Double.NaN,
                                      imaginary == 0 ? imaginary : Double.NaN);
        }




        if (real == 0) {


            return constructor.create(real, Math.tan(imaginary));
        }
        if (imaginary == 0) {

            return constructor.create(Math.tanh(real), imaginary);
        }










        if (x > SAFE_EXP / 2) {




            final double re = Math.copySign(1, real);








            double im = Math.sin(imaginary) * Math.cos(imaginary);
            if (x > SAFE_EXP) {


                im = Math.copySign(0.0, im);
            } else {

                im = 4 * im / EXP_M / Math.exp(2 * x - SAFE_EXP);
            }
            return constructor.create(re, im);
        }






        final double sinhx = Math.sinh(real);
        final double coshx = Math.cosh(real);
        final double siny = Math.sin(imaginary);
        final double cosy = Math.cos(imaginary);
        final double divisor = sinhx * sinhx + cosy * cosy;
        return constructor.create(sinhx * coshx / divisor,
                                  siny * cosy / divisor);
    }





































    public Complex asinh() {





        return asin(-imaginary, real, Complex::multiplyNegativeI);
    }













































    public Complex acosh() {







        if (Double.isNaN(imaginary) && real == 0) {
            return new Complex(Double.NaN, PI_OVER_2);
        }
        return acos(real, imaginary, (re, im) ->

            negative(im) ?

                new Complex(-im, re) :

                new Complex(im, -re)
        );
    }













































    public Complex atanh() {
        return atanh(real, imaginary, Complex::ofCartesian);
    }





















    private static Complex atanh(final double real, final double imaginary,
                                 final ComplexConstructor constructor) {

        double x = Math.abs(real);
        double y = Math.abs(imaginary);

        double re;
        double im;


        if (Double.isNaN(x)) {
            if (isPosInfinite(y)) {

                return constructor.create(0, Math.copySign(PI_OVER_2, imaginary));
            }


            return NAN;
        } else if (Double.isNaN(y)) {
            if (isPosInfinite(x)) {
                return constructor.create(Math.copySign(0, real), Double.NaN);
            }
            if (x == 0) {
                return constructor.create(real, Double.NaN);
            }

            return NAN;
        } else {








            if (inRegion(x, y, SAFE_LOWER, SAFE_UPPER)) {



                final double mxp1 = 1 - x;
                final double yy = y * y;









                re = Math.log1p(4 * x / (mxp1 * mxp1 + yy));



                final double numerator = 2 * y;
                final double denominator;
                if (x < y) {
                    final double tmp = x;
                    x = y;
                    y = tmp;
                }

                if (x >= 1) {
                    denominator = (1 - x) * (1 + x) - y * y;
                } else {



                    denominator = -x2y2m1(x, y);
                }
                im = Math.atan2(numerator, denominator);
            } else {




                if (x == 0) {
                    if (imaginary == 0) {
                        return constructor.create(real, imaginary);
                    }

                    return constructor.create(real, Math.atan(imaginary));
                }






                if (x >= SAFE_UPPER) {


                    if (isPosInfinite(x) || isPosInfinite(y)) {
                        re = 0;
                    } else if (y >= SAFE_UPPER) {

                        re = Math.log1p((4 / y) / (x / y + y / x));
                    } else if (y > 1) {

                        re = Math.log1p(4 / (x + y * y / x));
                    } else {

                        re = Math.log1p(4 / x);
                    }
                } else if (y >= SAFE_UPPER) {
                    if (x > 1) {

                        final double mxp1 = 1 - x;
                        re = Math.log1p((4 * x / y) / (mxp1 * mxp1 / y + y));
                    } else {



                        re = 4 * x / y / y;
                    }
                } else if (x == 1) {










                    re = 2 * (LN_2 - Math.log(y));
                } else {


                    final double mxp1 = 1 - x;
                    re = Math.log1p((4 * x) / (mxp1 * mxp1 + y * y));
                }






                if (x >= SAFE_UPPER || y >= SAFE_UPPER) {
                    im = Math.PI;
                } else if (x <= SAFE_LOWER) {

                    if (y <= SAFE_LOWER) {

                        im = Math.atan2(2 * y, 1);
                    } else {
                        im = Math.atan2(2 * y, 1 - y * y);
                    }
                } else {




                    im = Math.atan2(2 * y, (1 - x) * (1 + x));
                }
            }
        }

        re /= 4;
        im /= 2;
        return constructor.create(changeSign(re, real),
                                  changeSign(im, imaginary));
    }










    private static double x2y2m1(double x, double y) {

















        final double xx = x * x;
        final double yy = y * y;





        if (x < 1 && xx + yy > 0.5) {











            final double xHigh = splitHigh(x);
            final double xLow  = x - xHigh;
            final double yHigh = splitHigh(y);
            final double yLow  = y - yHigh;


            final double x2Low = squareLow(xLow, xHigh, xx);
            final double y2Low = squareLow(yLow, yHigh, yy);

            return sumx2y2m1(xx, x2Low, yy, y2Low);
        }
        return (x - 1) * (x + 1) + yy;
    }






















    private static double splitHigh(double a) {
        final double c = MULTIPLIER * a;
        return c - (c - a);
    }

















    private static double squareLow(double low, double high, double square) {
        final double lh = low * high;
        return low * low - (((square - high * high) - lh) - lh);
    }













    private static double fastSumLow(double a, double b, double x) {



        return b - (x - a);
    }












    private static double sumLow(double a, double b, double x) {






        final double bVirtual = x - a;
        return (a - (x - bVirtual)) + (b - bVirtual);
    }














    private static double sumx2y2m1(double x2High, double x2Low, double y2High, double y2Low) {

































        double q = x2Low - 1;
        double e1 = fastSumLow(-1, x2Low, q);
        double e3 = q + x2High;
        double e2 = sumLow(q, x2High, e3);

        final double f1 = y2Low;
        final double f2 = y2High;


        q = f1 + e1;
        e1 = sumLow(f1, e1, q);
        double p = q + e2;
        e2 = sumLow(q, e2, p);
        double e4 = p + e3;
        e3 = sumLow(p, e3, e4);


        q = f2 + e2;
        e2 = sumLow(f2, e2, q);
        p = q + e3;
        e3 = sumLow(q, e3, p);
        final double e5 = p + e4;
        e4 = sumLow(p, e4, e5);









        return e1 + e2 + e3 + e4 + e5;
    }



















    public List<Complex> nthRoot(int n) {
        if (n == 0) {
            throw new IllegalArgumentException("cannot compute zeroth root");
        }

        final List<Complex> result = new ArrayList<>();


        final double nthRootOfAbs = Math.pow(abs(), 1.0 / n);


        final double nthPhi = arg() / n;
        final double slice = 2 * Math.PI / n;
        double innerPart = nthPhi;
        for (int k = 0; k < Math.abs(n); k++) {

            final double realPart = nthRootOfAbs *  Math.cos(innerPart);
            final double imaginaryPart = nthRootOfAbs *  Math.sin(innerPart);
            result.add(ofCartesian(realPart, imaginaryPart));
            innerPart += slice;
        }

        return result;
    }



















































    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Complex) {
            final Complex c = (Complex) other;
            return equals(real, c.real) &&
                equals(imaginary, c.imaginary);
        }
        return false;
    }













    @Override
    public int hashCode() {
        return 31 * (31 + Double.hashCode(real)) + Double.hashCode(imaginary);
    }















    @Override
    public String toString() {
        return new StringBuilder(TO_STRING_SIZE)
            .append(FORMAT_START)
            .append(real).append(FORMAT_SEP)
            .append(imaginary)
            .append(FORMAT_END)
            .toString();
    }









    private static boolean equals(double x, double y) {
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(y);
    }













    private static boolean negative(double d) {
        return d < 0 || Double.doubleToLongBits(d) == NEGATIVE_ZERO_LONG_BITS;
    }









    private static boolean isPosInfinite(double d) {
        return d == Double.POSITIVE_INFINITY;
    }









    private static boolean isPosFinite(double d) {
        return d <= Double.MAX_VALUE;
    }













    private static Complex multiplyNegativeI(double real, double imaginary) {
        return new Complex(imaginary, -real);
    }


















    private static double changeSign(double magnitude, double signedValue) {
        return negative(signedValue) ? -magnitude : magnitude;
    }




































    private static int getScale(double a, double b) {

        final long x = Double.doubleToRawLongBits(a) & UNSIGN_MASK;
        final long y = Double.doubleToRawLongBits(b) & UNSIGN_MASK;

        final long bits = Math.max(x, y);

        int exp = (int) (bits >>> 52) - EXPONENT_OFFSET;



        if (exp == Double.MIN_EXPONENT - 1) {

            if (bits == 0) {
                return Double.MAX_EXPONENT + 1;
            }




            final long mantissa = bits & MANTISSA_MASK;
            exp -= Long.numberOfLeadingZeros(mantissa << 12);
        }
        return exp;
    }






















    private static int getMaxExponent(double a, double b) {



        return Math.max(Math.getExponent(a), Math.getExponent(b));
    }










    private static boolean inRegion(double x, double y, double min, double max) {
        return x < max && x > min && y < max && y > min;
    }































































    private static double hypot(double x, double y) {








































        final long xbits = Double.doubleToRawLongBits(x) & UNSIGN_MASK;
        final long ybits = Double.doubleToRawLongBits(y) & UNSIGN_MASK;


        double a;
        double b;

        final int ha;
        final int hb;
        if (ybits > xbits) {
            a = y;
            b = x;
            ha = (int) (ybits >>> 32);
            hb = (int) (xbits >>> 32);
        } else {
            a = x;
            b = y;
            ha = (int) (xbits >>> 32);
            hb = (int) (ybits >>> 32);
        }




        if ((ha - hb) > EXP_54) {



            return Math.abs(a);
        }

        double rescale = 1.0;
        if (ha > EXP_500) {

            if (ha >= EXP_1024) {



                return Math.abs(b) == Double.POSITIVE_INFINITY ?
                    Double.POSITIVE_INFINITY :
                    Math.abs(a);
            }




            a *= TWO_POW_NEG_600;
            b *= TWO_POW_NEG_600;
            rescale = TWO_POW_600;
        } else if (hb < EXP_NEG_500) {





            if (b == 0) {
                return Math.abs(a);
            }






            a *= TWO_POW_600;
            b *= TWO_POW_600;
            rescale = TWO_POW_NEG_600;
        }


        return Math.sqrt(x2y2(a, b)) * rescale;
    }
















    private static double x2y2(double x, double y) {












        final double xx = x * x;
        final double yy = y * y;





        final double xHigh = splitHigh(x);
        final double xLow = x - xHigh;
        final double xxLow = squareLow(xLow, xHigh, xx);

        final double yHigh = splitHigh(y);
        final double yLow = y - yHigh;
        final double yyLow = squareLow(yLow, yHigh, yy);

        final double r = xx + yy;









        return xx - r + yy + yyLow + xxLow + r;
    }
}
