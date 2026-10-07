















package org.apache.commons.numbers.gamma;




class GammaException extends IllegalArgumentException {

    static final String OUT_OF_RANGE = "Number %s is out of range [%s, %s]";


    private static final long serialVersionUID = 20170505L;








    GammaException(String message, Object... formatArguments) {
        super(String.format(message, formatArguments));
    }
}
