package com.example.practice.ui.free;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.practice.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.practice.ui.free.Fragment3.APP_TAG;

public class SamsungPermissionRequest extends Activity {
    private Set<HealthPermissionManager.PermissionKey> mKeySet;
    private HealthDataStore mStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mKeySet = new HashSet<>();
        mKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));

        mStore = new HealthDataStore(this, mConnectionListener);

        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
        pmsManager.requestPermissions(mKeySet, SamsungPermissionRequest.this).setResultListener(mPermissionListener);
    }

    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResult(HealthPermissionManager.PermissionResult permissionResult) {
                    Log.d(APP_TAG, "Permission callback is received.");
                    Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = permissionResult.getResultMap();

                    if (resultMap.containsValue(Boolean.FALSE)) {
                        Log.i(APP_TAG, "Permission denied");
                        finish();
                        System.exit(0);
                    } else {
                        finish();
                    }
                }
            };

    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onConnected() {
            HealthPermissionManager permissionManager = new HealthPermissionManager(mStore);

            try {
                permissionManager.requestPermissions(mKeySet, SamsungPermissionRequest.this).setResultListener(mPermissionListener);
            } catch (Exception e) {
                Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(APP_TAG, "Permission setting fails.");
            }
        };

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult healthConnectionErrorResult) {
            Log.d(APP_TAG, "Health data service is not available.");
        }

        @Override
        public void onDisconnected() {
            Log.d(APP_TAG, "Health data service is disconnected.");
        }
    };
}
