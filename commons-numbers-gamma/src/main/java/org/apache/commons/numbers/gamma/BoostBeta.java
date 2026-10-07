





















package org.apache.commons.numbers.gamma;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.apache.commons.numbers.fraction.GeneralizedContinuedFraction;
import org.apache.commons.numbers.fraction.GeneralizedContinuedFraction.Coefficient;














final class BoostBeta {



























    private static final double EPSILON = 0x1.0p-52;




    private static final int LOG_MAX_VALUE = 709;




    private static final int LOG_MIN_VALUE = -708;

    private static final double HALF_PI = Math.PI / 2;


    private static final int MAX_FACTORIAL = 170;


    private static final int PN_SIZE = 30;

    private static final double TWO_POW_53 = 0x1.0p53;

    private static final double TWO_POW_M53 = 0x1.0p-53;


    private BoostBeta() {

    }









    static double beta(double p, double q) {
        if (!(p > 0 && q > 0)) {

            return Double.NaN;
        }

        final double c = p + q;


        if (c == p && q < EPSILON) {
            return 1 / q;
        } else if (c == q && p < EPSILON) {
            return 1 / p;
        }
        if (q == 1) {
            return 1 / p;
        } else if (p == 1) {
            return 1 / q;
        } else if (c < EPSILON) {
            return (c / p) / q;
        }


        final double a = p < q ? q : p;
        final double b = p < q ? p : q;


        final double agh = a + BoostGamma.Lanczos.GMH;
        final double bgh = b + BoostGamma.Lanczos.GMH;
        final double cgh = c + BoostGamma.Lanczos.GMH;
        double result = BoostGamma.Lanczos.lanczosSumExpGScaled(a) *
                (BoostGamma.Lanczos.lanczosSumExpGScaled(b) / BoostGamma.Lanczos.lanczosSumExpGScaled(c));
        final double ambh = a - 0.5f - b;
        if (Math.abs(b * ambh) < cgh * 100 && a > 100) {


            result *= Math.exp(ambh * Math.log1p(-b / cgh));
        } else {
            result *= Math.pow(agh / cgh, ambh);
        }

        if (cgh > 1e10f) {

            result *= Math.pow((agh / cgh) * (bgh / cgh), b);
        } else {
            result *= Math.pow((agh * bgh) / (cgh * cgh), b);
        }
        result *= Math.sqrt(Math.E / bgh);

        return result;
    }












    static double ibetaDerivative(double a, double b, double x) {



        if (!(a > 0 && b > 0) || !(x >= 0 && x <= 1)) {

            return Double.NaN;
        }



        if (x == 0) {
            if (a > 1) {
                return 0;
            }

            return a == 1 ? b : Double.POSITIVE_INFINITY;
        } else if (x == 1) {
            if (b > 1) {
                return 0;
            }

            return b == 1 ? a : Double.POSITIVE_INFINITY;
        }


        if (b == 1) {

            return a * Math.pow(x, a - 1);
        }
        if (a == 1) {

            if (x >= 0.5) {
                return b * Math.pow(1 - x, b - 1);
            }
            return b * Math.exp(Math.log1p(-x) * (b - 1));
        }




        final double y = (1 - x) * x;
        return ibetaPowerTerms(a, b, x, 1 - x, true, 1 / y);
    }















    private static double ibetaPowerTerms(double a, double b, double x,
            double y, boolean normalised) {
        return ibetaPowerTerms(a, b, x, y, normalised, 1);
    }





















