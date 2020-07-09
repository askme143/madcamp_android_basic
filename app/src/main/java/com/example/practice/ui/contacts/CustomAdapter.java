package com.example.practice.ui.contacts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice.R;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList<Contact> contactList;
    private Context context;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;

    public CustomAdapter(Context context, ArrayList<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment1_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.phone_number.setText(contact.getPhoneNumber());
        changeVisibility(holder.hidden_layer, selectedItems.get(position));

        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.get(position)) {
                    selectedItems.delete(position);
                } else {
                    selectedItems.delete(prePosition);
                    selectedItems.put(position, true);
                }

                if (prePosition != -1) notifyItemChanged(prePosition);
                notifyItemChanged(position);

                prePosition = position;
            }
        });

        holder.call.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + contact.getPhoneNumber()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.message.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + contact.getPhoneNumber()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private void changeVisibility(final View v, final boolean isExpanded) {
        float d = context.getResources().getDisplayMetrics().density;
        ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, (int) (60 * d)) : ValueAnimator.ofInt((int) (60 * d), 0);
        int duration = isExpanded ? 60 : 1;

        va.setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
                v.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        });

        va.start();
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "MainActivity";
        TextView name, phone_number;// init the item view's
        RelativeLayout hidden_layer;
        View line;
        ImageButton call, message;

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            phone_number = (TextView) itemView.findViewById(R.id.phoneNumber);
            hidden_layer = (RelativeLayout) itemView.findViewById(R.id.hiddenButtons);
            line = (View) itemView.findViewById(R.id.line);
            call = (ImageButton) itemView.findViewById(R.id.call);
            message = (ImageButton) itemView.findViewById(R.id.message);
        }
    }
}