package com.myfrequency;

import org.jtransforms.fft.FloatFFT_1D;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.*;
import java.nio.ByteBuffer;

public class Main {

    public static void main(String[] args) throws IOException, LineUnavailableException {
        //byte[] sound = loadSoundFile();
        //FloatFFT_1D fft = new FloatFFT_1D(longSound.length);

        CaptureAudio captureAudio = new CaptureAudio();
        captureAudio.captureAudio();
    }
}
