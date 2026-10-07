















package org.apache.commons.numbers.gamma;






final class InvGamma1pm1 {




    private static final double INV_GAMMA1P_M1_A0 = .611609510448141581788E-08;

    private static final double INV_GAMMA1P_M1_A1 = .624730830116465516210E-08;

    private static final double INV_GAMMA1P_M1_B1 = .203610414066806987300E+00;

    private static final double INV_GAMMA1P_M1_B2 = .266205348428949217746E-01;

    private static final double INV_GAMMA1P_M1_B3 = .493944979382446875238E-03;

    private static final double INV_GAMMA1P_M1_B4 = -.851419432440314906588E-05;

    private static final double INV_GAMMA1P_M1_B5 = -.643045481779353022248E-05;

    private static final double INV_GAMMA1P_M1_B6 = .992641840672773722196E-06;

    private static final double INV_GAMMA1P_M1_B7 = -.607761895722825260739E-07;

    private static final double INV_GAMMA1P_M1_B8 = .195755836614639731882E-09;

    private static final double INV_GAMMA1P_M1_P0 = .6116095104481415817861E-08;

    private static final double INV_GAMMA1P_M1_P1 = .6871674113067198736152E-08;

    private static final double INV_GAMMA1P_M1_P2 = .6820161668496170657918E-09;

    private static final double INV_GAMMA1P_M1_P3 = .4686843322948848031080E-10;

    private static final double INV_GAMMA1P_M1_P4 = .1572833027710446286995E-11;

    private static final double INV_GAMMA1P_M1_P5 = -.1249441572276366213222E-12;

    private static final double INV_GAMMA1P_M1_P6 = .4343529937408594255178E-14;

    private static final double INV_GAMMA1P_M1_Q1 = .3056961078365221025009E+00;

    private static final double INV_GAMMA1P_M1_Q2 = .5464213086042296536016E-01;

    private static final double INV_GAMMA1P_M1_Q3 = .4956830093825887312020E-02;

    private static final double INV_GAMMA1P_M1_Q4 = .2692369466186361192876E-03;

    private static final double INV_GAMMA1P_M1_C = -.422784335098467139393487909917598E+00;

    private static final double INV_GAMMA1P_M1_C0 = .577215664901532860606512090082402E+00;

    private static final double INV_GAMMA1P_M1_C1 = -.655878071520253881077019515145390E+00;

    private static final double INV_GAMMA1P_M1_C2 = -.420026350340952355290039348754298E-01;

    private static final double INV_GAMMA1P_M1_C3 = .166538611382291489501700795102105E+00;

    private static final double INV_GAMMA1P_M1_C4 = -.421977345555443367482083012891874E-01;

    private static final double INV_GAMMA1P_M1_C5 = -.962197152787697356211492167234820E-02;

    private static final double INV_GAMMA1P_M1_C6 = .721894324666309954239501034044657E-02;

    private static final double INV_GAMMA1P_M1_C7 = -.116516759185906511211397108401839E-02;

    private static final double INV_GAMMA1P_M1_C8 = -.215241674114950972815729963053648E-03;

    private static final double INV_GAMMA1P_M1_C9 = .128050282388116186153198626328164E-03;

    private static final double INV_GAMMA1P_M1_C10 = -.201348547807882386556893914210218E-04;

    private static final double INV_GAMMA1P_M1_C11 = -.125049348214267065734535947383309E-05;

    private static final double INV_GAMMA1P_M1_C12 = .113302723198169588237412962033074E-05;

    private static final double INV_GAMMA1P_M1_C13 = -.205633841697760710345015413002057E-06;


    private InvGamma1pm1() {

    }











    static double value(final double x) {
        if (x < -0.5 || x > 1.5) {
            throw new GammaException(GammaException.OUT_OF_RANGE, x, -0.5, 1.5);
        }

        final double t = x <= 0.5 ? x : (x - 0.5) - 0.5;
        if (t < 0) {
            final double a = INV_GAMMA1P_M1_A0 + t * INV_GAMMA1P_M1_A1;
            double b = INV_GAMMA1P_M1_B8;
            b = INV_GAMMA1P_M1_B7 + t * b;
            b = INV_GAMMA1P_M1_B6 + t * b;
            b = INV_GAMMA1P_M1_B5 + t * b;
            b = INV_GAMMA1P_M1_B4 + t * b;
            b = INV_GAMMA1P_M1_B3 + t * b;
            b = INV_GAMMA1P_M1_B2 + t * b;
            b = INV_GAMMA1P_M1_B1 + t * b;
            b = 1.0 + t * b;

            double c = INV_GAMMA1P_M1_C13 + t * (a / b);
            c = INV_GAMMA1P_M1_C12 + t * c;
            c = INV_GAMMA1P_M1_C11 + t * c;
            c = INV_GAMMA1P_M1_C10 + t * c;
            c = INV_GAMMA1P_M1_C9 + t * c;
            c = INV_GAMMA1P_M1_C8 + t * c;
            c = INV_GAMMA1P_M1_C7 + t * c;
            c = INV_GAMMA1P_M1_C6 + t * c;
            c = INV_GAMMA1P_M1_C5 + t * c;
            c = INV_GAMMA1P_M1_C4 + t * c;
            c = INV_GAMMA1P_M1_C3 + t * c;
            c = INV_GAMMA1P_M1_C2 + t * c;
            c = INV_GAMMA1P_M1_C1 + t * c;
            c = INV_GAMMA1P_M1_C + t * c;
            if (x > 0.5) {
                return t * c / x;
            }
            return x * ((c + 0.5) + 0.5);
        }
        double p = INV_GAMMA1P_M1_P6;
        p = INV_GAMMA1P_M1_P5 + t * p;
        p = INV_GAMMA1P_M1_P4 + t * p;
        p = INV_GAMMA1P_M1_P3 + t * p;
        p = INV_GAMMA1P_M1_P2 + t * p;
        p = INV_GAMMA1P_M1_P1 + t * p;
        p = INV_GAMMA1P_M1_P0 + t * p;

        double q = INV_GAMMA1P_M1_Q4;
        q = INV_GAMMA1P_M1_Q3 + t * q;
        q = INV_GAMMA1P_M1_Q2 + t * q;
        q = INV_GAMMA1P_M1_Q1 + t * q;
        q = 1.0 + t * q;

        double c = INV_GAMMA1P_M1_C13 + (p / q) * t;
        c = INV_GAMMA1P_M1_C12 + t * c;
        c = INV_GAMMA1P_M1_C11 + t * c;
        c = INV_GAMMA1P_M1_C10 + t * c;
        c = INV_GAMMA1P_M1_C9 + t * c;
        c = INV_GAMMA1P_M1_C8 + t * c;
        c = INV_GAMMA1P_M1_C7 + t * c;
        c = INV_GAMMA1P_M1_C6 + t * c;
        c = INV_GAMMA1P_M1_C5 + t * c;
        c = INV_GAMMA1P_M1_C4 + t * c;
        c = INV_GAMMA1P_M1_C3 + t * c;
        c = INV_GAMMA1P_M1_C2 + t * c;
        c = INV_GAMMA1P_M1_C1 + t * c;
        c = INV_GAMMA1P_M1_C0 + t * c;

        if (x > 0.5) {
            return (t / x) * ((c - 0.5) - 0.5);
        }
        return x * c;
    }
}
