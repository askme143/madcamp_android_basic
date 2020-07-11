package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.example.practice.R;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class DailyStepHolder extends HealthHolder {
    private static final int type = 2;

    private HealthDataStore mStore;
    private HealthDataResolver mHealthDataResolver;

    public DailyStepHolder(HealthDataStore store) {
        mStore = store;
        mHealthDataResolver = new HealthDataResolver(mStore, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    void show(View view) {
        HealthDataResolver.Filter filter = HealthDataResolver.Filter.and(
                HealthDataResolver.Filter.eq("day_time", getStartTimeOfToday()),
                HealthDataResolver.Filter.eq("source_type", -2)
        );

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType("com.samsung.shealth.step_daily_trend")
                .setFilter(filter)
                .build();

        try {
            mHealthDataResolver.read(request).setResultListener(mRdResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mRdResult =
            new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
                @SuppressLint("SetTextI18n")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResult(HealthDataResolver.ReadResult healthData) {
                    try {
                        for (HealthData data : healthData) {
                            long dayTime = data.getLong("day_time");
                            long totalCount = data.getLong("count");

                            System.out.println(dayTime + " " + totalCount);
                        }
                    } finally {
                        healthData.close();
                    }
                }
            };

    @Override
    int getType() {
        return type;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long getStartTimeOfToday() {
        return LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
    }
}
