package com.example.practice.ui.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image {
    private String mAbsolutePath;
    private Bitmap mScaledImage;
    private Bitmap mOriginalImage = null;

    public Image(String path, Bitmap scaledImage) {
        mAbsolutePath = path;
        mScaledImage = scaledImage;
    }
}
