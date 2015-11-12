package com.alexmontjohn.dohadust;

public class CurrentData {
    private int mConcentration;
    private int mTime;

    public int getConcentration() {
        return mConcentration;
    }

    public void setConcentration(String concentration) {
        double d = Double.parseDouble(concentration);
        int i = (int) d;

        mConcentration = i;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
        mTime = time;
    }
}
