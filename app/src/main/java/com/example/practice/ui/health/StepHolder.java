package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.practice.R;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class StepHolder extends HealthHolder {
    private static final int type = 0;
    Context mContext;

    private HealthDataStore mStore;
    private View mView;

    private TextView mStepCount;
    private TextView mStepGoal;
    private TextView mStepPercent;
    private ProgressBar mStepProgress;
    private BarChart mStepBarChart;
    private ConstraintLayout mTimeProgress;
    private TextView mTime0;
    private TextView mTime12;
    private TextView mTime24;
    private ImageButton mEditStepGoal;

    public StepHolder(HealthDataStore store, Context context) {
        mStore = store;
        mContext = context;
    }

    public final String PREFERENCE = "com.example.practice.samplesharedpreferece";

    private String STEPDATA;

    @RequiresApi(api = Build.VERSION_CODES.O)
    void show(View view) {
        mStepCount = (TextView) view.findViewById(R.id.stepCount);
        mStepGoal = (TextView) view.findViewById(R.id.stepGoal);
        mStepPercent = (TextView) view.findViewById(R.id.stepPercent);
        mStepProgress = (ProgressBar) view.findViewById(R.id.stepProgress);
        mStepBarChart = (BarChart) view.findViewById(R.id.stepBarChart);
        mTimeProgress = (ConstraintLayout) view.findViewById(R.id.timeProgress);
        mTime0 = (TextView) view.findViewById(R.id.time0);
        mTime12 = (TextView) view.findViewById(R.id.time12);
        mTime24 = (TextView) view.findViewById(R.id.time24);
        mEditStepGoal = (ImageButton) view.findViewById(R.id.edit_stepGoal);

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

        mEditStepGoal.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final EditText edittext = new EditText(view.getContext());

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Set step goal");
                builder.setMessage("Please enter the step number");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                STEPDATA = edittext.getText().toString();
                                mStepGoal.setText("/ "+STEPDATA+" steps");
                                setPREFERENCE("step_data", STEPDATA);
                                updateGoal();
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });
    }

    public void setPREFERENCE(String key, String value){
        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPREFERENCE(String key){
        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        return pref.getString(key, "");
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

                        String get_STEPDATA = getPREFERENCE("step_data");

                        int goal_step_pref = 6000;
                        if (get_STEPDATA != null && get_STEPDATA.length() > 0 )
                            goal_step_pref = Integer.parseInt(get_STEPDATA);
                        if (goal_step_pref == 0)
                            goal_step_pref = 6000;

                        mStepPercent.setText((totalStep * 100) / goal_step_pref + "%");
                        mStepCount.setText(totalStep + "");
                        mStepProgress.setProgress((totalStep * 100) / goal_step_pref == 0 ? 1 : (totalStep * 100) / goal_step_pref);
                        mStepGoal.setText("/ "+get_STEPDATA+" steps");

                        mStepBarChart.clearChart();
                        for (int i = 0; i < 12; i++) {
                            mStepBarChart.addBar(new BarModel("", (float) stepTime[i], 0xFFF7C744));
                        }
                        mStepBarChart.startAnimation();

                        float biasedValue = ((float) (Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
                                .getTimeInMillis() - todayStart) / ONE_DAY_IN_MILLIS);
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTimeProgress.getLayoutParams();
                        params.horizontalBias = biasedValue;
                        mTimeProgress.setLayoutParams(params);

                        if (biasedValue < 0.25) {
                            mTime0.setVisibility(View.INVISIBLE);
                            mTime12.setVisibility(View.VISIBLE);
                            mTime24.setVisibility(View.VISIBLE);
                        } else if (biasedValue > 0.75) {
                            mTime0.setVisibility(View.VISIBLE);
                            mTime12.setVisibility(View.VISIBLE);
                            mTime24.setVisibility(View.INVISIBLE);
                        } else {
                            mTime0.setVisibility(View.VISIBLE);
                            mTime12.setVisibility(View.INVISIBLE);
                            mTime24.setVisibility(View.VISIBLE);
                        }
                    }
                }
            };

    private void updateGoal() {
        String get_STEPDATA = getPREFERENCE("step_data");

        int goal_step_pref = 6000;
        if (get_STEPDATA != null)
            goal_step_pref = Integer.parseInt(get_STEPDATA);
        if (goal_step_pref == 0)
            goal_step_pref = 6000;

        int totalStep = Integer.parseInt((String) mStepCount.getText());

        mStepPercent.setText((totalStep * 100) / goal_step_pref + "%");
        mStepProgress.setProgress((totalStep * 100) / goal_step_pref == 0 ? 1 : (totalStep * 100) / goal_step_pref);
        mStepGoal.setText("/ "+get_STEPDATA+" steps");
    }

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