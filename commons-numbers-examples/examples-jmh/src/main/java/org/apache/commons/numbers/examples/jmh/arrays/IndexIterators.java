















package org.apache.commons.numbers.examples.jmh.arrays;






final class IndexIterators {


    private IndexIterators() {}







    static IndexIterator ofIndex(int k) {
        return new SingleIndex(k);
    }










    static IndexIterator ofInterval(int k1, int k2) {

        if (k1 == k2) {
            return new SingleIndex(k1);
        }

        final int i1 = k1 < k2 ? k1 : k2;
        final int i2 = k1 < k2 ? k2 : k1;
        return new SingleInterval(i1, i2);
    }




    private static final class SingleIndex implements IndexIterator {

        private final int k;




        SingleIndex(int k) {
            this.k = k;
        }

        @Override
        public int left() {
            return k;
        }

        @Override
        public int right() {
            return k;
        }

        @Override
        public int end() {
            return k;
        }

        @Override
        public boolean next() {
            return false;
        }

        @Override
        public boolean positionAfter(int index) {
            return k > index;
        }

        @Override
        public boolean nextAfter(int index) {

            return true;
        }
    }




    private static final class SingleInterval implements IndexIterator {

        private final int l;

        private final int r;





        SingleInterval(int l, int r) {
            this.l = l;
            this.r = r;
        }

        @Override
        public int left() {
            return l;
        }

        @Override
        public int right() {
            return r;
        }

        @Override
        public int end() {
            return r;
        }

        @Override
        public boolean next() {
            return false;
        }

        @Override
        public boolean positionAfter(int index) {
            return r > index;
        }

        @Override
        public boolean nextAfter(int index) {

            return true;
        }
    }
}
