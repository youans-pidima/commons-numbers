
















package org.apache.commons.numbers.arrays;

import java.util.Arrays;




















public final class MultidimensionalCounter {



    private final int dimension;



    private final int[] uniCounterOffset;



    private final int[] size;



    private final int totalSize;



    private final int last;








    private MultidimensionalCounter(int... size) {
        dimension = size.length;
        this.size = Arrays.copyOf(size, size.length);

        uniCounterOffset = new int[dimension];

        last = dimension - 1;
        uniCounterOffset[last] = 1;





        long tS = 1;
        for (int i = last - 1; i >= 0; i--) {
            final int index = i + 1;
            checkSizeStrictlyPositive(size[index]);
            tS *= size[index];
            checkSizeRepresentable(tS);
            uniCounterOffset[i] = (int) tS;
        }

        checkSizeStrictlyPositive(size[0]);
        final long total = tS * size[0];
        checkSizeRepresentable(total);
        totalSize = (int) total;
    }









    public static MultidimensionalCounter of(int... size) {
        return new MultidimensionalCounter(size);
    }






    public int getDimension() {
        return dimension;
    }









    public int[] toMulti(int index) {
        if (index < 0 ||
            index >= totalSize) {
            throw new IndexOutOfBoundsException(createIndexOutOfBoundsMessage(totalSize, index));
        }

        final int[] indices = new int[dimension];

        int pos = index;
        for (int i = 0; i < last; i++) {
            indices[i] = pos / uniCounterOffset[i];

            pos = pos - indices[i] * uniCounterOffset[i];
        }

        indices[last] = pos;

        return indices;
    }












    public int toUni(int... c) {
        if (c.length != dimension) {
            throw new IllegalArgumentException("Wrong number of arguments: " + c.length +
                                               "(expected: " + dimension + ")");
        }
        int count = 0;
        for (int i = 0; i < dimension; i++) {
            final int index = c[i];
            if (index < 0 ||
                index >= size[i]) {
                throw new IndexOutOfBoundsException(createIndexOutOfBoundsMessage(size[i], index));
            }
            count += uniCounterOffset[i] * index;
        }
        return count;
    }






    public int getSize() {
        return totalSize;
    }






    public int[] getSizes() {
        return Arrays.copyOf(size, size.length);
    }


    @Override
    public String toString() {
        return Arrays.toString(size);
    }






    private static void checkSizeStrictlyPositive(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Dimension size not strictly positive: " + size);
        }
    }







    private static void checkSizeRepresentable(long size) {
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Cumulative size too large: " + size);
        }
    }








    private static String createIndexOutOfBoundsMessage(int size, int index) {
        return "Index out of bounds [0, " + (size - 1) + "]: " + index;
    }
}
