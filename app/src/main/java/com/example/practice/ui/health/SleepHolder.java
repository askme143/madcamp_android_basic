package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.practice.R;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.Calendar;
import java.util.TimeZone;

public class SleepHolder extends HealthHolder {
    private static final int type = 1;

    private HealthDataStore mStore;

    private TextView mTextView1;

    public SleepHolder(HealthDataStore store) {
        mStore = store;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void show(View view) {
        mTextView1 = (TextView) view.findViewById(R.id.sleepTime);

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        long startTime = getSleepStartTime();
        long endTime = startTime + ONE_DAY_IN_MILLIS;

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.Sleep.HEALTH_DATA_TYPE)
                .setLocalTimeRange(HealthConstants.StepCount.START_TIME, HealthConstants.StepCount.TIME_OFFSET,
                        startTime, endTime)
                .build();
        try {
            resolver.read(request).setResultListener(mRdResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int getType() { return type; }

    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mRdResult =
            new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
                @SuppressLint("SetTextI18n")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResult(HealthDataResolver.ReadResult healthData) {
                    int totalSleepTime = 0;

                    try {
                        for (HealthData data : healthData) {
                            long startMilli = data.getLong(HealthConstants.Sleep.START_TIME);
                            long endMilli = data.getLong(HealthConstants.Sleep.END_TIME);
                            int sleepTime = (int) (endMilli - startMilli);

                            totalSleepTime += sleepTime;
                        }
                    } finally {
                        healthData.close();
                        int hour = totalSleepTime / 1000 / 60 / 60;
                        int minute = (totalSleepTime / 1000 / 60) % 60;

                        mTextView1.setText(hour+"hour "+minute+"minute");
                    }
                }
            };

    private long getSleepStartTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        cal.add(Calendar.HOUR, -16);

        return cal.getTimeInMillis();
    }

    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;
}
