package com.example.practice.ui.health;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.practice.R;

import java.util.ArrayList;

public class HealthAdapter extends BaseAdapter {
    private static final int ITEM_VIEW_TYPE_STP = 0;
    private static final int ITEM_VIEW_TYPE_SLP = 1;
    private static final int ITEM_VIEW_TYPE_EXR = 2;
    private static final int ITEM_VIEW_TYPE_MAX = 3;

    private ArrayList<HealthHolder> healthHolderList;

    public HealthAdapter (ArrayList<HealthHolder> healthHolderList) {
        this.healthHolderList = healthHolderList;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        return healthHolderList.get(position).getType();
    }

    @Override
    public int getCount() {
        return healthHolderList.size();
    }

    @Override
    public Object getItem(int i) {
        return healthHolderList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            HealthHolder healthHolder = healthHolderList.get(i);

            switch (healthHolder.getType()) {
                case ITEM_VIEW_TYPE_STP:
                    view = inflater.inflate(R.layout.fragment3_step, viewGroup, false);
                    break;
                case ITEM_VIEW_TYPE_SLP:
                    view = inflater.inflate(R.layout.fragment3_sleep, viewGroup, false);
                    break;
                case ITEM_VIEW_TYPE_EXR:
                    view = inflater.inflate(R.layout.fragment3_exercise, viewGroup, false);
                    break;
            }

            healthHolder.show(view);
        }

        return view;
    }
}
