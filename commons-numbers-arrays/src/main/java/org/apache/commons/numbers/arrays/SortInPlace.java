
















package org.apache.commons.numbers.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
















public enum SortInPlace {

    ASCENDING((o1, o2) -> Double.compare(o1.key(), o2.key())),

    DESCENDING((o1, o2) -> Double.compare(o2.key(), o1.key()));


    private final Comparator<PairDoubleInteger> comparator;




    SortInPlace(Comparator<PairDoubleInteger> comparator) {
        this.comparator = comparator;
    }










    public void apply(double[] x,
                      double[]... yList) {
        final int yListLen = yList.length;
        final int len = x.length;

        for (int j = 0; j < yListLen; j++) {
            final double[] y = yList[j];
            if (y.length != len) {
                throw new IllegalArgumentException("Size mismatch: " +
                                                   y.length + " != " + len);
            }
        }


        final List<PairDoubleInteger> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(new PairDoubleInteger(x[i], i));
        }


        Collections.sort(list, comparator);



        final int[] indices = new int[len];
        for (int i = 0; i < len; i++) {
            final PairDoubleInteger e = list.get(i);
            x[i] = e.key();
            indices[i] = e.value();
        }


        for (int j = 0; j < yListLen; j++) {

            final double[] yInPlace = yList[j];
            final double[] yOrig = Arrays.copyOf(yInPlace, len);

            for (int i = 0; i < len; i++) {
                yInPlace[i] = yOrig[indices[i]];
            }
        }
    }




    private static class PairDoubleInteger {

        private final double key;

        private final int value;





        PairDoubleInteger(double key,
                          int value) {
            this.key = key;
            this.value = value;
        }



        double key() {
            return key;
        }



        int value() {
            return value;
        }
    }
}
