
















package org.apache.commons.numbers.arrays;



















































































































public final class Selection {


    private Selection() {}










    public static void select(double[] a, int k) {
        IndexSupport.checkIndex(0, a.length, k);
        doSelect(a, 0, a.length, k);
    }










    public static void select(double[] a, int[] k) {
        IndexSupport.checkIndices(0, a.length, k);
        doSelect(a, 0, a.length, k);
    }













    public static void select(double[] a, int fromIndex, int toIndex, int k) {
        IndexSupport.checkFromToIndex(fromIndex, toIndex, a.length);
        IndexSupport.checkIndex(fromIndex, toIndex, k);
        doSelect(a, fromIndex, toIndex, k);
    }













    public static void select(double[] a, int fromIndex, int toIndex, int[] k) {
        IndexSupport.checkFromToIndex(fromIndex, toIndex, a.length);
        IndexSupport.checkIndices(fromIndex, toIndex, k);
        doSelect(a, fromIndex, toIndex, k);
    }













    private static void doSelect(double[] a, int fromIndex, int toIndex, int k) {
        if (toIndex - fromIndex <= 1) {
            return;
        }


        int cn = 0;
        int end = toIndex;
        for (int i = toIndex; --i >= fromIndex;) {
            final double v = a[i];

            if (Double.doubleToRawLongBits(v) == Long.MIN_VALUE) {
                cn++;


                a[i] = 0.0;
            } else if (v != v) {

                a[i] = a[--end];
                a[end] = v;
            }
        }


        if (end - fromIndex > 1 && k < end) {
            QuickSelect.select(a, fromIndex, end - 1, k);
        }


        if (cn != 0) {



            for (int j = a[k] < 0 ? k : fromIndex - 1;;) {
                if (a[++j] == 0) {
                    a[j] = -0.0;
                    if (--cn == 0) {
                        break;
                    }
                }
            }
        }
    }













    private static void doSelect(double[] a, int fromIndex, int toIndex, int[] k) {
        if (k.length == 0 || toIndex - fromIndex <= 1) {
            return;
        }


        int cn = 0;
        int end = toIndex;
        for (int i = toIndex; --i >= fromIndex;) {
            final double v = a[i];

            if (Double.doubleToRawLongBits(v) == Long.MIN_VALUE) {
                cn++;


                a[i] = 0.0;
            } else if (v != v) {

                a[i] = a[--end];
                a[end] = v;
            }
        }


        int n = 0;
        if (end - fromIndex > 1) {
            n = k.length;

            if (end < toIndex) {
                for (int i = n; --i >= 0;) {
                    final int index = k[i];
                    if (index >= end) {

                        k[i] = k[--n];
                        k[n] = index;
                    }
                }
            }


            n = QuickSelect.select(a, fromIndex, end - 1, k, n);
        }


        if (cn != 0) {



            int j = fromIndex - 1;
            if (n < 0) {

                int lo = 0;
                int hi = ~n;
                while (lo <= hi) {
                    final int mid = (lo + hi) >>> 1;
                    if (a[k[mid]] < 0) {

                        j = k[mid];
                        lo = mid + 1;
                    } else {
                        hi = mid - 1;
                    }
                }
            } else {

                for (int i = n; --i >= 0;) {
                    if (a[k[i]] < 0) {
                        j = k[i];
                    }
                }
            }
            for (;;) {
                if (a[++j] == 0) {
                    a[j] = -0.0;
                    if (--cn == 0) {
                        break;
                    }
                }
            }
        }
    }










    public static void select(int[] a, int k) {
        IndexSupport.checkIndex(0, a.length, k);
        if (a.length <= 1) {
            return;
        }
        QuickSelect.select(a, 0, a.length - 1, k);
    }










    public static void select(int[] a, int[] k) {
        IndexSupport.checkIndices(0, a.length, k);
        if (k.length == 0 || a.length <= 1) {
            return;
        }
        QuickSelect.select(a, 0, a.length - 1, k, k.length);
    }













    public static void select(int[] a, int fromIndex, int toIndex, int k) {
        IndexSupport.checkFromToIndex(fromIndex, toIndex, a.length);
        IndexSupport.checkIndex(fromIndex, toIndex, k);
        if (toIndex - fromIndex <= 1) {
            return;
        }
        QuickSelect.select(a, fromIndex, toIndex - 1, k);
    }













    public static void select(int[] a, int fromIndex, int toIndex, int[] k) {
        IndexSupport.checkFromToIndex(fromIndex, toIndex, a.length);
        IndexSupport.checkIndices(fromIndex, toIndex, k);
        if (k.length == 0 || toIndex - fromIndex <= 1) {
            return;
        }
        QuickSelect.select(a, fromIndex, toIndex - 1, k, k.length);
    }











    public static void select(long[] a, int k) {
        IndexSupport.checkIndex(0, a.length, k);
        if (a.length <= 1) {
            return;
        }
        QuickSelect.select(a, 0, a.length - 1, k);
    }











    public static void select(long[] a, int[] k) {
        IndexSupport.checkIndices(0, a.length, k);
        if (k.length == 0 || a.length <= 1) {
            return;
        }
        QuickSelect.select(a, 0, a.length - 1, k, k.length);
    }














    public static void select(long[] a, int fromIndex, int toIndex, int k) {
        IndexSupport.checkFromToIndex(fromIndex, toIndex, a.length);
        IndexSupport.checkIndex(fromIndex, toIndex, k);
        if (toIndex - fromIndex <= 1) {
            return;
        }
        QuickSelect.select(a, fromIndex, toIndex - 1, k);
    }














    public static void select(long[] a, int fromIndex, int toIndex, int[] k) {
        IndexSupport.checkFromToIndex(fromIndex, toIndex, a.length);
        IndexSupport.checkIndices(fromIndex, toIndex, k);
        if (k.length == 0 || toIndex - fromIndex <= 1) {
            return;
        }
        QuickSelect.select(a, fromIndex, toIndex - 1, k, k.length);
    }
}
