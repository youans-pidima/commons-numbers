















package org.apache.commons.numbers.examples.jmh.arrays;






final class PivotCaches {


    private static final int UPPER_DEFAULT = Integer.MAX_VALUE;


    private PivotCaches() {}







    static PivotCache ofIndex(int k) {
        return new PointPivotCache(k);
    }









    static PivotCache ofPairedIndex(int k) {
        if (k >= 0) {
            return new PointPivotCache(k);
        }

        final int ka = k & Integer.MAX_VALUE;
        return new RangePivotCache(ka, ka + 1);
    }
















    static PivotCache ofRange(int left, int right) {
        validateRange(left, right);
        return left == right ?
            new PointPivotCache(left) :
            new RangePivotCache(left, right);
    }











    static PivotCache ofFullRange(int left, int right) {
        validateRange(left, right);
        if (right - left <= 1) {
            return left == right ?
                new PointPivotCache(left) :
                new RangePivotCache(left, right);
        }
        return IndexSet.ofRange(left, right);
    }







    private static void validateRange(int left, int right) {
        if (right < left) {
            throw new IllegalArgumentException("Invalid range");
        }
    }




    private static class PointPivotCache implements ScanningPivotCache {

        private final int target;


        private int lowerPivot = -1;


        private int upperPivot = UPPER_DEFAULT;




        PointPivotCache(int index) {
            this.target = index;
        }

        @Override
        public void add(int index) {

            if (index <= target) {


                lowerPivot = Math.max(index, lowerPivot);
            } else {
                upperPivot = Math.min(index, upperPivot);
            }
        }

        @Override
        public void add(int fromIndex, int toIndex) {

            if (toIndex <= target) {


                lowerPivot = Math.max(toIndex, lowerPivot);
            } else if (fromIndex > target) {
                upperPivot = Math.min(fromIndex, upperPivot);
            } else {

                lowerPivot = upperPivot = target;
            }
        }

        @Override
        public int left() {
            return target;
        }

        @Override
        public int right() {
            return target;
        }

        @Override
        public boolean sparse() {

            return false;
        }

        @Override
        public boolean moveLeft(int newLeft) {

            return false;
        }

        @Override
        public boolean contains(int k) {
            return lowerPivot == k;
        }

        @Override
        public int previousPivot(int k) {

            return lowerPivot;
        }



        @Override
        public int nextPivotOrElse(int k, int other) {


            if (lowerPivot == target) {
                return target;
            }
            return upperPivot == UPPER_DEFAULT ? other : upperPivot;
        }

        @Override
        public int nextNonPivot(int k) {

            return lowerPivot == target ? target + 1 : target;
        }

        @Override
        public int previousNonPivot(int k) {

            return lowerPivot == target ? target - 1 : target;
        }
    }









    private static class RangePivotCache implements ScanningPivotCache {

        private final int left;

        private final int right;


        private int lowerPivot = -1;


        private int upperPivot = UPPER_DEFAULT;





        RangePivotCache(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void add(int index) {

            if (index <= left) {
                lowerPivot = Math.max(index, lowerPivot);
            } else if (index >= right) {
                upperPivot = Math.min(index, upperPivot);
            }
        }

        @Override
        public void add(int fromIndex, int toIndex) {

            if (toIndex <= left) {


                lowerPivot = Math.max(toIndex, lowerPivot);
            } else if (fromIndex >= right) {


                upperPivot = Math.min(fromIndex, upperPivot);
            } else {






                if (fromIndex <= left) {
                    lowerPivot = left;
                }
                if (toIndex >= right) {
                    upperPivot = right;
                }
            }
        }

        @Override
        public int left() {
            return left;
        }

        @Override
        public int right() {
            return right;
        }

        @Override
        public boolean sparse() {

            return right - left > 1;
        }

        @Override
        public boolean moveLeft(int newLeft) {

            return false;
        }

        @Override
        public boolean contains(int k) {
            return lowerPivot == k || upperPivot == k;
        }

        @Override
        public int previousPivot(int k) {


            return k == upperPivot ? k : lowerPivot;
        }



        @Override
        public int nextPivotOrElse(int k, int other) {


            if (k == lowerPivot) {
                return k;
            }
            return upperPivot == UPPER_DEFAULT ? other : upperPivot;
        }

        @Override
        public int nextNonPivot(int k) {


            if (sparse()) {
                throw new UnsupportedOperationException();
            }


            int i = k;
            if (i == left) {
                if (lowerPivot != left) {
                    return left;
                }
                i++;
            }
            if (i == right && upperPivot == right) {
                i++;
            }
            return i;
        }

        @Override
        public int previousNonPivot(int k) {


            if (sparse()) {
                throw new UnsupportedOperationException();
            }


            int i = k;
            if (i == right) {
                if (upperPivot != right) {
                    return right;
                }
                i--;
            }
            if (i == left && lowerPivot == left) {
                i--;
            }
            return i;
        }
    }
}
