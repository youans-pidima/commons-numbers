















package org.apache.commons.numbers.combinatorics;




class CombinatoricsException extends IllegalArgumentException {

    static final String OUT_OF_RANGE = "Number %s is out of range [%s, %s]";

    static final String NEGATIVE = "Number %s is negative";

    static final String MISMATCH = "Expected %s but was %s";


    private static final long serialVersionUID = 20170515L;








    CombinatoricsException(String message,
                           Object... formatArguments) {
        super(String.format(message, formatArguments));
    }
}
