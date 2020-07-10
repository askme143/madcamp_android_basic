package com.example.practice.ui.free;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.practice.MainActivity;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import com.example.practice.R;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Fragment3 extends Fragment {
    public static final String APP_TAG = "TestHealthApp";

    private View mView;
    private TextView mTextView;

    private Context mContext;
    private Activity mActivity;

    private Set<PermissionKey> mKeySet;
    private HealthDataStore mStore;
    private HealthConnectionErrorResult mConnectionErrorResult;
    private static Activity resolveActivity = null;    // e.g. Move to install Samsung Health

    public Fragment3 () {
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mContext = context;
//
//        if (context instanceof Activity)
//            mActivity = (Activity) context;
//
//        mKeySet = new HashSet<>();
//
//        mKeySet.add (new PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
//        mStore = new HealthDataStore(context, mConnectionListener);
//        System.out.println(context);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment3, null);
//        return viewGroup;
        mView = inflater.inflate(R.layout.fragment3, container, false);
        mTextView = mView.findViewById(R.id.stepCount);

        mKeySet = new HashSet<>();

        mKeySet.add (new PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        mStore = new HealthDataStore(getActivity(), mConnectionListener);

        System.out.println(getActivity());

        return mView;
    }

    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onConnected() {
            HealthPermissionManager permissionManager = new HealthPermissionManager(mStore);

            try {
                Map<PermissionKey, Boolean> resultMap = permissionManager.isPermissionAcquired(mKeySet);

                if (resultMap.containsValue(Boolean.FALSE)) {
                    permissionManager.requestPermissions(mKeySet, getActivity()).setResultListener(mPermissionListener);
                } else {
                    showStepCount();
                }
            } catch (Exception e) {
                Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(APP_TAG, "Permission setting fails.");
            }
        };

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult healthConnectionErrorResult) {

            Log.d(APP_TAG, "Health data service is not available.");
            showConnectionFailureDialog(healthConnectionErrorResult);
        }

        @Override
        public void onDisconnected() {
            Log.d(APP_TAG, "Health data service is disconnected.");
        }
    };

    private void showConnectionFailureDialog(HealthConnectionErrorResult error) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        mConnectionErrorResult = error;
        String message = "Connection with Samsung Health is not available.";

        if (mConnectionErrorResult.hasResolution()) {
            switch (error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    message = "Please install Samsung Health";
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    message = "Please upgrade Samsung Health";
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    message = "Please enable Samsung Health";
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    message = "Please agree with Samsung Health policy";
                    break;
                default:
                    message = "Please make Samsung Health available";
                    break;
            }
        }

        alert.setMessage(message);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mConnectionErrorResult.hasResolution()) {
                    mConnectionErrorResult.resolve(resolveActivity);
                }
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
    }

    @Override
    public void onDestroyView() {
        mStore.disconnectService();
        super.onDestroyView();
    }

    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResult(HealthPermissionManager.PermissionResult permissionResult) {
                    Log.d(APP_TAG, "Permission callback is received.");
                    Map<PermissionKey, Boolean> resultMap = permissionResult.getResultMap();

                    if (resultMap.containsValue(Boolean.FALSE)) {
                        Log.i(APP_TAG, "Permission denied");
                    } else {
                        showStepCount();
                    }
                }
            };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showStepCount() {
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
            Log.i(APP_TAG, "Reading health data fails.");
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
                        Iterator<HealthData> iterator = healthData.iterator();
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                        while (iterator.hasNext()) {
                            HealthData data = iterator.next();

                            String start = dtf.format(Instant.ofEpochMilli(data.getLong(HealthConstants.StepCount.START_TIME)).atZone(ZoneOffset.UTC));
                            String end = dtf.format(Instant.ofEpochMilli(data.getLong(HealthConstants.StepCount.END_TIME)).atZone(ZoneOffset.UTC));

                            Log.i(APP_TAG, "======= { steps: " + data.getInt(HealthConstants.StepCount.COUNT)
                                    + ", start: " + start
                                    + ", end: " + end + " } ");

                            count += data.getInt(HealthConstants.StepCount.COUNT);
                        }
                    } finally {
                        healthData.close();
                        Log.i(APP_TAG, "======= Total step count: " + count);
                        mTextView.setText(Integer.toString(count));
                    }
                }
            };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long getStartTimeOfToday() {
        return LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;
}
