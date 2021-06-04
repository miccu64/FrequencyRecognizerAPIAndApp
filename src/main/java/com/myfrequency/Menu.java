package com.myfrequency;

import com.myfrequency.models.FreqMagnModel;
import com.myfrequency.models.ResultModel;
import com.myfrequency.soundprocessing.CaptureAudioObservable;
import com.myfrequency.websocket.WebSocketController;
import com.myfrequency.websocket.WebSocketEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

@SpringBootApplication
public class Menu extends JFrame implements Observer {
    //instance for audio capture
    private static CaptureAudioObservable audio;

    private JPanel panel1;
    private JLabel freqLabel;
    private JTable keyTable;
    private JButton addButton;
    private JButton deleteButton;
    private JButton startButton;
    private JButton stopButton;
    private JLabel magnLabel;
    private JButton sourceButton;
    private JLabel stateLabel;
    private JLabel strongLabel;
    private JLabel foundFreqLabel;
    private JSpinner magnSpinner;

    private float frequency = 0;
    private int magnitude = 0;
    private boolean work;
    //model for changing data from GUI
    private DefaultTableModel model;
    //contains possible sounds in Hz
    private List<Float> soundList;
    //for key pressing
    private Robot robot;
    private int pressedKey = 0;
    private boolean fromPhone;
    private int minMagn = 3;

    //for getting callback when get info from phone
    @Autowired
    WebSocketController webSocket;
    @Autowired
    WebSocketEventListener listener;

    //init GUI
    public Menu() {
        JFrame frame = new JFrame("Graj przez częstotliwość");
        freqLabel.setFont(new Font(freqLabel.getFont().getName(), Font.PLAIN, 26));
        magnLabel.setFont(new Font(freqLabel.getFont().getName(), Font.PLAIN, 16));
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(500, 500));
        frame.pack();
        frame.setVisible(true);

