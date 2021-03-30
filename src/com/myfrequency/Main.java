package com.myfrequency;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class Main extends JFrame implements Observer {
    private static CaptureAudioObservable audio;
    private JPanel panel1;
    private JLabel freqLabel;
    private JTable keyTable;
    private JButton addButton;
    private JButton deleteButton;
    private float frequency = 0;

    //init GUI
    public Main() {
        JFrame frame = new JFrame("Graj przez częstotliwość");
        freqLabel.setFont(new Font(freqLabel.getFont().getName(), Font.PLAIN, 26));
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        //adding new rows and deleting via buttons
        addButton.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) keyTable.getModel();
            model.addRow(new Object[] {"a", 0});
        });
        deleteButton.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) keyTable.getModel();
            int[] rows = keyTable.getSelectedRows();
            for(int i=0;i<rows.length;i++) {
                model.removeRow(rows[i] - i);
            }
        });
    }

    //perform operations on frequency change
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

    private void createUIComponents() {
        Object[][] listData = new Object[][]{
                {"A", 500},
                {"B", 600}
        };
        String[] colNames = {"Przycisk", "Częstotliwość"};
        DefaultTableModel model = new DefaultTableModel(listData, colNames);
        keyTable = new JTable(model);
    }
}
