
















package org.apache.commons.numbers.complex.streams;

import org.apache.commons.numbers.complex.Complex;




public final class ComplexUtils {

    private static final int DIM_X = 0;

    private static final int DIM_Y = 1;

    private static final int DIM_Z = 2;




    private ComplexUtils() {}

























    public static Complex polar2Complex(double r, double theta) {
        if (r < 0) {
            throw new NegativeModulusException(r);
        }
        return Complex.ofCartesian(r * Math.cos(theta), r * Math.sin(theta));
    }










    public static Complex[] polar2Complex(double[] r, double[] theta) {
        final int length = r.length;
        final Complex[] c = new Complex[length];
        for (int x = 0; x < length; x++) {
            if (r[x] < 0) {
                throw new NegativeModulusException(r[x]);
            }
            c[x] = Complex.ofCartesian(r[x] * Math.cos(theta[x]), r[x] * Math.sin(theta[x]));
        }
        return c;
    }










    public static Complex[][] polar2Complex(double[][] r, double[][] theta) {
        final int length = r.length;
        final Complex[][] c = new Complex[length][];
        for (int x = 0; x < length; x++) {
            c[x] = polar2Complex(r[x], theta[x]);
        }
        return c;
    }










    public static Complex[][][] polar2Complex(double[][][] r, double[][][] theta) {
        final int length = r.length;
        final Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < length; x++) {
            c[x] = polar2Complex(r[x], theta[x]);
        }
        return c;
    }









    public static Complex extractComplexFromRealArray(double[] real, int index) {
        return Complex.ofCartesian(real[index], 0);
    }









    public static Complex extractComplexFromRealArray(float[] real, int index) {
        return Complex.ofCartesian(real[index], 0);
    }









    public static Complex extractComplexFromImaginaryArray(double[] imaginary, int index) {
        return Complex.ofCartesian(0, imaginary[index]);
    }









    public static Complex extractComplexFromImaginaryArray(float[] imaginary, int index) {
        return Complex.ofCartesian(0, imaginary[index]);
    }









    public static double extractRealFromComplexArray(Complex[] complex, int index) {
        return complex[index].getReal();
    }









    public static float extractRealFloatFromComplexArray(Complex[] complex, int index) {
        return (float) complex[index].getReal();
    }









    public static double extractImaginaryFromComplexArray(Complex[] complex, int index) {
        return complex[index].getImaginary();
    }









    public static float extractImaginaryFloatFromComplexArray(Complex[] complex, int index) {
        return (float) complex[index].getImaginary();
    }










    public static Complex extractComplexFromInterleavedArray(double[] d, int index) {
        return Complex.ofCartesian(d[index * 2], d[index * 2 + 1]);
    }










    public static Complex extractComplexFromInterleavedArray(float[] f, int index) {
        return Complex.ofCartesian(f[index * 2], f[index * 2 + 1]);
    }









    public static double[] extractInterleavedFromComplexArray(Complex[] complex, int index) {
        return new double[] {complex[index].getReal(), complex[index].getImaginary()};
    }









    public static float[] extractInterleavedFloatFromComplexArray(Complex[] complex, int index) {
        return new float[] {(float) complex[index].getReal(), (float) complex[index].getImaginary()};
    }







    public static Complex[] real2Complex(double[] real) {
        int index = 0;
        final Complex[] c = new Complex[real.length];
        for (final double d : real) {
            c[index] = Complex.ofCartesian(d, 0);
            index++;
        }
        return c;
    }







    public static Complex[] real2Complex(float[] real) {
        int index = 0;
        final Complex[] c = new Complex[real.length];
        for (final float d : real) {
            c[index] = Complex.ofCartesian(d, 0);
            index++;
        }
        return c;
    }








    public static Complex[][] real2Complex(double[][] d) {
        final int w = d.length;
        final Complex[][] c = new Complex[w][];
        for (int n = 0; n < w; n++) {
            c[n] = ComplexUtils.real2Complex(d[n]);
        }
        return c;
    }








    public static Complex[][] real2Complex(float[][] d) {
        final int w = d.length;
        final Complex[][] c = new Complex[w][];
        for (int n = 0; n < w; n++) {
            c[n] = ComplexUtils.real2Complex(d[n]);
        }
        return c;
    }








    public static Complex[][][] real2Complex(double[][][] d) {
        final int w = d.length;
        final Complex[][][] c = new Complex[w][][];
        for (int x = 0; x < w; x++) {
            c[x] = ComplexUtils.real2Complex(d[x]);
        }
        return c;
    }








    public static Complex[][][] real2Complex(float[][][] d) {
        final int w = d.length;
        final Complex[][][] c = new Complex[w][][];
        for (int x = 0; x < w; x++) {
            c[x] = ComplexUtils.real2Complex(d[x]);
        }
        return c;
    }








    public static Complex[][][][] real2Complex(double[][][][] d) {
        final int w = d.length;
        final Complex[][][][] c = new Complex[w][][][];
        for (int x = 0; x < w; x++) {
            c[x] = ComplexUtils.real2Complex(d[x]);
        }
        return c;
    }








    public static double[] complex2Real(Complex[] c) {
        int index = 0;
        final double[] d = new double[c.length];
        for (final Complex cc : c) {
            d[index] = cc.getReal();
            index++;
        }
        return d;
    }








    public static float[] complex2RealFloat(Complex[] c) {
        int index = 0;
        final float[] f = new float[c.length];
        for (final Complex cc : c) {
            f[index] = (float) cc.getReal();
            index++;
        }
        return f;
    }








    public static double[][] complex2Real(Complex[][] c) {
        final int length = c.length;
        final double[][] d = new double[length][];
        for (int n = 0; n < length; n++) {
            d[n] = complex2Real(c[n]);
        }
        return d;
    }








    public static float[][] complex2RealFloat(Complex[][] c) {
        final int length = c.length;
        final float[][] f = new float[length][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2RealFloat(c[n]);
        }
        return f;
    }








    public static double[][][] complex2Real(Complex[][][] c) {
        final int length = c.length;
        final double[][][] d = new double[length][][];
        for (int n = 0; n < length; n++) {
            d[n] = complex2Real(c[n]);
        }
        return d;
    }








    public static float[][][] complex2RealFloat(Complex[][][] c) {
        final int length = c.length;
        final float[][][] f = new float[length][][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2RealFloat(c[n]);
        }
        return f;
    }








    public static double[][][][] complex2Real(Complex[][][][] c) {
        final int length = c.length;
        final double[][][][] d = new double[length][][][];
        for (int n = 0; n < length; n++) {
            d[n] = complex2Real(c[n]);
        }
        return d;
    }








    public static float[][][][] complex2RealFloat(Complex[][][][] c) {
        final int length = c.length;
        final float[][][][] f = new float[length][][][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2RealFloat(c[n]);
        }
        return f;
    }








    public static Complex[] imaginary2Complex(double[] imaginary) {
        int index = 0;
        final Complex[] c = new Complex[imaginary.length];
        for (final double d : imaginary) {
            c[index] = Complex.ofCartesian(0, d);
            index++;
        }
        return c;
    }







    public static Complex[] imaginary2Complex(float[] imaginary) {
        int index = 0;
        final Complex[] c = new Complex[imaginary.length];
        for (final float d : imaginary) {
            c[index] = Complex.ofCartesian(0, d);
            index++;
        }
        return c;
    }








    public static Complex[][] imaginary2Complex(double[][] i) {
        final int w = i.length;
        final Complex[][] c = new Complex[w][];
        for (int n = 0; n < w; n++) {
            c[n] = ComplexUtils.imaginary2Complex(i[n]);
        }
        return c;
    }








    public static Complex[][][] imaginary2Complex(double[][][] i) {
        final int w = i.length;
        final Complex[][][] c = new Complex[w][][];
        for (int n = 0; n < w; n++) {
            c[n] = ComplexUtils.imaginary2Complex(i[n]);
        }
        return c;
    }








    public static Complex[][][][] imaginary2Complex(double[][][][] i) {
        final int w = i.length;
        final Complex[][][][] c = new Complex[w][][][];
        for (int n = 0; n < w; n++) {
            c[n] = ComplexUtils.imaginary2Complex(i[n]);
        }
        return c;
    }








    public static double[] complex2Imaginary(Complex[] c) {
        int index = 0;
        final double[] i = new double[c.length];
        for (final Complex cc : c) {
            i[index] = cc.getImaginary();
            index++;
        }
        return i;
    }








    public static float[] complex2ImaginaryFloat(Complex[] c) {
        int index = 0;
        final float[] f = new float[c.length];
        for (final Complex cc : c) {
            f[index] = (float) cc.getImaginary();
            index++;
        }
        return f;
    }








    public static double[][] complex2Imaginary(Complex[][] c) {
        final int length = c.length;
        final double[][] i = new double[length][];
        for (int n = 0; n < length; n++) {
            i[n] = complex2Imaginary(c[n]);
        }
        return i;
    }








    public static float[][] complex2ImaginaryFloat(Complex[][] c) {
        final int length = c.length;
        final float[][] f = new float[length][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2ImaginaryFloat(c[n]);
        }
        return f;
    }








    public static double[][][] complex2Imaginary(Complex[][][] c) {
        final int length = c.length;
        final double[][][] i = new double[length][][];
        for (int n = 0; n < length; n++) {
            i[n] = complex2Imaginary(c[n]);
        }
        return i;
    }








    public static float[][][] complex2ImaginaryFloat(Complex[][][] c) {
        final int length = c.length;
        final float[][][] f = new float[length][][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2ImaginaryFloat(c[n]);
        }
        return f;
    }








    public static double[][][][] complex2Imaginary(Complex[][][][] c) {
        final int length = c.length;
        final double[][][][] i = new double[length][][][];
        for (int n = 0; n < length; n++) {
            i[n] = complex2Imaginary(c[n]);
        }
        return i;
    }








    public static float[][][][] complex2ImaginaryFloat(Complex[][][][] c) {
        final int length = c.length;
        final float[][][][] f = new float[length][][][];
        for (int n = 0; n < length; n++) {
            f[n] = complex2ImaginaryFloat(c[n]);
        }
        return f;
    }










    public static Complex[] interleaved2Complex(double[] interleaved) {
        final int length = interleaved.length / 2;
        final Complex[] c = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = Complex.ofCartesian(interleaved[n * 2], interleaved[n * 2 + 1]);
        }
        return c;
    }








    public static Complex[] interleaved2Complex(float[] interleaved) {
        final int length = interleaved.length / 2;
        final Complex[] c = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = Complex.ofCartesian(interleaved[n * 2], interleaved[n * 2 + 1]);
        }
        return c;
    }









    public static double[] complex2Interleaved(Complex[] c) {
        int index = 0;
        final double[] i = new double[c.length * 2];
        for (final Complex cc : c) {
            final int real = index * 2;
            final int imag = index * 2 + 1;
            i[real] = cc.getReal();
            i[imag] = cc.getImaginary();
            index++;
        }
        return i;
    }









    public static float[] complex2InterleavedFloat(Complex[] c) {
        int index = 0;
        final float[] f = new float[c.length * 2];
        for (final Complex cc : c) {
            final int real = index * 2;
            final int imag = index * 2 + 1;
            f[real] = (float) cc.getReal();
            f[imag] = (float) cc.getImaginary();
            index++;
        }
        return f;
    }











    public static double[][] complex2Interleaved(Complex[][] c, int interleavedDim) {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = c.length;
        final int h = c[0].length;
        double[][] i;
        if (interleavedDim == 0) {
            i = new double[2 * w][h];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    i[x * 2][y] = c[x][y].getReal();
                    i[x * 2 + 1][y] = c[x][y].getImaginary();
                }
            }
        } else {
            i = new double[w][2 * h];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    i[x][y * 2] = c[x][y].getReal();
                    i[x][y * 2 + 1] = c[x][y].getImaginary();
                }
            }
        }
        return i;
    }










    public static double[][] complex2Interleaved(Complex[][] c) {
        return complex2Interleaved(c, 1);
    }











    public static double[][][] complex2Interleaved(Complex[][][] c, int interleavedDim) {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = c.length;
        final int h = c[0].length;
        final int d = c[0][0].length;
        double[][][] i;
        if (interleavedDim == 0) {
            i = new double[2 * w][h][d];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        i[x * 2][y][z] = c[x][y][z].getReal();
                        i[x * 2 + 1][y][z] = c[x][y][z].getImaginary();
                    }
                }
            }
        } else if (interleavedDim == 1) {
            i = new double[w][2 * h][d];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        i[x][y * 2][z] = c[x][y][z].getReal();
                        i[x][y * 2 + 1][z] = c[x][y][z].getImaginary();
                    }
                }
            }
        } else {
            i = new double[w][h][2 * d];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        i[x][y][z * 2] = c[x][y][z].getReal();
                        i[x][y][z * 2 + 1] = c[x][y][z].getImaginary();
                    }
                }
            }
        }
        return i;
    }











    public static double[][][][] complex2Interleaved(Complex[][][][] c, int interleavedDim) {
        if (interleavedDim > 3 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = c.length;
        final int h = c[0].length;
        final int d = c[0][0].length;
        final int v = c[0][0][0].length;
        double[][][][] i;
        if (interleavedDim == DIM_X) {
            i = new double[2 * w][h][d][v];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        for (int t = 0; t < v; t++) {
                            i[x * 2][y][z][t] = c[x][y][z][t].getReal();
                            i[x * 2 + 1][y][z][t] = c[x][y][z][t].getImaginary();
                        }
                    }
                }
            }
        } else if (interleavedDim == DIM_Y) {
            i = new double[w][2 * h][d][v];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        for (int t = 0; t < v; t++) {
                            i[x][y * 2][z][t] = c[x][y][z][t].getReal();
                            i[x][y * 2 + 1][z][t] = c[x][y][z][t].getImaginary();
                        }
                    }
                }
            }
        } else if (interleavedDim == DIM_Z) {
            i = new double[w][h][2 * d][v];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        for (int t = 0; t < v; t++) {
                            i[x][y][z * 2][t] = c[x][y][z][t].getReal();
                            i[x][y][z * 2 + 1][t] = c[x][y][z][t].getImaginary();
                        }
                    }
                }
            }
        } else {
            i = new double[w][h][d][2 * v];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        for (int t = 0; t < v; t++) {
                            i[x][y][z][t * 2] = c[x][y][z][t].getReal();
                            i[x][y][z][t * 2 + 1] = c[x][y][z][t].getImaginary();
                        }
                    }
                }
            }
        }
        return i;
    }










    public static double[][][] complex2Interleaved(Complex[][][] c) {
        return complex2Interleaved(c, 2);
    }










    public static double[][][][] complex2Interleaved(Complex[][][][] c) {
        return complex2Interleaved(c, 3);
    }











    public static float[][] complex2InterleavedFloat(Complex[][] c, int interleavedDim) {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = c.length;
        final int h = c[0].length;
        float[][] i;
        if (interleavedDim == 0) {
            i = new float[2 * w][h];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    i[x * 2][y] = (float) c[x][y].getReal();
                    i[x * 2 + 1][y] = (float) c[x][y].getImaginary();
                }
            }
        } else {
            i = new float[w][2 * h];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    i[x][y * 2] = (float) c[x][y].getReal();
                    i[x][y * 2 + 1] = (float) c[x][y].getImaginary();
                }
            }
        }
        return i;
    }











    public static float[][] complex2InterleavedFloat(Complex[][] c) {
        return complex2InterleavedFloat(c, 1);
    }











    public static float[][][] complex2InterleavedFloat(Complex[][][] c, int interleavedDim) {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = c.length;
        final int h = c[0].length;
        final int d = c[0][0].length;
        float[][][] i;
        if (interleavedDim == 0) {
            i = new float[2 * w][h][d];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        i[x * 2][y][z] = (float) c[x][y][z].getReal();
                        i[x * 2 + 1][y][z] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        } else if (interleavedDim == 1) {
            i = new float[w][2 * h][d];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        i[x][y * 2][z] = (float) c[x][y][z].getReal();
                        i[x][y * 2 + 1][z] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        } else {
            i = new float[w][h][2 * d];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        i[x][y][z * 2] = (float) c[x][y][z].getReal();
                        i[x][y][z * 2 + 1] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        }
        return i;
    }











    public static float[][][] complex2InterleavedFloat(Complex[][][] c) {
        return complex2InterleavedFloat(c, 2);
    }










    public static Complex[][] interleaved2Complex(double[][] i, int interleavedDim) {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = i.length;
        final int h = i[0].length;
        Complex[][] c;
        if (interleavedDim == 0) {
            c = new Complex[w / 2][h];
            for (int x = 0; x < w / 2; x++) {
                for (int y = 0; y < h; y++) {
                    c[x][y] = Complex.ofCartesian(i[x * 2][y], i[x * 2 + 1][y]);
                }
            }
        } else {
            c = new Complex[w][h / 2];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h / 2; y++) {
                    c[x][y] = Complex.ofCartesian(i[x][y * 2], i[x][y * 2 + 1]);
                }
            }
        }
        return c;
    }









    public static Complex[][] interleaved2Complex(double[][] d) {
        return interleaved2Complex(d, 1);
    }










    public static Complex[][][] interleaved2Complex(double[][][] i, int interleavedDim) {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = i.length;
        final int h = i[0].length;
        final int d = i[0][0].length;
        Complex[][][] c;
        if (interleavedDim == DIM_X) {
            c = new Complex[w / 2][h][d];
            for (int x = 0; x < w / 2; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        c[x][y][z] = Complex.ofCartesian(i[x * 2][y][z], i[x * 2 + 1][y][z]);
                    }
                }
            }
        } else if (interleavedDim == DIM_Y) {
            c = new Complex[w][h / 2][d];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h / 2; y++) {
                    for (int z = 0; z < d; z++) {
                        c[x][y][z] = Complex.ofCartesian(i[x][y * 2][z], i[x][y * 2 + 1][z]);
                    }
                }
            }
        } else {
            c = new Complex[w][h][d / 2];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d / 2; z++) {
                        c[x][y][z] = Complex.ofCartesian(i[x][y][z * 2], i[x][y][z * 2 + 1]);
                    }
                }
            }
        }
        return c;
    }










    public static Complex[][][][] interleaved2Complex(double[][][][] i, int interleavedDim) {
        if (interleavedDim > 3 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = i.length;
        final int h = i[0].length;
        final int d = i[0][0].length;
        final int v = i[0][0][0].length;
        Complex[][][][] c;
        if (interleavedDim == 0) {
            c = new Complex[w / 2][h][d][v];
            for (int x = 0; x < w / 2; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        for (int t = 0; t < v; t++) {
                            c[x][y][z][t] = Complex.ofCartesian(i[x * 2][y][z][t], i[x * 2 + 1][y][z][t]);
                        }
                    }
                }
            }
        } else if (interleavedDim == 1) {
            c = new Complex[w][h / 2][d][v];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h / 2; y++) {
                    for (int z = 0; z < d; z++) {
                        for (int t = 0; t < v; t++) {
                            c[x][y][z][t] = Complex.ofCartesian(i[x][y * 2][z][t], i[x][y * 2 + 1][z][t]);
                        }
                    }
                }
            }
        } else if (interleavedDim == 2) {
            c = new Complex[w][h][d / 2][v];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d / 2; z++) {
                        for (int t = 0; t < v; t++) {
                            c[x][y][z][t] = Complex.ofCartesian(i[x][y][z * 2][t], i[x][y][z * 2 + 1][t]);
                        }
                    }
                }
            }
        } else {
            c = new Complex[w][h][d][v / 2];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        for (int t = 0; t < v / 2; t++) {
                            c[x][y][z][t] = Complex.ofCartesian(i[x][y][z][t * 2], i[x][y][z][t * 2 + 1]);
                        }
                    }
                }
            }
        }
        return c;
    }









    public static Complex[][][] interleaved2Complex(double[][][] d) {
        return interleaved2Complex(d, 2);
    }










    public static Complex[][] interleaved2Complex(float[][] i, int interleavedDim) {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = i.length;
        final int h = i[0].length;
        Complex[][] c;
        if (interleavedDim == 0) {
            c = new Complex[w / 2][h];
            for (int x = 0; x < w / 2; x++) {
                for (int y = 0; y < h; y++) {
                    c[x][y] = Complex.ofCartesian(i[x * 2][y], i[x * 2 + 1][y]);
                }
            }
        } else {
            c = new Complex[w][h / 2];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h / 2; y++) {
                    c[x][y] = Complex.ofCartesian(i[x][y * 2], i[x][y * 2 + 1]);
                }
            }
        }
        return c;
    }









    public static Complex[][] interleaved2Complex(float[][] d) {
        return interleaved2Complex(d, 1);
    }










    public static Complex[][][] interleaved2Complex(float[][][] i, int interleavedDim) {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw new IndexOutOfRangeException(interleavedDim);
        }
        final int w = i.length;
        final int h = i[0].length;
        final int d = i[0][0].length;
        Complex[][][] c;
        if (interleavedDim == 0) {
            c = new Complex[w / 2][h][d];
            for (int x = 0; x < w / 2; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        c[x][y][z] = Complex.ofCartesian(i[x * 2][y][z], i[x * 2 + 1][y][z]);
                    }
                }
            }
        } else if (interleavedDim == 1) {
            c = new Complex[w][h / 2][d];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h / 2; y++) {
                    for (int z = 0; z < d; z++) {
                        c[x][y][z] = Complex.ofCartesian(i[x][y * 2][z], i[x][y * 2 + 1][z]);
                    }
                }
            }
        } else {
            c = new Complex[w][h][d / 2];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d / 2; z++) {
                        c[x][y][z] = Complex.ofCartesian(i[x][y][z * 2], i[x][y][z * 2 + 1]);
                    }
                }
            }
        }
        return c;
    }









    public static Complex[][][] interleaved2Complex(float[][][] d) {
        return interleaved2Complex(d, 2);
    }











    public static Complex[] split2Complex(double[] real, double[] imag) {
        final int length = real.length;
        final Complex[] c = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = Complex.ofCartesian(real[n], imag[n]);
        }
        return c;
    }









    public static Complex[][] split2Complex(double[][] real, double[][] imag) {
        final int length = real.length;
        final Complex[][] c = new Complex[length][];
        for (int x = 0; x < length; x++) {
            c[x] = split2Complex(real[x], imag[x]);
        }
        return c;
    }









    public static Complex[][][] split2Complex(double[][][] real, double[][][] imag) {
        final int length = real.length;
        final Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < length; x++) {
            c[x] = split2Complex(real[x], imag[x]);
        }
        return c;
    }









    public static Complex[][][][] split2Complex(double[][][][] real, double[][][][] imag) {
        final int length = real.length;
        final Complex[][][][] c = new Complex[length][][][];
        for (int x = 0; x < length; x++) {
            c[x] = split2Complex(real[x], imag[x]);
        }
        return c;
    }









    public static Complex[] split2Complex(float[] real, float[] imag) {
        final int length = real.length;
        final Complex[] c = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = Complex.ofCartesian(real[n], imag[n]);
        }
        return c;
    }









    public static Complex[][] split2Complex(float[][] real, float[][] imag) {
        final int length = real.length;
        final Complex[][] c = new Complex[length][];
        for (int x = 0; x < length; x++) {
            c[x] = split2Complex(real[x], imag[x]);
        }
        return c;
    }









    public static Complex[][][] split2Complex(float[][][] real, float[][][] imag) {
        final int length = real.length;
        final Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < length; x++) {
            c[x] = split2Complex(real[x], imag[x]);
        }
        return c;
    }










    public static Complex[] initialize(Complex[] c) {
        final int length = c.length;
        for (int x = 0; x < length; x++) {
            c[x] = Complex.ZERO;
        }
        return c;
    }








    public static Complex[][] initialize(Complex[][] c) {
        final int length = c.length;
        for (int x = 0; x < length; x++) {
            c[x] = initialize(c[x]);
        }
        return c;
    }








    public static Complex[][][] initialize(Complex[][][] c) {
        final int length = c.length;
        for (int x = 0; x < length; x++) {
            c[x] = initialize(c[x]);
        }
        return c;
    }








    public static double[] abs(Complex[] c) {
        final int length = c.length;
        final double[] i = new double[length];
        for (int x = 0; x < length; x++) {
            i[x] = c[x].abs();
        }
        return i;
    }








    public static double[] arg(Complex[] c) {
        final int length = c.length;
        final double[] i = new double[length];
        for (int x = 0; x < length; x++) {
            i[x] = c[x].arg();
        }
        return i;
    }




    private static class NegativeModulusException extends IllegalArgumentException {

        private static final long serialVersionUID = 20181205L;




        NegativeModulusException(double r) {
            super("Modulus is negative: " + r);
        }
    }




    private static class IndexOutOfRangeException extends IllegalArgumentException {

        private static final long serialVersionUID = 20181205L;




        IndexOutOfRangeException(int i) {
            super("Out of range: " + i);
        }
    }
}
