package com.example.practice.ui.health;

import android.annotation.SuppressLint;

import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;

import java.util.Calendar;
import java.util.TimeZone;

public class ExerciseData {
    private long startTimeMilli;
    private long durationMilli;
    private float calorie;
    private float distance;
    private int exerciseType;
    private Calendar mCalendar;

    public ExerciseData(HealthData data) {
        startTimeMilli = data.getLong(HealthConstants.Exercise.START_TIME);
        durationMilli = data.getLong(HealthConstants.Exercise.DURATION);
        calorie = data.getFloat(HealthConstants.Exercise.CALORIE);
        distance = data.getFloat(HealthConstants.Exercise.DISTANCE);
        exerciseType = data.getInt(HealthConstants.Exercise.EXERCISE_TYPE);

        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(startTimeMilli);
        mCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public long getStartTimeMilli() {
        return startTimeMilli;
    }

    public String getMonthString() {
        return (mCalendar.get(Calendar.MONTH) + 1) + "";
    }

    public String getDayString() {
        return (mCalendar.get(Calendar.DAY_OF_MONTH)) + "";
    }

    public String getHourString() {
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        return hour < 10 ? "0" + hour : hour + "";
    }

    public String getMinString() {
        int minute = mCalendar.get(Calendar.MINUTE);
        return minute < 10 ? "0" + minute : minute + "";
    }

    @SuppressLint("DefaultLocale")
    public String getCalorieString() {
        return String.format("%.2f", calorie);
    }
}
