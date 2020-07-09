package com.example.practice.ui.contacts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.practice.R;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList<String> personNames;
    private ArrayList<String> phoneNumbers;
    private Context context;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;

    public CustomAdapter(Context context, ArrayList<String> personNames, ArrayList<String> phoneNumbers) {
        this.context = context;
        this.personNames = personNames;
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment1_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // set the data in items
        holder.name.setText(personNames.get(position));
        holder.phone_number.setText(phoneNumbers.get(position));

        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:".concat(phoneNumbers.get(position))));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

//                if (selectedItems.get(position)) {
//                    selectedItems.delete(position);
//                } else {
//                    selectedItems.delete(prePosition);
//                    selectedItems.put(position, true);
//                }
//
//                if (prePosition != -1) notifyItemChanged(prePosition);
//                notifyItemChanged(position);
//
//                if (prePosition != position)
//                    prePosition = position;
//                else
//                    prePosition = -1;

                Toast.makeText(context, personNames.get(position).concat(" ").concat(phoneNumbers.get(position)), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return personNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "MainActivity";
        TextView name, phone_number;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            phone_number = (TextView) itemView.findViewById(R.id.phoneNumber);
        }

        void onBind(int position) {
            changeVisibility(selectedItems.get(position));
        }

        private void changeVisibility(final boolean isExpanded) {
            int dpValue = 150;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);

            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // value는 height 값
                    int value = (int) animation.getAnimatedValue();
                    // imageView의 높이 변경
                    itemView.getLayoutParams().height = value;
                    itemView.requestLayout();
                    // imageView가 실제로 사라지게하는 부분
                    itemView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            // Animation start
            va.start();
        }
    }
}