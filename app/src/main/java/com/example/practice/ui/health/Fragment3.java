package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class Fragment3 extends Fragment {
    public static final String TAB_TAG = "TestHealthApp";

    private View mView;

    private Set<PermissionKey> mKeySet;
    private HealthDataStore mStore = null;
    private HealthConnectionErrorResult mConnectionErrorResult;

    private StepHolder mStepHolder = null;
    private SleepHolder mSleepHolder = null;

    public Fragment3 () {
    }

    @Override
    public void onDestroyView() {
        mStore.disconnectService();
        super.onDestroyView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment3, container, false);

        mKeySet = new HashSet<>();
        mKeySet.add (new PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        mKeySet.add (new PermissionKey(HealthConstants.Sleep.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));

        mStore = new HealthDataStore(getActivity(), mConnectionListener);
        mStepHolder = new StepHolder(mStore, mView);
        mSleepHolder = new SleepHolder(mStore, mView);
        mStore.connectService();

        return mView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showContent() {
        mStepHolder.show();
        mSleepHolder.show();
    }

    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onConnected() {
            HealthPermissionManager permissionManager = new HealthPermissionManager(mStore);

            try {
                Map<PermissionKey, Boolean> resultMap = permissionManager.isPermissionAcquired(mKeySet);

                if (resultMap.containsValue(Boolean.FALSE)) {
                    permissionManager.requestPermissions(mKeySet, (Activity) getContext()).setResultListener(mPermissionListener);
                } else {
                    showContent();
                }
            } catch (Exception e) {
                Log.e(TAB_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(TAB_TAG, "Permission setting fails.");
            }
        };

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult healthConnectionErrorResult) {
            Log.d(TAB_TAG, "Health data service is not available.");
            showConnectionFailureDialog(healthConnectionErrorResult);
        }

        @Override
        public void onDisconnected() {
            Log.d(TAB_TAG, "Health data service is disconnected.");
        }

    };

    private void showConnectionFailureDialog(final HealthConnectionErrorResult error) {
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
                    if (error.getErrorCode() == HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.sec.android.app.shealth&hl=en_US")));
                        System.exit(0);
                    }
                }
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
    }

    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResult(HealthPermissionManager.PermissionResult permissionResult) {
                    Log.d(TAB_TAG, "Permission callback is received.");
                    Map<PermissionKey, Boolean> resultMap = permissionResult.getResultMap();

                    if (resultMap.containsValue(Boolean.FALSE)) {
                        Log.i(TAB_TAG, "Permission denied");
                    } else {
                        showContent();
                    }
                }
            };
}
