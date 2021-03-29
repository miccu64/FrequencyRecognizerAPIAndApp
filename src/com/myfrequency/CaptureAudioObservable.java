package com.myfrequency;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.Observable;


public class CaptureAudioObservable extends Observable {

    private final AudioFormat audioFormat;
    private float frequency;

    public float getFrequency() {
        return frequency;
    }

    private void setFrequency(float _freq) {
        frequency = _freq;
        setChanged();
        notifyObservers();
    }

    public CaptureAudioObservable () {
        //set up capture parameters
        float sampleRate = 44100.0F;//8000,16000,22050,44100 - only even numbers
        int sampleSizeInBits = 16;//8,16
        int channels = 1;//only 1 will work
        boolean signed = true;//true,false
        boolean bigEndian = false;//false - the script is written for little endian
        audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void captureAudio() {
        try{
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat);
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            //buffer for sound samples
            int len = (int) audioFormat.getSampleRate() * audioFormat.getFrameSize();
            byte[] bufByte = new byte[len];

            ProcessSound process = new ProcessSound(audioFormat);
            try {
                while (true) {
                    int length = targetDataLine.read(bufByte, 0, len);
                    if (length > 0) {
                        setFrequency(process.doProcessing(bufByte));
                    }
                }
                //byteArrayOutputStream.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }


}


