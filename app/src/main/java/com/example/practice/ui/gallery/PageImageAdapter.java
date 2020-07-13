package com.example.practice.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.practice.R;

import java.util.ArrayList;

public class PageImageAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Image> mImageArrayList;


//    public int[] GalImages = new int[] {
//            R.drawable.pic_1,
//            R.drawable.pic_2,
//            R.drawable.pic_3,
//            R.drawable.pic_4,
//            R.drawable.pic_5,
//            R.drawable.pic_6,
//            R.drawable.pic_7,
//            R.drawable.pic_8,
//            R.drawable.pic_9,
//            R.drawable.pic_10,
//            R.drawable.pic_11,
//            R.drawable.pic_12,
//            R.drawable.pic_13,
//            R.drawable.pic_14,
//            R.drawable.pic_15,
//    };

    PageImageAdapter(Context context, ArrayList<Image> imageArrayList){
        mContext=context;
        mImageArrayList = imageArrayList;
    }

    @Override
    public int getCount() {
        return mImageArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((ImageView) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        int padding = (int) mContext.getResources().getDimension(R.dimen.activity_vertical_margin);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageBitmap (mImageArrayList.get(position).getOriginalImage());
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

//    @Override
//    public int getCount() {
//        return GalImages.length;
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == ((ImageView) object);
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        ImageView imageView = new ImageView(context);
//        int padding = (int) context.getResources().getDimension(R.dimen.activity_vertical_margin);
//        imageView.setPadding(padding, padding, padding, padding);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        imageView.setImageResource(GalImages[position]);
//        ((ViewPager) container).addView(imageView, 0);
//        return imageView;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        ((ViewPager) container).removeView((ImageView) object);
//    }
}