    private static double ibetaPowerTerms(double a, double b, double x,
            double y, boolean normalised, double prefix) {
        if (!normalised) {

            return Math.pow(x, a) * Math.pow(y, b);
        }

        double result;

        final double c = a + b;


        final double agh = a + BoostGamma.Lanczos.GMH;
        final double bgh = b + BoostGamma.Lanczos.GMH;
        final double cgh = c + BoostGamma.Lanczos.GMH;
        result = BoostGamma.Lanczos.lanczosSumExpGScaled(c) /
                (BoostGamma.Lanczos.lanczosSumExpGScaled(a) * BoostGamma.Lanczos.lanczosSumExpGScaled(b));
        result *= prefix;

        result *= Math.sqrt(bgh / Math.E);
        result *= Math.sqrt(agh / cgh);


        double l1 = (x * b - y * agh) / agh;
        double l2 = (y * a - x * bgh) / bgh;
        if (Math.min(Math.abs(l1), Math.abs(l2)) < 0.2) {


            if (l1 * l2 > 0 || Math.min(a, b) < 1) {













                if (Math.abs(l1) < 0.1) {
                    result *= Math.exp(a * Math.log1p(l1));
                } else {
                    result *= Math.pow((x * cgh) / agh, a);
                }
                if (Math.abs(l2) < 0.1) {
                    result *= Math.exp(b * Math.log1p(l2));
                } else {
                    result *= Math.pow((y * cgh) / bgh, b);
                }
            } else if (Math.max(Math.abs(l1), Math.abs(l2)) < 0.5) {



















                final boolean smallA = a < b;
                final double ratio = b / a;
                if ((smallA && ratio * l2 < 0.1) || (!smallA && l1 / ratio > 0.1)) {
                    double l3 = Math.expm1(ratio * Math.log1p(l2));
                    l3 = l1 + l3 + l3 * l1;
                    l3 = a * Math.log1p(l3);
                    result *= Math.exp(l3);
                } else {
                    double l3 = Math.expm1(Math.log1p(l1) / ratio);
                    l3 = l2 + l3 + l3 * l2;
                    l3 = b * Math.log1p(l3);
                    result *= Math.exp(l3);
                }
            } else if (Math.abs(l1) < Math.abs(l2)) {

                double l = a * Math.log1p(l1) + b * Math.log((y * cgh) / bgh);
                if (l <= LOG_MIN_VALUE || l >= LOG_MAX_VALUE) {
                    l += Math.log(result);

                    result = Math.exp(l);
                } else {
                    result *= Math.exp(l);
                }
            } else {

                double l = b * Math.log1p(l2) + a * Math.log((x * cgh) / agh);
                if (l <= LOG_MIN_VALUE || l >= LOG_MAX_VALUE) {
                    l += Math.log(result);

                    result = Math.exp(l);
                } else {
                    result *= Math.exp(l);
                }
            }
        } else {

            final double b1 = (x * cgh) / agh;
            final double b2 = (y * cgh) / bgh;
            l1 = a * Math.log(b1);
            l2 = b * Math.log(b2);
            if (l1 >= LOG_MAX_VALUE || l1 <= LOG_MIN_VALUE || l2 >= LOG_MAX_VALUE || l2 <= LOG_MIN_VALUE) {

                if (a < b) {
                    final double p1 = Math.pow(b2, b / a);
                    final double l3 = a * (Math.log(b1) + Math.log(p1));
                    if (l3 < LOG_MAX_VALUE && l3 > LOG_MIN_VALUE) {
                        result *= Math.pow(p1 * b1, a);
                    } else {
                        l2 += l1 + Math.log(result);

                        result = Math.exp(l2);
                    }
                } else {
                    final double p1 = Math.pow(b1, a / b);
                    final double l3 = (Math.log(p1) + Math.log(b2)) * b;
                    if (l3 < LOG_MAX_VALUE && l3 > LOG_MIN_VALUE) {
                        result *= Math.pow(p1 * b2, b);
                    } else {
                        l2 += l1 + Math.log(result);

                        result = Math.exp(l2);
                    }
                }
            } else {

                result *= Math.pow(b1, a) * Math.pow(b2, b);
            }
        }

        return result;
    }










    static double beta(double a, double b, double x) {
        return betaIncompleteImp(a, b, x, Policy.getDefault(), false, false);
    }











    static double beta(double a, double b, double x, Policy policy) {
        return betaIncompleteImp(a, b, x, policy, false, false);
    }










    static double betac(double a, double b, double x) {
        return betaIncompleteImp(a, b, x, Policy.getDefault(), false, true);
    }











    static double betac(double a, double b, double x, Policy policy) {
        return betaIncompleteImp(a, b, x, policy, false, true);
    }










    static double ibeta(double a, double b, double x) {
        return betaIncompleteImp(a, b, x, Policy.getDefault(), true, false);
    }











    static double ibeta(double a, double b, double x, Policy policy) {
        return betaIncompleteImp(a, b, x, policy, true, false);
    }










    static double ibetac(double a, double b, double x) {
        return betaIncompleteImp(a, b, x, Policy.getDefault(), true, true);
    }











