package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice.R;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

public class ExerciseHolder extends HealthHolder {
    private static final int type = 2;

    private Context mContext;

    private HealthDataStore mStore;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    public ExerciseHolder (HealthDataStore store) {
        mStore = store;
    }

    @Override
    void show(View view) {
        mContext = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.Exercise.HEALTH_DATA_TYPE)
                .setSort(HealthConstants.Sleep.START_TIME, HealthDataResolver.SortOrder.DESC)
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
                    ExerciseData[] exerciseDatas = new ExerciseData[10];
                    int count = 0;

                    try {
                        for (HealthData data: healthData) {
                            exerciseDatas[count++] = new ExerciseData(data);
                            if (count > 9)
                                break;
                        }
                    } finally {
                        healthData.close();

                        mRecyclerView.setLayoutManager(mLayoutManager);

                        HealthExerciseAdapter healthExerciseAdapter = new HealthExerciseAdapter(mContext, exerciseDatas);
                        mRecyclerView.setAdapter(healthExerciseAdapter);
                    }
                }
            };

    @Override
    int getType() {
        return type;
    }
}
