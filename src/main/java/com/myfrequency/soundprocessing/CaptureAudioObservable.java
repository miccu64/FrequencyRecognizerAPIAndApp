package com.myfrequency.soundprocessing;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.Observable;

import static java.lang.Thread.sleep;


public class CaptureAudioObservable extends Observable {

    private final AudioFormat audioFormat;
    private float frequency;
    private int magnitude;

    public float getFrequency() {
        return frequency;
    }

    public int getMagnitude() {
        return magnitude;
    }

    private void setFrequencyAndMagnitude(float _freq, int _magn) {
        //notify observers about change in frequency
        frequency = _freq;
        magnitude = _magn;
        setChanged();
        notifyObservers();
    }

    public CaptureAudioObservable() {
        //set up capture parameters
        float sampleRate = 44100.0F;//8000,16000,22050,44100 - only even numbers
        int sampleSizeInBits = 16;//8,16
        int channels = 1;//only 1 will work
        boolean signed = true;//works for me only with true
        boolean bigEndian = false;//false - the script is written for little endian
        audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    private TargetDataLine initDataLine() {
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            return targetDataLine;
        } catch (Exception e) {
            try {
                sleep(300);
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    public void captureAudio() {
        //if missing input, wait for add new one by user
        TargetDataLine targetDataLine;
        do {
            targetDataLine = initDataLine();
        } while (targetDataLine == null);
        targetDataLine.start();

        //buffer for sound samples - DERIVED BY 5 to get more frequent changes
        int len = (int) audioFormat.getSampleRate() * audioFormat.getFrameSize() / 5;

        byte[] bufByte = new byte[len];
        ProcessSound process = new ProcessSound(audioFormat);

        while (true) {
            //read data form input
            int length = targetDataLine.read(bufByte, 0, len);
            if (length > 0) {
                process.doProcessing(bufByte);
                float newFreq = process.getFrequency();
                int newMagn = process.getMaxMagnitude();
                //notify observers if found new frequency with big magnitude
                setFrequencyAndMagnitude(newFreq, newMagn);
            }
        }
    }
}