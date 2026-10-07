















package org.apache.commons.numbers.rootfinder;




class SolverException extends IllegalArgumentException {

    static final String TOO_LARGE = "%s > %s";

    static final String OUT_OF_RANGE = "%s is out of range [%s, %s]";

    static final String BRACKETING = "No bracketing: f(%s)=%s, f(%s)=%s";

    static final String INVALID_ACCURACY = "Invalid %s accuracy: %s";

    static final String NAN = "Function value at %s is NaN";

    static final String NON_FINITE = "%s is non-finite: %s";


    private static final long serialVersionUID = 20190602L;









    SolverException(String message, Object... formatArguments) {
        super(String.format(message, formatArguments));
    }
}
