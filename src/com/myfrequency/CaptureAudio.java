package com.myfrequency;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;



public class CaptureAudio {

    private TargetDataLine targetDataLine;
    private final AudioFormat audioFormat;

    public CaptureAudio () {
        //set up capture parameters
        float sampleRate = 8000.0F;//8000,11025,16000,22050,44100
        int sampleSizeInBits = 8;//8,16
        int channels = 1;//1,2
        boolean signed = true;//true,false
        boolean bigEndian = false;//false - the script is written for little endian
        audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void captureAudio() {
        try{
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            //buffer for sound samples
            int len = (int) audioFormat.getSampleRate();
            byte[] bufByte = new byte[len];

            ProcessSound process = new ProcessSound(audioFormat);
            try {
                while (true) {
                    int length = targetDataLine.read(bufByte, 0, len);
                    if (length > 0) {
                        process.doProcessing(bufByte);
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


