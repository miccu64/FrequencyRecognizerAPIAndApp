package com.myfrequency.soundprocessing;

import com.myfrequency.models.FreqMagnModel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.Observable;

import static java.lang.Thread.sleep;


public class CaptureAudioObservable extends Observable {

    private final AudioFormat audioFormat;

    private void setFrequencyAndMagnitude(float freq, int magn) {
        //notify observers about change in frequency
        setChanged();
        notifyObservers(new FreqMagnModel(freq, magn, false));
    }

    public CaptureAudioObservable() {
        //set up capture parameters
        float sampleRate = 8000;//8000,16000,22050,44100 - only even numbers
        int sampleSizeInBits = 8;//8,16
        int channels = 1;//only 1 will work there
        boolean signed = false;//with false I can get good magnitude, but can't use bigger frequencies bcs of init mic errors
        boolean bigEndian = true;//script is written for big endian
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
        ProcessSound process = new ProcessSound(audioFormat, len);

        while (true) {
            //read data form input
            int length = targetDataLine.read(bufByte, 0, len);
            if (length > 0) {
                process.doProcessing(bufByte);
                float newFreq = process.getFrequency();
                int newMagn = process.getMaxMagnitude();
                //notify observers if found new frequency
                setFrequencyAndMagnitude(newFreq, newMagn);
            }
        }
    }
}