    static double ibetac(double a, double b, double x, Policy policy) {
        return betaIncompleteImp(a, b, x, policy, true, true);
    }

















    private static double betaIncompleteImp(double a, double b, double x,
            Policy pol, boolean normalised, boolean inv) {







        if (!(x >= 0 && x <= 1)) {

            return Double.NaN;
        }

        if (normalised) {
            if (!(a >= 0 && b >= 0)) {

                return Double.NaN;
            }

            if (a == 0) {
                if (b == 0) {

                    return Double.NaN;
                }

                return inv ? 0 : 1;
            } else if (b == 0) {

                return inv ? 1 : 0;
            }
        } else {
            if (!(a > 0 && b > 0)) {

                return Double.NaN;
            }
        }

        if (x == 0) {
            if (inv) {
                return normalised ? 1 : beta(a, b);
            }
            return 0;
        }
        if (x == 1) {
            if (!inv) {
                return normalised ? 1 : beta(a, b);
            }
            return 0;
        }

        if (a == 0.5f && b == 0.5f) {

            final double z = inv ? 1 - x : x;
            final double asin = Math.asin(Math.sqrt(z));
            return normalised ? asin / HALF_PI : 2 * asin;
        }

        boolean invert = inv;
        double y = 1 - x;
        if (a == 1) {

            double tmp = a;
            a = b;
            b = tmp;

            tmp = x;
            x = y;
            y = tmp;
            invert = !invert;
        }
        if (b == 1) {




            if (a == 1) {
                return invert ? y : x;
            }

            double p;
            if (y < 0.5) {
                p = invert ? -Math.expm1(a * Math.log1p(-y)) : Math.exp(a * Math.log1p(-y));
            } else {
                p = invert ? -BoostMath.powm1(x, a) : Math.pow(x, a);
            }
            if (!normalised) {
                p /= a;
            }
            return p;
        }

        double fract;
        if (Math.min(a, b) <= 1) {
            if (x > 0.5) {

                double tmp = a;
                a = b;
                b = tmp;

                tmp = x;
                x = y;
                y = tmp;
                invert = !invert;
            }
            if (Math.max(a, b) <= 1) {

                if (a >= Math.min(0.2, b) || Math.pow(x, a) <= 0.9) {
                    if (invert) {
                        fract = -(normalised ? 1 : beta(a, b));
                        invert = false;
                        fract = -ibetaSeries(a, b, x, fract, normalised, pol);
                    } else {
                        fract = ibetaSeries(a, b, x, 0, normalised, pol);
                    }
                } else {

                    double tmp = a;
                    a = b;
                    b = tmp;

                    tmp = x;
                    x = y;
                    y = tmp;
                    invert = !invert;
                    if (y >= 0.3) {
                        if (invert) {
                            fract = -(normalised ? 1 : beta(a, b));
                            invert = false;
                            fract = -ibetaSeries(a, b, x, fract, normalised, pol);
                        } else {
                            fract = ibetaSeries(a, b, x, 0, normalised, pol);
                        }
                    } else {

                        final double prefix;
                        if (normalised) {
                            prefix = 1;
                        } else {
                            prefix = risingFactorialRatio(a + b, a, 20);
                        }
                        fract = ibetaAStep(a, b, x, y, 20, normalised);
                        if (invert) {
                            fract -= normalised ? 1 : beta(a, b);
                            invert = false;
                            fract = -betaSmallBLargeASeries(a + 20, b, x, y, fract, prefix, pol, normalised);
                        } else {
                            fract = betaSmallBLargeASeries(a + 20, b, x, y, fract, prefix, pol, normalised);
                        }
                    }
                }
            } else {

                if (b <= 1 || (x < 0.1 && Math.pow(b * x, a) <= 0.7)) {
                    if (invert) {
                        fract = -(normalised ? 1 : beta(a, b));
                        invert = false;
                        fract = -ibetaSeries(a, b, x, fract, normalised, pol);
                    } else {
                        fract = ibetaSeries(a, b, x, 0, normalised, pol);
                    }
                } else {

                    double tmp = a;
                    a = b;
                    b = tmp;

                    tmp = x;
                    x = y;
                    y = tmp;
                    invert = !invert;

                    if (y >= 0.3) {
                        if (invert) {
                            fract = -(normalised ? 1 : beta(a, b));
                            invert = false;
                            fract = -ibetaSeries(a, b, x, fract, normalised, pol);
                        } else {
                            fract = ibetaSeries(a, b, x, 0, normalised, pol);
                        }
                    } else if (a >= 15) {
                        if (invert) {
                            fract = -(normalised ? 1 : beta(a, b));
                            invert = false;
                            fract = -betaSmallBLargeASeries(a, b, x, y, fract, 1, pol, normalised);
                        } else {
                            fract = betaSmallBLargeASeries(a, b, x, y, 0, 1, pol, normalised);
                        }
                    } else {

                        final double prefix;
                        if (normalised) {
                            prefix = 1;
                        } else {
                            prefix = risingFactorialRatio(a + b, a, 20);
                        }
                        fract = ibetaAStep(a, b, x, y, 20, normalised);
                        if (invert) {
                            fract -= normalised ? 1 : beta(a, b);
                            invert = false;
                            fract = -betaSmallBLargeASeries(a + 20, b, x, y, fract, prefix, pol, normalised);
                        } else {
                            fract = betaSmallBLargeASeries(a + 20, b, x, y, fract, prefix, pol, normalised);
                        }
                    }
                }
            }
        } else {




            final double lambda;
            if (a < b) {
                lambda = a - (a + b) * x;
            } else {
                lambda = (a + b) * y - b;
            }
            if (lambda < 0) {

                double tmp = a;
                a = b;
                b = tmp;

                tmp = x;
                x = y;
                y = tmp;
                invert = !invert;
            }

            if (b < 40) {

                if (Math.rint(a) == a && Math.rint(b) == b && a < (Integer.MAX_VALUE - 100) && y != 1) {



                    final int k = (int) (a - 1);
                    final int n = (int) (b + k);
                    fract = binomialCCdf(n, k, x, y);
                    if (!normalised) {
                        fract *= beta(a, b);
                    }
                } else if (b * x <= 0.7) {
                    if (invert) {
                        fract = -(normalised ? 1 : beta(a, b));
                        invert = false;
                        fract = -ibetaSeries(a, b, x, fract, normalised, pol);
                    } else {
                        fract = ibetaSeries(a, b, x, 0, normalised, pol);
                    }
                } else if (a > 15) {

                    int n = (int) b;
                    if (n == b) {
                        --n;
                    }
                    final double bbar = b - n;
                    final double prefix;
                    if (normalised) {
                        prefix = 1;
                    } else {
                        prefix = risingFactorialRatio(a + bbar, bbar, n);
                    }
                    fract = ibetaAStep(bbar, a, y, x, n, normalised);
                    fract = betaSmallBLargeASeries(a, bbar, x, y, fract, 1, pol, normalised);
                    fract /= prefix;
                } else if (normalised) {



                    int n = (int) Math.floor(b);
                    double bbar = b - n;
                    if (bbar <= 0) {
                        --n;
                        bbar += 1;
                    }
                    fract = ibetaAStep(bbar, a, y, x, n, normalised);
                    fract += ibetaAStep(a, bbar, x, y, 20, normalised);
                    if (invert) {


                        fract -= 1;
                    }
                    fract = betaSmallBLargeASeries(a + 20, bbar, x, y, fract, 1, pol, normalised);
                    if (invert) {
                        fract = -fract;
                        invert = false;
                    }
                } else {
                    fract = ibetaFraction2(a, b, x, y, pol, normalised);
                }
            } else {
                fract = ibetaFraction2(a, b, x, y, pol, normalised);
            }
        }
        if (invert) {
            return (normalised ? 1 : beta(a, b)) - fract;
        }
        return fract;
    }












