package com.myfrequency;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class Main extends JFrame implements Observer {
    //instance for audio capture
    private static CaptureAudioObservable audio;
    private JPanel panel1;
    private JLabel freqLabel;
    private JTable keyTable;
    private JButton addButton;
    private JButton deleteButton;
    private JButton startButton;
    private JButton stopButton;
    private float frequency = 0;
    private boolean work;
    //model for changing data from GUI
    private DefaultTableModel model;

    //init GUI
    public Main() {
        JFrame frame = new JFrame("Graj przez częstotliwość");
        freqLabel.setFont(new Font(freqLabel.getFont().getName(), Font.PLAIN, 26));
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(500, 500));
        frame.pack();
        frame.setVisible(true);

        //adding new rows and deleting via buttons
        addButton.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) keyTable.getModel();
            model.addRow(new Object[] {"a", 0});
        });
        deleteButton.addActionListener(e -> {
            int[] rows = keyTable.getSelectedRows();
            for(int i=0; i<rows.length; i++) {
                model.removeRow(rows[i] - i);
            }
        });
        startButton.addActionListener(e -> {
            work = true;
        });
        stopButton.addActionListener(e -> {
            work = false;
        });
    }

    private boolean compareFreq(double freq1, double freq2) {
        double res = 110;
        double root = Math.pow(2, (double) 1/12);//ZZACZYNA SIE OD A (110Hz)
        //dzieli sie lub mnozy x razy przez to
        return false;
    }

    //perform operations on frequency change
    @Override
    public void update(Observable o, Object arg) {
        audio = (CaptureAudioObservable) o;
        float newFrequency = audio.getFrequency();
        freqLabel.setText("" + newFrequency);
        //3000Hz will be max
        if (work && frequency == newFrequency && frequency < 3000) {
            DefaultTableModel model = (DefaultTableModel) keyTable.getModel();
            Vector vector = model.getDataVector();
            for (Object obj : vector) {

            }
        }
        frequency = newFrequency;
    }

    public static void main(String[] args) {
        //make observer for notifying when frequency is changed
        audio = new CaptureAudioObservable();
        //GUI open
        Main observer = new Main();
        audio.addObserver(observer);
        audio.captureAudio();
    }

    private void createUIComponents() {
        Object[][] listData = new Object[][]{
                {"A", 500},
                {"B", 600}
        };
        String[] colNames = {"Przycisk", "Częstotliwość"};
        model = new DefaultTableModel(listData, colNames);
        keyTable = new JTable(model);
    }
}
