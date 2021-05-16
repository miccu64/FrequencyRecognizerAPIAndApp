package com.myfrequency.soundprocessing;

import org.apache.commons.math3.complex.Complex;

public class MyFFT {
    private final int length;
    private final int fftLength;
    private final float[] realFFT;
    private final float[] imagFFT;

    public MyFFT(float[] buf) {
        length = buf.length;
        fftLength = length / 2;
        realFFT = new float[fftLength];
        imagFFT = new float[fftLength];
        //process(buf);
    }

    public void process(float[] buffer) {
        //using Cooley-Turkey algorithm
        //k<fftLength, bcs after length/2 we can get only mirror values from 1st part
        //so will work faster
        for (int k=0; k<fftLength; k++) {
            Complex ek = new Complex(0,0);
            Complex ok = new Complex(0,0);

            for (int m=0; m<(length/2-1);m++) {
                Complex comp = new Complex(0, -2 * Math.PI / ((double) length/2) * m * k);
                Complex expo = comp.exp();
                ek = ek.add(expo.multiply(buffer[2*m]));
                ok = ok.add(expo.multiply(buffer[2*m + 1]));
            }
            Complex comp = new Complex(0, -2 * Math.PI / ((double) length) * k);
            ok = ok.multiply(comp.exp());
            Complex res = ok.add(ek);

            realFFT[k] = (float) res.getReal();
            imagFFT[k] = (float) res.getImaginary();
        }
    }

    public double[] getMagnitudes() {
        double[] magn = new double[realFFT.length];
        for (int i = 0; i < magn.length-1; i++) {
            magn[i] = Math.sqrt(realFFT[i] * realFFT[i] + imagFFT[i] * imagFFT[i]);
        }
        return magn;
    }
}
