package com.example.domocha.diaryproject;

import java.util.Calendar;

/**
 * Created by DoMoCha on 2015-11-19.
 */
public class CurrentDate {
    private static CurrentDate uniqueInstance = null;
    private Calendar calendar = null;

    private CurrentDate() {
        calendar = Calendar.getInstance();
    }

    public static CurrentDate getInstance() {
        if(uniqueInstance == null) {
            uniqueInstance = new CurrentDate();
        }
        return uniqueInstance;
    }

    public int getCurrentYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getCurrentMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getCurrentDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
}