    private static double ibetaSeries(double a, double b, double x, double s0, boolean normalised, Policy pol) {
        double result;

        if (normalised) {
            final double c = a + b;


            final double agh = a + BoostGamma.Lanczos.GMH;
            final double bgh = b + BoostGamma.Lanczos.GMH;
            final double cgh = c + BoostGamma.Lanczos.GMH;
            result = BoostGamma.Lanczos.lanczosSumExpGScaled(c) /
                    (BoostGamma.Lanczos.lanczosSumExpGScaled(a) * BoostGamma.Lanczos.lanczosSumExpGScaled(b));

            final double l1 = Math.log(cgh / bgh) * (b - 0.5f);
            final double l2 = Math.log(x * cgh / agh) * a;



            if (l1 > LOG_MIN_VALUE && l1 < LOG_MAX_VALUE && l2 > LOG_MIN_VALUE && l2 < LOG_MAX_VALUE) {
                if (a * b < bgh * 10) {
                    result *= Math.exp((b - 0.5f) * Math.log1p(a / bgh));
                } else {
                    result *= Math.pow(cgh / bgh, b - 0.5f);
                }
                result *= Math.pow(x * cgh / agh, a);
                result *= Math.sqrt(agh / Math.E);
            } else {



                result = Math.log(result) + l1 + l2 + (Math.log(agh) - 1) / 2;
                result = Math.exp(result);
            }
        } else {

            result = Math.pow(x, a);
        }
        double rescale = 1.0;
        if (result < Double.MIN_NORMAL) {








            if (s0 + result / a == s0) {
                return s0;
            }
            s0 *= TWO_POW_53;
            result *= TWO_POW_53;
            rescale = TWO_POW_M53;
        }

        final double eps = pol.getEps();
        final int maxIterations = pol.getMaxIterations();


        final double result1 = result;
        final DoubleSupplier gen = new DoubleSupplier() {

            private double result = result1;

            private final double poch = -b;

            private int n;

            @Override
            public double getAsDouble() {
                final double r = result / (a + n);
                n++;
                result *= (n + poch) * x / n;
                return r;
            }
        };

        return BoostTools.sumSeries(gen, eps, maxIterations, s0) * rescale;
    }















