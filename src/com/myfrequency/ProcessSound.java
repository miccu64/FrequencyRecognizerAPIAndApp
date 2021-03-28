package com.myfrequency;

import javafx.util.Pair;
import org.jtransforms.fft.FloatFFT_1D;

import javax.sound.sampled.AudioFormat;

public class ProcessSound {
    FloatFFT_1D fft;
    AudioFormat format;
    static int len;

    public ProcessSound(AudioFormat _format) {
        format = _format;
        len = (int) format.getSampleRate();
        //init FFT with number of FFTsamples, which we want to get
        fft = new FloatFFT_1D((len / format.getFrameSize()));
    }

    void doProcessing(byte[] bufByte) {
        //convert to floats
        float[] bufFloat = ProcessSound.bytesToFloat(bufByte, format);

        //save FFT result to bufFloat
        //realForward, bcs we have got real data
        fft.realForward(bufFloat);

        Pair<float[], float[]> fftRealImag = getRealAndImag(bufFloat);
        double[] magnitudes = getMagnitudes(fftRealImag);

        //find max magnitude
        double max = 0;
        for (double m: magnitudes) {
            if (m > max) max = m;
        }
        System.out.println("aaa");
    }

    private static Pair<float[], float[]> getRealAndImag (float[] bufFloat) {
        //derive it to real and complex result
        float[] real = new float[len / 2];
        float[] imag = new float[len / 2 - 1];

        //pattern:
        //a[2*k] = Re[k], 0<=k<n/2
        //a[2*k+1] = Im[k], 0<k<n/2
        //a[1] = Re[n/2]
        real[0] = bufFloat[0];
        real[len / 2 - 1] = bufFloat[1];

        for (int pos = 2; pos < len; pos++) {
            if (pos % 2 == 0) {
                real[pos / 2] = bufFloat[pos];
            } else imag[pos / 2 - 1] = bufFloat[pos];
        }

        return new Pair<>(real, imag);
    }

    private static double[] getMagnitudes(Pair<float[], float[]> realImag) {
        float[] real = realImag.getKey();
        float[] imag = realImag.getValue();
        double[] magn = new double[real.length / 2];
        for (int i = 0; i < magn.length; i++) {
            magn[i] = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);
        }
        return magn;
    }


    public static float[] bytesToFloat(byte[] bytes, AudioFormat format) {
        int frameSize = format.getFrameSize();
        float[] res = new float[bytes.length / frameSize];
        for (int pos = 0; pos < bytes.length / frameSize; pos++) {
            //convert bytes to float (little endian)
            float sample = 0;
            for (int part = 0; part < frameSize; part++) {
                sample += bytes[pos * frameSize + part] << 8 * part;
            }
            res[pos] = sample;
        }
        return res;
    }
}