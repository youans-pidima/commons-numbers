





















package org.apache.commons.numbers.gamma;

import org.apache.commons.numbers.core.DD;

















final class BoostErf {

    private static final double ONE_OVER_ROOT_PI = 0.5641895835477562869480794515607725858;


    private static final double ERFCX_APPROX = 6.71e7;



    private static final double COMPUTE_ERF = 0.5;


    private static final double ERFCX_NEG_X_MAX = Math.sqrt(Math.log(Double.MAX_VALUE / 2));




    private static final double EXP_XX_1 = 0x1.5p-27;


    private BoostErf() {

    }









































    static double erfc(double x) {
        return erfImp(x, true, false);
    }







    static double erf(double x) {
        return erfImp(x, false, false);
    }













    private static double erfImp(double z, boolean invert, boolean scaled) {
        if (Double.isNaN(z)) {
            return Double.NaN;
        }

        if (z < 0) {

            if (!invert) {
                return -erfImp(-z, invert, false);
            } else if (z < -0.5) {
                return 2 - erfImp(-z, invert, false);
            } else {
                return 1 + erfImp(-z, false, false);
            }
        }

        double result;






        if (z < COMPUTE_ERF) {




            if (z < 1e-10) {
                if (z == 0) {
                    result = z;
                } else {
                    final double c = 0.003379167095512573896158903121545171688;
                    result = z * 1.125f + z * c;
                }
            } else {





                final double Y = 1.044948577880859375f;
                final double zz = z * z;
                double P;
                P = -0.000322780120964605683831;
                P =  -0.00772758345802133288487 + P * zz;
                P =   -0.0509990735146777432841 + P * zz;
                P =    -0.338165134459360935041 + P * zz;
                P =    0.0834305892146531832907 + P * zz;
                double Q;
                Q = 0.000370900071787748000569;
                Q =  0.00858571925074406212772 + Q * zz;
                Q =   0.0875222600142252549554 + Q * zz;
                Q =    0.455004033050794024546 + Q * zz;
                Q =                        1.0 + Q * zz;
                result = z * (Y + P / Q);
            }


        } else if (scaled || (invert ? (z < 27.300781f) : (z < 5.9306640625f))) {




            invert = !invert;
            if (z < 1.5f) {




                final double Y = 0.405935764312744140625f;
                final double zm = z - 0.5;
                double P;
                P = 0.00180424538297014223957;
                P =  0.0195049001251218801359 + P * zm;
                P =  0.0888900368967884466578 + P * zm;
                P =   0.191003695796775433986 + P * zm;
                P =   0.178114665841120341155 + P * zm;
                P =  -0.098090592216281240205 + P * zm;
                double Q;
                Q = 0.337511472483094676155e-5;
                Q =   0.0113385233577001411017 + Q * zm;
                Q =     0.12385097467900864233 + Q * zm;
                Q =    0.578052804889902404909 + Q * zm;
                Q =     1.42628004845511324508 + Q * zm;
                Q =     1.84759070983002217845 + Q * zm;
                Q =                        1.0 + Q * zm;
                result = Y + P / Q;
                if (scaled) {
                    result /= z;
                } else {
                    result *= expmxx(z) / z;
                }
            } else if (z < 2.5f) {




                final double Y = 0.50672817230224609375f;
                final double zm = z - 1.5;
                double P;
                P = 0.000235839115596880717416;
                P =  0.00323962406290842133584 + P * zm;
                P =   0.0175679436311802092299 + P * zm;
                P =     0.04394818964209516296 + P * zm;
                P =   0.0386540375035707201728 + P * zm;
                P =  -0.0243500476207698441272 + P * zm;
                double Q;
                Q = 0.00410369723978904575884;
                Q =  0.0563921837420478160373 + Q * zm;
                Q =   0.325732924782444448493 + Q * zm;
                Q =   0.982403709157920235114 + Q * zm;
                Q =    1.53991494948552447182 + Q * zm;
                Q =                       1.0 + Q * zm;
                result = Y + P / Q;
                if (scaled) {
                    result /= z;
                } else {
                    result *= expmxx(z) / z;
                }


            } else if (z < 4.0f) {




                final double Y = 0.5405750274658203125f;
                final double zm = z - 3.5;
                double P;
                P = 0.113212406648847561139e-4;
                P = 0.000250269961544794627958 + P * zm;
                P =  0.00212825620914618649141 + P * zm;
                P =  0.00840807615555585383007 + P * zm;
                P =   0.0137384425896355332126 + P * zm;
                P =  0.00295276716530971662634 + P * zm;
                double Q;
                Q = 0.000479411269521714493907;
                Q =   0.0105982906484876531489 + Q * zm;
                Q =   0.0958492726301061423444 + Q * zm;
                Q =    0.442597659481563127003 + Q * zm;
                Q =     1.04217814166938418171 + Q * zm;
                Q =                        1.0 + Q * zm;
                result = Y + P / Q;
                if (scaled) {
                    result /= z;
                } else {
                    result *= expmxx(z) / z;
                }
            } else {













                final double izz = 1 / (z * z);
                double p;
                p = 1.63153871373020978498e-2;
                p = 3.05326634961232344035e-1 + p * izz;
                p = 3.60344899949804439429e-1 + p * izz;
                p = 1.25781726111229246204e-1 + p * izz;
                p = 1.60837851487422766278e-2 + p * izz;
                p = 6.58749161529837803157e-4 + p * izz;
                double q;
                q = 1;
                q = 2.56852019228982242072e00 + q * izz;
                q = 1.87295284992346047209e00 + q * izz;
                q = 5.27905102951428412248e-1 + q * izz;
                q = 6.05183413124413191178e-2 + q * izz;
                q = 2.33520497626869185443e-3 + q * izz;

                result = izz * p / q;
                result = (ONE_OVER_ROOT_PI - result) / z;

                if (!scaled) {



                    result *= expmxx(z);
                }
            }
        } else {



            result = 0;
            invert = !invert;
        }

        if (invert) {



            result = 1 - result;
        }

        return result;
    }










