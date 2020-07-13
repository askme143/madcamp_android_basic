package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Fragment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice.MainActivity;
import com.example.practice.R;
import com.example.practice.ui.contacts.CustomAdapter;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class HealthExerciseAdapter extends RecyclerView.Adapter<HealthExerciseAdapter.HealthExerciseViewHolder> {
    private long[] mStartTimeMilliArray;

    public HealthExerciseAdapter(long[] pStartTimeMilliArray) {
        mStartTimeMilliArray = pStartTimeMilliArray;
    }

    @NonNull
    @Override
    public HealthExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment3_exercise_item, parent, false);

        return new HealthExerciseViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HealthExerciseViewHolder holder, int position) {
        long startTimeMilli = mStartTimeMilliArray[position];


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTimeMilli);
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String stringHour = hour < 10 ? "0" + hour : hour + "";
        String stringMinute = minute < 10 ? "0" + minute : minute + "";

        holder.date.setText(month + "/" + day);
        holder.time.setText(stringHour + ":" + stringMinute);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class HealthExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView date, time;

        public HealthExerciseViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
        }
    }
}
