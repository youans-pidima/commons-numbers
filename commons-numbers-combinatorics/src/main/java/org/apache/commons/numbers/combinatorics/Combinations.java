
















package org.apache.commons.numbers.combinatorics;

import java.io.Serializable;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Comparator;

import org.apache.commons.numbers.core.ArithmeticUtils;






public final class Combinations implements Iterable<int[]> {

    private final int n;

    private final int k;







    private Combinations(int n,
                         int k) {
        BinomialCoefficient.checkBinomial(n, k);
        this.n = n;
        this.k = k;
    }










    public static Combinations of(int n,
                                  int k) {
        return new Combinations(n, k);
    }






    public int getN() {
        return n;
    }






    public int getK() {
        return k;
    }


















    @Override
    public Iterator<int[]> iterator() {
        return k == 0 || k == n ?
            new SingletonIterator(k) :
            new LexicographicIterator(n, k);
    }









    public Comparator<int[]> comparator() {
        return new LexicographicComparator(n, k);
    }












    private static class LexicographicIterator implements Iterator<int[]> {

        private final int k;









        private final int[] c;


        private boolean more = true;


        private int j;











        LexicographicIterator(int n, int k) {
            this.k = k;
            c = new int[k + 3];

            for (int i = 1; i <= k; i++) {
                c[i] = i - 1;
            }

            c[k + 1] = n;
            c[k + 2] = 0;
            j = k;
        }




        @Override
        public boolean hasNext() {
            return more;
        }




        @Override
        public int[] next() {
            if (!more) {
                throw new NoSuchElementException();
            }

            final int[] ret = new int[k];
            System.arraycopy(c, 1, ret, 0, k);



            int x = 0;
            if (j > 0) {
                x = j;
                c[j] = x;
                j--;
                return ret;
            }

            if (c[1] + 1 < c[2]) {
                c[1]++;
                return ret;
            } else {
                j = 2;
            }

            boolean stepDone = false;
            while (!stepDone) {
                c[j - 1] = j - 2;
                x = c[j] + 1;
                if (x == c[j + 1]) {
                    j++;
                } else {
                    stepDone = true;
                }
            }

            if (j > k) {
                more = false;
                return ret;
            }

            c[j] = x;
            j--;
            return ret;
        }




        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }





    private static class SingletonIterator implements Iterator<int[]> {

        private final int n;

        private boolean more = true;





        SingletonIterator(final int n) {
            this.n = n;
        }




        @Override
        public boolean hasNext() {
            return more;
        }




        @Override
        public int[] next() {
            if (more) {
                more = false;

                final int[] s = new int[n];
                for (int i = 0; i < n; i++) {
                    s[i] = i;
                }
                return s;
            } else {
                throw new NoSuchElementException();
            }
        }





        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }








    private static class LexicographicComparator
        implements Comparator<int[]>,
                   Serializable {

        private static final long serialVersionUID = 20170520L;

        private final int n;

        private final int k;





        LexicographicComparator(int n,
                                int k) {
            this.n = n;
            this.k = k;
        }









        @Override
        public int compare(int[] c1,
                           int[] c2) {
            if (c1.length != k) {
                throw new CombinatoricsException(CombinatoricsException.MISMATCH, k, c1.length);
            }
            if (c2.length != k) {
                throw new CombinatoricsException(CombinatoricsException.MISMATCH, k, c2.length);
            }


            final int[] c1s = Arrays.copyOf(c1, k);
            final int[] c2s = Arrays.copyOf(c2, k);
            Arrays.sort(c1s);
            Arrays.sort(c2s);

            final long v1 = lexNorm(c1s);
            final long v2 = lexNorm(c2s);

            return Long.compare(v1, v2);
        }













        private long lexNorm(int[] c) {
            long ret = 0;
            for (int i = 0; i < c.length; i++) {
                final int digit = c[i];
                if (digit < 0 ||
                    digit >= n) {
                    throw new CombinatoricsException(CombinatoricsException.OUT_OF_RANGE, digit, 0, n - 1);
                }

                ret += c[i] * ArithmeticUtils.pow((long) n, i);
            }
            return ret;
        }
    }
}
