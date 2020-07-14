package com.example.practice.ui.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.content.Intent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.practice.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class Fragment2 extends Fragment {
    private Fragment2 mFragment = this;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private Context mContext;
    private GridView mGridView;
    FloatingActionButton mFloatButton;
    private int mCellSize;

    private String mCurrentPhotoPath;
    private String mImageDirPath;

    private String[] mImagePaths;
    private ImageAdapter mImageAdapter;
    private ArrayList<Image> mImageArrayList;

    private int click_enable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment2, container, false);
        mContext = view.getContext();
        mImageDirPath = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/MadCampApp";

        /* Get GRID_VIEW */
        mGridView = (GridView) view.findViewById(R.id.grid_view);

        if (mImagePaths == null) {
            initFragment();
        }
        mGridView.setAdapter(mImageAdapter);

        /* Floating camera button */
        mFloatButton = view.findViewById(R.id.cameraIcon);

        setListener();

        return view;
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Timestamp(System.currentTimeMillis()));
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(mImageDirPath);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
            imageFileName, ".jpg", storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("captureCamera Error", ex.toString());
                return;
            }

            Uri photoURI = FileProvider.getUriForFile(mContext, "com.example.practice.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                galleryAddPic();
                /* Update an array of image paths */
                File dir = new File(mImageDirPath);
                mImagePaths = dir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        boolean bOK = false;
                        if(s.toLowerCase().endsWith(".jpg")) bOK = true;

                        return bOK;
                    }
                });

                /* Insert new image */
                mImageArrayList.add(new Image(mCurrentPhotoPath, mCellSize));

                /* Update View */
                mImageAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("Request Take Photo", e.toString());
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
        Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
    }

    private void initFragment() {
        /* Make an array of image paths */
        File dir = new File(mImageDirPath);
        mImagePaths = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                boolean bOK = false;
                if(s.toLowerCase().endsWith(".jpg")) bOK = true;

                return bOK;
            }
        });

        /* Get proper CELL_SIZE which is (width pixels - space between cells) / 3 */
        mCellSize = (getResources().getDisplayMetrics().widthPixels - mGridView.getRequestedHorizontalSpacing()) / 3;

        /* Make an array list of IMAGEs */
        mImageArrayList = new ArrayList<>();
        if (mImagePaths != null) {
            for (String path : mImagePaths) {
                mImageArrayList.add(new Image(mImageDirPath + "/" + path, mCellSize));
            }
        }

        /* Set new image adapter to GRIDVIEW */
        mImageAdapter = new ImageAdapter(getActivity(), mCellSize, mImageArrayList);

        click_enable = 1;
    }

    private void setListener() {
        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        /* Set click listener. Start FULL_IMAGE_ACTIVITY with POSITION which
            indicates an clicked image */
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (click_enable == 1) {
                    Intent i = new Intent(getActivity(), FullImageActivity.class);
                    i.putExtra("id", position);
                    i.putExtra("imagePaths", mImagePaths);
                    i.putExtra("imageDirPath", mImageDirPath);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
             @SuppressLint("FragmentBackPressedCallback")
             @Override
             public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                 click_enable = 0;
                 final ImageSelectAdapter deleteAdapter = new ImageSelectAdapter(mContext, mCellSize, mImageArrayList, true);
                 mGridView.setAdapter(deleteAdapter);

                 final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                     @Override
                     public void handleOnBackPressed() {
                         mGridView.setAdapter(mImageAdapter);

                         click_enable = 1;
                         mFloatButton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 dispatchTakePictureIntent();
                             }
                         });

                         this.remove();
                     }
                 };
                 requireActivity().getOnBackPressedDispatcher().addCallback(mFragment, callback);

                 mFloatButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         deleteAdapter.deleteChecked();
                         mGridView.setAdapter(mImageAdapter);

                         click_enable = 1;
                         mFloatButton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 dispatchTakePictureIntent();
                             }
                         });

                         callback.remove();
                     }
                 });
                 return false;
             }
         }
        );
    }
}
