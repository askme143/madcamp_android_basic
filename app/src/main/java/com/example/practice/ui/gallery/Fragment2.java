package com.example.practice.ui.gallery;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

import com.example.practice.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Fragment2 extends Fragment {
    ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment2, container, false);

        GridView gridView = (GridView) view.findViewById(R.id.grid_view);

        int mCellSize = (this.getResources().getDisplayMetrics().widthPixels - gridView.getRequestedHorizontalSpacing()) / 3;

        // Instance of ImageAdapter Class
        gridView.setAdapter(new ImageAdapter(getActivity(), mCellSize));

        //// On Click event for Single GridView Item ////
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Sending image id to FullScreenActivity
                Intent i = new Intent(getActivity(), FullImageActivity.class);
                // passing array index
                i.putExtra("id", position);
                startActivity(i);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.cameraIcon);
        Uri uri;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File photoFile = createFile();
//                Uri providerFileUri = FileProvider.getUriForFile(getActivity(), getPackageName(), photoFile);
//                i.putExtra(MediaStore.EXTRA_OUTPUT, providerFileUri);
//                startActivityForResult(i, 1);
                Intent i = new Intent(getActivity(), Camera.class);
                i.putExtra("id", 0);
                startActivity(i);
            }
        });


        return view;
    }

//    private File createFile() {
//        String fileName = "profileSample";
//        File imageDir = getActivity().getDir("profileImages", 0); // 0 = MODE_PRIVATE
//        return new File (imageDir, fileName+".png");
//    }
//
//    private void saveImageFile(Bitmap profileImage) {
//        // 중복 제거
//        boolean deleted = profileImagePath.delete();
//        Log.w(TAG, "Profile Delete Check : "+ deleted);
//        FileOutputStream fosImage = null;
//
//        try {
//            fosImage = new FileOutputStream(imagePath);
//            profileImage.compress(Bitmap.CompressFormat.PNG,100,fosImage);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                assert fosImage != null;
//                fosImage.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
