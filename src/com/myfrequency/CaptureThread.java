package com.myfrequency;

import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;

class CaptureThread extends Thread{
    //buffer for sound samples
    byte[] buffer = new byte[10000];
    private final TargetDataLine targetDataLine;

    public CaptureThread(TargetDataLine _targetDataLine) {
        targetDataLine = _targetDataLine;
    }

    public void run() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try{
            while(true) {
                int length = targetDataLine.read(buffer, 0, buffer.length);
                if(length > 0){
                    output.write(buffer, 0, length);
                }
            }
            //byteArrayOutputStream.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
