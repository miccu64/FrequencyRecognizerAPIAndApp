package com.myfrequency;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class Main extends JFrame implements Observer {
    private static CaptureAudioObservable audio;
    private JPanel panel1;
    private JLabel freqLabel;
    private JTextField textField1;
    private float frequency = 0;

    public Main() {
        JFrame frame = new JFrame("Frequency recognizer");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        audio = (CaptureAudioObservable) o;
        frequency = audio.getFrequency();
        freqLabel.setText("" + frequency);
    }


    public static void main(String[] args) {
        //GUI open
        new Main();

        //make observer for notifying when frequency is changed
        audio = new CaptureAudioObservable();
        Main observer = new Main();
        audio.addObserver(observer);
        audio.captureAudio();
    }
}
