package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.practice.R;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class StepHolder {
    private HealthDataStore mStore;
    private View mView;

    private TextView mTextView1;

    public StepHolder(HealthDataStore store, View view) {
        mStore = store;
        mView = view;

        mTextView1 = view.findViewById(R.id.stepCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void show() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        long startTime = getStartTimeOfToday();
        long endTime = startTime + ONE_DAY_IN_MILLIS;

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                .setLocalTimeRange(HealthConstants.StepCount.START_TIME, HealthConstants.StepCount.TIME_OFFSET,
                        startTime, endTime)
                .build();
        try {
            resolver.read(request).setResultListener(mRdResult);
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
                    int count = 0;

                    try {
                        for (HealthData data : healthData) {
                            count += data.getInt(HealthConstants.StepCount.COUNT);
                        }
                    } finally {
                        healthData.close();
                        mTextView1.setText(Integer.toString(count));
                    }
                }
            };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long getStartTimeOfToday() {
        return LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;
}
