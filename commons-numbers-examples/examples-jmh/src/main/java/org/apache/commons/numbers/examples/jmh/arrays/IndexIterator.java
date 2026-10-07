















package org.apache.commons.numbers.examples.jmh.arrays;

















interface IndexIterator {





    int left();






    int right();






    int end();









    boolean next();
























    default boolean positionAfter(int index) {
        while (right() <= index) {
            if (!next()) {
                return false;
            }
        }
        return true;
    }













    default boolean nextAfter(int index) {
        return right() >= end();
    }
}
