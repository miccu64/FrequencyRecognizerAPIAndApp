package com.myfrequency;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class CaptureAudio {
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;

    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;//8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;//8,16
        int channels = 1;//1,2
        boolean signed = true;//true,false
        boolean bigEndian = false;//true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void captureAudio() {
        try{
            //set up capture parameters
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            //thread to capture from mic
            Thread captureThread = new CaptureThread(targetDataLine);
            captureThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}