        //adding new rows and deleting via buttons
        addButton.addActionListener(e -> {
            model.addRow(new Object[]{"Przycisk", (float) 0.});
        });
        deleteButton.addActionListener(e -> {
            int[] rows = keyTable.getSelectedRows();
            for (int i = 0; i < rows.length; i++) {
                model.removeRow(rows[i] - i);
            }
        });
        startButton.addActionListener(e -> {
            work = true;
            stateLabel.setText("Status: działa");
        });
        stopButton.addActionListener(e -> {
            work = false;
            stateLabel.setText("Status: zatrzymany");
        });
        sourceButton.addActionListener(e -> {
            if (!fromPhone) {
                if (listener.getUsersCount() < 1) {
                    sourceButton.setText("Brak połączenia");
                } else {
                    webSocket.addObserver(this);
                    fromPhone = true;
                    sourceButton.setText("Źródło: telefon");
                }
            } else {
                fromPhone = false;
                sourceButton.setText("Źródło: komputer");
            }
        });
        magnSpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            minMagn = (int) spinner.getValue();
        });

        try {
            robot = new Robot();
        } catch (Exception ignored) {
        }

        audio.addObserver(this);
    }

    private float findNearestNumber(float num) {
        float curr = soundList.get(0);
        for (float f : soundList) {
            if (Math.abs(num - f) < Math.abs(num - curr))
                curr = f;
        }
        return curr;
    }

    //perform operations on frequency change
    @Override
    public void update(Observable o, Object arg) {
        FreqMagnModel freqMagnModel = (FreqMagnModel) arg;

        //if invoked by not used source - ignore result
        if (freqMagnModel == null || freqMagnModel.getIsPhone() != fromPhone) {
            return;
        }

        float newFrequency = freqMagnModel.getFrequency();
        int newMagnitude = freqMagnModel.getMagnitude();

        //find closest value corresponding to particular fret on guitar
        float nearestNewFreq = findNearestNumber(newFrequency);
        int compare = Float.compare(nearestNewFreq, frequency);

        int strongEnough = Float.compare(newMagnitude, minMagn);
        if (strongEnough >= 0)
            strongLabel.setText("tak");
        else strongLabel.setText("nie");
        freqLabel.setText("" + newFrequency);
        magnLabel.setText("" + newMagnitude);
        foundFreqLabel.setText("" + nearestNewFreq);

        //send response to phone
        if (freqMagnModel.getIsPhone()) {
            ResultModel result;
            if (Float.compare(newMagnitude, minMagn) >= 0)
                result = new ResultModel(nearestNewFreq, true);
            else result = new ResultModel(nearestNewFreq, false);

            WebSocketController w = (WebSocketController) o;
            w.sendResult(result);
        }

        if (work && newFrequency < 2000 && newFrequency > 48 && strongEnough >= 0 &&
                Float.compare(magnitude, minMagn) >= 0 && compare == 0) {
            //find corresponding key in table and press it
            for (int i = 0; i < model.getRowCount(); i++) {
                Float freqFromTable = (Float) model.getValueAt(i, 1);
                if (Float.compare(freqFromTable, nearestNewFreq) == 0) {
                    String key = (String) model.getValueAt(i, 0);

                    //if it's ASCII char, press corresponding button
                    if (key.length() == 1) {
                        pressedKey = key.charAt(0);
                        robot.keyPress(pressedKey);
                    } else {
                        Point current = MouseInfo.getPointerInfo().getLocation();
                        switch (key) {
                            case "LPM":
                                robot.mousePress(InputEvent.BUTTON1_MASK);
                                break;
                            case "PPM":
                                robot.mousePress(InputEvent.BUTTON2_MASK);
                                break;
                            case "Spacja":
                                robot.mousePress(KeyEvent.VK_SPACE);
                                break;
                            case "MyszLewo":
                                robot.mouseMove(current.x - 100, current.y);
                                break;
                            case "MyszPrawo":
                                robot.mouseMove(current.x + 100, current.y);
                                break;
                            case "MyszDol":
                                robot.mouseMove(current.x, current.y  - 100);
                                break;
                            case "MyszGora":
                                robot.mouseMove(current.x, current.y + 100);
                                break;
                        }
                    }
                    break;
                }
            }
        } else try {
            //needed try to release keys, bcs pressedKey contains mouse or key id
            robot.keyRelease(pressedKey);
            robot.mouseRelease(pressedKey);
        } catch (Exception ignored) { }

        //overwrite with newest values
        frequency = nearestNewFreq;
        magnitude = newMagnitude;
    }

    public static void main(String[] args) {
        //make observer for notifying when frequency is changed
        audio = new CaptureAudioObservable();

        //make thread for allow working mic capture and Spring at the same time
        Thread thread = new Thread(() -> audio.captureAudio());
        thread.start();

        SpringApplication.run(Menu.class, args);
    }

    private float round(float num) {
        int der = (int) Math.pow(10, 1);
        num = num * der;
        num = Math.round(num);
        return num / der;
    }

    private void createUIComponents() {
        //init soundSet - start from 110Hz and derive/multiply it by mult (special equation)
        soundList = new ArrayList<>();
        float initial = 110;
        soundList.add(initial);
        float mult = (float) Math.pow(2, (double) 1 / 12);
        for (int i = 0; i < 15; i++) {
            initial /= mult;
            soundList.add(round(initial));
        }
        initial = 110;
        for (int i = 0; i < 52; i++) {
            initial *= mult;
            soundList.add(round(initial));
        }
        //sort the list
        Collections.sort(soundList);

        Object[][] listData = new Object[][]{
                {"A", (float) 146.8},
                {"D", (float) 155.6},
                {"W", (float) 164.8},
                {"S", (float) 174.6},
                {"MyszLewo", (float) 185.0},
                {"MyszPrawo", (float) 196.0},
                {"MyszGora", (float) 207.7},
                {"MyszDol", (float) 220.0}
        };
        String[] colNames = {"Przycisk", "Częstotliwość"};
        model = new DefaultTableModel(listData, colNames);
        //allow only to add freqs from list
        JComboBox comboBox = new JComboBox();
        for (Float f : soundList) {
            comboBox.addItem(f);
        }
        keyTable = new JTable(model);
        //set freqs for choice in GUI
        TableColumn column = keyTable.getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(comboBox));

        //set keys for choice in the GUI
        column = keyTable.getColumnModel().getColumn(0);
        comboBox = new JComboBox();
        List<String> strList = new ArrayList<>(
                Arrays.asList("LPM", "PPM", "Spacja", "MyszLewo", "MyszPrawo", "MyszDol", "MyszGora")
        );
        for (String s : strList) {
            comboBox.addItem(s);
        }
        for (int ascii = 65; ascii <= 90; ascii++) {
            char c = (char) ascii;
            comboBox.addItem(c);
        }
        column.setCellEditor(new DefaultCellEditor(comboBox));

        SpinnerNumberModel model = new SpinnerNumberModel(2, 0, 111, 1);
        magnSpinner = new JSpinner(model);
        //add(spinner);
    }
}
