
















package org.apache.commons.numbers.gamma;











final class Policy {



    private static final Policy DEFAULT = new Policy(0x1.0p-53, 1000000);


    private final double eps;

    private final int maxIterations;








    Policy(double eps, int maxIterations) {
        this.eps = eps;
        this.maxIterations = maxIterations;
    }






    static Policy getDefault() {
        return DEFAULT;
    }






    double getEps() {
        return eps;
    }






    int getMaxIterations() {
        return maxIterations;
    }
}
