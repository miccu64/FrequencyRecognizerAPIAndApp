package com.myfrequency;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;
import java.util.List;

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
    //contains possible sounds in Hz
    private List<Float> soundList;

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
        //fast compare for big difference to improve optimization
        if (Math.abs(freq1-freq2) > 150)
            return false;
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
        if (work && newFrequency < 2000 && newFrequency > 50) {
            for (int i=0; i<soundList.size(); i++) {
                if (soundList.get(i).compareTo(newFrequency) < 0) {
                    float diffDown = (soundList.get(i) - soundList.get(i-1)) / 2;
                    float diffUp = (soundList.get(i+1) - soundList.get(i)) / 2;


                }
            }

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

    private float round(float num) {
        int der = (int) Math.pow(10, 1);
        num = num * der;
        num = Math.round(num);
        return num / der;
    }

    private void createUIComponents() {
        //init soundSet - start from 110Hz and derive/multiply it by mult
        soundList = new ArrayList<>();
        float initial = 110;
        soundList.add(initial);
        float mult = (float) Math.pow(2, (double) 1/12);
        for (int i=0; i<15; i++) {
            initial /= mult;
            soundList.add(round(initial));
        }
        initial = 110;
        for (int i=0; i<52; i++) {
            initial *= mult;
            soundList.add(round(initial));
        }
        //sort the list
        Collections.sort(soundList);

        Object[][] listData = new Object[][]{

        };
        String[] colNames = {"Przycisk", "Częstotliwość"};
        model = new DefaultTableModel(listData, colNames);
        //allow only to add freqs from list
        JComboBox comboBox = new JComboBox();
        for (Float f : soundList) {
            comboBox.addItem(f);
        }
        keyTable = new JTable(model);
        TableColumn column = keyTable.getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(comboBox));
    }
}
