















package org.apache.commons.numbers.gamma;







public final class LogBeta {

    private static final double TEN = 10;

    private static final double TWO = 2;

    private static final double THOUSAND = 1000;


    private static final double HALF_LOG_TWO_PI = 0.9189385332046727;















    private static final double[] DELTA = {
        .833333333333333333333333333333E-01,
        -.277777777777777777777777752282E-04,
        .793650793650793650791732130419E-07,
        -.595238095238095232389839236182E-09,
        .841750841750832853294451671990E-11,
        -.191752691751854612334149171243E-12,
        .641025640510325475730918472625E-14,
        -.295506514125338232839867823991E-15,
        .179643716359402238723287696452E-16,
        -.139228964661627791231203060395E-17,
        .133802855014020915603275339093E-18,
        -.154246009867966094273710216533E-19,
        .197701992980957427278370133333E-20,
        -.234065664793997056856992426667E-21,
        .171348014966398575409015466667E-22
    };


    private LogBeta() {

    }












    private static double deltaMinusDeltaSum(final double a,
                                             final double b) {



        final double h = a / b;
        final double p = h / (1 + h);
        final double q = 1 / (1 + h);
        final double q2 = q * q;



        final double[] s = new double[DELTA.length];
        s[0] = 1;
        for (int i = 1; i < s.length; i++) {
            s[i] = 1 + (q + q2 * s[i - 1]);
        }



        final double sqrtT = 10 / b;
        final double t = sqrtT * sqrtT;
        double w = DELTA[DELTA.length - 1] * s[s.length - 1];
        for (int i = DELTA.length - 2; i >= 0; i--) {
            w = t * w + DELTA[i] * s[i];
        }
        return w * p / b;
    }












    private static double sumDeltaMinusDeltaSum(final double p,
                                                final double q) {

        if (p < TEN) {
            throw new GammaException(GammaException.OUT_OF_RANGE, p, TEN, Double.POSITIVE_INFINITY);
        }
        if (q < TEN) {
            throw new GammaException(GammaException.OUT_OF_RANGE, q, TEN, Double.POSITIVE_INFINITY);
        }

        final double a = Math.min(p, q);
        final double b = Math.max(p, q);
        final double sqrtT = 10 / a;
        final double t = sqrtT * sqrtT;
        double z = DELTA[DELTA.length - 1];
        for (int i = DELTA.length - 2; i >= 0; i--) {
            z = t * z + DELTA[i];
        }
        return z / a + deltaMinusDeltaSum(a, b);
    }











    public static double value(double p,
                               double q) {
        if (Double.isNaN(p) ||
            Double.isNaN(q) ||
            p <= 0 ||
            q <= 0) {
            return Double.NaN;
        }

        final double a = Math.min(p, q);
        final double b = Math.max(p, q);
        if (a >= TEN) {
            final double w = sumDeltaMinusDeltaSum(a, b);
            final double h = a / b;
            final double c = h / (1 + h);
            final double u = -(a - 0.5) * Math.log(c);
            final double v = b * Math.log1p(h);
            if (u <= v) {
                return (((-0.5 * Math.log(b) + HALF_LOG_TWO_PI) + w) - u) - v;
            }
            return (((-0.5 * Math.log(b) + HALF_LOG_TWO_PI) + w) - v) - u;
        } else if (a > TWO) {
            if (b > THOUSAND) {
                final int n = (int) Math.floor(a - 1);
                double prod = 1;
                double ared = a;
                for (int i = 0; i < n; i++) {
                    ared -= 1;
                    prod *= ared / (1 + ared / b);
                }
                return (Math.log(prod) - n * Math.log(b)) +
                        (LogGamma.value(ared) +
                         logGammaMinusLogGammaSum(ared, b));
            }
            double prod1 = 1;
            double ared = a;
            while (ared > TWO) {
                ared -= 1;
                final double h = ared / b;
                prod1 *= h / (1 + h);
            }
            if (b < TEN) {
                double prod2 = 1;
                double bred = b;
                while (bred > TWO) {
                    bred -= 1;
                    prod2 *= bred / (ared + bred);
                }
                return Math.log(prod1) +
                       Math.log(prod2) +
                       (LogGamma.value(ared) +
                       (LogGamma.value(bred) -
                        LogGammaSum.value(ared, bred)));
            }
            return Math.log(prod1) +
                   LogGamma.value(ared) +
                   logGammaMinusLogGammaSum(ared, b);
        } else if (a >= 1) {
            if (b > TWO) {
                if (b < TEN) {
                    double prod = 1;
                    double bred = b;
                    while (bred > TWO) {
                        bred -= 1;
                        prod *= bred / (a + bred);
                    }
                    return Math.log(prod) +
                           (LogGamma.value(a) +
                            (LogGamma.value(bred) -
                             LogGammaSum.value(a, bred)));
                }
                return LogGamma.value(a) +
                    logGammaMinusLogGammaSum(a, b);
            }
            return LogGamma.value(a) +
                   LogGamma.value(b) -
                   LogGammaSum.value(a, b);
        } else {
            if (b >= TEN) {
                return LogGamma.value(a) +
                       logGammaMinusLogGammaSum(a, b);
            }




            final double beta = Gamma.value(a) * Gamma.value(b) / Gamma.value(a + b);
            if (Double.isFinite(beta)) {
                return Math.log(beta);
            }
            return LogGamma.value(a) + (LogGamma.value(b) - LogGamma.value(a + b));
        }
    }














    private static double logGammaMinusLogGammaSum(double a,
                                                   double b) {
        if (a < 0) {
            throw new GammaException(GammaException.OUT_OF_RANGE, a, 0, Double.POSITIVE_INFINITY);
        }
        if (b < TEN) {
            throw new GammaException(GammaException.OUT_OF_RANGE, b, TEN, Double.POSITIVE_INFINITY);
        }





        final double d = b + (a - 0.5);
        final double w = deltaMinusDeltaSum(a, b);

        final double u = d * Math.log1p(a / b);
        final double v = a * (Math.log(b) - 1);

        return u <= v ?
            (w - u) - v :
            (w - v) - u;
    }
}
