















package org.apache.commons.numbers.examples.jmh.core;







public interface LinearCombination {



    @FunctionalInterface
    interface TwoD {









        double value(double a1, double b1, double a2, double b2);
    }




    @FunctionalInterface
    interface ThreeD {











        double value(double a1, double b1, double a2, double b2, double a3, double b3);
    }




    @FunctionalInterface
    interface FourD {













        double value(double a1, double b1, double a2, double b2, double a3, double b3, double a4, double b4);
    }




    @FunctionalInterface
    interface ND {








        double value(double[] a, double[] b);
    }
}
