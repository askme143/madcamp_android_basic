package com.example.practice.ui.health;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice.MainActivity;
import com.example.practice.R;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Array;

public class HealthExerciseAdapter extends RecyclerView.Adapter<HealthExerciseAdapter.HealthExerciseViewHolder> {
    private ExerciseData[] mExcerciseDataArray;
    private Context mContext;
    private String mImageDirPath;
    private String[] mImagePaths;

    public HealthExerciseAdapter(Context context, ExerciseData[] excerciseDataArray) {
        mExcerciseDataArray = excerciseDataArray;
        mContext = context;
        mImageDirPath = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/MadCampApp/Exercise/";

        File storageDir = new File(mImageDirPath);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        mImagePaths = storageDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                boolean bOK = false;
                if(s.toLowerCase().endsWith(".jpg")) bOK = true;

                return bOK;
            }
        });
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
        ExerciseData exerciseData = mExcerciseDataArray[position];
        if (exerciseData == null)
            return;

        final String startTimeCode = Long.toString(exerciseData.getStartTimeMilli());
        String imagePath = null;
        for (String path : mImagePaths) {
            if (path.contains(startTimeCode)) {
                imagePath = path;
                break;
            }
        }

        if (imagePath != null) {
            System.out.println(position);
            holder.image.setImageBitmap(BitmapFactory.decodeFile(mImageDirPath + imagePath));
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.image.setImageDrawable(mContext.getDrawable(R.drawable.dotted_rectangle));
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mContext).startSelectImage(startTimeCode);
                }
            });
        }
        holder.date.setText(exerciseData.getMonthString() + "/" + exerciseData.getDayString());
        holder.time.setText(exerciseData.getHourString() + ":" + exerciseData.getMinString());
        holder.calorie.setText(exerciseData.getCalorieString() + " kcal");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class HealthExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView date, time, calorie, add;
        ImageView image;

        public HealthExerciseViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            calorie = itemView.findViewById(R.id.calorie);

            image = itemView.findViewById(R.id.image);

            add = itemView.findViewById(R.id.add);
        }
    }
}