    static double erfcx(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }


        final double ax = Math.abs(x);
        if (ax < COMPUTE_ERF) {



            final double erfx = erf(x);
            if (ax < EXP_XX_1) {

                return 1 - erfx;
            }













            final double em1 = Math.expm1(x * x);
            return -erfx * em1 + em1 - erfx + 1;
        }


        if (x < 0) {


            if (x < -ERFCX_NEG_X_MAX) {

                return Double.POSITIVE_INFINITY;
            }

            final double e = expxx(x);
            return e - erfImp(-x, true, true) + e;
        }


        if (x > ERFCX_APPROX) {
            return ONE_OVER_ROOT_PI / x;
        }


        return erfImp(x, true, true);
    }







    static double erfcInv(double z) {



        if (z < 0 || z > 2 || Double.isNaN(z)) {

            return Double.NaN;
        }



        if (z == (int) z) {




            return z == 1 ? 0 : (1 - z) * Double.POSITIVE_INFINITY;
        }






        final double p;
        final double q;
        final double s;
        if (z > 1) {
            q = 2 - z;
            p = 1 - q;
            s = -1;
        } else {
            p = 1 - z;
            q = z;
            s = 1;
        }




        return s * erfInvImp(p, q);
    }







    static double erfInv(double z) {



        if (z < -1 || z > 1 || Double.isNaN(z)) {

            return Double.NaN;
        }



        if (z == (int) z) {





            return z == 0 ? z : z * Double.POSITIVE_INFINITY;
        }






        final double p;
        final double q;
        final double s;
        if (z < 0) {
            p = -z;
            q = 1 - p;
            s = -1;
        } else {
            p = z;
            q = 1 - z;
            s = 1;
        }



        return s * erfInvImp(p, q);
    }








    private static double erfInvImp(double p, double q) {
        final double result;

        if (p <= 0.5) {












            final float Y = 0.0891314744949340820313f;
            double P;
            P =  -0.00538772965071242932965;
            P =   0.00822687874676915743155 + P * p;
            P =    0.0219878681111168899165 + P * p;
            P =   -0.0365637971411762664006 + P * p;
            P =   -0.0126926147662974029034 + P * p;
            P =    0.0334806625409744615033 + P * p;
            P =  -0.00836874819741736770379 + P * p;
            P = -0.000508781949658280665617 + P * p;
            double Q;
            Q = 0.000886216390456424707504;
            Q = -0.00233393759374190016776 + Q * p;
            Q =   0.0795283687341571680018 + Q * p;
            Q =  -0.0527396382340099713954 + Q * p;
            Q =    -0.71228902341542847553 + Q * p;
            Q =    0.662328840472002992063 + Q * p;
            Q =     1.56221558398423026363 + Q * p;
            Q =    -1.56574558234175846809 + Q * p;
            Q =   -0.970005043303290640362 + Q * p;
            Q =                        1.0 + Q * p;
            final double g = p * (p + 10);
            final double r = P / Q;
            result = g * Y + g * r;
        } else if (q >= 0.25) {












            final float Y = 2.249481201171875f;
            final double xs = q - 0.25f;
            double P;
            P =  -3.67192254707729348546;
            P =   21.1294655448340526258 + P * xs;
            P =    17.445385985570866523 + P * xs;
            P =  -44.6382324441786960818 + P * xs;
            P =  -18.8510648058714251895 + P * xs;
            P =   17.6447298408374015486 + P * xs;
            P =   8.37050328343119927838 + P * xs;
            P =  0.105264680699391713268 + P * xs;
            P = -0.202433508355938759655 + P * xs;
            double Q;
            Q =  1.72114765761200282724;
            Q = -22.6436933413139721736 + Q * xs;
            Q =  10.8268667355460159008 + Q * xs;
            Q =  48.5609213108739935468 + Q * xs;
            Q = -20.1432634680485188801 + Q * xs;
            Q = -28.6608180499800029974 + Q * xs;
            Q =   3.9713437953343869095 + Q * xs;
            Q =  6.24264124854247537712 + Q * xs;
            Q =                     1.0 + Q * xs;
            final double g = Math.sqrt(-2 * Math.log(q));
            final double r = P / Q;
            result = g / (Y + r);
        } else {




















            final double x = Math.sqrt(-Math.log(q));
            if (x < 3) {

                final float Y = 0.807220458984375f;
                final double xs = x - 1.125f;
                double P;
                P = -0.681149956853776992068e-9;
                P =  0.285225331782217055858e-7 + P * xs;
                P = -0.679465575181126350155e-6 + P * xs;
                P =   0.00214558995388805277169 + P * xs;
                P =    0.0290157910005329060432 + P * xs;
                P =     0.142869534408157156766 + P * xs;
                P =     0.337785538912035898924 + P * xs;
                P =     0.387079738972604337464 + P * xs;
                P =     0.117030156341995252019 + P * xs;
                P =    -0.163794047193317060787 + P * xs;
                P =    -0.131102781679951906451 + P * xs;
                double Q;
                Q =  0.01105924229346489121;
                Q = 0.152264338295331783612 + Q * xs;
                Q = 0.848854343457902036425 + Q * xs;
                Q =  2.59301921623620271374 + Q * xs;
                Q =  4.77846592945843778382 + Q * xs;
                Q =  5.38168345707006855425 + Q * xs;
                Q =  3.46625407242567245975 + Q * xs;
                Q =                     1.0 + Q * xs;
                final double R = P / Q;
                result = Y * x + R * x;
            } else if (x < 6) {

                final float Y = 0.93995571136474609375f;
                final double xs = x - 3;
                double P;
                P = 0.266339227425782031962e-11;
                P = -0.230404776911882601748e-9 + P * xs;
                P =  0.460469890584317994083e-5 + P * xs;
                P =  0.000157544617424960554631 + P * xs;
                P =   0.00187123492819559223345 + P * xs;
                P =   0.00950804701325919603619 + P * xs;
                P =    0.0185573306514231072324 + P * xs;
                P =  -0.00222426529213447927281 + P * xs;
                P =   -0.0350353787183177984712 + P * xs;
                double Q;
                Q = 0.764675292302794483503e-4;
                Q =  0.00263861676657015992959 + Q * xs;
                Q =   0.0341589143670947727934 + Q * xs;
                Q =    0.220091105764131249824 + Q * xs;
                Q =    0.762059164553623404043 + Q * xs;
                Q =      1.3653349817554063097 + Q * xs;
                Q =                        1.0 + Q * xs;
                final double R = P / Q;
                result = Y * x + R * x;
            } else if (x < 18) {

                final float Y = 0.98362827301025390625f;
                final double xs = x - 6;
                double P;
                P =   0.99055709973310326855e-16;
                P = -0.281128735628831791805e-13 + P * xs;
                P =   0.462596163522878599135e-8 + P * xs;
                P =   0.449696789927706453732e-6 + P * xs;
                P =   0.149624783758342370182e-4 + P * xs;
                P =   0.000209386317487588078668 + P * xs;
                P =    0.00105628862152492910091 + P * xs;
                P =   -0.00112951438745580278863 + P * xs;
                P =    -0.0167431005076633737133 + P * xs;
                double Q;
                Q = 0.282243172016108031869e-6;
                Q = 0.275335474764726041141e-4 + Q * xs;
                Q = 0.000964011807005165528527 + Q * xs;
                Q =   0.0160746087093676504695 + Q * xs;
                Q =    0.138151865749083321638 + Q * xs;
                Q =    0.591429344886417493481 + Q * xs;
                Q =                        1.0 + Q * xs;
                final double R = P / Q;
                result = Y * x + R * x;
            } else {


                final float Y = 0.99714565277099609375f;
                final double xs = x - 18;
                double P;
                P = -0.116765012397184275695e-17;
                P =  0.145596286718675035587e-11 + P * xs;
                P =   0.411632831190944208473e-9 + P * xs;
                P =   0.396341011304801168516e-7 + P * xs;
                P =   0.162397777342510920873e-5 + P * xs;
                P =   0.254723037413027451751e-4 + P * xs;
                P =  -0.779190719229053954292e-5 + P * xs;
                P =    -0.0024978212791898131227 + P * xs;
                double Q;
                Q = 0.509761276599778486139e-9;
                Q = 0.144437756628144157666e-6 + Q * xs;
                Q = 0.145007359818232637924e-4 + Q * xs;
                Q = 0.000690538265622684595676 + Q * xs;
                Q =   0.0169410838120975906478 + Q * xs;
                Q =    0.207123112214422517181 + Q * xs;
                Q =                        1.0 + Q * xs;
                final double R = P / Q;
                result = Y * x + R * x;
            }
        }
        return result;
    }
















    static double expxx(double x) {




        final DD x2 = DD.ofSquare(x);
        return expxx(x2.hi(), x2.lo());
    }















    static double expmxx(double x) {
        final DD x2 = DD.ofSquare(x);
        return expxx(-x2.hi(), -x2.lo());
    }


















    private static double expxx(double a, double b) {











        final double ea = Math.exp(a);

        return ea * b + ea;
    }
}