    private static double risingFactorialRatio(double a, double b, int k) {








        double result = 1;
        for (int i = 0; i < k; ++i) {
            result *= (a + i) / (b + i);
        }
        return result;
    }












    private static double binomialCCdf(int n, int k, double x, double y) {
        double result = Math.pow(x, n);

        if (result > Double.MIN_NORMAL) {
            double term = result;
            for (int i = n - 1; i > k; --i) {
                term *= ((i + 1) * y) / ((n - i) * x);
                result += term;
            }
        } else {


            int start = (int) (n * x);
            if (start <= k + 1) {
                start = k + 2;
            }


            result = binomialTerm(n, start, x, y);
            if (result == 0) {


                for (int i = start - 1; i > k; --i) {
                    result += binomialTerm(n, i, x, y);
                }
            } else {
                double term = result;
                final double startTerm = result;
                for (int i = start - 1; i > k; --i) {
                    term *= ((i + 1) * y) / ((n - i) * x);
                    result += term;
                }
                term = startTerm;
                for (int i = start + 1; i <= n; ++i) {
                    term *= (n - i + 1) * x / (i * y);
                    result += term;
                }
            }
        }

        return result;
    }















    private static double binomialTerm(int n, int k, double x, double y) {




        final double binom = binomialCoefficient(n, k);
        if (!Double.isFinite(binom)) {

            return 0;
        }




        return binom * Math.pow(y, n - k) * Math.pow(x, k);
    }















    static double binomialCoefficient(int n, int k) {


        final int m = Math.min(k, n - k);


        if (m == 0) {
            return 1;
        }
        if (m == 1) {
            return n;
        }
        if (m == 2) {
            return 0.5 * n * (n - 1);
        }
        if (m == 3) {

            return 0.5 * n * (n - 1) * (n - 2) / 3;
        }

        double result;
        if (n <= MAX_FACTORIAL) {

            result = BoostGamma.uncheckedFactorial(n);

            result /= BoostGamma.uncheckedFactorial(m);
            result /= BoostGamma.uncheckedFactorial(n - m);
        } else {








            result = 1;
            for (int i = 1; i < m; i++) {
                result *= n - m + i;
                result /= i;
            }

            if (result * n > Double.MAX_VALUE) {
                result /= m;
                result *= n;
            } else {
                result *= n;
                result /= m;
            }
        }

        return Math.ceil(result - 0.5f);
    }












    private static double ibetaAStep(double a, double b, double x, double y, int k, boolean normalised) {
        double prefix = ibetaPowerTerms(a, b, x, y, normalised);
        prefix /= a;
        if (prefix == 0) {
            return prefix;
        }
        double sum = 1;
        double term = 1;

        for (int i = 0; i < k - 1; ++i) {
            term *= (a + b + i) * x / (a + i + 1);
            sum += term;
        }
        prefix *= sum;

        return prefix;
    }














