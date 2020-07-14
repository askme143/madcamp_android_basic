package com.example.practice.ui.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

public class Image {
    private String mAbsolutePath;
    private int mHieght;
    private int mWidth;
    private Bitmap mScaledImage;
    private Bitmap mOriginalImage = null;

    public Image(String path, int cellSize) {
        mAbsolutePath = path;
        mHieght = cellSize;
        mWidth = cellSize;
    }

    public Image(String path, int height, int width) {
        mAbsolutePath = path;
        mHieght = height;
        mWidth = width;
    }

    public Bitmap getOriginalImage() {
        if (mOriginalImage == null)
            try {
                mOriginalImage = rotateImage(BitmapFactory.decodeFile(mAbsolutePath, new BitmapFactory.Options()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return mOriginalImage;
    }

    public Bitmap getScaledImage() {
        if (mScaledImage == null)
            try {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                /* Get the dimensions of the bitmap */
                BitmapFactory.decodeFile(mAbsolutePath, bmOptions);
                int imageHeight = bmOptions.outHeight;
                int imageWidth = bmOptions.outWidth;

                /* Get the SCALE_FACTOR that is a power of 2 and
                    keeps both height and width larger than CELL_SIZE. */
                int scaleFactor = 1;
                if (imageHeight > mHieght || imageWidth > mWidth) {
                    final int halfHeight = imageHeight / 2;
                    final int halfWidth = imageWidth / 2;

                    while ((halfHeight / scaleFactor) >= mHieght
                            && (halfWidth / scaleFactor) >= mWidth) {
                        scaleFactor *= 2;
                    }
                }

                /* Decode the image file into a Bitmap sized to fill the View */
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                mScaledImage = rotateImage(BitmapFactory.decodeFile(mAbsolutePath, bmOptions));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return mScaledImage;
    }

    public String getAbsolutePath() {
        return mAbsolutePath;
    }

    private Bitmap rotateImage (Bitmap bitmap) throws IOException {
        /* Code below rotates the bitmap to be original direction */
        ExifInterface exif = new ExifInterface(mAbsolutePath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                break;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
