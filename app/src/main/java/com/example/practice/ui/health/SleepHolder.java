package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

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

import static android.content.Context.MODE_PRIVATE;

public class SleepHolder extends HealthHolder {
    private static final int type = 1;
    private Context mContext;
    private View mView;

    private int mSleepTimeGoal;
    private int mStartOffset;

    private HealthDataStore mStore;

    private TextView mTextHour;
    private TextView mTextMin;
    private ArrayList<View> mSleepTimeBarList;
    private ImageButton mEditSleepGoal;
    private TextView mSleepStartGoal;
    private TextView mSleepFinishGoal;

    private long[] mStartTimes = new long[5];
    private long[] mEndTimes = new long[5];
    private long[] mDayStartTimes = new long[5];

    int startHour = 23;
    int startMin = 0;
    int endHour = 7;
    int endMin = 0;

    public final String PREFERENCE2 = "com.example.practice.samplesharedpreferece2";

    float dp;

    public SleepHolder(HealthDataStore store, Context context) {
        mStore = store;
        mContext = context;

        setGoal();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void show(View view) {
        mView = view;
        dp = view.getContext().getResources().getDisplayMetrics().density;
        mTextHour = (TextView) view.findViewById(R.id.sleepHour);
        mTextMin = (TextView) view.findViewById(R.id.sleepMin);
        mSleepTimeBarList = new ArrayList<>();
        mEditSleepGoal = (ImageButton) view.findViewById(R.id.edit_sleep);
        mSleepStartGoal = (TextView) view.findViewById(R.id.sleepStartGoal);
        mSleepFinishGoal = (TextView) view.findViewById(R.id.sleepFinishGoal);

        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar0));
        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar1));
        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar2));
        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar3));
        mSleepTimeBarList.add(view.findViewById(R.id.sleepBar4));

        mSleepStartGoal.setText(getPREFERENCE2("sleep_startGoal"));
        mSleepFinishGoal.setText(getPREFERENCE2("sleep_endGoal"));

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

        mEditSleepGoal.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(final View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        String sleep_endHour = Integer.toString(timePicker.getHour());
                        String sleep_endMin = Integer.toString(timePicker.getMinute());

                        setPREFERENCE2("sleep_endHour", sleep_endHour);
                        setPREFERENCE2("sleep_endMin", sleep_endMin);
                        setPREFERENCE2("sleep_endGoal", sleep_endHour+" : "+sleep_endMin);
                        mSleepFinishGoal.setText(getPREFERENCE2("sleep_endGoal"));

                        setGoal();
                        drawSleepTimeBar();
                    }
                }, 0, 0, true);
                timePickerDialog.show();
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        String sleep_startHour = Integer.toString(timePicker.getHour());
                        String sleep_startMin = Integer.toString(timePicker.getMinute());

                        setPREFERENCE2("sleep_startHour", sleep_startHour);
                        setPREFERENCE2("sleep_startMin", sleep_startMin);
                        setPREFERENCE2("sleep_startGoal", sleep_startHour+" : "+sleep_startMin);
                        mSleepStartGoal.setText(getPREFERENCE2("sleep_startGoal"));
                    }
                }, 0, 0, true);
                timePickerDialog1.show();
            }
        });
    }

    public void setPREFERENCE2(String key, String value){
        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE2, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPREFERENCE2(String key){
        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE2, MODE_PRIVATE);
        return pref.getString(key, "");
    }

    int getType() { return type; }

    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mRdResult =
            new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
                @SuppressLint("SetTextI18n")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResult(HealthDataResolver.ReadResult healthData) {
                    mDayStartTimes = new long[5];
                    mStartTimes = new long[5];
                    mEndTimes = new long[5];
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

                            mDayStartTimes[count] = dayStart;
                            mStartTimes[count] = startMilli;
                            mEndTimes[count] = endMilli;

                            dayStart -= ONE_DAY_IN_MILLIS;
                            if (--count < 0)
                                break;
                        }
                    } finally {
                        healthData.close();
                        drawSleepTimeBar();
                    }
                }
            };

    /* STRT_##: Time to sleep, END_##: Time to wake up */
    private void setGoal() {
        /* Check if there are saved data. Otherwise, use default values */
        String sh = getPREFERENCE2("sleep_startHour");
        String sm = getPREFERENCE2("sleep_startMin");
        String eh = getPREFERENCE2("sleep_endHour");
        String em = getPREFERENCE2("sleep_endMin");

        if (sh != null && sh.length() > 0 && sm != null && sm.length() > 0
                && eh != null && eh.length() > 0 && em != null && em.length() > 0 ){
            startHour = Integer.parseInt(sh);
            startMin = Integer.parseInt(sm);
            endHour = Integer.parseInt(eh);
            endMin = Integer.parseInt(em);

            setPREFERENCE2("sleep_startGoal", sh+" : "+sm);
            setPREFERENCE2("sleep_endGoal", eh+" : "+em);
        } else {
            setPREFERENCE2("sleep_startGoal", 23+" : "+00);
            setPREFERENCE2("sleep_endGoal", 7+" : "+00);
        }

        /* Set offsets used to draw bat chart */
        int sleepGoal = (startHour * 60 + startMin) * (60 * 1000);
        int wakeUpGoal = (endHour * 60 + endMin) * (60 * 1000);

        if (sleepGoal > wakeUpGoal)
            sleepGoal -= ONE_DAY_IN_MILLIS;

        mSleepTimeGoal = wakeUpGoal - sleepGoal;
        mStartOffset = sleepGoal - mSleepTimeGoal / 2;
    }

    /* Draw bar chart. This function is dependent on mRdResult. */
    private void drawSleepTimeBar() {
        for (int i = 0; i < 5; i++) {
            long startMilli = mStartTimes[i];
            long endMilli = mEndTimes[i];
            long dayStart = mDayStartTimes[i];

            int startTime = Math.max((int) (startMilli - dayStart - mStartOffset), 0);
            int endTime = Math.min ((int) (endMilli - dayStart - mStartOffset), mSleepTimeGoal * 2);

            int sleepTimeInPeriod = endTime - startTime;

            View sleepTimeBar = mSleepTimeBarList.get(i);

            ViewGroup.LayoutParams params = sleepTimeBar.getLayoutParams();
            params.height = (int) (sleepTimeInPeriod * dp / (mSleepTimeGoal * 2 / 120)) + 1;
            sleepTimeBar.setLayoutParams(params);

            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)
                    sleepTimeBar.getLayoutParams();
            marginParams.setMargins(0, (int) (startTime * dp / (mSleepTimeGoal * 2 / 120)), 0, 0);
            sleepTimeBar.requestLayout();
        }

        int sleepTime = (int) (mStartTimes[4] - mEndTimes[4]);
        int hour =  sleepTime / 1000 / 60 / 60;
        int minute = (sleepTime / 1000 / 60) % 60;

        mTextHour.setText(hour+"");
        mTextMin.setText(minute+"");
    }

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
