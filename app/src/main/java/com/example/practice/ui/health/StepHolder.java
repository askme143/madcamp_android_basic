package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.practice.R;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class StepHolder extends HealthHolder {
    private static final int type = 0;

    private HealthDataStore mStore;
    private View mView;

    private TextView mStepCount;
    private TextView mStepGoal;
    private TextView mStepPercent;
    private ProgressBar mStepProgress;
    private BarChart mStepBarChart;

    public StepHolder(HealthDataStore store) {
        mStore = store;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void show(View view) {
        mStepCount = (TextView) view.findViewById(R.id.stepCount);
        mStepGoal = (TextView) view.findViewById(R.id.stepGoal);
        mStepPercent = (TextView) view.findViewById(R.id.stepPercent);
        mStepProgress = (ProgressBar) view.findViewById(R.id.stepProgress);
        mStepBarChart = (BarChart) view.findViewById(R.id.stepBarChart);

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

    int getType() {
        return type;
    }

    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mRdResult =
            new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
                @SuppressLint("SetTextI18n")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResult(HealthDataResolver.ReadResult healthData) {
                    int totalStep = 0;
                    long todayStart = getStartTimeOfTodaySeoul();
                    int[] stepTime = new int[12];

                    try {
                        for (HealthData data : healthData) {
                            int index = (int) ((data.getLong(HealthConstants.StepCount.START_TIME)
                                    - todayStart) / 60 / 60 / 1000 - 1) / 2;
                            int step = data.getInt(HealthConstants.StepCount.COUNT);

                            stepTime[index] += step;
                            totalStep += step;
                        }
                    } finally {
                        healthData.close();
                        mStepPercent.setText(totalStep / 60 + "%");
                        mStepCount.setText(totalStep + "");
                        mStepProgress.setProgress(totalStep / 60 == 0 ? 1 : totalStep / 60);
                        mStepBarChart.clearChart();

                        for (int i = 0; i < 12; i++) {
                            mStepBarChart.addBar(new BarModel("", (float) stepTime[i], 0xFFF7C744));
                        }

                        mStepBarChart.startAnimation();
                    }
                }
            };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long getStartTimeOfToday() {
        return LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long getStartTimeOfTodaySeoul() {
        return LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.of("+09:00")) * 1000;
    }

    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;
}