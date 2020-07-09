package com.example.practice.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Fragment1 extends Fragment {
    ArrayList<String> personNames = new ArrayList<>();
    ArrayList<String> phoneNumbers = new ArrayList<>();

    public Fragment1 () {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment1, null) ;
        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()); // .getApplicationContext???
        recyclerView.setLayoutManager(linearLayoutManager);

        if (personNames.size() == 0) {
            try {
                // get JSONObject from JSON file
                JSONObject obj = new JSONObject(loadJSONFromAsset());
                // fetch JSONArray named users
                JSONArray userArray = obj.getJSONArray("People");
                // implement for loop for getting users list data
                for (int i = 0; i < userArray.length(); i++) {
                    // create a JSONObject for fetching single user data
                    JSONObject userDetail = userArray.getJSONObject(i);
                    // fetch email and name and store it in arraylist
                    personNames.add(userDetail.getString("name"));
                    phoneNumbers.add(userDetail.getString("phone_number"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), personNames, phoneNumbers);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView

        return view;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("phone_num.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
