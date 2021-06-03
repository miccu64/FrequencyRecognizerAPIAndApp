package com.myfrequency.models;

public class FreqMagnModel {
    private final float frequency;
    private final int magnitude;
    private final boolean isPhone;

    public boolean getIsPhone() {
        return isPhone;
    }

    public FreqMagnModel(float freq, int magn, boolean fromPhone) {
        frequency = freq;
        magnitude = magn;
        isPhone = fromPhone;
    }

    public float getFrequency() {
        return frequency;
    }

    public int getMagnitude() {
        return magnitude;
    }
}