    private static double betaSmallBLargeASeries(double a, double b, double x, double y, double s0, double mult,
            Policy pol, boolean normalised) {





        final double bm1 = b - 1;
        final double t = a + bm1 / 2;
        final double lx;
        if (y < 0.35) {
            lx = Math.log1p(-y);
        } else {
            lx = Math.log(x);
        }
        final double u = -t * lx;

        final double h = BoostGamma.regularisedGammaPrefix(b, u);
        if (h <= Double.MIN_NORMAL) {




            if (s0 == 0) {
                return ibetaFraction(a, b, x, y, pol, normalised);
            }
            return s0;
        }
        double prefix;
        if (normalised) {
            prefix = h / GammaRatio.delta(a, b);
            prefix /= Math.pow(t, b);
        } else {
            prefix = BoostGamma.fullIgammaPrefix(b, u) / Math.pow(t, b);
        }
        prefix *= mult;





        final double[] p = new double[PN_SIZE];

        p[0] = 1;



        double j = BoostGamma.gammaQ(b, u, pol) / h;




        double sum = s0 + prefix * j;


        int tnp1 = 1;
        double lx2 = lx / 2;
        lx2 *= lx2;
        double lxp = 1;
        final double t4 = 4 * t * t;
        double b2n = b;

        for (int n = 1; n < PN_SIZE; ++n) {



            tnp1 += 2;
            p[n] = 0;
            int tmp1 = 3;
            for (int m = 1; m < n; ++m) {
                final double mbn = m * b - n;
                p[n] += mbn * p[n - m] / BoostGamma.uncheckedFactorial(tmp1);
                tmp1 += 2;
            }
            p[n] /= n;
            p[n] += bm1 / BoostGamma.uncheckedFactorial(tnp1);



            j = (b2n * (b2n + 1) * j + (u + b2n + 1) * lxp) / t4;
            lxp *= lx2;
            b2n += 2;



            final double r = prefix * p[n] * j;
            final double previous = sum;
            sum += r;




            if (sum == previous) {
                break;
            }
        }
        return sum;
    }


















    static double ibetaFraction2(double a, double b, double x, double y, Policy pol, boolean normalised) {
        final double result = ibetaPowerTerms(a, b, x, y, normalised);
        if (result == 0) {
            return result;
        }

        final double eps = pol.getEps();
        final int maxIterations = pol.getMaxIterations();

        final Supplier<Coefficient> gen = new Supplier<Coefficient>() {

            private int m;

            @Override
            public Coefficient get() {
                double aN = (a + m - 1) * (a + b + m - 1) * m * (b - m) * x * x;
                final double denom = a + 2 * m - 1;
                aN /= denom * denom;

                double bN = m;
                bN += (m * (b - m) * x) / (a + 2 * m - 1);
                bN += ((a + m) * (a * y - b * x + 1 + m * (2 - x))) / (a + 2 * m + 1);

                ++m;
                return Coefficient.of(aN, bN);
            }
        };


        final double fract = GeneralizedContinuedFraction.value(gen, eps, maxIterations);
        return result / fract;
    }
























    static double ibetaFraction(double a, double b, double x, double y, Policy pol, boolean normalised) {
        final double result = ibetaPowerTerms(a, b, x, y, normalised);
        if (result == 0) {
            return result;
        }

        final double eps = pol.getEps();
        final int maxIterations = pol.getMaxIterations();

        final Supplier<Coefficient> gen = new Supplier<Coefficient>() {

            private int n;

            @Override
            public Coefficient get() {


                final int m = n;
                final int k = m / 2;
                final double aN;
                if ((m & 0x1) == 0) {


                    aN = (k * (b - k) * x) /
                        ((a + m - 1) * (a + m));
                } else {



                    aN = -((a + k) * (a + b + k) * x) /
                        ((a + m - 1) * (a + m));
                }

                n = m + 1;
                return Coefficient.of(aN, 1);
            }
        };


        final double fract = GeneralizedContinuedFraction.value(gen, eps, maxIterations);
        return (result / a) / fract;
    }
}
