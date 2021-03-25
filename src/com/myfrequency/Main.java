package com.myfrequency;

import org.jtransforms.fft.FloatFFT_1D;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.*;
import java.nio.ByteBuffer;

public class Main {

    private static byte[] loadSoundFile() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream("Splish_Splash.wav"));

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0)
        {
            out.write(buff, 0, read);
        }
        out.flush();
        return out.toByteArray();
    }



    public static void main(String[] args) throws IOException, LineUnavailableException {
        //byte[] sound = loadSoundFile();
        //FloatFFT_1D fft = new FloatFFT_1D(longSound.length);

        CaptureAudio captureAudio = new CaptureAudio();
        captureAudio.captureAudio();
    }
}
