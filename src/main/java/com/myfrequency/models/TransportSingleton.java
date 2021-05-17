package com.myfrequency.models;

public class TransportSingleton {
    public FreqMagnModel model = new FreqMagnModel(0,0);
    //show count of connected users
    public int usersCount = 0;
    public ResultModel resultModel = new ResultModel(0, false);

    private TransportSingleton() { }

    public static TransportSingleton getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final TransportSingleton instance = new TransportSingleton();
    }
}
