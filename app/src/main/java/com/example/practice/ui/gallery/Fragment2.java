package com.example.practice.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

import com.example.practice.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // OCR

            }
        });

        return view;
    }
}
