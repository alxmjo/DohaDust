package com.alexmontjohn.dohadust;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CurrentData {
    private int mConcentration;
    private Date mTime;

    public int getConcentration() {
        return mConcentration;
    }

    public void setConcentration(String concentration) {
        double d = Double.parseDouble(concentration);
        int i = (int) d;

        mConcentration = i;
    }

    public Date getTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(mTime);
        cal.add(Calendar.HOUR, +3);
        mTime = cal.getTime();
        return mTime;
    }

    public void setTime(String time) throws ParseException {
        String input = time;
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'", Locale.ENGLISH).parse(input);

        mTime = date;
    }
}
