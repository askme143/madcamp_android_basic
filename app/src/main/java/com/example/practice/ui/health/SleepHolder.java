package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.practice.R;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class SleepHolder extends HealthHolder {
    private static final int type = 1;
    private Context mContext;

    private int mSleepTimeGoal;
    private int mStartOffset;

    private HealthDataStore mStore;

    private TextView mTextHour;
    private TextView mTextMin;
    private ArrayList<View> mSleepTimeBarList;

    float dp;

    public SleepHolder(HealthDataStore store, Context context) {
        mStore = store;
        mContext = context;

        int startHour = 23;
        int startMin = 0;
        int endHour = 7;
        int endMin = 0;


        int sleepGoal = (startHour * 60 + startMin) * (60 * 1000);
        int wakeUpGoal = (endHour * 60 + endMin) * (60 * 1000);

        if (sleepGoal > wakeUpGoal)
            sleepGoal -= ONE_DAY_IN_MILLIS;

        mSleepTimeGoal = wakeUpGoal - sleepGoal;

        mStartOffset = sleepGoal - mSleepTimeGoal / 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void show(View view) {
        dp = view.getContext().getResources().getDisplayMetrics().density;
        mTextHour = (TextView) view.findViewById(R.id.sleepHour);
        mTextMin = (TextView) view.findViewById(R.id.sleepMin);
        mSleepTimeBarList = new ArrayList<>();

        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar0));
        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar1));
        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar2));
        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar3));
        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar4));

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        long startTime = getStartTimeSeoul() - 5 * ONE_DAY_IN_MILLIS;

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.Sleep.HEALTH_DATA_TYPE)
                .setFilter(HealthDataResolver.Filter.greaterThan(HealthConstants.Sleep.START_TIME, startTime))
                .setSort(HealthConstants.Sleep.START_TIME, HealthDataResolver.SortOrder.DESC)
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
                    int[] startTimes = new int[5];
                    int[] endTimes = new int[5];
                    int[] sleepTimes = new int[5];
                    int count = 4;

                    try {
                        long dayStart = getStartTimeSeoul();
                        for (HealthData data : healthData) {
                            long startMilli = data.getLong(HealthConstants.Sleep.START_TIME);
                            long endMilli = data.getLong(HealthConstants.Sleep.END_TIME);

                            while (endMilli <= dayStart) {
                                dayStart -= ONE_DAY_IN_MILLIS;

                                if (--count < 0)
                                    break;
                            }
                            if (count < 0)
                                break;

                            int startTime = Math.max((int) (startMilli - dayStart - mStartOffset), 0);
                            int endTime = Math.min ((int) (endMilli - dayStart - mStartOffset), mSleepTimeGoal * 2);

                            startTimes[count] = startTime;
                            endTimes[count] = endTime;
                            sleepTimes[count] = (int) (endMilli - startMilli);

                            dayStart -= ONE_DAY_IN_MILLIS;
                            if (--count < 0)
                                break;
                        }
                    } finally {
                        healthData.close();
                        for (int i = 0; i < 5; i++) {
                            int sleepTimeInPeriod = endTimes[i] - startTimes[i];

                            System.out.println(sleepTimeInPeriod + " " + startTimes[i] + " " + endTimes[i] + " " + mSleepTimeGoal);

                            View sleepTimeBar = mSleepTimeBarList.get(i);

                            ViewGroup.LayoutParams params = sleepTimeBar.getLayoutParams();
                            params.height = (int) (sleepTimeInPeriod * dp / (mSleepTimeGoal * 2 / 120)) + 1;
                            sleepTimeBar.setLayoutParams(params);

                            System.out.println((int) (sleepTimeInPeriod * dp / (mSleepTimeGoal * 2 / 120)) + 1);
                            System.out.println((int) (startTimes[i] * dp / (mSleepTimeGoal * 2 / 120)));

                            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)
                                    sleepTimeBar.getLayoutParams();
                            marginParams.setMargins(0, (int) (startTimes[i] * dp / (mSleepTimeGoal * 2 / 120)), 0, 0);
                            sleepTimeBar.requestLayout();
                        }

                        int sleepTime = sleepTimes[4];
                        int hour =  sleepTime / 1000 / 60 / 60;
                        int minute = (sleepTime / 1000 / 60) % 60;

                        mTextHour.setText(hour+"");
                        mTextMin.setText(minute+"");
                    }
                }
            };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long getStartTimeSeoul() {
        return LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.of("+09:00")) * 1000;
    }

    private long getSleepStartTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));

        cal.add(Calendar.HOUR, -16 - 24*4);

        return cal.getTimeInMillis();
    }

    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;
}
