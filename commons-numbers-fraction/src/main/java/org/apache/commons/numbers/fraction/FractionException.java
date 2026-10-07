















package org.apache.commons.numbers.fraction;




class FractionException extends ArithmeticException {


    static final String ERROR_CONVERSION_OVERFLOW = "Overflow trying to convert %s to fraction (%d/%d)";

    static final String ERROR_CONVERSION = "Unable to convert %s to fraction after %d iterations";

    static final String ERROR_ZERO_DENOMINATOR = "Denominator must be different from 0";

    static final String ERROR_DIVIDE_BY_ZERO = "The value to divide by must not be zero";


    private static final long serialVersionUID = 201701191744L;








    FractionException(String message, Object... formatArguments) {
        super(String.format(message, formatArguments));
    }






    FractionException(String message) {
        super(message);
    }
}
